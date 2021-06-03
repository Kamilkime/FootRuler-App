package pl.kamilkime.footruler.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "foot_data")
public class FootData {

    @NotNull
    @PrimaryKey
    @ColumnInfo
    public String image;

    @ColumnInfo(name = "on_server")
    public boolean savedOnServer;

    @ColumnInfo
    public long timestamp;

    @ColumnInfo(name = "feet_count")
    public int feetCount;

    @NotNull
    @ColumnInfo
    public String data;

    public FootData(@NotNull final String image, final long timestamp, final int feetCount, @NotNull final String data) {
        this.image = image;
        this.timestamp = timestamp;
        this.feetCount = feetCount;
        this.data = data;
    }

}
