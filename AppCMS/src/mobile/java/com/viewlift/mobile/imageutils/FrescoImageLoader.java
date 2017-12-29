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
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viewlift.views.utilities.ImageLoader;

/**
 * Created by viewlift on 11/6/17.
 */

public class FrescoImageLoader implements ImageLoader {
    private GradientPostProcessor gradientPostProcessor;

    public FrescoImageLoader() {
        this.gradientPostProcessor = new GradientPostProcessor();
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }

    @Override
    public void loadImage(ImageView view, String url) {
        if (view instanceof SimpleDraweeView) {
            ((SimpleDraweeView) view).setImageURI(url);
        }
    }

    @Override
    public void loadImageWithLinearGradient(ImageView view,
                                            String url,
                                            int imageWidth,
                                            int imageHeight) {
        gradientPostProcessor.imageWidth = imageWidth;
        gradientPostProcessor.imageHeight = imageHeight;
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setPostprocessor(gradientPostProcessor)
                .build();

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        view.getLayoutParams().width = imageWidth;
        view.getLayoutParams().height = imageHeight;
        ((SimpleDraweeView) view).setController(draweeController);
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
