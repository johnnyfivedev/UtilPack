package com.johnnyfivedev.utilpack;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager {

    private final Context context;


    public ResourceManager(Context context) {
        this.context = context;
    }

    public String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    public List<String> getStrings(List<Integer> resIds) {
        List<String> result = new ArrayList<>();
        if (resIds != null) {
            for (Integer resId : resIds) {
                result.add(getString(resId));
            }
        }
        return result;
    }

    public String getString(int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public String getQuantityString(@PluralsRes int resId, int quantity) {
        return context.getResources().getQuantityString(resId, quantity);
    }

    public String getQuantityString(@PluralsRes int resId, int quantity, Object... formatArgs) {
        return context.getResources().getQuantityString(resId, quantity, formatArgs);
    }

    public int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public float getDimen(@DimenRes int resId) {
        return context.getResources().getDimension(resId);
    }

    public String[] getStringArray(@ArrayRes int resId) {
        return context.getResources().getStringArray(resId);
    }
}
