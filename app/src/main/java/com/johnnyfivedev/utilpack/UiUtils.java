package com.johnnyfivedev.utilpack;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import javax.annotation.Nullable;


// todo import necessary classes from Doctor's Handbook and uncomment logic
public class UiUtils {

    public static void hideAllTextInputLayoutErrors(ViewGroup root) {
        LayoutTraverser.build(view -> {
            if (view instanceof TextInputLayout) {
                TextInputLayout textInputLayout = ((TextInputLayout) view);
                if (textInputLayout.isErrorEnabled()) {
                    // setError(null) так же убирает helper text, чего тут не требуется
                    if (textInputLayout.getHelperText() == null) {
                        textInputLayout.setError(null);
                    }
                }
            }
        }).traverse(root);
    }

    public static void setViewHierarchyEnabled(@Nullable View root, boolean enabled) {
        LayoutTraverser.build(view -> view.setEnabled(enabled)).traverse(root);
    }

    public static void smoothScrollToView(ScrollView scrollView, View target) {
        scrollView.post(() -> scrollView.smoothScrollTo(0, target.getTop()));
    }

    public static void smoothScrollToView(NestedScrollView scrollView, View target) {
        scrollView.post(() -> scrollView.smoothScrollTo(0, target.getTop()));
    }

    /**
     * Prevents views from clicking through by intercepting click events
     */
    public static void interceptClicks(View target, boolean soundEffectsEnabled) {
        target.setOnClickListener(v -> {
        });
        target.setSoundEffectsEnabled(soundEffectsEnabled);
    }

    public static boolean isLastItemReached(LinearLayoutManager recycleViewLayoutManager) {
        int visibleItemCount = recycleViewLayoutManager.getChildCount();
        int totalItemCount = recycleViewLayoutManager.getItemCount();
        int firstVisibleItemPosition = recycleViewLayoutManager.findFirstVisibleItemPosition();
        return firstVisibleItemPosition + visibleItemCount >= totalItemCount;
    }

    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    // see https://stackoverflow.com/questions/54983755
    public static void setHtml(HtmlTextView htmlTextView, @Nullable String text) {
        htmlTextView.setHtml(StringUtils.toEmptyIfNull(text));
        htmlTextView.setMovementMethod(null);
    }

    public static void setHtml(HtmlTextView htmlTextView, @Nullable CharSequence charSequence) {
        htmlTextView.setHtml(StringUtils.toEmptyIfNull(charSequence).toString());
        htmlTextView.setMovementMethod(null);
    }

    public static void setClickableHtml(HtmlTextView htmlTextView, @Nullable String text) {
        htmlTextView.setHtml(StringUtils.toEmptyIfNull(text));
    }

    public static void setHtmlWithHttpImageGetter(HtmlTextView htmlTextView, @Nullable String text) {
        CustomHtmpHttpImageGetter htmlHttpImageGetter = new CustomHtmpHttpImageGetter(htmlTextView);
        //htmlHttpImageGetter.enableCompressImage(true, 100);
        htmlTextView.setHtml(StringUtils.toEmptyIfNull(text), htmlHttpImageGetter);
        htmlTextView.setMovementMethod(null);
    }

    public static void setClickableHtmlWithHttpImageGetter(HtmlTextView htmlTextView, @Nullable String text) {
        CustomHtmpHttpImageGetter htmlHttpImageGetter = new CustomHtmpHttpImageGetter(htmlTextView);
        htmlTextView.setHtml(StringUtils.toEmptyIfNull(text), htmlHttpImageGetter);
    }

