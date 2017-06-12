package xyz.docampo.roi.classtimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextClock;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{

    ConstraintLayout mMainLayout;
    GrowingCircle mGrowingCircle;
    TextClock mTextClock;
    ConfigButton mConfigButton;

    long mCircleUpdateDelay = 1000;
    Handler mHandler;
    Runnable mRunnable;

    Calendar mCalendar;

    int mNumClases = 5;
    boolean[] mClassEnabled;
    long[] mClassStart;
    long[] mClassEnd;

    Paint mCirclePaint;
    int mCircleX = 0;
    int mCircleY = 0;
    float mCircleSize = -1f;
    float mCircleRadius = 0f;

    int mBlue   = 0xff48a7d9;
    int mGreen  = 0xff93c35b;
    int mYellow = 0xfffccb39;
    int mRed    = 0xffed5c53;
    int mGray   = 0xffececec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainLayout = new ConstraintLayout(this);
        mMainLayout.setId(R.id.main_layout);

        mGrowingCircle = new GrowingCircle(this);
        mGrowingCircle.setId(R.id.growing_circle);
        mMainLayout.addView(mGrowingCircle);

        mTextClock = new TextClock(this);
        mTextClock.setId(R.id.text_clock);
        mMainLayout.addView(mTextClock);

        mConfigButton = new ConfigButton(this);
        mConfigButton.setId(R.id.config_button);
        mMainLayout.addView(mConfigButton);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMainLayout.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mMainLayout);
        int[] sides = { ConstraintSet.TOP, ConstraintSet.BOTTOM, ConstraintSet.LEFT, ConstraintSet.RIGHT };
        int[] widgets = { mGrowingCircle.getId(), mTextClock.getId() };
        for (int side : sides) {
            for (int widget : widgets) {
                constraintSet.connect(widget, side, mMainLayout.getId(), side, 30);
            }
        }
        constraintSet.applyTo(mMainLayout);

        mTextClock.setTextSize(100);
        mTextClock.setFormat12Hour("h:mm");
        mTextClock.setFormat24Hour("h:mm");
        mTextClock.setTextColor(mGray);

        setContentView(mMainLayout);

        mCalendar = new GregorianCalendar();
        mClassEnabled = new boolean[mNumClases];
        mClassStart = new long[mNumClases];
        mClassEnd = new long[mNumClases];

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mRed);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                updateCircle();
                mHandler.postDelayed(mRunnable, mCircleUpdateDelay);
            }
        };

        updatePreferences();
        mHandler.post(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
        mHandler.post(mRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    private void updatePreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i=0; i<mNumClases; ++i) {
            mClassEnabled[i] = prefs.getBoolean(String.format("class_enabled_%d",i+1), false);
            mClassStart[i] = prefs.getLong(String.format("class_start_%d",i+1), TimePreference.DEFAULT_TIME);
            mClassEnd[i] = prefs.getLong(String.format("class_end_%d",i+1), TimePreference.DEFAULT_TIME);
        }
    }

    private void updateCircle() {
        Calendar now = new GregorianCalendar();
        float currentTime = 0f
                + now.get(Calendar.HOUR_OF_DAY) * 60
                + now.get(Calendar.MINUTE)
                + now.get(Calendar.SECOND) / 60f;
        for (int i=0; i<mNumClases; ++i) {
            if (mClassEnabled[i]) {
                if (currentTime >= (mClassStart[i] - 10)
                        && currentTime <= mClassEnd[i] + 10) {
                    mCircleSize = (currentTime - mClassStart[i]) * 1f / (mClassEnd[i] - mClassStart[i]);
                    mCircleRadius = (1f - mCircleSize) * 0.54f * mCircleY + mCircleSize * 1.025f * mCircleY;
                    if (currentTime >= mClassEnd[i] - 3)
                        mCirclePaint.setColor(mRed);
                    else if (currentTime >= mClassEnd[i] - 10)
                        mCirclePaint.setColor(mYellow);
                    else if (mCircleSize >= .5f)
                        mCirclePaint.setColor(mGreen);
                    else
                        mCirclePaint.setColor(mBlue);
                    mGrowingCircle.invalidate();
                    return;
                }

            }
        }
        if (mCircleSize >= -.5f) {
            mCircleSize = -1f;
            mCircleRadius = 0;
            mCirclePaint.setColor(Color.BLACK);
            mGrowingCircle.invalidate();
        }
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    private class ConfigButton extends View {

        int desiredWidth = 150;
        int desiredHeight = 150;
        Paint mPaint;

        public ConfigButton(Context context) {
            super(context);
            setOnClickListener((OnClickListener) context);
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mGray);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(60, 60, 10, mPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int width;
            int height;

            if (widthMode == MeasureSpec.EXACTLY) {
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(desiredWidth, widthSize);
            } else {
                width = desiredWidth;
            }

            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desiredHeight, heightSize);
            } else {
                height = desiredHeight;
            }

            setMeasuredDimension(width, height);
        }
    }

    private class GrowingCircle extends View {

        public GrowingCircle(Context context) {
            super(context);
        }

        @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCircleX = w/2;
            mCircleY = 2*h;
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawColor(Color.BLACK);
            if (mCircleSize >= -.5f)
                canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePaint);
        }
    }

}
