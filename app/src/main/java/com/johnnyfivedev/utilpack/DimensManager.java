package com.johnnyfivedev.utilpack;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.DimenRes;


public class DimensManager {

    public static int getDimensionInPx(Context context, @DimenRes int dimenResId) {
        return context.getResources().getDimensionPixelSize(dimenResId);
    }

    public static int getDisplayHeightInPixel(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getDisplayWidthInPixel(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpResToPx(Context context, @DimenRes int id) {
        return context.getResources().getDimensionPixelSize(id);
    }

    public static int spToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
