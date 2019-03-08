package com.example.jiac.chartdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * version  0.1
 * date     2019/3/8 14:05
 * author   caojiaxu
 * desc
 */
public abstract class AbstractChartView extends View {
    private Paint mEmptyTextPaint =new Paint();

    public AbstractChartView(Context context) {
        super(context);
    }

    public AbstractChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mEmptyTextPaint.setAntiAlias(true);
        mEmptyTextPaint.setTextSize(dp2px(context, 14));
        mEmptyTextPaint.setStrokeWidth(dp2px(context, 1));
        mEmptyTextPaint.setStrokeWidth(1);
        mEmptyTextPaint.setColor(Color.parseColor("#666666"));
    }

    public AbstractChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void drawEmpty(Canvas canvas) {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_no_data);
        Bitmap bitmap = drawableToBitmap(drawable);
        int bLeft = getWidth() / 2 - drawable.getIntrinsicWidth() / 2;
        int bTop = getHeight() / 2 - drawable.getIntrinsicHeight() / 2;
        canvas.drawBitmap(bitmap,bLeft,bTop,null);
        float measureWidth = mEmptyTextPaint.measureText("暂无数据");
        canvas.drawText("暂无数据", getWidth() / 2-measureWidth/2,getHeight()/2+drawable.getIntrinsicHeight()/2+dp2px(this.getContext(),15), mEmptyTextPaint);
    }

    protected Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    public int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);

    }
}
