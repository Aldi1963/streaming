#!/bin/bash
echo "=============================================="
echo "Installing SonzaiX Streaming onto Connected Device (Unix)"
echo "=============================================="
APK_PATH="app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

if [ ! -f "$APK_PATH" ]; then
    echo "[ERROR] Compiled APK not found. Building first..."
    chmod +x build-apk.sh
    ./build-apk.sh
fi

if [ -f "$APK_PATH" ]; then
    echo "Installing $APK_PATH..."
    adb install -r -d "$APK_PATH"
    if [ $? -eq 0 ]; then
        echo "[SUCCESS] Installation successful! Starting the application..."
        adb shell am start -n com.sonzaix.streaming/.MainActivity
    else
        echo "[ERROR] Installation failed. Ensure your device is connected via USB/WiFi debug."
        exit 1
    fi
else
    echo "[ERROR] APK could not be generated."
    exit 1
fi
