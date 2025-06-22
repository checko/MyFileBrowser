# MyFileBrowser System App for AOSP

## File Placement
- Place all app source files (Kotlin, XML, AndroidManifest.xml, Android.bp) in:
  `packages/apps/MyFileBrowser/`
- (Project must be in `packages/apps/` for AOSP build system compatibility)

## Add to System Partition
- You must add your app to the system partition package list.
- For example, for cuttlefish or generic targets, add this line to `build/make/target/product/handheld_system.mk`:
  ```makefile
  PRODUCT_PACKAGES += MyFileBrowser
  ```
- Or, add to your device/product makefile (e.g. `device/<vendor>/<board>/<product>.mk`).

## Platform Signing
- Build with platform keys (default for `certificate: "platform"` in Android.bp).
- If signing manually:
  ```
  java -jar signapk.jar platform.x509.pem platform.pk8 MyFileBrowser.apk MyFileBrowser-signed.apk
  ```

## Build Commands
```sh
# In AOSP root
source build/envsetup.sh
lunch <your_target>
m MyFileBrowser
```

## Flash & Test
```sh
adb root
adb remount
adb push out/target/product/<device>/system/priv-app/MyFileBrowser/MyFileBrowser.apk /system/priv-app/MyFileBrowser/
adb shell pm install -r /system/priv-app/MyFileBrowser/MyFileBrowser.apk
adb shell setenforce 1 # Ensure SELinux is enforcing
adb shell am start -n com.example.myfilebrowser/.MainActivity
```

## SELinux Policy
- No custom policy is needed for /storage/emulated/0 access as a privileged, platform-signed app.
- If you see denials, use `adb logcat | grep avc` and adjust policy as needed.

## Smoke Test
```sh
# Get MyFileBrowser's UID
adb shell dumpsys package com.example.myfilebrowser | grep userId

# List /storage/emulated/0 as MyFileBrowser's UID (should succeed)
adb shell run-as com.example.myfilebrowser ls /storage/emulated/0
```
