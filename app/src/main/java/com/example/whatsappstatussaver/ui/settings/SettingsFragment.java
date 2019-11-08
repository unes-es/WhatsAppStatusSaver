package com.example.whatsappstatussaver.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.whatsappstatussaver.R;
import com.example.whatsappstatussaver.ui.share.ShareViewModel;

import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        setHasOptionsMenu(true);
        SwitchPreferenceCompat darkModeSwitch = findPreference("darkModeSwitch");
        CheckBoxPreference enableNotification = findPreference("enableNotifications");

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkModeSwitch.setChecked(true);
        }
        else {
            darkModeSwitch.setChecked(false);
        }
        darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
               if(!((SwitchPreferenceCompat)preference).isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putBoolean("IS_DARK_MODE_ON",!((SwitchPreferenceCompat)preference).isChecked());
                editor.apply();
                editor.commit();
                return true;
            }
        });

        enableNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putBoolean("ENABLE_NOTIFICATIONS",!((CheckBoxPreference)preference).isChecked());
                editor.apply();
                editor.commit();

                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.refresh).setVisible(false);
    }
}