@echo off
echo ==============================================
echo Installing SonzaiX Streaming onto Connected Device (Windows)
echo ==============================================
set APK_PATH=app-debug.apk
if not exist %APK_PATH% (
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
)

if not exist %APK_PATH% (
    echo [ERROR] Compiled APK not found. Running build-apk.bat first...
    call build-apk.bat
)

if exist %APK_PATH% (
    echo Installing %APK_PATH%...
    adb install -r -d %APK_PATH%
    if %ERRORLEVEL% equ 0 (
        echo [SUCCESS] Installation successful! Starting the application...
        adb shell am start -n com.sonzaix.streaming/.MainActivity
    ) else (
        echo [ERROR] Installation failed. Ensure your device is connected via USB/WiFi debug.
    )
) else (
    echo [ERROR] APK could not be generated.
)
pause
