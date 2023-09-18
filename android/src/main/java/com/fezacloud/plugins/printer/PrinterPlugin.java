package com.fezacloud.plugins.printer;

import static com.printer.sdk.PrinterConstants.*;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.usb.USBPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CapacitorPlugin(
        name = "Printer",
        permissions = {
                @Permission(
                        alias = "storage",
                        strings = {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                ),
                @Permission(
                        alias = "bluetooth",
                        strings = {
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN
                        }
                ),
                @Permission(
                        alias = "internet",
                        strings = { Manifest.permission.INTERNET }
                ),
                @Permission(
                        alias = "wifi",
                        strings = {
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE
                        }
                ),
                @Permission(
                        alias = "location",
                        strings = { Manifest.permission.ACCESS_COARSE_LOCATION }
                ),
                @Permission(
                        alias = "camera",
                        strings = { Manifest.permission.CAMERA }
                )
        }
)
public class PrinterPlugin extends Plugin {

    private Context _context;

    private boolean isConnected = false;

    private String deviceName = "Unknown Device";
    private String deviceAddress;

    private final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private List<UsbDevice> usbDevices = new ArrayList<>();
    private PrinterInstance printer;


    @Override
    public void load() {
        _context = this.getContext();
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);

        // Register the USB receiver with the appropriate IntentFilter
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        _context.registerReceiver(usbReceiver, filter);

        loadUsbDevices();
    }

    @PluginMethod
    public void getConnectedDevices(PluginCall call) {
        JSObject result = new JSObject();
        JSArray deviceList = new JSArray();

        // Check if the device list has been loaded, if not, load it
        if (usbDevices.isEmpty()) {
            loadUsbDevices();
        }

        // Iterate through the list of USB devices and add USB printers to the deviceList
        for (UsbDevice device : usbDevices) {
            if (USBPort.isUsbPrinter(device)) {
                JSObject deviceInfo = new JSObject();
                deviceInfo.put("device_name", device.getDeviceName());
                deviceInfo.put("device_address", "vid: " + device.getVendorId() + "  pid: " + device.getProductId());
                // Add more device information as needed

                deviceList.put(deviceInfo);
            }
        }

        // Put the deviceList in the result object
        result.put("devices", deviceList);

        // Return the result to Capacitor
        call.resolve(result);
    }

    // Helper method to load USB devices
    private void loadUsbDevices() {
        // Get USB Devices connected and connect to the first one
        HashMap<String, UsbDevice> connectedDevices = usbManager.getDeviceList();

        // Iterate through connected devices and filter USB printers
        List<UsbDevice> usbPrinters = new ArrayList<>();
        for (UsbDevice device : connectedDevices.values()) {
            if (USBPort.isUsbPrinter(device)) {
                usbPrinters.add(device);
            }
        }

        usbDevices.addAll(usbPrinters);
    }

    @PluginMethod
    public void connectToDevice(PluginCall call) {
        String deviceName = call.getString("device_name");

        // Iterate through the list of USB devices and find the one with the specified name
        UsbDevice selectedDevice = null;
        for (UsbDevice device : usbDevices) {
            if (deviceName.equals(device.getDeviceName())) {
                selectedDevice = device;
                break; // Found the device, no need to continue searching
            }
        }

        if (selectedDevice != null) {
            // Check if you have permission to access the selected device
            if(usbManager.hasPermission(selectedDevice)) {
                // We can connect to this device
                startConnection(selectedDevice);
                call.resolve(); // Indicate successful connection
            } else {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                _context.registerReceiver(usbReceiver, filter);
                usbManager.requestPermission(selectedDevice, pendingIntent);
            }
        } else {
            call.reject("No USB Printer found");
        }
    }

    @PluginMethod
    public void print(PluginCall call) {
        String content = call.getString("content");

        if (printer == null || !isConnected) {
            // Handle the case where the printer is not connected
            call.reject("Printer not connected");
            return;
        }

        try {
            printer.initPrinter();
            printer.setFont(0, 0, 0, 0, 0);
            printer.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
            printer.printText(content);
            printer.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
            printer.cutPaper(66, 50);
            call.resolve();
        } catch (Exception e) {
            // Handle printing errors
            call.reject("Printing failed: " + e.getMessage());
        }
    }

    @PluginMethod
    public void disconnect(PluginCall call) {
        if (printer != null) {
            // Close the printer connection
            printer.closeConnection();
            isConnected = false;
            // Additional cleanup if necessary
            printer = null;
        }
        call.resolve();
    }

    // Our Handler
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Connect.SUCCESS:
                    isConnected = true;
                    GlobalConstants.ISCONNECTED = true;
                    GlobalConstants.DEVICENAME = deviceName;
                    break;
                case Connect.FAILED:
                    GlobalConstants.ISCONNECTED = isConnected = false;
                    Toast.makeText(_context, "Connection Failed", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", "Connection failed!");
                    break;
                case Connect.CLOSED:
                    GlobalConstants.ISCONNECTED = isConnected = false;
                    GlobalConstants.DEVICENAME = deviceName;
                    Toast.makeText(_context, "Connection Closed", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", "Connection Closed!");
                    break;
                case Connect.NODEVICE:
                    GlobalConstants.ISCONNECTED = isConnected = false;
                    Toast.makeText(_context, "No Device Found", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    // Receiver
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        // Continue to connect to the device
                        startConnection(device);
                    } else {
                        // Permission denied. Show error message
                        new AlertDialog.Builder(_context)
                                .setTitle("USB Permissions Denied")
                                .setMessage("Unable to connect to device. Permissions Error")
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();
                    }
                }
            } else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(_context, "USB Device Attached", Toast.LENGTH_SHORT).show();
            } else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(_context, "USB Device Dettached", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startConnection(UsbDevice device) {
        if(!isConnected) {
            // Initialize your PrinterInstance here if not already initialized
            if(printer == null) {
                printer = PrinterInstance.getPrinterInstance(_context, device, handler);
            }

            // Create and start the ConnectionThread
            ConnectionThread connectionThread = new ConnectionThread();
            connectionThread.start();
        }
    }

    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            if(printer != null) {
                isConnected = printer.openConnection();
            }
        }
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();

        _context.unregisterReceiver(usbReceiver);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        super.requestPermissions(call);
    }


}