package com.example.jiac.chartdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiBubbleChartView extends View {
    private float mAvgW;//每个点平均分到的宽度
    private float mChartW;//气泡图的宽度
    private float mChartH;//气泡图的高度
    private Paint mXTextPaint;//x轴字体画笔
    private Paint mYTtextPaint;//Y轴字体画笔
    private Paint mYTripBgPaint;//Y轴提示字体背景画笔
    private Paint mIndicatrixPaint;//x,y轴指示线画笔
    private Paint mLinePaint;//x,y轴画笔
    private Paint mBubblePaint;//气泡的画笔
    private int mXTextColot = Color.GRAY;
    private float mXTextSize;
    private int mYTextColot = Color.WHITE;
    private float mYTextSize;
    private int mYTripBgColor = Color.parseColor("#80000000");
    private int mIndicatrixColor = Color.BLACK;
    private int mLineColor = Color.BLACK;
    private Rect mXTextRect;
    private float mGap;
    private float mRadius;
    private float mLineMargin = 0;
    private float mTouchX, mTouchY;
    private boolean mIsTouch;
    private int mSelectedIndex;
    private float mValueRatio;
    private int mXTextHeight;
    private int mBubbleColor = Color.parseColor("#80ff0000");
    private int mXSpaceCount = 12;
    private LinkedHashMap<String, MultiBubbleData> mData;
    private BubbleData mIsSelBubble;

    public MultiBubbleChartView(Context context) {
        super(context);
    }

    public MultiBubbleChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiBubbleChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * mXTextPaint
     * mYTtextPaint
     * mYTripBgPaint
     * mIndicatrixPaint
     * mLinePaint
     *
     * @param context
     */
    private void init(Context context) {
        mXTextSize = UiUtils.dip2px(context, 10);//默认字体大小10dp
        mYTextSize = UiUtils.dip2px(context, 10);//默认字体大小10dp
        mRadius = UiUtils.dip2px(context, 3);//气泡的半径
        mLineMargin = UiUtils.dip2px(context, 1);
        mXTextPaint = new Paint();
        mYTtextPaint = new Paint();
        mYTripBgPaint = new Paint();
        mIndicatrixPaint = new Paint();
        mLinePaint = new Paint();
        mBubblePaint = new Paint();

        mXTextPaint.setAntiAlias(true);
        mXTextPaint.setStrokeWidth(1);
        mXTextPaint.setColor(mXTextColot);
        mXTextPaint.setTextSize(mXTextSize);

        mYTtextPaint.setAntiAlias(true);
        mYTtextPaint.setStrokeWidth(1);
        mYTtextPaint.setColor(mYTextColot);
        mYTtextPaint.setTextSize(mYTextSize);

        mYTripBgPaint.setAntiAlias(true);
        mYTripBgPaint.setStrokeWidth(1);
        mYTripBgPaint.setColor(mYTripBgColor);

        mIndicatrixPaint.setAntiAlias(true);
        mIndicatrixPaint.setStrokeWidth(1);
        mIndicatrixPaint.setColor(mIndicatrixColor);

        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setColor(mLineColor);

        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setStrokeWidth(1);
        mBubblePaint.setColor(mBubbleColor);
        mXTextRect = new Rect();//x轴坐标点文字矩形
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mChartH, getWidth(), mChartH, mLinePaint);

        int index = 0;
        for (String key : mData.keySet()) {
            if (index % mXSpaceCount == 0) {//判断间隔多少个点画一个x轴坐标

                float y = getHeight() - getPaddingBottom();
                Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
                mXTextPaint.getFontMetrics(fontMetrics);
                float stringWidth = mXTextPaint.measureText(key);
                float x = (mAvgW * index + getPaddingLeft() + mRadius - stringWidth / 2);
                canvas.drawText(key, x, y, mXTextPaint);
            }
            List<BubbleData> bubbleValues = mData.get(key).values;
            for (int i = 0; i < bubbleValues.size(); i++) {
                //绘制坐标文本
                BubbleData bubble = bubbleValues.get(i);

                //绘制气泡
                if (bubble.value != 0) {

                    canvas.drawCircle(bubble.x, bubble.y, mRadius, mBubblePaint);
                }

            }
            index++;
        }


        if (mIsTouch) {


            drawXYTrip(canvas);
        }
    }

    private void drawXYTrip(Canvas canvas) {
        if(mIsSelBubble==null){
            return;
        }
        float x = mIsSelBubble.x + mRadius;
        float y = mIsSelBubble.y - mRadius;
        canvas.drawText(String.valueOf(mIsSelBubble.value), x, y, mXTextPaint);
        Paint.FontMetrics fm = mYTtextPaint.getFontMetrics();
        float top = fm.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fm.bottom;//为基线到字体下边框的距离,即上图中的bottom

        /*-------------画X轴提示文字和背景-------------*/
        String xText = mIsSelBubble.xText;
        if (!TextUtils.isEmpty(xText)) {

            Rect rectX = new Rect();
            float widthX = mYTtextPaint.measureText(xText);//y轴值字体宽度
            rectX.set((int) (mIsSelBubble.x - widthX / 2), getHeight() - getPaddingBottom() - mXTextHeight, (int) (mIsSelBubble.x + widthX / 2), getHeight() - getPaddingBottom());
            int baseLineY = (int) (rectX.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
            canvas.drawRect(rectX, mYTripBgPaint);
            canvas.drawText(xText, rectX.left, baseLineY, mYTtextPaint);
        }

        /*-------------画Y轴提示文字和背景-------------*/
        String valueY = (mChartH - mTouchY) * mValueRatio + "";
        float yTextW = mYTtextPaint.measureText(valueY);//y轴值字体宽度
        Rect rectY = new Rect();
        mYTtextPaint.getTextBounds(valueY, 0, valueY.length(), rectY);
        int height = rectY.height();
        rectY.set(0, (int) (mTouchY + height / 2 + 4), (int) yTextW, (int) (mTouchY - height / 2 - 4));
        canvas.drawRect(rectY, mYTripBgPaint);
        //基线中间点的y轴计算公式
        int baseLineY = (int) (rectY.centerY() - top / 2 - bottom / 2);
        //画y轴提示字体
        canvas.drawText(valueY + "", 0, baseLineY, mYTtextPaint);

        //画X轴标线
        canvas.drawLine(yTextW, mTouchY, getWidth(), mTouchY, mIndicatrixPaint);
        //画Y轴标线
        canvas.drawLine(mIsSelBubble.x, 0, mIsSelBubble.x, mChartH, mIndicatrixPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mTouchX = event.getX();
                mTouchY = event.getY();
                mIsTouch = true;

                for (String key : mData.keySet()) {
                    MultiBubbleData multiBubbleData = mData.get(key);
                    List<BubbleData> values = multiBubbleData.values;
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i).isInside(event.getX(), event.getY(), mRadius)) {
//                        enableGrowAnimation = false;
//                        invalidate();
                            mIsSelBubble = values.get(i);
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                mSelectedIndex = -1;
//                enableGrowAnimation = false;
                mIsTouch = false;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        String xText = mData != null ? mData.keySet().iterator().next() : "";

        mXTextPaint.getTextBounds(xText, 0, xText.length(), mXTextRect);
        mChartW = w - getPaddingLeft() - getPaddingRight() - mRadius * 2;//获取气泡图图表区的宽度
        mXTextHeight = mXTextRect.height();
        mChartH = h - getPaddingTop() - getPaddingBottom() - mXTextHeight - mRadius;//获取气泡图图表区高度
        mAvgW = mChartW / (mData.size() - 1);
        //计算气泡图最大高度与最大数值的比例
        float maxValue = getMaxValue(mData)*2;
        float heightRatio = (mChartH - mGap) / maxValue;
        //计算气泡图最大数值与最大高度的比例
        mValueRatio = maxValue / (mChartH - mGap);
        int index = 0;
        for (String key : mData.keySet()) {
            MultiBubbleData multiBubbleData = mData.get(key);
            List<BubbleData> bubbleValues = multiBubbleData.values;
            float x = mAvgW * index + getPaddingLeft() + mRadius;
            multiBubbleData.x = x;
            multiBubbleData.xText = key;
            for (int i = 0; i < bubbleValues.size(); i++) {

                BubbleData bubbleData = bubbleValues.get(i);
                bubbleData.x = x;
                float transValue = bubbleData.value * heightRatio;
                bubbleData.y = getPaddingTop() + mChartH - transValue;
            }
            index++;
        }

    }

    public static float getMaxValue(Map<String, MultiBubbleData> map) {
        float maxValue = 0;

        for (String key : map.keySet()) {

            List<BubbleData> values = map.get(key).values;
            if (values != null && values.size() != 0) {
                BubbleData max = Collections.max(values);
                maxValue = maxValue > max.value ? maxValue : max.value;
            }

        }

        return maxValue;

    }


    public void setBubbleData(LinkedHashMap<String, MultiBubbleData> data) {
        mData = data;
    }
}
