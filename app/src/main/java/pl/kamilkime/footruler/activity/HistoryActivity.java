package pl.kamilkime.footruler.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import pl.kamilkime.footruler.R;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.history_activity);

        final LinearLayout entries = this.findViewById(R.id.entries);

        for (int i = 0; i < 10; i++) {
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View entry = inflater.inflate(R.layout.history_entry, null);

            entry.setLayoutParams(new LinearLayout.LayoutParams(600, 250));

            ((LinearLayout.LayoutParams) entry.getLayoutParams()).setMargins(35, 0, 35, 40);
            entry.requestLayout();

            int finalI = i;
            entry.setOnClickListener(v -> {
                Log.i("Entry", String.valueOf(finalI));
            });

            entries.addView(entry);
        }
    }

}