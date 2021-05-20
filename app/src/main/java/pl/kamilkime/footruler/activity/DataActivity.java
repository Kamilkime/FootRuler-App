package pl.kamilkime.footruler.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import pl.kamilkime.footruler.R;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.data_activity);

        final LinearLayout entries = this.findViewById(R.id.entries);

        for (int i = 0; i < 5; i++) {
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View entry = inflater.inflate(R.layout.data_entry, null);

            entry.setLayoutParams(new LinearLayout.LayoutParams(600, 250));

            ((LinearLayout.LayoutParams) entry.getLayoutParams()).setMargins(35, 20, 35, 20);
            entry.requestLayout();

            int finalI = i;
            entry.setOnClickListener(v -> {
                Log.i("Entry", String.valueOf(finalI));
            });

            entries.addView(entry);
        }
    }
}