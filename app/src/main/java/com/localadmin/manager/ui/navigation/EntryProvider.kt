package com.localadmin.manager.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.localadmin.manager.AppContainer
import com.localadmin.manager.R
import com.localadmin.manager.feature.applications.AppChooserScreen
import com.localadmin.manager.feature.applications.AppChooserViewModel
import com.localadmin.manager.feature.applications.AppDetailsViewModel
import com.localadmin.manager.feature.applications.AppFeaturesViewModel
import com.localadmin.manager.feature.applications.AppGroupsScreen
import com.localadmin.manager.feature.applications.AppPermissionsManagerScreen
import com.localadmin.manager.feature.applications.ApplicationDetailsScreen
import com.localadmin.manager.feature.applications.ApplicationsFeaturesScreen
import com.localadmin.manager.feature.applications.ClearAppStorageScreen
import com.localadmin.manager.feature.applications.CredentialManagerPolicyScreen
import com.localadmin.manager.feature.applications.EditAppGroupScreen
import com.localadmin.manager.feature.applications.EnableSystemAppScreen
import com.localadmin.manager.feature.applications.InstallExistingAppScreen
import com.localadmin.manager.feature.applications.ManagedConfigurationScreen
import com.localadmin.manager.feature.applications.ManagedConfigurationViewModel
import com.localadmin.manager.feature.applications.PackageFunctionScreen
import com.localadmin.manager.feature.applications.PermissionDetailScreen
import com.localadmin.manager.feature.applications.PermissionManagerScreen
import com.localadmin.manager.feature.applications.PermittedAsAndImPackagesScreen
import com.localadmin.manager.feature.applications.SetDefaultDialerScreen
import com.localadmin.manager.feature.applications.UninstallAppScreen
import com.localadmin.manager.feature.network.AddApnSettingScreen
import com.localadmin.manager.feature.network.AddPreferentialNetworkServiceConfigScreen
import com.localadmin.manager.feature.network.AlwaysOnVpnPackageScreen
import com.localadmin.manager.feature.network.NetworkLoggingScreen
import com.localadmin.manager.feature.network.NetworkOptionsScreen
import com.localadmin.manager.feature.network.NetworkScreen
import com.localadmin.manager.feature.network.NetworkStatsScreen
import com.localadmin.manager.feature.network.NetworkStatsViewerScreen
import com.localadmin.manager.feature.network.OverrideApnScreen
import com.localadmin.manager.feature.network.PreferentialNetworkServiceScreen
import com.localadmin.manager.feature.network.PrivateDnsScreen
import com.localadmin.manager.feature.network.RecommendedGlobalProxyScreen
import com.localadmin.manager.feature.network.UpdateNetworkScreen
import com.localadmin.manager.feature.network.WifiScreen
import com.localadmin.manager.feature.network.WifiSecurityLevelScreen
import com.localadmin.manager.feature.network.WifiSsidPolicyScreen
import com.localadmin.manager.feature.password.KeyguardDisabledFeaturesScreen
import com.localadmin.manager.feature.password.PasswordInfoScreen
import com.localadmin.manager.feature.password.PasswordScreen
import com.localadmin.manager.feature.password.RequiredPasswordComplexityScreen
import com.localadmin.manager.feature.password.RequiredPasswordQualityScreen
import com.localadmin.manager.feature.password.ResetPasswordScreen
import com.localadmin.manager.feature.password.ResetPasswordTokenScreen
import com.localadmin.manager.feature.privilege.AddDelegatedAdminScreen
import com.localadmin.manager.feature.privilege.DelegatedAdminsScreen
import com.localadmin.manager.feature.privilege.DhizukuServerSettingsScreen
import com.localadmin.manager.feature.privilege.TransferOwnershipScreen
import com.localadmin.manager.feature.privilege.WorkModesScreen
import com.localadmin.manager.feature.settings.AboutScreen
import com.localadmin.manager.feature.settings.AppLockSettingsScreen
import com.localadmin.manager.feature.settings.AppearanceScreen
import com.localadmin.manager.feature.settings.NotificationsScreen
import com.localadmin.manager.feature.settings.SettingsOptionsScreen
import com.localadmin.manager.feature.settings.SettingsScreen
import com.localadmin.manager.feature.settings.SetupPasswordScreen
import com.localadmin.manager.feature.system.CaCertScreen
import com.localadmin.manager.feature.system.ContentProtectionPolicyScreen
import com.localadmin.manager.feature.system.DefaultInputMethodScreen
import com.localadmin.manager.feature.system.DeviceInfoScreen
import com.localadmin.manager.feature.system.DisableAccountManagementScreen
import com.localadmin.manager.feature.system.FrpPolicyScreen
import com.localadmin.manager.feature.system.HardwareMonitorScreen
import com.localadmin.manager.feature.system.KeyguardScreen
import com.localadmin.manager.feature.system.LockScreenInfoScreen
import com.localadmin.manager.feature.system.LockTaskModeScreen
import com.localadmin.manager.feature.system.MtePolicyScreen
import com.localadmin.manager.feature.system.NearbyStreamingPolicyScreen
import com.localadmin.manager.feature.system.PermissionPolicyScreen
import com.localadmin.manager.feature.system.SecurityLoggingScreen
import com.localadmin.manager.feature.system.SupportMessageScreen
import com.localadmin.manager.feature.system.SystemOptionsScreen
import com.localadmin.manager.feature.system.SystemScreen
import com.localadmin.manager.feature.system.SystemUpdateScreen
import com.localadmin.manager.feature.system.TimeScreen
import com.localadmin.manager.feature.system.WipeDataScreen
import com.localadmin.manager.feature.user_restriction.UserRestrictionEditorScreen
import com.localadmin.manager.feature.user_restriction.UserRestrictionOptionsScreen
import com.localadmin.manager.feature.user_restriction.UserRestrictionScreen
import com.localadmin.manager.feature.users.AffiliationIdScreen
import com.localadmin.manager.feature.users.ChangeUsernameScreen
import com.localadmin.manager.feature.users.CreateUserScreen
import com.localadmin.manager.feature.users.UserInfoScreen
import com.localadmin.manager.feature.users.UserOperationScreen
import com.localadmin.manager.feature.users.UserSessionMessageScreen
import com.localadmin.manager.feature.users.UsersOptionsScreen
import com.localadmin.manager.feature.users.UsersScreen
import com.localadmin.manager.feature.work_profile.CreateWorkProfileScreen
import com.localadmin.manager.feature.work_profile.CrossProfileIntentFilterHistoryScreen
import com.localadmin.manager.feature.work_profile.CrossProfileIntentFilterPresetsScreen
import com.localadmin.manager.feature.work_profile.CrossProfileIntentFilterScreen
import com.localadmin.manager.feature.work_profile.DeleteWorkProfileScreen
import com.localadmin.manager.feature.work_profile.SuspendPersonalAppScreen
import com.localadmin.manager.feature.work_profile.WorkProfileScreen
import com.localadmin.manager.ui.screen.HomeScreen
import com.localadmin.manager.utils.viewModelFactory

