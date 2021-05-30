package pl.kamilkime.footruler.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import pl.kamilkime.footruler.database.entity.Setting;

@Dao
public interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDefaults(Setting... settings);

    @Update
    void updateSettings(Setting... settings);

    @Query("SELECT * FROM footruler_settings WHERE setting_name = :name")
    Setting getSetting(final String name);

}
