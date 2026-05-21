@echo off
echo ==============================================
echo Building SonzaiX Streaming Debug APK (Windows)
echo ==============================================
call gradlew.bat assembleDebug
if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Build completed successfully!
    echo APK Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo [ERROR] Build failed. Please check the logs above.
)
pause
