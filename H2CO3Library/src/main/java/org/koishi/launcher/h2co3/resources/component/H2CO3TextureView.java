package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class H2CO3TextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private SurfaceTextureListener surfaceTextureListener;
    private ExecutorService renderExecutor;
    private Surface surface;
    private Paint textPaint;
    private long lastFrameTime;
    private int frameCount;

    public H2CO3TextureView(Context context) {
        super(context);
        init();
    }

    public H2CO3TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public H2CO3TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        renderExecutor = Executors.newSingleThreadExecutor();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        lastFrameTime = System.currentTimeMillis();
        frameCount = 0;
    }

    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        this.surfaceTextureListener = listener;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
        if (surfaceTextureListener != null) {
            surfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (surfaceTextureListener != null) {
            surfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (this.surface != null) {
            this.surface.release();
            this.surface = null;
        }
        if (renderExecutor != null) {
            renderExecutor.shutdown();
        }
        if (surfaceTextureListener != null) {
            return surfaceTextureListener.onSurfaceTextureDestroyed(surface);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (surfaceTextureListener != null) {
            surfaceTextureListener.onSurfaceTextureUpdated(surface);
        }
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= 1000) {
            float fps = frameCount * 1000f / (currentTime - lastFrameTime);
            drawFps(fps);
            lastFrameTime = currentTime;
            frameCount = 0;
        }
    }

    private void drawFps(float fps) {
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawText("FPS: " + String.format("%.2f", fps), 10, 40, textPaint);
            unlockCanvasAndPost(canvas);
        }
    }

    public Surface getSurface() {
        return surface;
    }

    public void renderFrame(Runnable renderTask) {
        renderExecutor.execute(renderTask);
    }

    public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

        void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

        boolean onSurfaceTextureDestroyed(SurfaceTexture surface);

        void onSurfaceTextureUpdated(SurfaceTexture surface);
    }
}