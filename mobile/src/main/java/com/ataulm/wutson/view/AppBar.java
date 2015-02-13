package com.ataulm.wutson.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.ataulm.wutson.R;

public class AppBar extends Toolbar {

    public AppBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setId(R.id.app_bar);
        setBackground(new ColorDrawable(Color.WHITE));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
        int desiredHeightSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, desiredHeightSpec);
    }

}