    // https://stackoverflow.com/a/48596543/6325722
    public static void destroyWebView(WebView webView) {
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.destroy();
            // Do not remove
            // original link should be nullified too
            webView = null;
        }
    }

    public static ColorStateList createEnableColorStateList(Context context,
                                                            @ColorRes int disabledColorId) {
        return createEnableColorStateList(context, disabledColorId, android.R.color.black);
    }

    // https://stackoverflow.com/a/17788095/6325722
    public static ColorStateList createEnableColorStateList(Context context,
                                                            @ColorRes int disabledColorId,
                                                            @ColorRes int defaultColor) {
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{},
        };

        int[] colors = new int[]{
                ContextCompat.getColor(context, disabledColorId),
                ContextCompat.getColor(context, defaultColor)
        };

        return new ColorStateList(states, colors);
    }

    public static void setTextColorStateList(TextView textView,
                                             @ColorRes int disabledColorId,
                                             @ColorRes int defaultColor) {
        textView.setTextColor(createEnableColorStateList(textView.getContext(), disabledColorId, defaultColor));
    }

    public static void setTextColorStateList(TextView textView,
                                             @ColorRes int disabledColorId) {
        textView.setTextColor(createEnableColorStateList(textView.getContext(), disabledColorId));
    }

    public static void setButtonDrawableLeft(Context context, Button target, @DrawableRes int id) {
        target.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, id),
                null, null, null);
    }

    public static void setTextViewDrawableLeft(Context context, TextView target, @DrawableRes int id) {
        target.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, id),
                null, null, null);
    }

    // https://stackoverflow.com/a/17454562/6325722
    public static void showListPopupWindow(ListPopupWindow listPopupWindow) {
        new Handler().post(listPopupWindow::show);
    }

    public static void setButtonWithDrawablesEnabled(Context context,
                                                     @NonNull Button button,
                                                     boolean enabled,
                                                     @ColorRes int drawableEnabledColorId,
                                                     @ColorRes int drawableDisabledColorId) {
        Drawable[] drawables = button.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setColorFilter(context.getResources().getColor(enabled ? drawableEnabledColorId : drawableDisabledColorId),
                        PorterDuff.Mode.SRC_IN);
            }
        }

        button.setEnabled(enabled);
    }

    /**
     * Default LayoutParams will be generated.
     * View will not be attached to root.
     */
    public static View inflate(Context context, @LayoutRes int layoutId) {
        return LayoutInflater.from(context).inflate(layoutId, null, false);
    }

    /**
     * Parent's LayoutParams will be taken.
     * View will not be attached to root.
     */
    public static View inflateWithParentParams(Context context, @LayoutRes int layoutId, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    /**
     * Parent's LayoutParams will be taken.
     * View will be attached to root.
     */
    public static void inflateInParent(Context context, @LayoutRes int layoutId, @NonNull ViewGroup parent) {
        LayoutInflater.from(context).inflate(layoutId, parent, true);
    }

    public static Drawable tintDrawable(Context context, @DrawableRes int drawableId, @ColorRes int colorId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));
        }
        return drawable;
    }

    public static Drawable tintDrawable(Context context, Drawable drawable, @ColorRes int colorId) {
        if (drawable != null) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));
        }
        return drawable;
    }

    //region ===================== SnackBar ======================

    // https://stackoverflow.com/questions/46254786/android-kitkat-snackbar-is-not-in-the-bottom-of-the-screen
    public static void showSnackbar(View target, String message) {
        Snackbar snackbar = Snackbar.make(target, message, Snackbar.LENGTH_SHORT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Handler().postDelayed(snackbar::show, 200);
        } else {
            snackbar.show();
        }
    }

    public static void showSnackbar(View target, @StringRes int resId) {
        Snackbar snackbar = Snackbar.make(target, resId, Snackbar.LENGTH_SHORT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Handler().postDelayed(snackbar::show, 200);
        } else {
            snackbar.show();
        }
    }

    public static void showSnackbar(View target, String message, @StringRes int actionTextId, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(target, message, Snackbar.LENGTH_SHORT).setAction(actionTextId, actionListener);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Handler().postDelayed(snackbar::show, 200);
        } else {
            snackbar.show();
        }
    }

    public static void showSnackbar(View target, @StringRes int resId, @StringRes int actionTextId, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(target, resId, Snackbar.LENGTH_SHORT).setAction(actionTextId, actionListener);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Handler().postDelayed(snackbar::show, 200);
        } else {
            snackbar.show();
        }
    }

    public static void showSnackbarDismissible(View target, @StringRes int resId, @StringRes int actionTextId) {
        Snackbar snackbar = Snackbar.make(target, resId, Snackbar.LENGTH_INDEFINITE).setAction(actionTextId, v -> {
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Handler().postDelayed(snackbar::show, 200);
        } else {
            snackbar.show();
        }
    }

    //endregion

    //region ===================== Toast ======================

    public static void showToastShort(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    //endregion
}