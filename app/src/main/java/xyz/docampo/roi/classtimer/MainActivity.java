package xyz.docampo.roi.classtimer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextClock;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout mMainLayout;
    MainView mMainView;
    TextClock mTextClock;
    ConfigButton mConfigButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainLayout = new ConstraintLayout(this);
/*
        mMainView = new MainView(this);
        mMainView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT ));
        mMainLayout.addView(mMainView);
*/
        mTextClock = new TextClock(this);
        mTextClock.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT ));
        mTextClock.setTextSize(80);
        mTextClock.setTextColor(0xffececec);

        mMainLayout.addView(mTextClock);

        mConfigButton = new ConfigButton(this);
        mMainLayout.addView(mConfigButton);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mMainLayout.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        setContentView(mMainLayout);
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    private class ConfigButton extends View {

        int mGray   = 0xffececec;
        int desiredWidth = 150;
        int desiredHeight = 150;

        public ConfigButton(Context context) {
            super(context);
            setOnClickListener((OnClickListener) context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mGray);
            canvas.drawCircle(60,60,10,paint);
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

    private class MainView extends View {

        Paint mClockPaint;
        int mClockX = 0;
        int mClockY = 0;

        Paint mCirclePaint;
        int mCircleX = 0;
        int mCircleY = 0;

        int mBlue   = 0xff48a7d9;
        int mGreen  = 0xff93c35b;
        int mYellow = 0xfffccb39;
        int mRed    = 0xffed5c53;
        int mGray   = 0xffececec;

        public MainView(Context context) {
            super(context);

            mClockPaint = new Paint();
            mClockPaint.setStyle(Paint.Style.FILL);
            mClockPaint.setColor(mGray);
            mClockPaint.setTextSize(200);
            mClockPaint.setTextAlign(Paint.Align.CENTER);

            mCirclePaint = new Paint();
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setColor(mBlue);
        }

        @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mClockX = w/2;
            mClockY = h/2;
            mCircleX = w/2;
            mCircleY = 2*h;
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawColor(Color.BLACK);
            canvas.drawCircle(mCircleX, mCircleY, 0.8f * mCircleY, mCirclePaint);
            canvas.drawText("10:30", mClockX, mClockY, mClockPaint);

        }
    }

}
