package com.jp.recorderandplayer.recorderview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;

/**
 * @author jpwang
 * @since 6/2/2016
 */
@SuppressLint("ViewConstructor")
@SuppressWarnings("DefaultFileTemplate")
class RecorderViewSmallCircleView extends View {

    float animationPhrase;
    float animationY;

    private RecorderView recordView;
    private Paint fillPaint;
    private Paint pathPaint;

    private Path clipPath = new Path();

    public RecorderViewSmallCircleView(Context context, RecorderView recorderView) {
        super(context);

        this.init(recorderView);
    }

    public void init(RecorderView recorderView) {
        Paint clipPaint = new Paint();
        clipPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        clipPaint.setAntiAlias(true);

        if (!this.isHardwareAccelerated()) {
            this.setLayerType(LAYER_TYPE_SOFTWARE, clipPaint);
        }

        this.recordView = recorderView;

        this.fillPaint = new Paint();
        this.fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);
        this.fillPaint.setColor(this.recordView.waterColor);

        this.pathPaint = new Paint();
        this.pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.pathPaint.setStyle(Paint.Style.FILL);
        this.pathPaint.setAntiAlias(true);
        this.pathPaint.setColor(this.recordView.smallCircleColor);
    }

    void updateFrame() {
        this.invalidate();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() / 2, this.fillPaint);

        canvas.save();
        clipPath.reset();
        clipPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
        canvas.clipPath(clipPath);

        this.drawAnimation(canvas);

        canvas.restore();
    }

    private void drawAnimation(Canvas canvas) {
        canvas.translate(-(this.recordView.bigCircleView.getWidth() - this.getWidth()) / 2,
                -(this.recordView.bigCircleView.getHeight() - this.getHeight()) / 2);

        float halfHeight = this.recordView.bigCircleView.getHeight() / 2;

        canvas.translate(0f, halfHeight);

        PointF start = new PointF();
        Path path = new Path();

        for (int t = 0; t <= RecorderView.RECORD_VIEW_REFER_HEIGHT; ++t) {
            float y = (float) (RecorderView.TFRecordViewWaveAnimationAmplitude * Math.sin(t * RecorderView.TFRecordViewWaveAnimationFrequency + this.recordView.bigCircleView._phrase));

            if (0 == t) {
                path.moveTo(0.0f, halfHeight + (y + this.recordView.bigCircleView._y) * this.recordView.bigCircleView.scaleX);
                start = new PointF(0, y * this.recordView.bigCircleView.scaleX);
            } else {
                path.lineTo(t * this.recordView.bigCircleView.scaleX, halfHeight + (y + this.recordView.bigCircleView._y) * this.recordView.bigCircleView.scaleX);
            }
        }

        path.lineTo(this.recordView.bigCircleView.getWidth(), this.recordView.bigCircleView.getHeight());
        path.lineTo(0, this.recordView.bigCircleView.getHeight());
        path.lineTo(start.x, start.y);

        canvas.drawPath(path, pathPaint);

        canvas.translate(0.0f, -halfHeight);

        canvas.translate((this.recordView.bigCircleView.getWidth() - this.getWidth()) / 2,
                (this.recordView.bigCircleView.getHeight() - this.getHeight()) / 2);
    }
}
