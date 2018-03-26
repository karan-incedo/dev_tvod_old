package com.viewlift.mobile.imageutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.net.Uri;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viewlift.views.utilities.ImageLoader;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by viewlift on 11/6/17.
 */

public class FrescoImageLoader implements ImageLoader {
    private GradientPostProcessor gradientPostProcessor;

    public FrescoImageLoader(Context context) {
        if (!Fresco.hasBeenInitialized()) {
            Set<RequestListener> requestListeners = new HashSet<>();
            requestListeners.add(new RequestLoggingListener());
            ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                    // other setters
                    .setDownsampleEnabled(true)
                    .setRequestListeners(requestListeners)
                    .build();
            Fresco.initialize(context, config);
//            FLog.setMinimumLoggingLevel(FLog.VERBOSE);

            Fresco.initialize(context, config);
        }

        if (this.gradientPostProcessor == null) {
            this.gradientPostProcessor = new GradientPostProcessor();
        }
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }

    @Override
    public void loadImage(ImageView view, String url, ImageLoader.ScaleType scaleType) {
        if (view instanceof SimpleDraweeView) {
            switch (scaleType) {
                case CENTER:
                    ((SimpleDraweeView) view).getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

                    break;
                case START:
                    ((SimpleDraweeView) view).getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_START);
                    break;
                case END:
                    ((SimpleDraweeView) view).getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_BOTTOM_START);
                    break;
                default:
            }

            ((SimpleDraweeView) view).setImageURI(url);
        }
    }

    @Override
    public void loadImageWithLinearGradient(ImageView view,
                                            String url,
                                            int imageWidth,
                                            int imageHeight) {
        imageWidth = (int) ((float) imageHeight * 16.0f / 9.0f);
        gradientPostProcessor.imageWidth = imageWidth;
        gradientPostProcessor.imageHeight = imageHeight;
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setPostprocessor(gradientPostProcessor)
                .build();

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        ((SimpleDraweeView) view).getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        ((SimpleDraweeView) view).setController(draweeController);
        view.getLayoutParams().width = imageWidth;
        view.getLayoutParams().height = imageHeight;
        ((FrameLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER;
    }

    private static class GradientPostProcessor extends BasePostprocessor {
        int imageWidth;
        int imageHeight;

        @Override
        public void process(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            boolean scaleImageUp = false;

            if (width < imageWidth &&
                    height < imageHeight) {
                scaleImageUp = true;
                float widthToHeightRatio =
                        (float) width / (float) height;
                width = (int) (imageHeight * widthToHeightRatio);
                height = imageHeight;
            }

            Canvas canvas = new Canvas(bitmap);
            if (!scaleImageUp) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }

            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                    0,
                    0,
                    height,
                    0xFFFFFFFF,
                    0xFF000000,
                    Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawRect(0, 0, width, height, paint);
            paint = null;
        }
    }
}
