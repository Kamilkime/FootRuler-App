package pl.kamilkime.footruler.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import pl.kamilkime.footruler.FootRuler;
import pl.kamilkime.footruler.R;
import pl.kamilkime.footruler.database.FootDatabase;
import pl.kamilkime.footruler.database.entity.Setting;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_activity);

        new Thread(() -> {
            final FootDatabase footDatabase = FootRuler.getFootDatabase();

            final Setting serverAddress = footDatabase.settingsDao().getSetting("server_address");
            final Setting saveOnServer = footDatabase.settingsDao().getSetting("save_on_server");

            new Handler(this.getApplicationContext().getMainLooper()).post(() -> {
                final EditText addressField = this.findViewById(R.id.addressField);
                addressField.setText(serverAddress.settingValue);

                final SwitchMaterial consentSwitch = this.findViewById(R.id.consent);
                consentSwitch.setChecked("true".equals(saveOnServer.settingValue));
            });
        }).start();
    }

    public void save(final View view) {
        final EditText addressField = this.findViewById(R.id.addressField);
        final Setting serverAddress = new Setting("server_address", addressField.getText().toString());

        final SwitchMaterial consentSwitch = this.findViewById(R.id.consent);
        final Setting saveOnServer = new Setting("save_on_server", consentSwitch.isChecked() ? "true" : "false");

        new Thread(() -> {
            final FootDatabase footDatabase = FootRuler.getFootDatabase();
            footDatabase.settingsDao().updateSettings(serverAddress, saveOnServer);
        }).start();

        this.finish();
    }

}