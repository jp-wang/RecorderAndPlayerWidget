package com.jp.recorderandplayer.recorderview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.jp.recorderandplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jpwang
 * @since 6/2/2016
 */
@SuppressLint("ViewConstructor")
@SuppressWarnings({"DefaultFileTemplate", "NullableProblems", "Annotator"})
class RecorderViewBigCircleView extends View {
    private RecorderView recordView;

    float _phrase;
    float _y;
    double animationStep;
    boolean isFull;
    float scaleX;

    private Paint shadowPaint;
    private Paint imagePaint;

    private Timer fixedRateTimer;
    private Paint fillPaint;
    private Paint pathPaint;

    private Path path = new Path();

    public RecorderViewBigCircleView(Context context, RecorderView recordView) {
        super(context);

        this.init(recordView);
    }

    public void init(RecorderView recordView){
        this.recordView = recordView;

        this.shadowPaint = new Paint();
        this.shadowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.shadowPaint.setAntiAlias(true);
        this.shadowPaint.setColor(getResources().getColor(R.color.lessGrey));

        if (!this.isHardwareAccelerated()) {
            this.setLayerType(LAYER_TYPE_SOFTWARE, this.shadowPaint);
        }

        this.imagePaint = new Paint();
        this.imagePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.imagePaint.setAntiAlias(true);

        this.fillPaint = new Paint();
        this.fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);

        this.pathPaint = new Paint();
        this.pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.pathPaint.setStyle(Paint.Style.FILL);
        this.pathPaint.setAntiAlias(true);
    }

    public void start(){
        //every 16 milliseconds to draw a frame
        this.animationStep = (RecorderView.RECORD_VIEW_REFER_HEIGHT / this.recordView.animationTimeIntervalInSeconds * 0.016 / 2);
        this.scaleX = getHeight() / RecorderView.RECORD_VIEW_REFER_HEIGHT; //scale for reference view

        this.isFull = false;

        this.fillPaint.setColor(this.recordView.waterColor);
        this.pathPaint.setColor(this.recordView.waterColor);

        this.fixedRateTimer = new Timer("RecorderViewBigCircleDrawable-fixedRateTimer");
        this.fixedRateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateFrame();
                    }
                });
            }
        }, 0, 16);

        this.invalidate();
    }

    public void stop(){
        this.fixedRateTimer.cancel();
        _phrase = 0;
        _y = 0;

        this.invalidate();
    }

    private void updateFrame(){
        _phrase += this.animationStep;
        _y -= this.animationStep * 2;

        if ((-_y * scaleX) >= getHeight()){
            this.isFull = true;
            this.stop();
            recordView.stopAnimation();
        }

        this.recordView.smallCircleView.animationPhrase = _phrase;
        this.recordView.smallCircleView.animationY = _y;
        this.recordView.smallCircleView.updateFrame();

        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.shadowPaint.setColor(this.recordView.isAnimationStarted ? this.recordView.bigCircleColor
                : this.recordView.waterColor);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, this.shadowPaint);

        canvas.save();

        path.reset();
        path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
        canvas.clipPath(path);


        if (this.recordView.isAnimationStarted) {
            drawAnimation(canvas);
        }else{
            drawStatic(canvas);
        }

        canvas.restore();
    }

    private void drawStatic(Canvas canvas) {
        canvas.save();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_voice);
        Rect src = new Rect();
        src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dest = new Rect();
        dest.left = getWidth() / 4;
        dest.top = getWidth() / 4;
        dest.right = getWidth() * 3 / 4;
        dest.bottom = getWidth() * 3 / 4;
        canvas.drawBitmap(bitmap, src, dest, this.imagePaint);

        canvas.restore();
    }

    private void drawAnimation(Canvas canvas) {
        if (this.isFull) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, this.fillPaint);
        }else{
            float halfHeight = getHeight() / 2;

            canvas.translate(0f, halfHeight);

            PointF start = new PointF();
            Path path = new Path();

            for (int t = 0; t <= RecorderView.RECORD_VIEW_REFER_HEIGHT; ++t){
                float y = (float)(RecorderView.TFRecordViewWaveAnimationAmplitude * Math.sin(t * RecorderView.TFRecordViewWaveAnimationFrequency + _phrase));

                if (0 == t){
                    path.moveTo(0.0f, halfHeight + (y  + _y) * scaleX );
                    start = new PointF(0, y * scaleX);
                }
                else{
                    path.lineTo(t * scaleX, halfHeight + (y + _y) * scaleX);
                }
            }

            path.lineTo(getWidth(), getHeight());
            path.lineTo(0, getHeight());
            path.lineTo(start.x, start.y);

            canvas.drawPath(path, pathPaint);

            canvas.translate(0.0f, -halfHeight);
        }
    }
}
