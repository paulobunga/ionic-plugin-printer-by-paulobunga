export interface PrinterPlugin {
  requestPermissions(): Promise<void>;
  getConnectedDevices(): Promise<{ devices: DeviceInfo[] }>;
  connectToDevice(options: { device_name: string }): Promise<void>;

  // Modify the print method to receive JSON
  print(options: { content: OrderData }): Promise<void>;

  disconnect(): Promise<void>;
}

export interface DeviceInfo {
  device_name: string;
  device_address: string;
  // Add more device information properties as needed
}

// Define the structure of the JSON data
interface OrderData {
  orderNum: string;
  total: string;
  type: string;
  paiementType: string;
  notes: string;
  company: string;
  address: string;
  tel: string;
  date: string;
  hour: string;
  items: OrderItem[];
}

interface OrderItem {
  title: string;
  price: string;
  quantity: string;
  combinations: Combination[];
}

interface Combination {
  title: string;
  choice: string[];
}