package ca.davidpellegrini.tipcalculatorf21;


import android.os.Bundle;
import android.preference.PreferenceFragment;


@SuppressWarnings("deprecation")
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}