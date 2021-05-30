package pl.kamilkime.footruler.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class ImageUtil {

    public static InputStream imageToInputStream(final ImageProxy image) {
        final ByteBuffer buffer = image.getPlanes()[0].getBuffer();

        final byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        if (bitmap.getWidth() > bitmap.getHeight()) { //TODO check orientation better
            final Matrix matrix = new Matrix();
            matrix.setRotate(90.0F);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private ImageUtil() {}

}
