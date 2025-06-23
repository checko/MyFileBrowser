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
## Get permissions
- Add the content of privapp-permissions-myfilebrower.xml to framework/base/data/etc/privapp-permissions.xml

## Build Commands
```sh
# In AOSP root, for cuttlefish emulator
source build/envsetup.sh
lunch aosp_cf_x86_64_phone-trunk-user 
m MyFileBrowser
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
