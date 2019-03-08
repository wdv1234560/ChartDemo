package com.example.jiac.chartdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MultiBubbleChartView mMultiChart;
    private ArrayList<BubbleData> mBubbleData;
    private LinkedHashMap<String, MultiBubbleData> mMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMultiChart = findViewById(R.id.multChart);
        mBubbleData = new ArrayList<>();
        /*for (int i = 0; i < 12; i++) {
            MultiBubbleData multiBubbleData = new MultiBubbleData();
            ArrayList<BubbleData> bubbleData = new ArrayList<>();
            bubbleData.add(new BubbleData((float) (1 * Math.random()), "00:0" + i, Color.RED));
            bubbleData.add(new BubbleData((float) (1 * Math.random()), "00:0" + i, Color.RED));
            bubbleData.add(new BubbleData((float) (1 * Math.random()), "00:0" + i, Color.RED));
            multiBubbleData.values = bubbleData;
            mMap.put("00:0" + i, multiBubbleData);
        }*/
        getFifteen();
        mMultiChart.setBubbleData(mMap);
    }

    private void getFifteen() {
        mMap.clear();

        List<Date> dateList = test(new Date());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < dateList.size(); i++) {
            if (i % 10 == 0) {
                MultiBubbleData value = new MultiBubbleData();
                value.values = new ArrayList<>();
                value.values.add(new BubbleData((float) (Math.random()*10f)));
                value.values.add(new BubbleData((float) (Math.random()*10f)));
                mMap.put(format.format(dateList.get(i)), value);
            } else {
                mMap.put(format.format(dateList.get(i)), null);

            }
        }
    }

    static List<Date> test(Date date) {
        Date start = dayStartDate(date);//转换为天的起始date
        Date nextDayDate = nextDay(start);//下一天的date

        List<Date> result = new ArrayList<Date>();
        while (start.compareTo(nextDayDate) < 0) {
            result.add(start);
            //日期加5分钟
            start = addFiveMin(start, 15);
        }

        return result;
    }

    private static Date addFiveMin(Date start, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.MINUTE, offset);
        return c.getTime();
    }

    private static Date nextDay(Date start) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private static Date dayStartDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
