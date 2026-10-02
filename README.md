# android-usb-host-emulator

Android USB host mode research and sample app for C-to-C USB host experiments.

## What this repository is for

This project is designed to explore how an Android phone can act as a USB host over a USB-C-to-USB-C cable, and how to build a practical proof-of-concept on devices that support host mode.

## Important reality check

A phone does not become a USB host purely by software. Actual host mode depends on the device hardware, USB role switching support, and kernel/firmware behavior. Apps can only interact with USB devices when the phone is already in host mode.

## Cable-focused experiment

The first goal is to test a USB-C cable connection between a host phone and another device and validate whether the host phone exposes USB host capabilities to the OS.

This repo includes:
- Android sample project
- USB host discovery flow
- permission handling and endpoint inspection
- notes for testing on a C-to-C cable setup

## Quick usage

1. Open the project in Android Studio.
2. Run the app on a device that supports USB host mode.
3. Connect a USB device using a USB-C or USB-C-to-C cable.
4. Observe the log and device enumeration output.

## Repo structure

- `app/` — Android app
- `docs/` — notes on USB host mode and limitations

## Cloud computer note

This project is designed so it can be developed in a remote/cloud environment and then built locally or remotely using Android toolchains as needed.
