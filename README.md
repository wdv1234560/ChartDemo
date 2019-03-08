# 自定义散点图 #

## 需求 ##
1. x轴坐标显示一天每隔15分钟的时间，
2. 每刻时间有多个数据点
3. 选择点显示Y轴的数值和X轴的时间，右上角显示具体时间和数值

## 效果图 ##

![](https://i.imgur.com/ivesp2Q.png)

## 构思 ##
1. 计算气泡图最大高度与最大数值的比例
2. 获取每个x轴坐标的平均宽度
3. 适配x轴第一个坐标文字被遮挡问题
4. 遍历集合，把数值点绘制在画布上
5. 获取手势点击的坐标点，判断是否属于某个数值的面积范围内，绘制X,Y提示值，标线和右上角显示具体时间和数值 

## 代码 ##
1. 计算各种尺寸的代码：
	

	    private void computeSize() {
	        if (mData == null) {
	            return;
	        }
	        String xText = mData != null ? mData.keySet().iterator().next() : "";
	
	        mXTextPaint.getTextBounds(xText, 0, xText.length(), mXTextRect);
	        mChartW = mWidth - getPaddingLeft() - getPaddingRight() - mRadius * 2;//获取气泡图图表区的宽度
	        mXTextHeight = mXTextRect.height();
	        mChartH = mHeight - getPaddingTop() - getPaddingBottom() - mXTextHeight - mRadius;//获取气泡图图表区高度
	        //计算X轴坐标的平均宽度
	        mAvgW = mChartW / (mData.size() - 1);
	        //计算气泡图最大高度与最大数值的比例
	        float maxValue = getMaxValue(mData) * 1.2f;
	        float heightRatio = (mChartH - mGap) / maxValue;
	        //计算气泡图最大数值与最大高度的比例
	        mValueRatio = maxValue / (mChartH - mGap);
	        int index = 0;
	        //遍历map集合获取每个圆点的x,y坐标
	        for (String key : mData.keySet()) {
	            MultiBubbleData multiBubbleData = mData.get(key);
	            if (multiBubbleData == null) {
	                multiBubbleData = new MultiBubbleData();
	                multiBubbleData.values = new ArrayList<>();
	                mData.put(key, multiBubbleData);
	            }
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

2. 绘制X轴坐标和散点

		private void drawBubble(Canvas canvas) {
	
	        canvas.drawLine(0, mChartH, mWidth, mChartH, mLinePaint);
	        int index = 0;
	        for (String key : mData.keySet()) {
	            if (index % mXSpaceCount == 0) {//判断间隔多少个点画一个x轴坐标
	
	                float y = mHeight - getPaddingBottom();
	                Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
	                mXTextPaint.getFontMetrics(fontMetrics);
	                float stringWidth = mXTextPaint.measureText(key);
	                float x = (mAvgW * index + getPaddingLeft() + mRadius - stringWidth / 2);
	                canvas.drawText(key, x, y, mXTextPaint);
	            }
	            MultiBubbleData multiBubbleData = mData.get(key);
	            if (multiBubbleData != null) {
	                List<BubbleData> bubbleValues = multiBubbleData.values;
	                if (bubbleValues != null) {
	                    for (int i = 0; i < bubbleValues.size(); i++) {
	                        //绘制坐标文本
	                        BubbleData bubble = bubbleValues.get(i);
	
	                        //绘制气泡
	                        if (bubble.value != 0) {
	
	                            canvas.drawCircle(bubble.x, bubble.y, mRadius, mBubblePaint);
	                        }
	
	                    }
	                }
	
	            }
	            index++;
	        }
	    }

3. 手势监听和绘制提示线和文字

		private void drawXYTrip(Canvas canvas) {
	        if (mIsSelBubble == null) {
	            return;
	        }
	        mTouchY = mIsSelBubble.y;
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
	            rectX.set((int) (mIsSelBubble.x - widthX / 2), mHeight - getPaddingBottom() - mXTextHeight, (int) (mIsSelBubble.x + widthX / 2), mHeight - getPaddingBottom());
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
	        canvas.drawLine(yTextW, mTouchY, mWidth, mTouchY, mIndicatrixPaint);
	        //画Y轴标线
	        canvas.drawLine(mIsSelBubble.x, 0, mIsSelBubble.x, mChartH, mIndicatrixPaint);
	    }
	
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        switch (event.getActionMasked()) {
	            case MotionEvent.ACTION_DOWN:
	                if (mData != null) {
	                    mTouchX = event.getX();
	                    mTouchY = event.getY();
	                    mIsTouch = true;
	
	                    for (String key : mData.keySet()) {
	                        MultiBubbleData multiBubbleData = mData.get(key);
	                        if (multiBubbleData != null) {
	                            List<BubbleData> values = multiBubbleData.values;
	                            for (int i = 0; i < values.size(); i++) {
	                                if (values.get(i).isInside(event.getX(), event.getY(), mRadius)) {
	
	                                    mIsSelBubble = values.get(i);
	                                }
	                            }
	                        }
	                    }
	                }
	                invalidate();
	                break;
	            case MotionEvent.ACTION_MOVE:
	
	                break;
	            case MotionEvent.ACTION_UP:
	
	                invalidate();
	                break;
	        }
	        return true;
	    }

## 后期优化 #

1. 添加手势放大操作
2. 添加滑屏操作

## 项目地址 ##

 