fun myEntryProvider(
    destination: Destination, backstack: NavBackStack<NavKey>, appChooserVm: AppChooserViewModel,
    container: AppContainer
) = entryProvider {
    fun navigate(dest: Destination) {
        backstack += dest
    }

    fun navigateUp() {
        if (backstack.size > 1) backstack.removeLastOrNull()
    }

    fun navigateToAppGroups() {
        navigate(Destination.AppGroups)
    }

    fun navigateAndPopAll(dest: Destination) {
        navigate(dest)
        repeat(backstack.size - 1) {
            backstack.removeFirstOrNull()
        }
    }

    fun choosePackage() {
        navigate(Destination.ApplicationsList(false, true))
    }

    fun chooseSinglePackage() {
        navigate(Destination.ApplicationsList(false, false))
    }

    entry<Destination.Home> {
        HomeScreen(
            container.privilegeState,
            { container.settingsRepo.data.applicationsListView },
            ::navigate
        )
    }
    entry<Destination.SetupPassword> {
        SetupPasswordScreen(
            viewModel(factory = container.viewModelFactory)
        ) {
            navigateAndPopAll(Destination.Home)
        }
    }
    entry<Destination.WorkingModes> {
        WorkModesScreen(viewModel(factory = container.viewModelFactory), it, ::navigateUp, {
            navigateAndPopAll(Destination.Home)
        }, {
            navigateAndPopAll(Destination.WorkingModes(false))
        }, ::navigate)
    }
    entry<Destination.DhizukuServerSettings> {
        DhizukuServerSettingsScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }

    entry<Destination.DelegatedAdmins> {
        DelegatedAdminsScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate
        )
    }
    entry<Destination.DelegatedAdminDetails>(
        metadata = navParentKey<Destination.DelegatedAdmins>()
    ) {
        AddDelegatedAdminScreen(
            viewModel(), container.chosenPackage, ::chooseSinglePackage, ::navigateUp
        )
    }
    entry<Destination.DeviceInfo>(
        metadata = navParentKey<Destination.System>()
    ) {
        DeviceInfoScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.LockScreenInfo>(
        metadata = navParentKey<Destination.System>()
    ) {
        LockScreenInfoScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.SupportMessage>(
        metadata = navParentKey<Destination.System>()
    ) {
        SupportMessageScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.TransferOwnership> {
        TransferOwnershipScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp
        ) {
            navigate(Destination.WorkingModes(false))
            while (backstack.size > 1) {
                backstack.removeFirstOrNull()
            }
        }
    }

    entry<Destination.System> {
        SystemScreen(viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate)
    }
    entry<Destination.SystemOptions> {
        SystemOptionsScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.Keyguard>(
        metadata = navParentKey<Destination.System>()
    ) {
        KeyguardScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.HardwareMonitor> {
        HardwareMonitorScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.DefaultInputMethod>(
        metadata = navParentKey<Destination.System>()
    ) {
        DefaultInputMethodScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.Time> {
        TimeScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.ContentProtectionPolicy>(
        metadata = navParentKey<Destination.System>()
    ) {
        ContentProtectionPolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.PermissionPolicy>(
        metadata = navParentKey<Destination.System>()
    ) {
        PermissionPolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.MtePolicy>(
        metadata = navParentKey<Destination.System>()
    ) {
        MtePolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.NearbyStreamingPolicy>(
        metadata = navParentKey<Destination.System>()
    ) {
        NearbyStreamingPolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.LockTaskMode> {
        LockTaskModeScreen(
            viewModel(factory = container.viewModelFactory),
            container.chosenPackage, ::chooseSinglePackage, ::choosePackage, ::navigateUp
        )
    }
    entry<Destination.CaCert> {
        CaCertScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.SecurityLogging> {
        SecurityLoggingScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.DisableAccountManagement>(
        metadata = navParentKey<Destination.System>()
    ) {
        DisableAccountManagementScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.SystemUpdate> {
        SystemUpdateScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    entry<Destination.FrpPolicy>(
        metadata = navParentKey<Destination.System>()
    ) {
        FrpPolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.WipeData>(
        metadata = navParentKey<Destination.System>()
    ) { WipeDataScreen(viewModel(), ::navigateUp) }

    entry<Destination.Network> {
        NetworkScreen(viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate)
    }
    entry<Destination.WiFi> {
        WifiScreen(
            viewModel(factory = container.viewModelFactory),
            ::navigate, ::navigateUp
        )
    }
    entry<Destination.UpdateNetwork>(
        metadata = navParentKey<Destination.WiFi>()
    ) {
        UpdateNetworkScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.WifiSecurityLevel>(
        metadata = navParentKey<Destination.WiFi>()
    ) {
        WifiSecurityLevelScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.WifiSsidPolicy>(
        metadata = navParentKey<Destination.WiFi>()
    ) {
        WifiSsidPolicyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.NetworkOptions>(
        metadata = navParentKey<Destination.Network>()
    ) {
        NetworkOptionsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.NetworkStats> {
        NetworkStatsScreen(
            viewModel(factory = container.viewModelFactory),
            ::chooseSinglePackage, container.chosenPackage, ::navigateUp
        ) {
            navigate(Destination.NetworkStatsViewer)
        }
    }
    entry<Destination.NetworkStatsViewer>(
        metadata = navParentKey<Destination.NetworkStats>()
    ) {
        NetworkStatsViewerScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.PrivateDns>(
        metadata = navParentKey<Destination.Network>()
    ) {
        PrivateDnsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.AlwaysOnVpnPackage>(
        metadata = navParentKey<Destination.Network>()
    ) {
        AlwaysOnVpnPackageScreen(
            viewModel(), container.chosenPackage, ::chooseSinglePackage, ::navigateUp
        )
    }
    entry<Destination.RecommendedGlobalProxy>(
        metadata = navParentKey<Destination.Network>()
    ) {
        RecommendedGlobalProxyScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.NetworkLogging> {
        NetworkLoggingScreen(viewModel(factory = container.viewModelFactory), ::navigateUp)
    }
    //entry<Destination.WifiAuthKeypair> { WifiAuthKeypairScreen(::navigateUp) }
    entry<Destination.PreferentialNetworkService> {
        PreferentialNetworkServiceScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate
        )
    }
    entry<Destination.AddPreferentialNetworkServiceConfig>(
        metadata = navParentKey<Destination.PreferentialNetworkService>()
    ) {
        AddPreferentialNetworkServiceConfigScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.OverrideApn> {
        OverrideApnScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp
        ) { navigate(Destination.AddApnSetting) }
    }
    entry<Destination.AddApnSetting>(
        metadata = navParentKey<Destination.OverrideApn>()
    ) {
        AddApnSettingScreen(viewModel(), ::navigateUp)
    }

    entry<Destination.WorkProfile> {
        WorkProfileScreen(viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate)
    }
    entry<Destination.CreateWorkProfile> {
        CreateWorkProfileScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.SuspendPersonalApp>(
        metadata = navParentKey<Destination.WorkProfile>()
    ) {
        SuspendPersonalAppScreen(
            viewModel(), ::navigateUp
        )
    }
    entry<Destination.CrossProfileIntentFilter> {
        CrossProfileIntentFilterScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate
        )
    }
    entry<Destination.CrossProfileIntentFilterPresets>(
        metadata = navParentKey<Destination.CrossProfileIntentFilter>()
    ) {
        CrossProfileIntentFilterPresetsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.CrossProfileIntentFilterHistory>(
        metadata = navParentKey<Destination.CrossProfileIntentFilter>()
    ) {
        CrossProfileIntentFilterHistoryScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.DeleteWorkProfile>(
        metadata = navParentKey<Destination.WorkProfile>()
    ) {
        DeleteWorkProfileScreen(viewModel(), ::navigateUp)
    }

    entry<Destination.ApplicationsList> { params ->
        AppChooserScreen(
            params, appChooserVm, { name ->
                if (params.canSwitchView) {
                    if (name == null) {
                        navigateUp()
                    } else {
                        navigate(Destination.ApplicationDetails(name))
                    }
                } else {
                    if (name != null) container.chosenPackage.trySend(name)
                    navigateUp()
                }
            }, {
                container.settingsRepo.update {
                    it.applicationsListView = false
                }
                navigate(Destination.ApplicationFeatures)
                backstack.removeAt(backstack.size - 2)
            })
    }

    entry<Destination.ApplicationFeatures> {
        ApplicationsFeaturesScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate
        ) {
            container.settingsRepo.update {
                it.applicationsListView = true
            }
            navigate(Destination.ApplicationsList(true, true))
            backstack.removeAt(backstack.size - 2)
        }
    }
    entry<Destination.ApplicationDetails> {
        ApplicationDetailsScreen(
            viewModel(
                factory = viewModelFactory {
                    AppDetailsViewModel(
                        it.packageName, container.app, container.privilegeHelper,
                        container.privilegeState, container.toastChannel
                    )
                }
            ), ::navigateUp, ::navigate
        )
    }
    entry<Destination.Suspend>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.suspend, vm.suspendedPackages, vm::getSuspendedPackaged,
            vm::setPackageSuspended, ::navigateUp, container.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, container.appGroupsState, R.string.info_suspend_app
        )
    }
    entry<Destination.Hide>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.hide, vm.hiddenPackages, vm::getHiddenPackages, vm::setPackageHidden,
            ::navigateUp, container.chosenPackage, ::choosePackage, ::navigateToAppGroups,
            container.appGroupsState
        )
    }
    entry<Destination.BlockUninstall>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.block_uninstall, vm.ubPackages, vm::getUbPackages, vm::setPackageUb,
            ::navigateUp, container.chosenPackage, ::choosePackage, ::navigateToAppGroups,
            container.appGroupsState
        )
    }
    entry<Destination.DisableUserControl>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.disable_user_control, vm.ucdPackages, vm::getUcdPackages,
            vm::setPackageUcd, ::navigateUp, container.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, container.appGroupsState, R.string.info_disable_user_control
        )
    }
    entry<Destination.AppPermissionsManager>(
        metadata = navParentKey<Destination.ApplicationDetails>()
    ) {
        AppPermissionsManagerScreen(
            viewModel(), ::navigateUp
        )
    }
    entry<Destination.PermissionManager> {
        PermissionManagerScreen(::navigate, ::navigateUp)
    }
    entry<Destination.PermissionDetail>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        PermissionDetailScreen(
            it, viewModel(), ::navigateUp
        )
    }
    entry<Destination.DisableMeteredData>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.disable_metered_data, vm.mddPackages, vm::getMddPackages,
            vm::setPackageMdd, ::navigateUp, container.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, container.appGroupsState
        )
    }
    entry<Destination.ClearAppStorage>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        ClearAppStorageScreen(
            viewModel(), container.chosenPackage, ::chooseSinglePackage, ::navigateUp
        )
    }
    entry<Destination.UninstallApp>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        UninstallAppScreen(
            vm, container.chosenPackage, ::chooseSinglePackage, ::navigateUp
        )
    }
    entry<Destination.KeepUninstalledPackages>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.keep_uninstalled_packages, vm.kuPackages, vm::getKuPackages,
            vm::setPackageKu, ::navigateUp, container.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, container.appGroupsState, R.string.info_keep_uninstalled_apps
        )
    }
    entry<Destination.InstallExistingApp>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        InstallExistingAppScreen(
            vm, container.chosenPackage, ::chooseSinglePackage, ::navigateUp
        )
    }
    entry<Destination.CrossProfilePackages>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.cross_profile_apps, vm.cpPackages,
            vm::getCpPackages, vm::setPackageCp, ::navigateUp, container.chosenPackage,
            ::choosePackage, ::navigateToAppGroups, container.appGroupsState
        )
    }
    entry<Destination.CrossProfileWidgetProviders>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PackageFunctionScreen(
            R.string.cross_profile_widget, vm.cpwProviders,
            vm::getCpwProviders, vm::setCpwProvider, ::navigateUp, container.chosenPackage,
            ::choosePackage, ::navigateToAppGroups, container.appGroupsState
        )
    }
    entry<Destination.CredentialManagerPolicy>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        CredentialManagerPolicyScreen(
            vm, container.chosenPackage, ::choosePackage, ::navigateUp
        )
    }
    entry<Destination.PermittedAccessibilityServices>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PermittedAsAndImPackagesScreen(
            R.string.permitted_accessibility_services,
            R.string.system_accessibility_always_allowed, container.chosenPackage, ::choosePackage,
            vm.pasAllowAll, vm.pasPackages, vm::getPasPolicy, vm::setPasAllowAll, vm::setPasPackage,
            vm::applyPasPolicy, ::navigateUp
        )
    }
    entry<Destination.PermittedInputMethods>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        PermittedAsAndImPackagesScreen(
            R.string.permitted_ime, R.string.system_ime_always_allowed,
            container.chosenPackage, ::choosePackage, vm.pimAllowAll, vm.pimPackages,
            vm::getPimPolicy, vm::setPimAllowAll,
            vm::setPimPackage, vm::applyPimPolicy, ::navigateUp
        )
    }
    entry<Destination.EnableSystemApp>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        EnableSystemAppScreen(
            container.chosenPackage, ::chooseSinglePackage, vm::enableSystemApp, ::navigateUp
        )
    }
    entry<Destination.SetDefaultDialer>(
        metadata = navParentKey<Destination.ApplicationFeatures>()
    ) {
        val vm = viewModel<AppFeaturesViewModel>()
        SetDefaultDialerScreen(
            container.chosenPackage, ::chooseSinglePackage, vm::setDefaultDialer, ::navigateUp
        )
    }
    entry<Destination.ManagedConfiguration> {
        ManagedConfigurationScreen(
            viewModel(factory = viewModelFactory {
                ManagedConfigurationViewModel(
                    it.packageName, container.app, container.privilegeHelper
                )
            }), ::navigateUp
        )
    }
    entry<Destination.AppGroups> {
        AppGroupsScreen(
            viewModel(factory = container.viewModelFactory),
            { navigate(Destination.EditAppGroup) },
            ::navigateUp
        )
    }
    entry<Destination.EditAppGroup>(
        metadata = navParentKey<Destination.AppGroups>()
    ) {
        EditAppGroupScreen(
            viewModel(), ::navigateUp, ::choosePackage, container.chosenPackage
        )
    }

    entry<Destination.UserRestriction> {
        UserRestrictionScreen(
            viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate
        )
    }
    entry<Destination.UserRestrictionEditor>(
        metadata = navParentKey<Destination.UserRestriction>()
    ) {
        UserRestrictionEditorScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.UserRestrictionOptions>(
        metadata = navParentKey<Destination.UserRestriction>()
    ) {
        UserRestrictionOptionsScreen(it, viewModel(), ::navigateUp)
    }

    entry<Destination.Users> {
        UsersScreen(viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate)
    }
    entry<Destination.UserInfo>(
        metadata = navParentKey<Destination.Users>()
    ) {
        UserInfoScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.UsersOptions>(
        metadata = navParentKey<Destination.Users>()
    ) {
        UsersOptionsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.UserOperation>(
        metadata = navParentKey<Destination.Users>()
    ) {
        UserOperationScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.CreateUser>(
        metadata = navParentKey<Destination.Users>()
    ) {
        CreateUserScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.ChangeUsername>(
        metadata = navParentKey<Destination.Users>()
    ) {
        ChangeUsernameScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.UserSessionMessage>(
        metadata = navParentKey<Destination.Users>()
    ) {
        UserSessionMessageScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.AffiliationId>(
        metadata = navParentKey<Destination.Users>()
    ) {
        AffiliationIdScreen(viewModel(), ::navigateUp)
    }

    entry<Destination.Password> {
        PasswordScreen(viewModel(factory = container.viewModelFactory), ::navigateUp, ::navigate)
    }
    entry<Destination.PasswordInfo>(
        metadata = navParentKey<Destination.Password>()
    ) {
        PasswordInfoScreen(
            viewModel(), ::navigateUp
        )
    }
    entry<Destination.ResetPasswordToken>(
        metadata = navParentKey<Destination.Password>()
    ) {
        ResetPasswordTokenScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.ResetPassword>(
        metadata = navParentKey<Destination.Password>()
    ) {
        ResetPasswordScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.RequiredPasswordComplexity>(
        metadata = navParentKey<Destination.Password>()
    ) {
        RequiredPasswordComplexityScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.KeyguardDisabledFeatures>(
        metadata = navParentKey<Destination.Password>()
    ) {
        KeyguardDisabledFeaturesScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.RequiredPasswordQuality>(
        metadata = navParentKey<Destination.Password>()
    ) {
        RequiredPasswordQualityScreen(viewModel(), ::navigateUp)
    }

    entry<Destination.Settings> {
        SettingsScreen(viewModel(factory = container.viewModelFactory), ::navigate, ::navigateUp)
    }
    entry<Destination.SettingsOptions>(
        metadata = navParentKey<Destination.Settings>()
    ) {
        SettingsOptionsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.AppearanceSettings>(
        metadata = navParentKey<Destination.Settings>()
    ) {
        AppearanceScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.AppLockSettings>(
        metadata = navParentKey<Destination.Settings>()
    ) {
        AppLockSettingsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.NotificationSettings>(
        metadata = navParentKey<Destination.Settings>()
    ) {
        NotificationsScreen(viewModel(), ::navigateUp)
    }
    entry<Destination.About> { AboutScreen(::navigateUp) }
}(destination) as NavEntry<NavKey>
