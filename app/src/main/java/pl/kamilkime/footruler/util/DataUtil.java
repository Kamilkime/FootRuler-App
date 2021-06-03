package pl.kamilkime.footruler.util;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pl.kamilkime.footruler.database.entity.FootData;

public final class DataUtil {

    public static FootData decodeJSON(final JSONObject json) {
        try {
            final String image = json.getString("image");
            final int feetCount = json.getInt("feetCount");
            final String data = json.getJSONArray("footData").toString();

            return new FootData(image, System.currentTimeMillis(), feetCount, data);
        } catch (final JSONException exception) {
            return null;
        }
    }

    public static int decodeColor(final String colorString) {
        final String[] colorSplit = colorString.split(",");

        final int r = Integer.parseInt(colorSplit[0]);
        final int g = Integer.parseInt(colorSplit[1]);
        final int b = Integer.parseInt(colorSplit[2]);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static void saveImage(final ByteArrayOutputStream imageStream, final File imageFile) {
        try (final OutputStream fileStream = new FileOutputStream(imageFile)) {
            imageStream.writeTo(fileStream);
        } catch (final IOException ignored) {}
    }

    public static File getImageFile(final String imageName) {
        final File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "footruler");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
        }

        return new File(imagesFolder, imageName);
    }

    private DataUtil() {}

}
