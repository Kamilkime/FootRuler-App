package pl.kamilkime.footruler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pl.kamilkime.footruler.FootRuler;
import pl.kamilkime.footruler.R;
import pl.kamilkime.footruler.util.DataUtil;
import pl.kamilkime.footruler.util.HttpUtil;

public class DataActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    private File imageFile;
    private boolean returnToMain;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.data_activity);

        this.imageFile = (File) this.getIntent().getSerializableExtra("image");
        this.returnToMain = this.getIntent().getBooleanExtra("returnToMain", true);

        try {
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout entries = this.findViewById(R.id.entries);
            final JSONArray data = new JSONArray(this.getIntent().getStringExtra("data"));

            for (int i = 0; i < data.length(); i++) {
                final JSONObject dataEntry = data.getJSONObject(i);
                final View entry = inflater.inflate(R.layout.data_entry, entries, false);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) entry.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new LinearLayout.LayoutParams(0, 0);
                    entry.setLayoutParams(layoutParams);
                }

                layoutParams.width = 600;
                layoutParams.height = 250;
                layoutParams.setMargins(35, 20, 35, 20);
                entry.requestLayout();

                final TextView footNo = entry.findViewById(R.id.foot_no);
                footNo.setText(this.getString(R.string.foot_no, i + 1));

                final TextView length = entry.findViewById(R.id.length);
                length.setText(this.getString(R.string.foot_value, dataEntry.getDouble("length")));

                final TextView width = entry.findViewById(R.id.width);
                width.setText(this.getString(R.string.foot_value, dataEntry.getDouble("width")));

                final TextView euSize = entry.findViewById(R.id.eu_size);
                euSize.setText(String.valueOf(dataEntry.getInt("size")));

                final Button color = entry.findViewById(R.id.color);
                color.setBackgroundColor(DataUtil.decodeColor(dataEntry.getString("color")));

                entries.addView(entry);
            }
        } catch (final JSONException exception) {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
        }

        final Button removeButton = this.findViewById(R.id.remove_from_server);
        removeButton.setEnabled(this.getIntent().getBooleanExtra("saved", false));

        final TextView date = this.findViewById(R.id.date);
        date.setText(DATE_FORMAT.format(this.getIntent().getLongExtra("timestamp", 0L)));

        final ImageView foot = this.findViewById(R.id.foot);
        if (this.imageFile.exists()) {
            foot.setImageBitmap(BitmapFactory.decodeFile(this.imageFile.getAbsolutePath()));
        }
    }

    public void removeFromServer(final View view) {
        new Thread(() -> {
            final boolean removed = HttpUtil.removeImage(this.imageFile.getName());
            if (!removed) {
                new Handler(this.getApplicationContext().getMainLooper()).post(() -> {
                    Toast.makeText(this, "Failed to remove image", Toast.LENGTH_SHORT).show();
                });

                return;
            }

            FootRuler.getFootDatabase().footDataDao().removeFromServer(this.imageFile.getName());

            new Handler(this.getApplicationContext().getMainLooper()).post(() -> {
                final Button removeButton = this.findViewById(R.id.remove_from_server);
                removeButton.setEnabled(false);

                Toast.makeText(this, "Image removed from server", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (this.returnToMain) {
            this.navigateUpTo(new Intent(this, FootRuler.class));
            return;
        }

        super.onBackPressed();
    }

}