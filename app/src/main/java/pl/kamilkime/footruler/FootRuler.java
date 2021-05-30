package pl.kamilkime.footruler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import pl.kamilkime.footruler.activity.CameraActivity;
import pl.kamilkime.footruler.activity.HistoryActivity;
import pl.kamilkime.footruler.activity.SettingsActivity;
import pl.kamilkime.footruler.database.FootDatabase;
import pl.kamilkime.footruler.database.entity.Setting;
import pl.kamilkime.footruler.util.PermissionUtil;

public class FootRuler extends AppCompatActivity {

    private ActivityResultLauncher<String[]> permissionCheck;
    private static FootDatabase footDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.permissionCheck = this.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            for (final boolean result : isGranted.values()) {
                if (result) {
                    continue;
                }

                final Toast toast = Toast.makeText(this, "You need to grant required\npermissions first", Toast.LENGTH_LONG);

                final TextView toastText = toast.getView().findViewById(android.R.id.message);
                if (toastText != null) {
                    toastText.setGravity(Gravity.CENTER);
                }

                toast.show();
                return;
            }

            final Intent intent = new Intent(this, CameraActivity.class);
            this.startActivity(intent);
        });

        this.setContentView(R.layout.main_activity);
        initDatabase(this.getApplicationContext());
    }

    public void openSettingsMenu(final View view) {
        final Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void openHistoryMenu(final View view) {
        final Intent intent = new Intent(this, HistoryActivity.class);
        this.startActivity(intent);
    }

    public void useCamera(final View view) {
        this.permissionCheck.launch(PermissionUtil.APP_PERMISSIONS);
    }

    private static void initDatabase(final Context context) {
        if (footDatabase != null) {
            return;
        }

        footDatabase = Room.databaseBuilder(context, FootDatabase.class, "footruler.db").fallbackToDestructiveMigration().build();

        new Thread(() -> {
            footDatabase.settingsDao().insertDefaults(
                    new Setting("server_address", "http://192.168.1.100:7000/"),
                    new Setting("save_on_server", "true")
            );
        }).start();
    }

    public static FootDatabase getFootDatabase() {
        return footDatabase;
    }

    @Override
    public void onBackPressed() {}

}