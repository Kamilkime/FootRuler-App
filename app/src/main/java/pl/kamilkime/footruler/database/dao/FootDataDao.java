package pl.kamilkime.footruler.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.kamilkime.footruler.database.entity.FootData;

@Dao
public interface FootDataDao {

    @Insert
    void addFootData(final FootData footData);

    @Query("SELECT * FROM foot_data ORDER BY timestamp DESC")
    List<FootData> getAllFootData();

    @Query("DELETE FROM foot_data")
    void clearFootData();

    @Query("UPDATE foot_data SET on_server = 0 WHERE image = :image")
    void removeFromServer(final String image);

}
