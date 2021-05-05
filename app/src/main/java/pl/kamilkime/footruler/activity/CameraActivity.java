package pl.kamilkime.footruler.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
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
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (final ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(final View view) {
        this.imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {

            @Override
            public void onCaptureSuccess(@NonNull final ImageProxy image) {
                final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                final byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);

                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                final String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

                Log.i("FootRuler", "Photo taken");
            }
        });
    }

}