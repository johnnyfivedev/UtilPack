package com.johnnyfivedev.utilpack;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DeviceManager {

    private final long[] DEFAULT_VIBRATE_PATTERN = {1000, 1000};

    private final Context context;


    public DeviceManager(Context context) {
        this.context = context;
    }

    public void showKeyboard(View target) {
        if (target.requestFocus()) {
            InputMethodManager inputMethodManager = getInputMethodManager(context);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public static void hideKeyboardOn(Context context, @Nullable TextView target) {
        if (target != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(target.getWindowToken(), 0);
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } else {
                throw new RuntimeException("InputMethodManager is null, keyboard wasn't closed. Consider to use DeviceManager.hideKeyboardOn() or debug current logic");
            }
        }
    }

    public boolean isDeviceSupportCamera() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

/*
    public void requestPermission(final Activity activity, final String strPermission, final int requestCode, final String rationale) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, strPermission) || rationale == null) {
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, requestCode);
        } else {
            showSimpleMessageDialog(activity, rationale, (dialog, which) -> ActivityCompat.requestPermissions(activity, new String[]{strPermission}, requestCode), (dialog, which) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.onRequestPermissionsResult(0, new String[]{}, new int[]{});
                }
            });
        }
    }

    public void requestPermissions(final Activity activity, String[] permissions, final int requestCode, final String rationale) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]) || rationale == null) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            showSimpleMessageDialog(activity, rationale, (dialog, which) -> ActivityCompat.requestPermissions(activity, permissions, requestCode), (dialog, which) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.onRequestPermissionsResult(0, new String[]{}, new int[]{});
                }
            });
        }
    }
*/


    public boolean isPermissionGranted(String permission, Context context) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns screen vertical side length in px.
     * Bottom software navigation buttons height is subtracted.
     * getSize() method returns value depending on a context (Activity or non-activity);
     */
    public int getScreenVerticalSideSizePx() {
        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Returns screen horizontal side length in px.
     * Bottom software navigation buttons height is subtracted.
     * getSize() method returns value depending on a context (Activity or non-activity);
     */
    public int getScreenHorizontalSideSizePx() {
        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Returns screen width
     * Bottom software navigation buttons height is subtracted.
     */
    public int getScreenPortraitWidthPx() {
        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            return size.x;
        } else {
            //noinspection SuspiciousNameCombination
            return size.y;
        }
    }

    /**
     * Returns screen height
     * Bottom software navigation buttons height is subtracted.
     */
    public int getScreenPortraitHeightPx() {
        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            return size.y;
        } else {
            //noinspection SuspiciousNameCombination
            return size.x;
        }
    }

    /**
     * @return width and height of screen respectively
     */
    public Pair<Integer, Integer> getScreenResolution() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new Pair<>(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public String getScreenResolutionString() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
    }

    public float getDensity() {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public int getStatusBarHeight() {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringUtils.capitalizeFirstChar(model);
        } else {
            return StringUtils.capitalizeFirstChar(manufacturer) + " " + model;
        }
    }

    public DeviceInfo getDeviceInfo() {
        return new DeviceInfo(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")",
                getDeviceName(),
                Build.VERSION.RELEASE,
                getScreenResolutionString());
    }

   /*
    }*/

   /* public boolean isTablet() {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    @Deprecated
    public DeviceType getDeviceType() {
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            return DeviceType.TABLET;
        } else {
            return DeviceType.PHONE;
        }
    }*/

    public int getScreenOrientation() {
        return context.getResources().getConfiguration().orientation;
    }

    public boolean isLandscape() {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // todo set loop https://stackoverflow.com/a/30292557/6325722
    public Ringtone getDefaultRingtone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        return RingtoneManager.getRingtone(context, uri);
    }

    public void vibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(DEFAULT_VIBRATE_PATTERN, 0));
            } else {
                vibrator.vibrate(DEFAULT_VIBRATE_PATTERN, 0);
            }
        } else {
            Log.d(DeviceManager.class.getSimpleName(), "No vibrator available");
        }
    }

    public void stopVibration() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        } else {
            Log.d(DeviceManager.class.getSimpleName(), "No vibrator available");
        }
    }

    public void vibrateOneTime() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{0, 100}, -1);
        } else {
            Log.d(DeviceManager.class.getSimpleName(), "No vibrator available");
        }
    }

    public void copyToClipbord(String label, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    //region ===================== Internal ======================

    private InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


   /* private void showSimpleMessageDialog(Context context,
                                         String message,
                                         DialogInterface.OnClickListener okListener,
                                         DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context, R.style.DialogStyle)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, okListener)
                .setNegativeButton(R.string.common_cancel, cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }*/

    public enum DeviceType {
        PHONE,
        TABLET
    }

    //endregion
}
