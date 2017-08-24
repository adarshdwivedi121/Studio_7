package com.example.adarsh.studio7.CustomViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by adarsh on 24/08/2017.
 */

public class SeekBar extends ProgressBar {

    public SeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int val = widthMeasureSpec > heightMeasureSpec ? heightMeasureSpec : widthMeasureSpec;
        setMeasuredDimension(val, val);
    }
}
