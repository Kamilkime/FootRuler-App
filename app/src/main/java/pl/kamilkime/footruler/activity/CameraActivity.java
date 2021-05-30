package pl.kamilkime.footruler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import pl.kamilkime.footruler.FootRuler;
import pl.kamilkime.footruler.R;
import pl.kamilkime.footruler.database.entity.FootData;
import pl.kamilkime.footruler.util.DataUtil;
import pl.kamilkime.footruler.util.HttpUtil;

public class CameraActivity extends AppCompatActivity {

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
        this.imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {

            @Override
            public void onCaptureSuccess(@NonNull final ImageProxy image) {
                new Thread(() -> {
                    try {
                        final Optional<FootData> response = HttpUtil.sendImage(image);
                        image.close();

                        if (!response.isPresent()) {
                            new Handler(CameraActivity.this.getApplicationContext().getMainLooper()).post(() -> {
                                Toast.makeText(CameraActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
                                CameraActivity.this.navigateUpTo(new Intent(CameraActivity.this, FootRuler.class));
                            });

                            return;
                        }

                        final Optional<ByteArrayOutputStream> imageStreamOptional = HttpUtil.downloadImage(response.get().image);
                        if (imageStreamOptional.isPresent()) {
                            final ByteArrayOutputStream imageStream = imageStreamOptional.get();

                            final File imageFile = DataUtil.getImageFile(response.get().image);
                            try (final OutputStream fileStream = new FileOutputStream(imageFile)) {
                                imageStream.writeTo(fileStream);
                            }
                        } else {
                            new Handler(CameraActivity.this.getApplicationContext().getMainLooper()).post(() -> {
                                Toast.makeText(CameraActivity.this, "Failed to download image", Toast.LENGTH_LONG).show();
                            });
                        }

                        FootRuler.getFootDatabase().footDataDao().addFootData(response.get());

                        new Handler(CameraActivity.this.getApplicationContext().getMainLooper()).post(() -> {
                            final Intent intent = new Intent(CameraActivity.this, DataActivity.class);
                            intent.putExtra("saved", response.get().savedOnServer);
                            intent.putExtra("data", response.get().data);
                            intent.putExtra("timestamp", response.get().timestamp);
                            intent.putExtra("image", DataUtil.getImageFile(response.get().image));
                            intent.putExtra("returnToMain", true);

                            CameraActivity.this.startActivity(intent);
                        });
                    } catch (final IOException exception) {
                        new Handler(CameraActivity.this.getApplicationContext().getMainLooper()).post(() -> {
                            Toast.makeText(CameraActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
                            CameraActivity.this.navigateUpTo(new Intent(CameraActivity.this, FootRuler.class));
                        });
                    }
                }).start();
            }
        });

        this.setContentView(R.layout.waiting_layout);
    }

}