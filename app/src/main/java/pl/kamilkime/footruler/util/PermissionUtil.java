package pl.kamilkime.footruler.util;

import android.Manifest;

public final class PermissionUtil {

    public static final String[] APP_PERMISSIONS;

    static {
        APP_PERMISSIONS = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    private PermissionUtil() {}

}
