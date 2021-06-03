package pl.kamilkime.footruler.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;

import pl.kamilkime.footruler.FootRuler;
import pl.kamilkime.footruler.R;
import pl.kamilkime.footruler.database.FootDatabase;
import pl.kamilkime.footruler.database.entity.FootData;
import pl.kamilkime.footruler.util.DataUtil;
import pl.kamilkime.footruler.util.HttpUtil;

public class HistoryActivity extends AppCompatActivity {

    private static final int MAX_ENTRIES = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.history_activity);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout entries = this.findViewById(R.id.entries);

        new Thread(() -> {
            int i = 0;

            for (final FootData footData : FootRuler.getFootDatabase().footDataDao().getAllFootData()) {
                if (i >= MAX_ENTRIES) {
                    break;
                }

                final File imageFile = DataUtil.getImageFile(footData.image);
                if (!imageFile.exists()) {
                    final Optional<ByteArrayOutputStream> imageStream = HttpUtil.downloadImage(footData.image);
                    imageStream.ifPresent(byteArrayOutputStream -> DataUtil.saveImage(byteArrayOutputStream, imageFile));
                }

                new Handler(this.getApplicationContext().getMainLooper()).post(() -> {
                    final View entry = inflater.inflate(R.layout.history_entry, entries, false);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) entry.getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new LinearLayout.LayoutParams(0, 0);
                        entry.setLayoutParams(layoutParams);
                    }

                    layoutParams.width = 600;
                    layoutParams.height = 250;
                    layoutParams.setMargins(35, 0, 35, 40);
                    entry.requestLayout();

                    final ImageView thumbnail = entry.findViewById(R.id.thumbnail);
                    if (imageFile.exists()) {
                        Glide.with(entry).load(imageFile).into(thumbnail);
                        //thumbnail.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                    }

                    final TextView timestamp = entry.findViewById(R.id.timestamp);
                    timestamp.setText(DATE_FORMAT.format(footData.timestamp));

                    final TextView feetCount = entry.findViewById(R.id.length);
                    if (footData.feetCount == 0) {
                        feetCount.setText(R.string.no_feet);
                    } else if (footData.feetCount == 1) {
                        feetCount.setText(R.string.one_foot);
                    } else {
                        feetCount.setText(this.getString(R.string.feet, footData.feetCount));
                    }

                    entry.setOnClickListener(v -> {
                        final Intent intent = new Intent(this, DataActivity.class);
                        intent.putExtra("saved", footData.savedOnServer);
                        intent.putExtra("data", footData.data);
                        intent.putExtra("timestamp", footData.timestamp);
                        intent.putExtra("image", imageFile);
                        intent.putExtra("returnToMain", false);

                        this.startActivity(intent);
                    });

                    entries.addView(entry);
                });

                i++;
            }

            if (i == 0) {
                new Handler(this.getApplicationContext().getMainLooper()).post(() -> {
                    this.findViewById(R.id.remove_from_server).setEnabled(false);
                });
            }
        }).start();
    }

    public void clearHistory(final View view) {
        new Thread(() -> {
            final FootDatabase database = FootRuler.getFootDatabase();
            if (!Boolean.parseBoolean(database.settingsDao().getSetting("save_on_server").settingValue)) {
                for (final FootData footData : database.footDataDao().getAllFootData()) {
                    if (!footData.savedOnServer) {
                        continue;
                    }

                    HttpUtil.removeImage(footData.image);
                }
            }

            database.footDataDao().clearFootData();
        }).start();

        Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        this.finish();
    }

}