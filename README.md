# Ionic Capacitor Printer Plugin

Ionic Plugin For Self Service Point Of Sale Systems

## Install

```bash
npm i ionic-plugin-printer-by-paulobunga
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


## Contact Information
For custom configuration, tech support, or additional developer work related to this plugin, you can contact:

### Name: Paul Obunga

#### Hourly Rate: $15-20/Hr

#### Quick Support & Consultation: $500

#### Port to Flutter, React Native, Cordova: $500

#### Additional Features: Refer to hourly rate

Contact Details: Paul Obunga's Linktree
<a href="https://linktr.ee/paulobunga">Contact Infomation </a>
