#!/bin/bash
echo "=============================================="
echo "Building SonzaiX Streaming Debug APK (Unix)"
echo "=============================================="

# Ensure execute permissions for gradlew
chmod +x gradlew

./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "[SUCCESS] Build completed successfully!"
    echo "APK Location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo ""
    echo "[ERROR] Build failed. Please check the logs above."
    exit 1
fi
