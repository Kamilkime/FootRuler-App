package pl.kamilkime.footruler.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import pl.kamilkime.footruler.R;

public class CameraActivity extends AppCompatActivity {

    private static final Size TARGET_RESOLUTION = new Size(1280, 720);
    private static final int LENS_FACING = CameraSelector.LENS_FACING_BACK;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.camera_activity);

        this.previewView = this.findViewById(R.id.camera_preview);
        this.cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        this.cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = this.cameraProviderFuture.get();

                final Preview preview = new Preview.Builder().build();
                final CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                preview.setSurfaceProvider(this.previewView.getSurfaceProvider());
                this.imageCapture = new ImageCapture.Builder().build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, this.imageCapture);
            } catch (final ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(final View view) {
//        this.imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
//
//            @Override
//            public void onCaptureSuccess(@NonNull final ImageProxy image) {
//                final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//
//                final byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
//                if (bitmap.getWidth() > bitmap.getHeight()) { //TODO check orientation better
//                    final Matrix matrix = new Matrix();
//                    matrix.setRotate(90.0F);
//
//                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                }
//
//                final File imageFile = new File(CameraActivity.this.getCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageFile));
//                } catch (final FileNotFoundException exception) {
//                    return;
//                }
//
//                new Thread(() -> {
//                    try {
//                        final CloseableHttpClient httpClient = HttpClients.createDefault();
//                        final HttpPost post = new HttpPost("http://192.168.1.100:7000/analyze");
//
//                        final MultipartEntityBuilder multipart = MultipartEntityBuilder.create();
//                        multipart.addBinaryBody("image", new FileInputStream(imageFile), ContentType.APPLICATION_OCTET_STREAM, imageFile.getName());
//                        post.setEntity(multipart.build());
//
//                        final CloseableHttpResponse response = httpClient.execute(post);
//                        Log.i("FootRuler", "Server response: " + IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
//                    } catch (final IOException exception) {
//                        exception.printStackTrace();
//                    }
//                }).start();
//            }
//        });

        this.startActivity(new Intent(this, DataActivity.class));
    }

}