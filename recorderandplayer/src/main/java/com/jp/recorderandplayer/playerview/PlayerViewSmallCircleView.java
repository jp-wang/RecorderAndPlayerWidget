package com.jp.recorderandplayer.playerview;

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
class PlayerViewSmallCircleView extends View {

    float animationPhrase;
    float animationY;

    private PlayerView playerView;
    private Paint fillPaint;
    private Paint pathPaint;

    public PlayerViewSmallCircleView(Context context, PlayerView playerView) {
        super(context);

        this.init(playerView);
    }

    public void init(PlayerView playerView) {
        this.playerView = playerView;

        this.fillPaint = new Paint();
        this.fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);
        this.fillPaint.setColor(this.playerView.waterColor);

        this.pathPaint = new Paint();
        this.pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.pathPaint.setStyle(Paint.Style.FILL);
        this.pathPaint.setAntiAlias(true);
        this.pathPaint.setColor(this.playerView.smallCircleColor);
    }

    void updateFrame() {
        this.invalidate();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), this.fillPaint);

        canvas.save();

        this.drawAnimation(canvas);

        canvas.restore();
    }

    private void drawAnimation(Canvas canvas) {
        canvas.translate(-(this.playerView.bigCircleView.getWidth() - this.getWidth()) / 2,
                -(this.playerView.bigCircleView.getHeight() - this.getHeight()) / 2);

        float halfHeight = this.playerView.bigCircleView.getHeight() / 2;

        canvas.translate(0f, halfHeight);

        PointF start = new PointF();
        Path path = new Path();

        for (int t = 0; t <= PlayerView.PLAYER_VIEW_REFER_HEIGHT; ++t) {
            float y = (float) (PlayerView.TFRecordViewWaveAnimationAmplitude * Math.sin(t * PlayerView.TFRecordViewWaveAnimationFrequency + this.playerView.bigCircleView._phrase));

            if (0 == t) {
                path.moveTo(0.0f, halfHeight + (y - this.playerView.bigCircleView._y) * this.playerView.bigCircleView.scaleX);
                start = new PointF(0, y * this.playerView.bigCircleView.scaleX);
            } else {
                path.lineTo(t * this.playerView.bigCircleView.scaleX, halfHeight + (y - this.playerView.bigCircleView._y) * this.playerView.bigCircleView.scaleX);
            }
        }

        path.lineTo(this.playerView.bigCircleView.getWidth(), this.playerView.bigCircleView.getHeight());
        path.lineTo(0, this.playerView.bigCircleView.getHeight());
        path.lineTo(start.x, start.y);

        canvas.drawPath(path, pathPaint);

        canvas.translate(0.0f, -halfHeight);

        canvas.translate((this.playerView.bigCircleView.getWidth() - this.getWidth()) / 2,
                (this.playerView.bigCircleView.getHeight() - this.getHeight()) / 2);
    }
}
