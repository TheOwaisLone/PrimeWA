package com.wmods.wppenhacer.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.waseemsabir.betterypermissionhelper.BatteryPermissionHelper;
import com.wmods.wppenhacer.App;
import com.wmods.wppenhacer.BuildConfig;
import com.wmods.wppenhacer.R;
import com.wmods.wppenhacer.activities.base.BaseActivity;
import com.wmods.wppenhacer.databinding.ActivityMainBinding;
import com.wmods.wppenhacer.ui.fragments.FeaturesHubFragment;
import com.wmods.wppenhacer.utils.FilePicker;

import java.io.File;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private BatteryPermissionHelper batteryPermissionHelper = BatteryPermissionHelper.Companion.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        var prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        var mode = Integer.parseInt(prefs.getString("thememode", "0"));
        App.setThemeMode(mode);
        App.changeLanguage(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FeaturesHubFragment())
                    .commit();
            handleNavigationIntent(getIntent());
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                updateToolbarForFragment(currentFragment);
                invalidateOptionsMenu();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        createMainDir();
        FilePicker.registerFilePicker(this);
        com.wmods.wppenhacer.utils.LegalDisclaimerDialog.checkAndShow(this);

        binding.btnRestartApp.setOnClickListener(v -> {
            boolean hasWpp = isPackageInstalled("com.whatsapp");
            boolean hasW4b = isPackageInstalled("com.whatsapp.w4b");
            if (hasWpp && hasW4b) {
                showRestartSelectionDialog();
            } else if (hasWpp) {
                App.instance.restartApp("com.whatsapp", true);
                Toast.makeText(this, getString(R.string.restarting_pkg, "WhatsApp"), Toast.LENGTH_SHORT).show();
            } else if (hasW4b) {
                App.instance.restartApp("com.whatsapp.w4b", true);
                Toast.makeText(this, getString(R.string.restarting_pkg, "WhatsApp Business"), Toast.LENGTH_SHORT).show();
            }
            hideRestartPrompt();
        });

        binding.btnDismissRestartBanner.setOnClickListener(v -> hideRestartPrompt());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavigationIntent(intent);
    }

    private void handleNavigationIntent(Intent intent) {
        if (intent == null) return;
        String targetFragment = intent.getStringExtra("target_fragment");
        String scrollToKey = intent.getStringExtra("scroll_to_preference");
        if (targetFragment != null) {
            Fragment fragment = switch (targetFragment) {
                case "GENERAL" -> new com.wmods.wppenhacer.ui.fragments.GeneralFragment();
                case "CONVERSATION" -> new com.wmods.wppenhacer.ui.fragments.ConversationFragment();
                case "STATUS" -> new com.wmods.wppenhacer.ui.fragments.StatusFragment();
                case "HOME" -> new com.wmods.wppenhacer.ui.fragments.HomeCustomizationFragment();
                case "PRIVACY" -> new com.wmods.wppenhacer.ui.fragments.PrivacyFragment();
                case "CALLS" -> new com.wmods.wppenhacer.ui.fragments.CallsFragment();
                case "CUSTOMIZATION" -> new com.wmods.wppenhacer.ui.fragments.CustomizationFragment();
                case "MEDIA" -> new com.wmods.wppenhacer.ui.fragments.MediaFragment();
                case "RECORDINGS" -> new com.wmods.wppenhacer.ui.fragments.RecordingsFragment();
                case "MISC" -> new com.wmods.wppenhacer.ui.fragments.MiscFragment();
                case "SETTINGS_ABOUT" -> new com.wmods.wppenhacer.ui.fragments.SettingsAboutFragment();
                default -> null;
            };

            if (fragment != null) {
                if (scrollToKey != null) {
                    Bundle args = new Bundle();
                    args.putString("scroll_to_preference", scrollToKey);
                    fragment.setArguments(args);
                }
                navigateToCategory(fragment);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invalidateOptionsMenu();
    }

    public void navigateToCategory(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        updateToolbarForFragment(fragment);
    }

    public void updateToolbarForFragment(Fragment fragment) {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        }
        if (fragment == null || fragment instanceof FeaturesHubFragment) {
            binding.toolbar.setLogo(R.drawable.ic_toolbar_logo);
            binding.toolbar.setTitle(R.string.app_name);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            binding.toolbar.setLogo(null);
            if (fragment instanceof com.wmods.wppenhacer.ui.fragments.HomeCustomizationFragment) {
                binding.toolbar.setTitle(R.string.home_screen);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.GeneralFragment) {
                binding.toolbar.setTitle(R.string.general);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.ConversationFragment) {
                binding.toolbar.setTitle(R.string.conversation);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.StatusFragment) {
                binding.toolbar.setTitle(R.string.status);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.PrivacyFragment) {
                binding.toolbar.setTitle(R.string.privacy);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.CallsFragment) {
                binding.toolbar.setTitle(R.string.calls);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.CustomizationFragment) {
                binding.toolbar.setTitle(R.string.customization);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.MediaFragment) {
                binding.toolbar.setTitle(R.string.media);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.RecordingsFragment) {
                binding.toolbar.setTitle(R.string.call_recordings_hub);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.MiscFragment) {
                binding.toolbar.setTitle(R.string.misc);
            } else if (fragment instanceof com.wmods.wppenhacer.ui.fragments.SettingsAboutFragment) {
                binding.toolbar.setTitle(R.string.settings_and_about);
            }
        }
    }

    private void createMainDir() {
        var nomedia = new File(App.getWaEnhancerFolder(), ".nomedia");
        if (nomedia.exists()) {
            nomedia.delete();
        }
    }

    private boolean isBatteryOptimizationIgnored() {
        var powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return powerManager != null && powerManager.isIgnoringBatteryOptimizations(getPackageName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isRoot = getSupportFragmentManager().getBackStackEntryCount() == 0;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        boolean isSettings = currentFragment instanceof com.wmods.wppenhacer.ui.fragments.SettingsAboutFragment;

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            searchItem.setVisible(isRoot && !isSettings);
        }

        MenuItem themeItem = menu.findItem(R.id.menu_theme_toggle);
        if (themeItem != null) {
            themeItem.setVisible(isRoot && !isSettings);
        }

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        if (settingsItem != null) {
            settingsItem.setVisible(isRoot && !isSettings);
        }

        MenuItem batteryItem = menu.findItem(R.id.batteryoptimization);
        if (batteryItem != null) {
            batteryItem.setVisible(isRoot && !isBatteryOptimizationIgnored());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void showThemeSelectionDialog() {
        var prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int currentMode = 0;
        try {
            currentMode = Integer.parseInt(prefs.getString("thememode", "0"));
        } catch (Exception ignored) {}

        String[] options = getResources().getStringArray(R.array.thememode_entries);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.theme_mode)
                .setSingleChoiceItems(options, currentMode, (dialog, which) -> {
                    prefs.edit().putString("thememode", String.valueOf(which)).apply();
                    App.setThemeMode(which);
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @SuppressLint("BatteryLife")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        } else if (item.getItemId() == R.id.menu_search) {
            var options = ActivityOptionsCompat.makeCustomAnimation(
                    this, R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(new Intent(this, SearchActivity.class), options.toBundle());
            return true;
        } else if (item.getItemId() == R.id.menu_theme_toggle) {
            showThemeSelectionDialog();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(currentFragment instanceof com.wmods.wppenhacer.ui.fragments.SettingsAboutFragment)) {
                navigateToCategory(new com.wmods.wppenhacer.ui.fragments.SettingsAboutFragment());
            }
            return true;
        } else if (item.getItemId() == R.id.batteryoptimization) {
            if (isBatteryOptimizationIgnored()) {
                invalidateOptionsMenu();
                return true;
            }
            if (batteryPermissionHelper.isBatterySaverPermissionAvailable(this, true)) {
                batteryPermissionHelper.getPermission(this, true, true);
            } else {
                var intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver restartPromptReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showRestartPrompt();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        androidx.core.content.ContextCompat.registerReceiver(
                this,
                restartPromptReceiver,
                new IntentFilter(BuildConfig.APPLICATION_ID + ".MANUAL_RESTART"),
                androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(restartPromptReceiver);
        } catch (Exception ignored) {}
    }

    public void showRestartPrompt() {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;

            boolean hasWpp = isPackageInstalled("com.whatsapp");
            boolean hasW4b = isPackageInstalled("com.whatsapp.w4b");

            if (!hasWpp && !hasW4b) return;

            if (binding.restartBanner.getVisibility() != android.view.View.VISIBLE) {
                binding.restartBanner.setAlpha(0f);
                binding.restartBanner.setTranslationY(60f);
                binding.restartBanner.setVisibility(android.view.View.VISIBLE);
                binding.restartBanner.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(250)
                        .start();
            }
        });
    }

    public void hideRestartPrompt() {
        runOnUiThread(() -> {
            if (binding.restartBanner.getVisibility() == android.view.View.VISIBLE) {
                binding.restartBanner.animate()
                        .alpha(0f)
                        .translationY(60f)
                        .setDuration(200)
                        .withEndAction(() -> binding.restartBanner.setVisibility(android.view.View.GONE))
                        .start();
            }
        });
    }

    private void showRestartSelectionDialog() {
        String[] options = new String[]{"WhatsApp", "WhatsApp Business", getString(R.string.restart_both)};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.restart_whatsapp_title)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        App.instance.restartApp("com.whatsapp", true);
                        Toast.makeText(this, getString(R.string.restarting_pkg, "WhatsApp"), Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        App.instance.restartApp("com.whatsapp.w4b", true);
                        Toast.makeText(this, getString(R.string.restarting_pkg, "WhatsApp Business"), Toast.LENGTH_SHORT).show();
                    } else if (which == 2) {
                        App.instance.restartApp("com.whatsapp", true);
                        App.instance.restartApp("com.whatsapp.w4b", true);
                        Toast.makeText(this, getString(R.string.restarting_pkg, "WhatsApp & Business"), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isXposedEnabled() {
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onSupportNavigateUp();
    }
}