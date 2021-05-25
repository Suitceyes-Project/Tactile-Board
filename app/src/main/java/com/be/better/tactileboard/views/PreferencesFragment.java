package com.be.better.tactileboard.views;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.be.better.tactileboard.R;
import com.be.better.tactileboard.controls.IntEditTextPreference;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String gridKey = getResources().getString(R.string.pref_grid_size_key);
        IntEditTextPreference gridSize = findPreference(gridKey);
        if(gridSize!=null){
            int size = prefs.getInt(gridKey, 4);
            gridSize.setText(String.valueOf(size));
            gridSize.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.selectAll();
                    int maxLength = 1;
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                }
            });
        }
    }
}
