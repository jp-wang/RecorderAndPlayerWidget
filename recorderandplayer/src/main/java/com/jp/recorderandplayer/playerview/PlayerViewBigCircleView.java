package com.jp.recorderandplayer.playerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jpwang
 * @since 6/2/2016
 */
@SuppressLint("ViewConstructor")
@SuppressWarnings({"DefaultFileTemplate", "NullableProblems", "Annotator"})
class PlayerViewBigCircleView extends View {
    private PlayerView playerView;

    float _phrase;
    float _y;
    double animationStep;
    float scaleX;

    private Paint trianglePaint;

    private Timer fixedRateTimer;
    private Paint backgroundPaint;
    private Paint fillPaint;
    private Paint pathPaint;
    private Path path = new Path();

    public PlayerViewBigCircleView(Context context, PlayerView recordView) {
        super(context);

        this.init(recordView);
    }

    public void init(PlayerView playerView) {
        this.playerView = playerView;

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setStyle(Paint.Style.FILL);

        if (!this.isHardwareAccelerated()) {
            this.setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);
        }

        this.trianglePaint = new Paint();
        this.trianglePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.trianglePaint.setAntiAlias(true);
        this.trianglePaint.setStyle(Paint.Style.FILL);

        this.fillPaint = new Paint();
        this.fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);

        this.pathPaint = new Paint();
        this.pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.pathPaint.setStyle(Paint.Style.FILL);
        this.pathPaint.setAntiAlias(true);
    }

    public void start() {
        //every 16 milliseconds to draw a frame
        this.animationStep = (PlayerView.PLAYER_VIEW_REFER_HEIGHT / this.playerView.animationTimeIntervalInSeconds * 0.016 / 2);
        this.scaleX = getHeight() / PlayerView.PLAYER_VIEW_REFER_HEIGHT; //scale for reference view

        _phrase = 0;
        _y = getHeight() / this.scaleX;

        this.fillPaint.setColor(this.playerView.waterColor);
        this.pathPaint.setColor(this.playerView.waterColor);

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

    public void stop() {
        this.fixedRateTimer.cancel();
        _phrase = 0;
        _y = 0;

        this.invalidate();
    }

    private void updateFrame() {
        _phrase += this.animationStep;
        _y -= this.animationStep * 2;

        if (_y <= 0) {
            this.stop();
            playerView.stopAnimation();
        }

        this.playerView.smallCircleView.animationPhrase = _phrase;
        this.playerView.smallCircleView.animationY = _y;
        this.playerView.smallCircleView.updateFrame();

        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.backgroundPaint.setColor(this.playerView.isAnimationStarted ? this.playerView.bigCircleColor
                : this.playerView.waterColor);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, this.backgroundPaint);

        canvas.save();
        path.reset();
        path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
        canvas.clipPath(path);

        if (this.playerView.isAnimationStarted) {
            drawAnimation(canvas);
        } else {
            drawStatic(canvas);
        }

        canvas.restore();
    }

    private void drawStatic(Canvas canvas) {
        canvas.save();

        this.trianglePaint.setColor(this.playerView.smallCircleColor);
        Path path = new Path();
        int triangleWidth = (int)(this.playerView.smallCircleWidth * 1.2);
        path.moveTo(getWidth() / 2 - triangleWidth / 3, getHeight() / 2 - triangleWidth / 2);
        path.lineTo(getWidth() / 2 + triangleWidth / 2, getHeight() / 2);
        path.lineTo(getWidth() / 2 - triangleWidth / 3, getHeight() / 2 + triangleWidth / 2);
        path.close();

        canvas.drawPath(path, trianglePaint);

        canvas.restore();
    }

    private void drawAnimation(Canvas canvas) {
        float halfHeight = getHeight() / 2;

        canvas.translate(0f, halfHeight);

        PointF start = new PointF();
        Path path = new Path();

        for (int t = 0; t <= PlayerView.PLAYER_VIEW_REFER_HEIGHT; ++t) {
            float y = (float) (PlayerView.TFRecordViewWaveAnimationAmplitude * Math.sin(t * PlayerView.TFRecordViewWaveAnimationFrequency + _phrase));

            if (0 == t) {
                path.moveTo(0.0f, halfHeight + (y  -  _y) * scaleX);
                start = new PointF(0, y * scaleX);
            } else {
                path.lineTo(t * scaleX, halfHeight + (y - _y) * scaleX);
            }
        }

        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.lineTo(start.x, start.y);

        canvas.drawPath(path, pathPaint);

        canvas.translate(0.0f, -halfHeight);
    }
}
