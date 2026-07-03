[English](Readme.md) | [简体中文](Readme-zh_CN.md)

# 本機管理

使用安卓的裝置策略管理器API管理你的裝置。

## 下載

- [IzzyOnDroid F-Droid Repository](https://apt.izzysoft.de/fdroid/index/apk/com.localadmin.manager)
- [GitHub Releases](https://github.com/520xiaoguai/OwnDroid/releases)

> [!NOTE]
> CI 建構和 Releases 提供的是未簽章 APK，請先在本地簽章後再安裝。

## 功能

- 系統：停用攝像頭、禁止截圖、全域靜音、停用USB訊號、鎖定任務模式、清除資料...
- 網路：新增/修改/刪除 Wi-Fi、網路統計、網路日誌...
- 應用：掛起/隱藏應用、阻止應用解除安裝、授予/撤銷權限、清除應用儲存、安裝/解除安裝應用...
- 使用者限制：禁止發送簡訊、禁止撥出電話、停用藍牙、停用NFC、停用USB檔案傳輸、禁止安裝/解除安裝應用...
- 使用者：使用者資訊、建立/啟動/切換/停止/刪除使用者...
- 密碼與鎖屏：重設密碼、設定螢幕逾時...

## 工作模式

- Device owner（推薦）

  啟用方式：
  - Shizuku
  - Dhizuku
  - Root
  - ADB shell命令 `dpm set-device-owner com.localadmin.manager/.Receiver`
- [Dhizuku](https://github.com/iamr0s/Dhizuku)
- 工作設定檔

## FAQ

### 裝置上已有帳戶

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already some accounts on the device
```

解決辦法：凍結持有這些帳戶的app，或刪除這些帳戶。

### 裝置上已有多個使用者

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already several users on the device
```

解決辦法：刪除其他使用者，包括工作設定檔、私密空間和應用分身。

### Device owner 已存在

```text
java.lang.IllegalStateException: Trying to set the device owner (com.localadmin.manager/.Receiver), but device owner (xxx) is already set.
```

一個裝置只能存在一個device owner，請先停用已存在的device owner。

### MIUI & HyperOS

```text
java.lang.SecurityException: Neither user 2000 nor current process has android.permission.MANAGE_DEVICE_ADMINS.
```

解決辦法： 在開發者設定中開啟`USB偵錯（安全設定）`，或在root命令列中執行啟用命令。

### ColorOS

```text
java.lang.IllegalStateException: Unexpected @ProvisioningPreCondition
```

解決辦法：使用與裝置匹配的平台金鑰簽章 APK，或透過 root shell 啟用 Device Owner。

### 三星

```text
user limit reached
```

三星限制了多使用者功能，暫無解決辦法。


### 建立工作設定檔/使用者

在大部分裝置上，設定device owner後不能建立工作設定檔，因為系統在設定device owner時會新增`no_add_managed_profile`等使用者限制。
Device owner不能修改系統設定的使用者限制，但如果你有root權限，你可以在adb shell中執行以下命令以關閉這個限制。
注意：device owner和工作設定檔的profile owner不能為同一個app，否則device owner可能會在重啟時失去特權。

```shell
pm set-user-restriction no_add_user 0
pm set-user-restriction no_add_managed_profile 0
pm set-user-restriction no_add_private_profile 0
pm set-user-restriction no_add_clone_profile 0
```

請謹慎繞過這些限制。這可能會導致一些預期之外的行為，比如你建立的使用者在重啟時被刪除。

一些系統在設定了device owner後不允許在安卓設定中建立使用者，你可以在本機管理中建立使用者。
如果你有root，你也可以在adb shell中執行以上命令以解除限制。

## 開發者

### 建構

你可以在命令列中使用Gradle以建構本機管理。建構產物為未簽章 APK，請在本地用自己的金鑰簽章後再安裝。
```shell
./gradlew assembleRelease
```
（在Windows系統中應使用`./gradlew.bat`)

未簽章 APK 路徑：`app/build/outputs/apk/release/app-release-unsigned.apk`

### 貢獻

請使用`dev`分支。

## 授權條款

[License.md](LICENSE.md)

> Copyright (C)  2026  BinTianqi
>
> This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
>
> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
>
> You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
