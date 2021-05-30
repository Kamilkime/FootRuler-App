package pl.kamilkime.footruler.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "footruler_settings")
public class Setting {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "setting_name")
    public String settingName;

    @NotNull
    @ColumnInfo(name = "setting_value")
    public String settingValue;

    public Setting(@NotNull final String settingName, @NotNull final String settingValue) {
        this.settingName = settingName;
        this.settingValue = settingValue;
    }

}
