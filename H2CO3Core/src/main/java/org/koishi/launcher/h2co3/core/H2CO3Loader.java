package org.koishi.launcher.h2co3.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class H2CO3Loader {

    private static final int HEAD_SIZE = 5000;
    private static final int HEAD_LEFT = 7;
    private static final int HEAD_TOP = 8;
    private static final int HEAD_RIGHT = 17;
    private static final int HEAD_BOTTOM = 16;

    private static final RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static Drawable getHeadDrawable(Context context, String texture) {
        if (context == null || texture == null) {
            throw new IllegalArgumentException("Context or texture is null");
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            return new BitmapDrawable(context.getResources(), headBitmap);
        } catch (Exception | OutOfMemoryError e) {
            throw new RuntimeException("Failed to get head drawable", e);
        }
    }

    public static void getHead(Context context, String texture, ImageView imageView) {
        if (context == null || texture == null || imageView == null) {
            throw new IllegalArgumentException("Context, texture or imageView is null");
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            Glide.with(context)
                    .load(headBitmap)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception | OutOfMemoryError e) {
            throw new RuntimeException("Failed to load head image", e);
        }
    }

    private static Bitmap decodeAndCropHeadBitmap(String texture) {
        byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
        Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        if (skinBitmap == null) {
            throw new RuntimeException("Failed to decode skin bitmap");
        }
        return cropHeadFromSkin(skinBitmap);
    }

    private static Bitmap cropHeadFromSkin(Bitmap skinBitmap) {
        Bitmap headBitmap = Bitmap.createBitmap(HEAD_SIZE, HEAD_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(headBitmap);
        Rect srcRect = new Rect(HEAD_LEFT, HEAD_TOP, HEAD_RIGHT, HEAD_BOTTOM);
        Rect dstRect = new Rect(0, 0, HEAD_SIZE, HEAD_SIZE);
        canvas.drawBitmap(skinBitmap, srcRect, dstRect, null);
        return headBitmap;
    }
}