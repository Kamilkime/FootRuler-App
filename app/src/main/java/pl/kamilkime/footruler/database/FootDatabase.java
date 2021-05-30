package pl.kamilkime.footruler.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import pl.kamilkime.footruler.database.dao.FootDataDao;
import pl.kamilkime.footruler.database.dao.SettingsDao;
import pl.kamilkime.footruler.database.entity.FootData;
import pl.kamilkime.footruler.database.entity.Setting;

@Database(entities = {FootData.class, Setting.class}, version = 2)
public abstract class FootDatabase extends RoomDatabase {

    public abstract FootDataDao footDataDao();
    public abstract SettingsDao settingsDao();

}
