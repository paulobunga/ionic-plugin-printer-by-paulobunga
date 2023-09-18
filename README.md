# capacitor-plugin-printer

Printer Plugin For Capacitor

## Install

```bash
npm install @paulobunga/capacitor-plugin-printer
ionic cap sync android
```

## API

<docgen-index>

* [`requestPermissions()`](#requestpermissions)
* [`getConnectedDevices()`](#getconnecteddevices)
* [`connectToDevice(...)`](#connecttodevice)
* [`print(...)`](#print)
* [`disconnect()`](#disconnect)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### requestPermissions()

```typescript
requestPermissions() => Promise<void>
```

--------------------


### getConnectedDevices()

```typescript
getConnectedDevices() => Promise<{ devices: DeviceInfo[]; }>
```

**Returns:** <code>Promise&lt;{ devices: DeviceInfo[]; }&gt;</code>

--------------------


### connectToDevice(...)

```typescript
connectToDevice(options: { device_name: string; }) => Promise<void>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ device_name: string; }</code> |

--------------------


### print(...)

```typescript
print(options: { content: string; }) => Promise<void>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ content: string; }</code> |

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------


### Interfaces


#### DeviceInfo

| Prop                 | Type                |
| -------------------- | ------------------- |
| **`device_name`**    | <code>string</code> |
| **`device_address`** | <code>string</code> |

</docgen-api>
"# capacitor-plugin-printer" 
