# USB cable test notes

## Goal

This project is focused on checking whether a phone can detect and interact with another USB device when connected by USB-C-to-USB-C cable.

## What to test

- does the phone enumerate the connected device?
- does the Android USB Host API see the device?
- is the cable role correct for host mode?
- are permissions granted and interfaces visible?

## Why this matters

If the phone does not show the device under USB host mode, then the OS and hardware are not exposing the cable as a host connection.

## Requirements

A real working host-mode cable setup needs:
- USB-C hardware support
- compatible cable
- compatible device
- host-mode capability on the phone
- proper firmware/kernel support

## Limitation

This app does not magically force host mode. It only demonstrates the correct Android API flow when the hardware already supports it.
