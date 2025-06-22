# FileManager System App for AOSP

## File Placement
- Place all app source files (Kotlin, XML, AndroidManifest.xml, Android.bp) in:
  `packages/apps/MyFileBrowser/`
- Place `filemanager.te` in:
  `device/<vendor>/<board>/sepolicy/private/`

## Platform Signing
- Build with platform keys (default for `certificate: "platform"` in Android.bp).
- If signing manually:
  ```
  java -jar signapk.jar platform.x509.pem platform.pk8 FileManager.apk FileManager-signed.apk
  ```

## Build Commands
```sh
# In AOSP root
source build/envsetup.sh
lunch <your_target>
m FileManager
```

## Flash & Test
```sh
adb root
adb remount
adb push out/target/product/<device>/system/priv-app/FileManager/FileManager.apk /system/priv-app/FileManager/
adb shell pm install -r /system/priv-app/FileManager/FileManager.apk
adb shell setenforce 1 # Ensure SELinux is enforcing
adb shell am start -n com.example.filemanager/.MainActivity
```

## SELinux Policy
- Rebuild and flash your device tree with the new `filemanager.te` policy.
- If you see denials, use `adb logcat | grep avc` and adjust policy as needed.

## Smoke Test
```sh
# Get FileManager's UID
adb shell dumpsys package com.example.filemanager | grep userId

# List /data/data as FileManager's UID
adb shell run-as com.example.filemanager ls /data/data
```
