package ca.davidpellegrini.tipcalculatorf21;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        Fragment settingsFragment = getFragmentManager().findFragmentById(R.id.settings_fragment);

        if(settingsFragment == null) {
            getMenuInflater().inflate(R.menu.options_menu, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.options_menu_two_pane, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int menuItemID = item.getItemId();
        if(menuItemID == R.id.menu_settings)
            startActivity(new Intent(
                    getApplicationContext(),
                    SettingsActivity.class
                )
            );
        else if(menuItemID == R.id.menu_about)
            Toast.makeText(this, "About Item", Toast.LENGTH_SHORT).show();
        else
            return super.onOptionsItemSelected(item);
        return true;
    }
}