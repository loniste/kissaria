package com.hakik.audio_player;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.loader.content.CursorLoader;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsAudioPlayer {
//     public static RequestListener<Drawable> glideErrorListenerForDrawable = new RequestListener<Drawable>() {
//        @Override
//        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//            if (e != null) {
//                Sentry.captureMessage(e.getMessage().toString());
//                e.printStackTrace();
//            } else {
//                Sentry.captureMessage("Glide error with null exception");
//            }
//            return false;
//        }
//
//        @Override
//        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource
//                dataSource, boolean isFirstResource) {
//            return false;
//        }
//    };
//    public static RequestListener<Bitmap> glideErrorListenerForBitmap = new RequestListener<Bitmap>() {
//        @Override
//        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//            if (e != null) {
//                Sentry.captureMessage(e.getMessage().toString());
//                e.printStackTrace();
//            } else {
//                Sentry.captureMessage("Glide error with null exception");
//            }
//            return false;
//        }
//
//        @Override
//        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource
//                dataSource, boolean isFirstResource) {
//            return false;
//        }
//    };
    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;


    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }


    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static float spToPixel(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    /**
     * @param towards:
     * @param ratio          width ratio in case Towards= Start or End
     * @param afterDuration: if!=0 then dont start animation immediatly
     * @param duration
     * @param totalDuration
     * @param views
     */
    public static void viewsEntry(boolean in, Towards towards, float ratio, boolean progressiveResilience, float resilience, long afterDuration, long duration, long totalDuration, View... views) {
        int[] dim = getScreenWidthHeight(getActivity(views[0].getContext()));
        int width = dim[0];
        int height = dim[1];
        for (int i = 0; i < views.length; i++) {
            int finalI = i;
            float[] a = UtilsAudioPlayer.getSlopeAndIntercept(0, resilience, views.length, 1);
            new Handler().postDelayed(() -> {
                views[finalI].setVisibility(View.VISIBLE);
                getValueAnimator(true, duration, new DecelerateInterpolator(progressiveResilience ? a[0] * finalI + a[1] : resilience), progress -> {
                    views[finalI].setAlpha(in ? progress : (1 - progress));
                    switch (towards) {
                        case START:
                            views[finalI].setTranslationX(((in ? 1 : 0) - progress) * width * ratio);
                            break;
                        case END:
                            views[finalI].setTranslationX((progress - (in ? 1 : 0)) * width * ratio);
                            break;
                        case TOP:
                            views[finalI].setTranslationY(((in ? 1 : 0) - progress) * height * ratio);
                            break;
                        case BOTTOM:
                            views[finalI].setTranslationY((progress - (in ? 1 : 0)) * height * ratio);
                            break;
                    }
                }).start();
            }, (views.length != 1 ? (i * (totalDuration - duration) / (views.length - 1)) : 0) + afterDuration);
        }
    }

    public static Drawable setTint(Context context, int drawable, String stringColor) {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable).getConstantState().newDrawable().mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(stringColor));
        return wrappedDrawable;
    }

    public static Drawable setTint(Drawable drawable, String stringColor) {
        Drawable unwrappedDrawable = drawable.getConstantState().newDrawable().mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(stringColor));
        return wrappedDrawable;
    }

    public static Drawable setTint(Context context, int drawable, int color) {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable).getConstantState().newDrawable().mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    //distance in m
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1000;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static int getWeekDay() {
        Calendar calendar = Calendar.getInstance(new Locale("KW"));
        int day = calendar.getFirstDayOfWeek() + 1;
        int localDay = calendar.get(Calendar.DAY_OF_WEEK) - 2;
//        if (getLang().equals("ar")) {
//            day += 2;
//        }
        if (day < 0) {
            day += 7;
        }

        if (localDay < 0) {
            day += 7;
            localDay += 7;
        }

//        if (isShopGooglePlace()) {
//            return day;
//        }
        return localDay;
    }

    public static void setSelectedLanguageId(String id, Context context) {
        final SharedPreferences prefs = getDefaultSharedPreference(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language_id", id);
        editor.apply();
    }

    private static SharedPreferences getDefaultSharedPreference(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()) != null)
            return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        else
            return null;
    }

    public static void updateLanguage(Context ctx, String lang) {

        Resources resources = ctx.getResources();
        Configuration cfg = resources.getConfiguration();
        if (!TextUtils.isEmpty(lang)) {
            cfg.locale = new Locale(lang);
        } else {
            cfg.locale = Locale.getDefault();
        }


        Locale.setDefault(cfg.locale);
        resources.updateConfiguration(cfg, resources.getDisplayMetrics());

    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static Bitmap createDrawableFromView(Context context, View view, int width, int height) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static int[] getWidthHeightFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
    }

    public static float[] getSlopeAndIntercept(float x1, float y1, float x2, float y2) {
        return new float[]{((y2 - y1) / (x2 - x1)), y1 - x1 * ((y2 - y1) / (x2 - x1))};
    }

    public static ValueAnimator getValueAnimator(
            boolean forward,
            long duration,
            TimeInterpolator interpolator,
            ExpandProgressInterface expandProgressInterface
    ) {
        ValueAnimator a = (forward) ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
        a.addUpdateListener(valueAnimator -> {
            expandProgressInterface.setExpandProgress((Float) a.getAnimatedValue());
        });

        a.setDuration(duration);
        a.setInterpolator(interpolator);
        return a;
    }



    public static String decodeString(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * this function check if the string in its parameter is a correct fomrmed email
     *
     * @param email
     * @return
     */
    public static boolean isValid(String email) {
        return (email.indexOf("@") > 0);
    }

    /**
     * overloading of the isValid(String email) function
     *
     * @param email
     * @param password
     * @return
     */
    public static boolean isValid(String email, String password) {
        return (email.indexOf("@") > 0) && (password.length() > 0);
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isSecondDateHigher(String date1, String date2, String format) {
        Log.d(TAG, "isSecondDateHigher() called with: date1 = [" + date1 + "], date2 = [" + date2 + "], format = [" + format + "]");
        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("en"));
        Date strDate1;
        Date strDate2;
        try {
            strDate1 = sdf.parse(date1);
            strDate2 = sdf.parse(date2);
            if (strDate2 != null) {
                return strDate2.after(strDate1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static String replaceArabicNumbers(String original) {
        return original.replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9")
                .replaceAll("٠", "0");
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static boolean compareTimes(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copy(File src, OutputStream out, long length, UpdateListener updateListener) {
        try (InputStream in = new FileInputStream(src)) {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            int counter = 0;
            Log.d(TAG, "copy: length: " + length);
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                counter = counter + 1024;
                Log.d(TAG, "copy: counter: " + counter);
                updateListener.update((int) (counter * 100 / length));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void toString(String tag, ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            Log.d(tag, "toString: " + list.get(i));
        }
    }

    public static String bytes2String(long sizeInBytes) {

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }

    public static int copy(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];


        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }



    public static int dip2px(Context c, float dip) {
        Resources r = c.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Date DateFromString(String inputFormat, String inputDate) {

        Date parsed = null;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);

        try {
            parsed = df_input.parse(inputDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parsed;

    }

    public static int getImageDrawableId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }



    public static int[] getScreenWidthHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }


    public static File generateThumbnail(Context context, File file) {

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
        BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

        // find the best scaling factor for the desired dimensions
        int desiredWidth = 400;
        int desiredHeight = 300;
        float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
        float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
        float scale = Math.min(widthScale, heightScale);

        int sampleSize = 1;
        while (sampleSize < scale) {
            sampleSize *= 2;
        }
        bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
        // this is why you can not have an image scaled as you would like
        bitmapOptions.inJustDecodeBounds = false; // now we want to load the image

        // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
        Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
        File thumbnailFile = null;
        try {
            // Save the thumbnail
//            PackageManager m = context.getPackageManager();
//            String s = context.getPackageName();
//            PackageInfo p = m.getPackageInfo(s, 0);
//            s = p.applicationInfo.dataDir;
//            thumbnailFile = new File(context.getFilesDir().getAbsolutePath()+ File.separator +  "new_file.jpeg");
            String root = context.getExternalCacheDir().toString();
            thumbnailFile = new File(root + File.separator + "new_file.jpeg");

//            if (thumbnailFile.exists()){
//
//                thumbnailFile.delete();
//            }
//            thumbnailFile = new File(root+ File.separator +  "new_file.jpeg");
            Log.d(TAG, "generateThumbnail: " + thumbnailFile.getPath());
            thumbnailFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(thumbnailFile);

            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);

            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use the thumbail on an ImageView or recycle it!
        thumbnail.recycle();
        return thumbnailFile;
    }

    public static Bitmap getBitmapFromView2(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);

        View v1 = view;// getActivity(view).getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        return bitmap;
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getIntFromColor(255, 255, 255));
        view.draw(canvas);
        return bitmap;
    }

    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static Activity getActivity(Context context) {

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static Bitmap darkenBitMap(Bitmap bm) {

        Canvas canvas = new Canvas(bm);
        Paint p = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(bm, new Matrix(), p);
        return bm;
    }

    public static Bitmap updateHSV(Bitmap src, float settingHue, float settingSat,
                                   float settingVal) {

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);

                // Adjust HSV
//                pixelHSV[0] = pixelHSV[0] + settingHue;
//                if (pixelHSV[0] < 0.0f) {
//                    pixelHSV[0] = 0.0f;
//                } else if (pixelHSV[0] > 360.0f) {
//                    pixelHSV[0] = 360.0f;
//                }
//
//                pixelHSV[1] = pixelHSV[1] + settingSat;
//                if (pixelHSV[1] < 0.0f) {
//                    pixelHSV[1] = 0.0f;
//                } else if (pixelHSV[1] > 1.0f) {
//                    pixelHSV[1] = 1.0f;
//                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);

    }

    public static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
    public static int getIntFromColor(int alpha, int Red, int Green, int Blue) {
        alpha = (alpha << 24) & 0xFF000000;
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return alpha | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
    public static boolean exists(Context context, Uri self) {
        final ContentResolver resolver = context.getContentResolver();

        Cursor c = null;
        try {
            c = resolver.query(self, new String[]{
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
            return c.getCount() > 0;
        } catch (Exception e) {
            Log.w(TAG, "Failed query: " + e);
            return false;
        }
    }

    public static String getRealPathFromURI(Context c, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(c, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {

            Log.d(TAG, "getFilePath: isExternalStorageDocument, docId: " + DocumentsContract.getDocumentId(uri));

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                Log.d(TAG, "getFilePath: isExternalStorageDocument");
                return Environment.getExternalStorageDirectory() + "/" + split[1];

            } else if (isDownloadsDocument(uri)) {
                Log.d(TAG, "getFilePath: isDownloadsDocument");

                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                Log.d(TAG, "getFilePath: isMediaDocument");

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d(TAG, "getFilePath: content");


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @SuppressLint("Range")
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Drawable resizeDrawable(Context c, int drawableInt, int w, int h) {
        Drawable dr = ContextCompat.getDrawable(c, drawableInt);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }

    //keep proportions
    public static Drawable resizeDrawable(Context c, int drawableInt, int dim, boolean isDimWidth) {
        Drawable dr = ContextCompat.getDrawable(c, drawableInt);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        float ratio = ((float) bitmap.getWidth() / bitmap.getHeight());
        int width = isDimWidth ? dim : (int) (dim * ratio);
        int height = isDimWidth ? (int) (dim / ratio) : dim;
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    public static Bitmap resizeBitmap(Context c, Bitmap bitmap, int dim, boolean isDimWidth) {
        float ratio = ((float) bitmap.getWidth() / bitmap.getHeight());
        int width = isDimWidth ? dim : (int) (dim * ratio);
        int height = isDimWidth ? (int) (dim / ratio) : dim;
        if (width == 0 || height == 0) return null;
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static Bitmap resizeBitmap(Context c, Bitmap bitmap, int size /*with x height in px*/) {
        int originalSize = bitmap.getWidth() * bitmap.getHeight();
        android.util.Log.d(TAG, "resizeBitmap: originalSize: " + originalSize);
        float ratio = (float) Math.sqrt((size) * 1.0f / originalSize);
        android.util.Log.d(TAG, "resizeBitmap: ratio: " + ratio);
        int width = (int) (bitmap.getWidth() * ratio);
        int height = (int) (bitmap.getHeight() * ratio);
        android.util.Log.d(TAG, "resizeBitmap: width: " + width);
        android.util.Log.d(TAG, "resizeBitmap: height: " + height);
        if (width == 0 || height == 0) return null;
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }


    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    /**
     * Get external sd card path using reflection
     *
     * @param mContext
     * @param is_removable is external storage removable
     * @return
     */
    public static String getExternalStoragePath(Context mContext, boolean is_removable) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            StorageVolume storageVolume;
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removable == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static float[] centroid(float[] points) {
        float[] centroid = {0, 0};

        for (int i = 0; i < points.length; i += 2) {
            centroid[0] += points[i];
            centroid[1] += points[i + 1];
        }

        int totalPoints = points.length / 2;
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }


    public static String code(int id) {
        return "@postId:" + id + "//";
    }

    public static int decode(String code) {
        Pattern mPattern = Pattern.compile("@postId:[1-9]*//");

        Matcher matcher = mPattern.matcher(code);
        if (!matcher.find()) {
        } else {
            String expression = matcher.group(0);
            android.util.Log.d(TAG, "decode: matcher.group(0): " + matcher.group(0));
            expression = expression.substring(8);
            expression = expression.substring(0, expression.length() - 2);
            android.util.Log.d(TAG, "decode: expression: " + expression);
            return Integer.parseInt(expression);

        }
        return -1;
    }

    public static String getCode(String string) {
        Pattern mPattern = Pattern.compile("@postId:[1-9]*//");

        Matcher matcher = mPattern.matcher(string);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public enum Towards {
        START,
        END,
        TOP,
        BOTTOM
    }

    public interface ExpandProgressInterface {
        void setExpandProgress(float progress);
    }

    interface UpdateListener {
        public void update(int i);
    }

    public interface OnFadeListener {
        void onFade();
    }
    public interface OnTimerListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    public static class CustomAnimations {

        /**
         * the given view must have view.getWidth==match parent or view.getHeight==match_parent,based on the @param from
         * translates view
         *
         * @param view
         */
        public static int LEFT = -1;
        public static int RIGHT = 1;
        public static int TOP = 2;
        public static int BOTTOM = -2;

        public static void fadeIn(int duration, boolean in, OnFadeListener onFadeListener, View... views) {
            AlphaAnimation anim = new AlphaAnimation(in ? 0 : 1, in ? 1 : 0); //0 means invisible
            anim.setDuration(duration);
            anim.setRepeatCount(0);
//                    anim.setRepeatMode(Animation.REVERSE);
            for (View view : views) {
                if (view != null)
                    view.startAnimation(anim);
            }


            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {

                    Log.d(TAG, "onAnimationStart: ");
                    if (in) {
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        for (View view : views) {
                            if (view != null)
                                view.setVisibility(View.VISIBLE);
                        }
                        if (onFadeListener != null) {
                            onFadeListener.onFade();
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    Log.d(TAG, "onAnimationEnd: ");
                    if (!in) {
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//hide status bar because it causes problemwhen screen shoting
                        for (View view : views) {
                            if (view != null)
                                view.setVisibility(View.INVISIBLE);
                        }
                        if (onFadeListener != null) {
                            onFadeListener.onFade();
                        }
                    }
                }
            });
        }

        public static void translateFrom(boolean towards, int from, int duration, View view) {
            android.util.Log.d(TAG, "translateFrom: from X: " + (towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: to X: " + (!towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: from Y: " + (towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: to Y: " + (!towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0));

            getValueAnimator(true, duration, new AccelerateDecelerateInterpolator(), progress -> {
                float fromX = towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0;
                float toX = !towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0;
                float fromY = towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0;
                float toY = !towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0;
                view.setX((toX - fromX) * progress + fromX);
                view.setY((toY - fromY) * progress + fromY);
            }).start();

        }

        public static void translateFrom(boolean towards, int from, int duration, View view, OnAnimationListener onAnimationListener) {
            android.util.Log.d(TAG, "translateFrom: from X: " + (towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: to X: " + (!towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: from Y: " + (towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0));
            android.util.Log.d(TAG, "translateFrom: to Y: " + (!towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0));

            ValueAnimator animator = getValueAnimator(true, duration, new AccelerateDecelerateInterpolator(), progress -> {
                float fromX = towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0;
                float toX = !towards ? (from == LEFT ? -view.getWidth() : (from == RIGHT ? view.getWidth() : 0)) : 0;
                float fromY = towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0;
                float toY = !towards ? (from == TOP ? -view.getHeight() : (from == BOTTOM ? view.getHeight() : 0)) : 0;
                view.setX((toX - fromX) * progress + fromX);
                view.setY((toY - fromY) * progress + fromY);
            });
            if (onAnimationListener == null) {
                animator.start();
                return;
            }
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onAnimationListener.onAnimationEnd();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    onAnimationListener.onAnimationStart();
                }
            });
            animator.start();

        }

        public static void color(boolean towards, int duration, int from, int to, TextView textView) {
            int fromRed = Color.red(from);
            int fromGreen = Color.green(from);
            int fromBlue = Color.blue(from);
            int toRed = Color.red(to);
            int toGreen = Color.green(to);
            int toBlue = Color.blue(to);
            getValueAnimator(towards, duration, new AccelerateDecelerateInterpolator(), progress -> {
                int valueRed = (int) (fromRed + (toRed - fromRed) * progress);
                int valueGreen = (int) (fromGreen + (toGreen - fromGreen) * progress);
                int valueBlue = (int) (fromBlue + (toBlue - fromBlue) * progress);
                textView.setTextColor(UtilsAudioPlayer.getIntFromColor(valueRed, valueGreen, valueBlue));
            }).start();
        }

        public static void translateToLeft(View view) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0, -view.getWidth(), 0, 0);
            translateAnimation.setDuration(300);
            if (view.getVisibility() == View.VISIBLE) {
                view.startAnimation(translateAnimation);
            }
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        public static void translateOutToLeft(View view) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0, -view.getWidth(), 0, 0);
            translateAnimation.setDuration(300);

            if (view.getVisibility() == View.INVISIBLE) {
                view.startAnimation(translateAnimation);
            }
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        public static void stretch(View view, float width, View... views) {
            ValueAnimator animator = getValueAnimator(true, 300, null, progress -> {
                float[] a = UtilsAudioPlayer.getSlopeAndIntercept(0, 0.85f, 1, 1);
                float p = a[0] * progress + a[1];
                view.setScaleX(p);
                view.setTranslationX((1 - p) * width / 2.0f);
                view.setAlpha(progress * progress);
            });
            ValueAnimator animatorIV = getValueAnimator(true, 600, new DecelerateInterpolator(), progress -> {
                for (int i = 0; i < views.length; i++) {
                    views[i].setTranslationX(TrigonometricAnimation.calculateTrigonometricFunction(+5, -9, 0, progress, 2));
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.play(animator).before(animatorIV);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
            });
            set.start();
        }

        /**
         * @param towards:
         * @param ratio          width ratio in case Towards= Start or End
         * @param afterDuration: if!=0 then dont start animation immediatly
         * @param duration
         * @param totalDuration
         * @param views
         */
        public static void viewsEntry(boolean in, Towards towards, float ratio, boolean progressiveResilience, float resilience, long afterDuration, long duration, long totalDuration, final View... views) {
            if (views != null && views.length == 0) return;

            //this may be useful one day
            boolean b = true;

            int totalViewsWithoutChildren = views.length;
            for (View view : views) {
                if (isThisViewAChild(view, views)) {
                    totalViewsWithoutChildren--;
                }
            }
            int[] dim = getScreenWidthHeight(getActivity(views[0].getContext()));
            int width = dim[0];
            int height = dim[1];

            int counter = -1;
            for (View view : views) {
                if (isThisViewAChild(view, views)) continue;
                counter++;
                int finalCounter = counter;
                float[] a = UtilsAudioPlayer.getSlopeAndIntercept(0, resilience, totalViewsWithoutChildren, 1);
                new Handler().postDelayed(() -> {
                    view.setVisibility(View.VISIBLE);
                    ValueAnimator animator = getValueAnimator(in, duration, new DecelerateInterpolator(progressiveResilience ? a[0] * finalCounter + a[1] : resilience), progress -> {
                        view.setAlpha(b ? progress : (1 - progress));
                        switch (towards) {
                            case START:
                                view.setTranslationX(((b ? 1 : 0) - progress) * width * ratio);
                                break;
                            case END:
                                view.setTranslationX((progress - (b ? 1 : 0)) * width * ratio);
                                break;
                            case TOP:
                                view.setTranslationY(((b ? 1 : 0) - progress) * height * ratio);
                                break;
                            case BOTTOM:
                                view.setTranslationY((progress - (b ? 1 : 0)) * height * ratio);
                                break;
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            View[] children = selectChildren(view, views);
                            for (int j = 0; j < children.length; j++) {
                                children[j].setVisibility(View.VISIBLE);
                                int finalJ = j;
                                ValueAnimator animatorIV = getValueAnimator(in, 600, new DecelerateInterpolator(), progress -> {
                                    children[finalJ].setTranslationX(TrigonometricAnimation.calculateTrigonometricFunction(-5, +9, 0, progress, 2));
                                });
                                animatorIV.start();
                            }
                        }
                    });
                    animator.start();
                }, (totalViewsWithoutChildren != 1 ? (counter * (totalDuration - duration) / (totalViewsWithoutChildren - 1)) : 0) + afterDuration);
            }
        }



        private static View[] selectChildren(View parent, View[] views) {
            List<View> children = new ArrayList<>();
            if (!(parent instanceof ViewGroup)) return new View[]{};
            else {
                for (int i = 0; i < views.length; i++) {
                    if (((ViewGroup) parent).indexOfChild(views[i]) != -1)
                        children.add(views[i]);
                }
            }
            android.util.Log.d(CustomAnimations.class.getSimpleName(), "ahmed selectChildren: children.size(): " + children.size());
            return children.size() == 0 ? new View[]{} : children.toArray(new View[0]);
        }

        /**
         * returns whether or not the specified view is a child of on of the views passed as array
         *
         * @param view
         * @param views
         * @return
         */
        private static boolean isThisViewAChild(View view, View[] views) {
            for (View viewItem : views) {
                if (viewItem instanceof ViewGroup) {
                    if (((ViewGroup) viewItem).indexOfChild(view) != -1) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static void shotTranslateView(View view, Point startPoint, Point endPoint, float progress) {
            float[] abX = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.x, 1, endPoint.x);
            float[] abY = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.y, 1, endPoint.y);
            view.setX(abX[0] * progress + abX[1] - view.getWidth() / 2f);
            view.setY(abY[0] * progress + abY[1] - view.getHeight() / 2f);
        }

        public static void shotTranslateView(View view, Pair<Float, Float> dim, Point startPoint, Point endPoint, float progress) {
            float[] abX = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.x, 1, endPoint.x);
            float[] abY = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.y, 1, endPoint.y);
            view.setX(abX[0] * progress + abX[1] - dim.first / 2);
            view.setY(abY[0] * progress + abY[1] - dim.second / 2);
        }

        public static void shotScaleTranslateView(View view, Pair<Float, Float> startDim, Pair<Float, Float> endDim, Point startPoint, Point endPoint, float progress) {
            float[] abX = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.x, 1, endPoint.x);
            float[] abY = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.y, 1, endPoint.y);

            float[] abW = UtilsAudioPlayer.getSlopeAndIntercept(0, startDim.first, 1, endDim.first);
            float[] abH = UtilsAudioPlayer.getSlopeAndIntercept(0, startDim.second, 1, endDim.second);

            float lineYW = abW[0] * progress + abW[1];
            float lineYH = abH[0] * progress + abH[1];
            view.getLayoutParams().width = (int) lineYW;
            view.getLayoutParams().height = (int) lineYH;

            view.setX(abX[0] * progress + abX[1] - lineYW / 2);
            view.setY(abY[0] * progress + abY[1] - lineYH / 2);
        }

        public static void shotFadeView(View view, float startPoint, float endPoint, float progress) {
            float[] abX = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint, 1, endPoint);
            view.setAlpha(abX[0] * progress + abX[1]);
        }

        public static void shotScaleView(View view, Pair<Float, Float> startPoint, Pair<Float, Float> endPoint, float progress) {
            float[] abX = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.first, 1, endPoint.first);
            float[] abY = UtilsAudioPlayer.getSlopeAndIntercept(0, startPoint.second, 1, endPoint.second);
            view.getLayoutParams().width = (int) (abX[0] * progress + abX[1]);
            view.getLayoutParams().height = (int) (abY[0] * progress + abY[1]);
        }

        public interface OnAnimationListener {
            void onAnimationStart();

            void onAnimationEnd();
        }
    }
    public static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1f - ratio;

        int a = (int) ((Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio));
        int r = (int) ((Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio));
        int g = (int) ((Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio));
        int b = (int) ((Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio));
        return getIntFromColor(a, r, g, b);
    }
    public static class Dates {

        public static long getRemainingTimeInMs(String stringEndDate, String format) throws ParseException {
            SimpleDateFormat sdfStart = new SimpleDateFormat(format);
            SimpleDateFormat sdfEnd = new SimpleDateFormat(format);
            Date startDate = sdfStart.parse(getCurrentDate(format));
            Date endDate = sdfEnd.parse(stringEndDate);
//        Date endDate = sdfEnd.parse("2020-11-10 23:57:06");
            return Dates.getDifferenceBetweenTwoDatesInMs(startDate, endDate);

        }

        public static String getCurrentDate(String format) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.format(c);
        }


        public static long[] getDifferenceBetweenTwoDatesAsArray(Date startDate, Date endDate) {

            //1 minute = 60 seconds
            //1 hour = 60 x 60 = 3600
            //1 day = 3600 x 24 = 86400
            //milliseconds
            long different = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
            return new long[]{elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
        }

        public static long getDifferenceBetweenTwoDatesInMs(Date startDate, Date endDate) {

            //milliseconds
            return endDate.getTime() - startDate.getTime();

        }

        public static String formatMilliseconds(final long msInput, String locale) {
            long ms = msInput;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = ms / daysInMilli;
            ms = ms % daysInMilli;

            long elapsedHours = ms / hoursInMilli;
            ms = ms % hoursInMilli;

            long elapsedMinutes = ms / minutesInMilli;
            ms = ms % minutesInMilli;

            long elapsedSeconds = ms / secondsInMilli;

            StringBuilder formattedRemainingTime = new StringBuilder("");
            if (elapsedDays != 0) {
                formattedRemainingTime.append(elapsedDays).append(locale.equals("ar") ? " يوم" + " " : " day ");
            }
            formattedRemainingTime.append(to2Digits(elapsedHours)).append(":");
            formattedRemainingTime.append(to2Digits(elapsedMinutes)).append(":");
            formattedRemainingTime.append(to2Digits(elapsedSeconds));

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH':'mm':'ss");
//            String toDisplay = elapsedDays + "j " + simpleDateFormat.format(msInput);
//            if (locale.equals("ar")) UtilsAudioPlayer.replaceArabicNumbers(toDisplay);

            return UtilsAudioPlayer.replaceArabicNumbers(String.valueOf(formattedRemainingTime));

        }

        private static String to2Digits(final String val) {
            if (val.length() == 1)
                return "0" + val;
            else if (val.length() == 2)
                return val;
            else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    android.util.Log.d(TAG, "to2Digits: to2Digits can't get String as parameter with length higher than 2");
                    e.printStackTrace();
                }
            }
            return "xx";
        }

        private static String to2Digits(final long val) {
            if (val < 10)
                return "0" + val;
            else if (10 <= val && val < 100)
                return String.valueOf(val);
            else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    android.util.Log.d(TAG, "to2Digits: to2Digits can't get String as parameter with length higher than 2");
                    e.printStackTrace();
                }
            }
            return "xx";
        }
    }

    public static class FilesManager {

        /**
         * @param image
         * @param storageDir
         * @param fileName
         * @return savedFilePath
         */
        public static void saveFile(Bitmap image, File storageDir, String fileName) {
            android.util.Log.d(TAG, "saveFile() called with: image = [" + image + "], storageDir = [" + storageDir + "], fileName = [" + fileName + "]");

//            try {
//                Log.d(TAG, "saveFile: saving image: "+new File(storageDir,fileName).getPath());
//                Log.d(TAG, "saveFile: isSuccess: "+AndroidBmpUtil.save( image, storageDir,fileName));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//        String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";
            boolean success = true;
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }
            android.util.Log.d(TAG, "saveImage: success: " + success);
            if (success) {
                File imageFile = new File(storageDir, fileName);
//                savedFilePath = imageFile.getAbsolutePath();

                try {
                    OutputStream fOut = new FileOutputStream(imageFile);
                    image.setHasAlpha(true);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        public static String saveImage(Context context, Bitmap image, File storageDir, String fileName) {
            String savedImagePath = null;

//        String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";

            boolean success = true;
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }
            android.util.Log.d(TAG, "saveImage: success: " + success);
            if (success) {

                File imageFile = new File(storageDir, fileName);
                savedImagePath = imageFile.getAbsolutePath();
                try {
                    OutputStream fOut = new FileOutputStream(imageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Add the image to the system gallery
                galleryAddPic(context, savedImagePath);
            }
            return savedImagePath;
        }

        public static void galleryAddPic(Context context, String imagePath) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(imagePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }

    public static class TrigonometricAnimation {

        public static float calculateTrigonometricFunction(float progress) {
            float min = 0.99f;
            float max = 1.05f;
            float middle = 1.0f;
            float pente = (float) ((min - max) / Math.PI);
            float b = (float) (max - middle - Math.PI * pente / 2);
            return (float) ((pente * progress + b) * Math.sin(2 * Math.PI * progress) + middle);
        }

        public static float calculateTrigonometricFunction(float min, float max, float middle, float progress) {
            float pente = (float) ((min - max) / Math.PI);
            float b = (float) (max - middle - Math.PI * pente / 2);
            return (float) ((pente * progress + b) * Math.sin(2 * Math.PI * progress) + middle);
        }

        public static float calculateTrigonometricFunction(float min, float max, float middle, float progress, int nodes) {
            float pente = (float) ((min - max) / Math.PI);
            float b = (float) (max - middle - Math.PI * pente / 2);
            return (float) ((pente * progress + b) * Math.sin(nodes * Math.PI * progress) + middle);
        }


    }

    public static class Log {
        public static int d(String tag, String msg) {
            System.out.println("DEBUG: " + tag + ": " + msg);
            return 0;
        }

        public static int i(String tag, String msg) {
            System.out.println("INFO: " + tag + ": " + msg);
            return 0;
        }

        public static int w(String tag, String msg) {
            System.out.println("WARN: " + tag + ": " + msg);
            return 0;
        }

        public static int e(String tag, String msg) {
            System.out.println("ERROR: " + tag + ": " + msg);
            return 0;
        }

        // add other methods if required...
    }

    private static class USER_INFO {
    }

    public static class InternalStorage {

        private static final int READ_BLOCK_SIZE = 100;
        private static final String TAG = "InternalStorage";

        // write text to file
        public static void write(Context context, String filename, String text, boolean appendMode, boolean unique) {
            if (appendMode) {
                if (unique) {
                    String[] strings = InternalStorage.readFullInternalStorage(context, filename).split("\n");
                    for (String s : strings) {
                        if (s.equals(text)) {
                            Log.d(TAG, "write: must not be written (1)");
                            return;
                        }
                    }
                }
            }

            Log.d(TAG, "write: this statment must not be reached if (1) is showed");

            // add-write text into file
            try {
                FileOutputStream fileout = context.openFileOutput(filename, (appendMode ? Context.MODE_APPEND : 0) | MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write(text);
                outputWriter.write("\n");
                outputWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void clearFile(Context context, String filename) {
            try {
                FileOutputStream fileout = context.openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write("");
                outputWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Read text from file
        public static String readFullInternalStorage(Context context, String filename) {
            //reading text from file
            try {
                FileInputStream fileIn = context.openFileInput(filename);
                InputStreamReader InputRead = new InputStreamReader(fileIn);

                char[] inputBuffer = new char[READ_BLOCK_SIZE];
                String s = "";
                int charRead;

                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                InputRead.close();
                return s;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public class Timer {
        CountDownTimer cdt;

        public void startTimer(long duration, OnTimerListener onTimerListener) {
            stopTimer();
            cdt = new CountDownTimer(duration, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (onTimerListener != null) {
                        onTimerListener.onTick(millisUntilFinished);
                    }
                }

                public void onFinish() {
                    stopTimer();
                    if (onTimerListener != null) {
                        onTimerListener.onFinish();
                    }
                }


            };
            cdt.start();
        }

        public void stopTimer() {
            if (cdt != null)
                cdt.cancel();
        }
    }


}
