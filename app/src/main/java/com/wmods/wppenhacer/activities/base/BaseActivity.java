package com.wmods.wppenhacer.activities.base;

import android.os.Bundle;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.wmods.wppenhacer.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getTheme().applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true);
        getTheme().applyStyle(R.style.ThemeOverlay, true);
        getTheme().applyStyle(R.style.ThemeOverlay_MaterialBlue, true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            getTheme().applyStyle(R.style.ThemeOverlay_LegacyTextColors, true);
        }
        super.onCreate(savedInstanceState);
    }

}
