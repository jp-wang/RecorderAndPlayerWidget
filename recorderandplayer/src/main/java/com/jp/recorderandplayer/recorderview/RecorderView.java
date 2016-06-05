package com.jp.recorderandplayer.recorderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.jp.recorderandplayer.R;

/**
 * @author jpwang
 * @since 6/2/2016
 */
@SuppressWarnings("DefaultFileTemplate")
public class RecorderView extends FrameLayout {
    public interface RecorderViewListener{
        void onRecorderFinished();
    }

    public final static int DEFAULT_RECORDER_TIME_INTERVAL = 10;

    static final float RECORD_VIEW_REFER_HEIGHT = 96;

    static final float TFRecordViewWaveAnimationAmplitude = 2f;
    static final float TFRecordViewWaveAnimationFrequency = 0.09f;

    int smallCircleWidth;

    int waterColor;
    int bigCircleColor;
    int bigCircleBorderColor;
    int smallCircleColor;
    int animationTimeIntervalInSeconds;

    boolean isAnimationStarted;
    RecorderViewBigCircleView bigCircleView;
    RecorderViewSmallCircleView smallCircleView;

    private RecorderViewListener recorderViewListener;

    public RecorderView(Context context) {
        this(context, null);
    }

    public RecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecorderPlayerView);
        try {
            waterColor = a.getColor(0, getResources().getColor(R.color.mainColor));
            this.animationTimeIntervalInSeconds = a.getInt(1, DEFAULT_RECORDER_TIME_INTERVAL);
        } finally {
            a.recycle();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void init() {
        this.smallCircleWidth = dipToPixels(24);
        this.animationTimeIntervalInSeconds = DEFAULT_RECORDER_TIME_INTERVAL; //default as 10 secs
//        this.shadowWidth = dipToPixels(6.6f) * 2;

        this.waterColor = getResources().getColor(R.color.mainColor);

        this.bigCircleColor = Color.argb(255, (int) (0.95 * 255), (int) (0.95 * 255), (int) (0.95 * 255));
        this.bigCircleBorderColor = Color.WHITE;
        this.smallCircleColor = Color.WHITE;

        this.bigCircleView = new RecorderViewBigCircleView(this.getContext(), this);
        this.smallCircleView = new RecorderViewSmallCircleView(this.getContext(), this);

        addView(bigCircleView, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

        addView(smallCircleView, 1, new LayoutParams(smallCircleWidth, smallCircleWidth, Gravity.CENTER));
//        addView(smallCircleView, 1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

        this.smallCircleView.setVisibility(View.INVISIBLE);
    }

    public boolean isAnimationStarted() {
        return this.isAnimationStarted;
    }

    public void startAnimation(int animationTimeIntervalInSeconds, RecorderViewListener listener) {
        this.animationTimeIntervalInSeconds = animationTimeIntervalInSeconds;
        startAnimation(listener);
    }

    public void startAnimation(RecorderViewListener listener) {
        this.recorderViewListener = listener;

        final Animation smallCircleAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.record_view_small_circle_scale);
        final Animation scaleAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.record_view_start_scale);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressWarnings("SuspiciousNameCombination")
            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationStarted = true;
                invalidate();

                bigCircleView.start();

                smallCircleView.setVisibility(View.VISIBLE);
                smallCircleView.startAnimation(smallCircleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(scaleAnimation);
    }

    public void stopAnimation() {
        if (!isAnimationStarted)
            return;

        final Animation scaleAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.record_view_start_scale);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                smallCircleView.clearAnimation();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                smallCircleView.setVisibility(View.INVISIBLE);

                isAnimationStarted = false;
                bigCircleView.stop();

                if (recorderViewListener != null){
                    recorderViewListener.onRecorderFinished();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(scaleAnimation);
    }

    private int dipToPixels(float dip) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return (int) px;
    }
}
