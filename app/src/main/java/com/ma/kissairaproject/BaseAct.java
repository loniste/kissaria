package com.ma.kissairaproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class BaseAct extends AppCompatActivity {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "BaseAct";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public String SEMIBOLD = "_semibold";
    public String BOLD = "_bold";
    public String REGULAR = "_regular";
    public String LIGHT = "_light";
    OnImageSelectedListener onImageSelectedListener;

    private int localImageFilter = -1;
    private ProgressDialog progressDialog;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;

        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public File getImageFile(Bitmap bitmap) {
        File f = new File(getCacheDir(), UUID.randomUUID() + ".jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @NonNull
    public MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("*/*"),
                        file
                );
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called with localImageFilter: " + localImageFilter);
    }


    public void showLoading() {
        if (progressDialog != null) {
            progressDialog.show();
            return;
        }
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog == null) return;
        progressDialog.dismiss();
    }


    public APIManager getAPIManager() {
        return APIManager.retrofit.create(APIManager.class);
    }


    protected interface OnTokenUpdatedListener {
        public void onTokenUpdated();
    }

    public interface OnImageSelectedListener {
        //code to filter, to know the caller
        void onImageSelected(int code);
    }


}
