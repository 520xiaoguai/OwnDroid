[English](Readme.md) | [繁體中文](Readme-zh_TW.md)

# 本机管理

使用安卓的设备策略管理器API管理你的设备。

## 下载

- [IzzyOnDroid F-Droid Repository](https://apt.izzysoft.de/fdroid/index/apk/com.localadmin.manager)
- [GitHub Releases](https://github.com/520xiaoguai/OwnDroid/releases)

> [!NOTE]
> CI 构建和 Releases 提供的是未签名 APK，请先在本地签名后再安装。

## 功能

- 系统：禁用摄像头、禁止截屏、全局静音、禁用USB信号、锁定任务模式、清除数据...
- 网络：添加/修改/删除 Wi-Fi、网络统计、网络日志...
- 应用：挂起/隐藏应用、阻止应用卸载、授予/撤销权限、清除应用存储、安装/卸载应用...
- 用户限制：禁止发送短信、禁止拨出电话、禁用蓝牙、禁用NFC、禁用USB文件传输、禁止安装/卸载应用...
- 用户：用户信息、创建/启动/切换/停止/删除用户...
- 密码与锁屏：重置密码、设置屏幕超时...

## 工作模式

- Device owner（推荐）

  激活方式：
  - Shizuku
  - Dhizuku
  - Root
  - ADB shell命令 `dpm set-device-owner com.localadmin.manager/.Receiver`
- [Dhizuku](https://github.com/iamr0s/Dhizuku)
- 工作资料

## FAQ

### 设备上已有账号

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already some accounts on the device
```

解决办法：冻结持有这些账号的app，或删除这些账号。

### 设备上已有多个用户

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already several users on the device
```

解决办法：删除其他用户，包括工作资料、私密空间和应用分身。

### Device owner 已存在

```text
java.lang.IllegalStateException: Trying to set the device owner (com.localadmin.manager/.Receiver), but device owner (xxx) is already set.
```

一个设备只能存在一个device owner，请先停用已存在的device owner。

### MIUI & HyperOS

```text
java.lang.SecurityException: Neither user 2000 nor current process has android.permission.MANAGE_DEVICE_ADMINS.
```

解决办法： 在开发者设置中打开`USB调试（安全设置）`，或在root命令行中执行激活命令。

### ColorOS

```text
java.lang.IllegalStateException: Unexpected @ProvisioningPreCondition
```

解决办法：使用与设备匹配的平台密钥签名 APK，或通过 root shell 激活 Device Owner。

### 三星

```text
user limit reached
```

三星限制了多用户功能，暂无解决办法。


### 创建工作资料/用户

在大部分设备上，设置device owner后不能创建工作资料，因为系统在设置device owner时会添加`no_add_managed_profile`等用户限制。
Device owner不能修改系统设置的用户限制，但如果你有root权限，你可以在adb shell中执行以下命令以关闭这个限制。
注意：device owner和工作资料的profile owner不能为同一个app，否则device owner可能会在重启时失去特权。

```shell
pm set-user-restriction no_add_user 0
pm set-user-restriction no_add_managed_profile 0
pm set-user-restriction no_add_private_profile 0
pm set-user-restriction no_add_clone_profile 0
```

请谨慎绕过这些限制。这可能会导致一些预期之外的行为，比如你创建的用户在重启时被删除。

一些系统在设置了device owner后不允许在安卓设置中创建用户，你可以在本机管理中创建用户。
如果你有root，你也可以在adb shell中运行以上命令以解除限制。

## 开发者

### 构建

你可以在命令行中使用Gradle以构建本机管理。构建产物为未签名 APK，请在本地用自己的密钥签名后再安装。
```shell
./gradlew assembleRelease
```
（在Windows系统中应使用`./gradlew.bat`)

未签名 APK 路径：`app/build/outputs/apk/release/app-release-unsigned.apk`

### 贡献

请使用`dev`分支。

## 许可证

[License.md](LICENSE.md)

> Copyright (C)  2026  BinTianqi
>
> This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
>
> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
>
> You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
