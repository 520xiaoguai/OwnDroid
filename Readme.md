[简体中文](Readme-zh_CN.md) | [繁體中文](Readme-zh_TW.md)

# Device Manager

Use Android's DevicePolicyManager API to manage your device.

## Download

- [IzzyOnDroid F-Droid Repository](https://apt.izzysoft.de/fdroid/index/apk/com.localadmin.manager)

> [!NOTE]
> ColorOS users should download testkey version

## Features

- System: disable camera, disable screenshot, master volume mute, disable USB signal, lock task mode, wipe data...
- Network: add/modify/delete Wi-Fi, network stats, network logging...
- Applications: suspend/hide app, block app uninstallation, grant/revoke permissions, clear app storage, install/uninstall app...
- User restriction: disable SMS, disable outgoing call, disable bluetooth, disable NFC, disable USB file transfer, disable app installing/uninstalling...
- Users: user information, create/start/switch/stop/delete user...
- Password and keyguard: reset password, set screen timeout...

## Working modes

- Device owner (recommended)

  Activating methods:
  - Shizuku
  - Dhizuku
  - Root
  - ADB shell command `dpm set-device-owner com.localadmin.manager/.Receiver`
- [Dhizuku](https://github.com/iamr0s/Dhizuku)
- Work profile

## FAQ

### Already some accounts on the device

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already some accounts on the device
```

Solutions: freeze the accounts' holder apps, or delete those accounts.

### Already several users on the device

```text
java.lang.IllegalStateException: Not allowed to set the device owner because there are already several users on the device
```

Solution: Delete secondary users, including work profile, private space and app cloning.

### Device owner is already set

```text
java.lang.IllegalStateException: Trying to set the device owner (com.localadmin.manager/.Receiver), but device owner (xxx) is already set.
```

Only one device owner can exist on a device. Please deactivate the existing device owner first.

### MIUI & HyperOS

```text
java.lang.SecurityException: Neither user 2000 nor current process has android.permission.MANAGE_DEVICE_ADMINS.
```

Solutions:
- Enable `USB debugging (Security setting)` in developer options.
- Or execute activating command in root shell.

### ColorOS

```text
java.lang.IllegalStateException: Unexpected @ProvisioningPreCondition
```

Solution: Use Device Manager testkey version

The testkey and signed versions differ only in their signatures. There is no functional difference between them.

### Samsung

```text
user limit reached
```

Samsung restricts Android's multiple users feature. There is currently no solution.

### Create work profile / user

On most devices, creating work profile is not allowed by the system when the device owner exist.
Because the system add `no_add_managed_profile` user restriction when a device owner is set.
Device owner can't modify user restrictions set by the system, but if your device is rooted, you can disable this restriction by executing the following commands in adb shell.
Note: the device owner and the work profile owner can't be the same app, or the device owner will lose its privilege during reboot.

```shell
pm set-user-restriction no_add_user 0
pm set-user-restriction no_add_managed_profile 0
pm set-user-restriction no_add_private_profile 0
pm set-user-restriction no_add_clone_profile 0
```

You should bypass the restrictions at your own risk. It may cause unexpected behavior, for example, the system may delete the user you created silently during reboot.

Some systems disable the feature of adding users in Android settings once a device owner is set.
You have to create users in Device Manager. Or if you have root, run the above command in adb shell to remove that restriction.

## For developers

### Build

You can use Gradle in command line to build Device Manager.
```shell
# Use testkey for signing (default)
./gradlew build
# Use your custom .jks key for signing
./gradlew build -PStoreFile="/path/to/your/jks/file" -PStorePassword="YOUR_KEYSTORE_PASSWORD" -PKeyPassword="YOUR_KEY_PASSWORD" -PKeyAlias="YOUR_KEY_ALIAS"
```
(Use `./gradlew.bat` instead on Windows)

### Contribute

Please use the `dev` branch.

## License

[License.md](LICENSE.md)

> Copyright (C)  2026  BinTianqi
>
> This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
>
> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
>
> You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
