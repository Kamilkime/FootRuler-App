package pl.kamilkime.footruler.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import pl.kamilkime.footruler.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_menu);

        // TODO load and set settings in activity

        final EditText addressField = this.findViewById(R.id.addressField);
        addressField.setText(R.string.default_server_address);
    }

    public void save(final View view) {
        // TODO save settings

        this.onBackPressed();
    }

}