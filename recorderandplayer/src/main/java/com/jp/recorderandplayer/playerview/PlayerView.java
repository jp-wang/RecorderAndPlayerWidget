package com.jp.recorderandplayer.playerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jp.recorderandplayer.R;

/**
 * @author jpwang
 * @since 6/2/2016
 */
@SuppressWarnings("DefaultFileTemplate")
public class PlayerView extends FrameLayout {
    public interface PlayerViewListener{
        void onPlayerFinished();
    }

    static final float TFRecordViewWaveAnimationAmplitude = 2f;
    static final float TFRecordViewWaveAnimationFrequency = 0.09f;
    static final float PLAYER_VIEW_REFER_HEIGHT = 50;

    int smallCircleWidth;

    int waterColor;
    int bigCircleColor;
    int bigCircleBorderColor;
    int smallCircleColor;
    double animationTimeIntervalInSeconds;

    boolean isAnimationStarted;
    PlayerViewBigCircleView bigCircleView;
    PlayerViewSmallCircleView smallCircleView;

    private PlayerViewListener playerViewListener;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecorderPlayerView);
        try {
            waterColor = a.getColor(0, getResources().getColor(R.color.mainColor));
        } finally {
            a.recycle();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void init() {
        this.smallCircleWidth = dipToPixels(12);

        this.waterColor = getResources().getColor(R.color.mainColor);

        this.bigCircleColor = Color.argb(255, (int) (0.95 * 255), (int) (0.95 * 255), (int) (0.95 * 255));
        this.bigCircleBorderColor = Color.argb(255, (int) (0.95 * 255), (int) (0.95 * 255), (int) (0.95 * 255));
        this.smallCircleColor = Color.WHITE;

        this.bigCircleView = new PlayerViewBigCircleView(this.getContext(), this);
        this.smallCircleView = new PlayerViewSmallCircleView(this.getContext(), this);

        addView(bigCircleView, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

        addView(smallCircleView, 1, new LayoutParams(smallCircleWidth, smallCircleWidth, Gravity.CENTER));

        this.smallCircleView.setVisibility(View.INVISIBLE);
    }

    public void startAnimation(double animationTimeIntervalInSeconds, PlayerViewListener listener) {
        if (isAnimationStarted)
            return;
        isAnimationStarted = true;

        this.playerViewListener = listener;

        this.animationTimeIntervalInSeconds = animationTimeIntervalInSeconds;
        invalidate();

        bigCircleView.start();

        smallCircleView.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        if (!isAnimationStarted)
            return;

        isAnimationStarted = false;

        bigCircleView.stop();

        smallCircleView.setVisibility(View.INVISIBLE);

        if (playerViewListener != null) {
            playerViewListener.onPlayerFinished();
        }
    }

    private int dipToPixels(float dip) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return (int) px;
    }
}
