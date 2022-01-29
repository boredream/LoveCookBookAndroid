package com.boredream.baseapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.SizeUtils;
import com.boredream.baseapplication.R;

public class RingLineView extends View {

    private int ringRadius = SizeUtils.dp2px(7);
    private int ringWidth = SizeUtils.dp2px(3);
    private int ringColor = getResources().getColor(R.color.colorPrimary);
    private int ringStartY = SizeUtils.dp2px(52);

    private int lineWidth = SizeUtils.dp2px(1);
    private int lineDashLength = SizeUtils.dp2px(6);
    private int lineDividerLength = SizeUtils.dp2px(4);
    private int lineColor = 0x4D999999;

    private Paint mRingPaint = new Paint();
    private Paint mRingHoverPaint = new Paint();
    private Paint mLinePaint = new Paint();

    private boolean hideTopLine;

    public RingLineView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public RingLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public RingLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RingLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mRingPaint.setColor(ringColor);
        mRingPaint.setAntiAlias(true);
        mRingHoverPaint.setColor(Color.WHITE);
        mRingHoverPaint.setAntiAlias(true);

        mLinePaint.setColor(lineColor);
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{lineDashLength, lineDividerLength}, 0));
        mLinePaint.setAntiAlias(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, mRingPaint);
    }

    public void setOvalColor(int colorRes) {
        ringColor = getResources().getColor(colorRes);
        mRingPaint.setColor(ringColor);
    }

    public void setHideTopLine(boolean hideTopLine) {
        this.hideTopLine = hideTopLine;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!hideTopLine) {
            canvas.drawLine(1f * ringRadius, 0, 1f * ringRadius, ringStartY - 2 * ringRadius, mLinePaint);
        }
        canvas.drawLine(1f * ringRadius, ringStartY, 1f * ringRadius, getHeight(), mLinePaint);
        canvas.drawCircle(ringRadius, ringStartY - ringRadius, ringRadius, mRingPaint);
        canvas.drawCircle(ringRadius, ringStartY - ringRadius, ringRadius - ringWidth, mRingHoverPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}