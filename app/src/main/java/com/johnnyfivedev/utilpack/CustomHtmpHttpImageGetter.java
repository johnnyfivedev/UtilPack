package com.johnnyfivedev.utilpack;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;


public class CustomHtmpHttpImageGetter implements Html.ImageGetter {
    TextView container;
    URI baseUri;
    boolean matchParentWidth;

    private boolean compressImage = false;
    private int qualityImage = 50;

    public CustomHtmpHttpImageGetter(TextView textView) {
        this.container = textView;
        this.matchParentWidth = false;
    }

    public CustomHtmpHttpImageGetter(TextView textView, boolean matchParentWidth) {
        this.container = textView;
        this.matchParentWidth = matchParentWidth;
    }


    public CustomHtmpHttpImageGetter(TextView textView, String baseUrl) {
        this.container = textView;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public CustomHtmpHttpImageGetter(TextView textView, String baseUrl, boolean matchParentWidth) {
        this.container = textView;
        this.matchParentWidth = matchParentWidth;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public void enableCompressImage(boolean enable) {
        enableCompressImage(enable, 50);
    }

    public void enableCompressImage(boolean enable, int quality) {
        compressImage = enable;
        qualityImage = quality;
    }

    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable, this, container,
                matchParentWidth, compressImage, qualityImage);

        asyncTask.execute(source);

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable;
    }

    /**
     * Static inner {@link AsyncTask} that keeps a {@link WeakReference} to the {@link UrlDrawable}
     * and {@link HtmlHttpImageGetter}.
     * <p/>
     * This way, if the AsyncTask has a longer life span than the UrlDrawable,
     * we won't leak the UrlDrawable or the HtmlRemoteImageGetter.
     */
    private static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private final WeakReference<UrlDrawable> drawableReference;
        private final WeakReference<CustomHtmpHttpImageGetter> imageGetterReference;
        private final WeakReference<View> containerReference;
        private final WeakReference<Resources> resources;
        private String source;
        private boolean matchParentWidth;
        private float scale;

        private boolean compressImage = false;
        private int qualityImage = 50;

        public ImageGetterAsyncTask(UrlDrawable d, CustomHtmpHttpImageGetter imageGetter, View container,
                                    boolean matchParentWidth, boolean compressImage, int qualityImage) {
            this.drawableReference = new WeakReference<>(d);
            this.imageGetterReference = new WeakReference<>(imageGetter);
            this.containerReference = new WeakReference<>(container);
            this.resources = new WeakReference<>(container.getResources());
            this.matchParentWidth = matchParentWidth;
            this.compressImage = compressImage;
            this.qualityImage = qualityImage;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            source = params[0];

            if (resources.get() != null) {
                if (compressImage) {
                    return fetchCompressedDrawable(resources.get(), source);
                } else {
                    return fetchDrawable(resources.get(), source);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result == null) {
                Log.w(HtmlTextView.TAG, "Drawable result is null! (source: " + source + ")");
                return;
            }
            final UrlDrawable urlDrawable = drawableReference.get();
            if (urlDrawable == null) {
                return;
            }
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, (int) (result.getIntrinsicWidth() * scale), (int) (result.getIntrinsicHeight() * scale));

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = result;

            final CustomHtmpHttpImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return;
            }
            // redraw the image by invalidating the container
            imageGetter.container.invalidate();
            // re-set text to fix images overlapping text
            imageGetter.container.setText(imageGetter.container.getText());
        }

        /**
         * Get the Drawable from URL
         */
        public Drawable fetchDrawable(Resources res, String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = new BitmapDrawable(res, is);
                scale = getScale();
                drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * scale), (int) (drawable.getIntrinsicHeight() * scale));
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Get the compressed image with specific quality from URL
         */
        public Drawable fetchCompressedDrawable(Resources res, String urlString) {
            try {
                InputStream is = fetch(urlString);
                Bitmap original = new BitmapDrawable(res, is).getBitmap();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.PNG, qualityImage, out);
                original.recycle();
                is.close();

                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                out.close();

                scale = getScale();
                BitmapDrawable b = new BitmapDrawable(res, decoded);

                b.setBounds(0, 0, (int) (b.getIntrinsicWidth() * scale), (int) (b.getIntrinsicHeight() * scale));
                return b;
            } catch (Exception e) {
                return null;
            }
        }

        // При скачке Bitmap уменьшается в размере на примерно 1.8
        private float getScale() {
            return 1.8f * DimensManager.getDensity(containerReference.get().getContext());
        }

/*        private float getScale(Bitmap bitmap) {
            View container = containerReference.get();
            if (container == null) {
                return 1f;
            }

            float maxWidth = container.getWidth();
            float originalDrawableWidth = bitmap.getWidth();

            return maxWidth / originalDrawableWidth;
        }

        private float getScale(Drawable drawable) {
            View container = containerReference.get();
            if (!matchParentWidth || container == null) {
                return 1f;
            }

            float maxWidth = container.getWidth();
            float originalDrawableWidth = drawable.getIntrinsicWidth();

            return maxWidth / originalDrawableWidth;
        }*/

        private InputStream fetch(String urlString) throws IOException {
            URL url;
            final CustomHtmpHttpImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return null;
            }
            if (imageGetter.baseUri != null) {
                url = imageGetter.baseUri.resolve(urlString).toURL();
            } else {
                url = URI.create(urlString).toURL();
            }

            return (InputStream) url.getContent();
        }
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
