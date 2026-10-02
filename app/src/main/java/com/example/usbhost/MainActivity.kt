package com.example.usbhost

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var statusText: TextView
    private lateinit var deviceLog: TextView
    private lateinit var scanButton: Button

    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (granted && device != null) {
                    statusText.text = "Permission granted: ${device.deviceName}"
                    openDevice(device)
                } else {
                    statusText.text = "Permission denied"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        statusText = findViewById(R.id.statusText)
        deviceLog = findViewById(R.id.deviceLog)
        scanButton = findViewById(R.id.scanButton)

        registerReceiver(permissionReceiver, IntentFilter(ACTION_USB_PERMISSION))

        scanButton.setOnClickListener {
            scanUsbDevices()
        }

        scanUsbDevices()
    }

    override fun onDestroy() {
        unregisterReceiver(permissionReceiver)
        super.onDestroy()
    }

    private fun scanUsbDevices() {
        val devices = usbManager.deviceList
        if (devices.isEmpty()) {
            statusText.text = "No USB devices detected"
            deviceLog.text = "No USB devices found.\nThis may mean the phone is not in host mode or the cable/device is not supported."
            return
        }

        val log = StringBuilder()
        devices.values.forEach { device ->
            log.append("Device: ${device.deviceName}\n")
            log.append("VID: ${device.vendorId} PID: ${device.productId}\n")
            log.append("Class: ${device.deviceClass} Subclass: ${device.deviceSubclass} Protocol: ${device.deviceProtocol}\n")
            log.append("Interface count: ${device.interfaceCount}\n")
            log.append("---\n")

            if (usbManager.hasPermission(device)) {
                openDevice(device)
            } else {
                requestPermission(device)
            }
        }

        deviceLog.text = log.toString()
        statusText.text = "USB scan completed"
    }

    private fun requestPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(device, permissionIntent)
    }

    private fun openDevice(device: UsbDevice) {
        val connection: UsbDeviceConnection? = usbManager.openDevice(device)
        if (connection == null) {
            statusText.text = "Could not open USB device"
            return
        }

        val builder = StringBuilder(deviceLog.text)
        builder.append("Opened: ${device.deviceName}\n")

        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            builder.append("Interface ${usbInterface.id}: class=${usbInterface.interfaceClass}, subclass=${usbInterface.interfaceSubclass}, protocol=${usbInterface.interfaceProtocol}\n")

            for (j in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(j)
                val type = when (endpoint.type) {
                    UsbConstants.USB_ENDPOINT_XFER_CONTROL -> "control"
                    UsbConstants.USB_ENDPOINT_XFER_ISOC -> "isoc"
                    UsbConstants.USB_ENDPOINT_XFER_BULK -> "bulk"
                    UsbConstants.USB_ENDPOINT_XFER_INT -> "interrupt"
                    else -> "unknown"
                }
                builder.append("  Endpoint ${endpoint.address}: type=$type, direction=${endpoint.direction}\n")
            }
        }

        deviceLog.text = builder.toString()
        statusText.text = "Device opened successfully"
        Log.d("USBHostCableTest", "USB cable test succeeded for ${device.deviceName}")
        connection.close()
    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.example.usbhost.USB_PERMISSION"
    }
}
