package com.example.adarsh.studio7.CustomViews;

/**
 * Created by adarsh on 16/08/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.adarsh.studio7.R;

public class CircularSeekBar extends View {

    private Context mContext;
    private OnSeekChangeListener mListener;
    private Paint circleColor;
    private Paint innerColor;
    private Paint circleRing;
    private int angle = 0;
    private int startAngle = 270;
    private int barWidth = 5;
    private int width;
    private int height;
    private int maxProgress = 100;
    private int progress;
    private int progressPercent;
    private float innerRadius;
    private float outerRadius;
    private float cx;
    private float cy;
    private float left;
    private float right;
    private float top;
    private float bottom;
    private float dx;
    private float dy;
    private float startPointX;
    private float startPointY;
    private float markPointX;
    private float markPointY;
    private float adjustmentFactor = 100;
    private Bitmap progressMark;
    private Bitmap progressMarkPressed;

    private boolean IS_PRESSED = false;
    private boolean CALLED_FROM_ANGLE = false;
    private boolean SHOW_SEEKBAR = true;

    private RectF rect = new RectF();

    {
        mListener = new OnSeekChangeListener() {

            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {

            }
        };

        circleColor = new Paint();
        innerColor = new Paint();
        circleRing = new Paint();

        circleColor.setColor(Color.parseColor("#ff33b5e5")); // Set default
        innerColor.setColor(Color.BLACK); // Set default background color to
        circleRing.setColor(Color.GRAY);// Set default background color to Gray

        circleColor.setAntiAlias(true);
        innerColor.setAntiAlias(true);
        circleRing.setAntiAlias(true);

        circleColor.setStrokeWidth(5);
        innerColor.setStrokeWidth(5);
        circleRing.setStrokeWidth(5);

        circleColor.setStyle(Paint.Style.FILL);
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDrawable();
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDrawable();
    }

    public CircularSeekBar(Context context) {
        super(context);
        mContext = context;
        initDrawable();
    }

    public void initDrawable() {
        progressMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scrubber_control_normal_holo);
        progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.scrubber_control_pressed_holo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getWidth(); // Get View Width
        height = getHeight();// Get View Height

        int size = (width > height) ? height : width; // Choose the smaller

        cx = width / 2; // Center X for circle
        cy = height / 2; // Center Y for circle
        outerRadius = size / 2; // Radius of the outer circle

        innerRadius = outerRadius - barWidth; // Radius of the inner circle

        left = cx - outerRadius; // Calculate left bound of our rect
        right = cx + outerRadius;// Calculate right bound of our rect
        top = cy - outerRadius;// Calculate top bound of our rect
        bottom = cy + outerRadius;// Calculate bottom bound of our rect

        startPointX = cx; // 12 O'clock X coordinate
        startPointY = cy - outerRadius;// 12 O'clock Y coordinate
        markPointX = startPointX;// Initial locatino of the marker X coordinate
        markPointY = startPointY;// Initial locatino of the marker Y coordinate

        rect.set(left, top, right, bottom); // assign size to rect
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(cx, cy, outerRadius, circleRing);
        canvas.drawArc(rect, startAngle, angle, true, circleColor);
        canvas.drawCircle(cx, cy, innerRadius, innerColor);
        if(SHOW_SEEKBAR){
            dx = getXFromAngle();
            dy = getYFromAngle();
            drawMarkerAtProgress(canvas);
        }
        super.onDraw(canvas);
    }

    public void drawMarkerAtProgress(Canvas canvas) {
        if (IS_PRESSED) {
            canvas.drawBitmap(progressMarkPressed, dx, dy, null);
        } else {
            canvas.drawBitmap(progressMark, dx, dy, null);
        }
    }

    public float getXFromAngle() {
        int size1 = progressMark.getWidth();
        int size2 = progressMarkPressed.getWidth();
        int adjust = (size1 > size2) ? size1 : size2;
        float x = markPointX - (adjust / 2);
        return x;
    }

    public float getYFromAngle() {
        int size1 = progressMark.getHeight();
        int size2 = progressMarkPressed.getHeight();
        int adjust = (size1 > size2) ? size1 : size2;
        float y = markPointY - (adjust / 2);
        return y;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
        float donePercent = (((float) this.angle) / 360) * 100;
        float progress = (donePercent / 100) * getMaxProgress();
        setProgressPercent(Math.round(donePercent));
        CALLED_FROM_ANGLE = true;
        setProgress(Math.round(progress));
    }

    public void setSeekBarChangeListener(OnSeekChangeListener listener) {
        mListener = listener;
    }

    public OnSeekChangeListener getSeekBarChangeListener() {
        return mListener;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public interface OnSeekChangeListener {
        public void onProgressChange(CircularSeekBar view, int newProgress);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            if (!CALLED_FROM_ANGLE) {
                int newPercent = (this.progress * 100) / this.maxProgress;
                int newAngle = (newPercent * 360) / 100 ;
                this.setAngle(newAngle);
                this.setProgressPercent(newPercent);
            }
            mListener.onProgressChange(this, this.getProgress());
            CALLED_FROM_ANGLE = false;
        }
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public void setRingBackgroundColor(int color) {
        circleRing.setColor(color);
    }

    public void setBackGroundColor(int color) {
        innerColor.setColor(color);
    }

    public void setProgressColor(int color) {
        circleColor.setColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean up = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moved(x, y, up);
                break;
            case MotionEvent.ACTION_MOVE:
                moved(x, y, up);
                break;
            case MotionEvent.ACTION_UP:
                up = true;
                moved(x, y, up);
                break;
        }
        return true;
    }

    private void moved(float x, float y, boolean up) {
        float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
        if (distance < outerRadius + adjustmentFactor && distance > innerRadius - adjustmentFactor && !up) {
            IS_PRESSED = true;

            markPointX = (float) (cx + outerRadius * Math.cos(Math.atan2(x - cx, cy - y) - (Math.PI /2)));
            markPointY = (float) (cy + outerRadius * Math.sin(Math.atan2(x - cx, cy - y) - (Math.PI /2)));

            float degrees = (float) ((float) ((Math.toDegrees(Math.atan2(x - cx, cy - y)) + 360.0)) % 360.0);
            // and to make it count 0-360
            if (degrees < 0) {
                degrees += 2 * Math.PI;
            }

            setAngle(Math.round(degrees));
            invalidate();

        } else {
            IS_PRESSED = false;
            invalidate();
        }

    }

    public float getAdjustmentFactor() {
        return adjustmentFactor;
    }

    public void setAdjustmentFactor(float adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    public void ShowSeekBar() {
        SHOW_SEEKBAR = true;
    }

    public void hideSeekBar() {
        SHOW_SEEKBAR = false;
    }
}
