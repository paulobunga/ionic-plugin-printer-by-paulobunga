export interface PrinterPlugin {
  requestPermissions(): Promise<void>;
  getConnectedDevices(): Promise<{ devices: DeviceInfo[] }>;
  connectToDevice(options: { device_name: string }): Promise<void>;
  print(options: { content: string }): Promise<void>;
  disconnect(): Promise<void>;
}

export interface DeviceInfo {
  device_name: string;
  device_address: string;
  // Add more device information properties as needed
}