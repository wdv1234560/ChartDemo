package com.example.jiac.chartdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private MultiBubbleChartView mMultiChart;
    private ArrayList<BubbleData> mBubbleData;
    private LinkedHashMap<String,MultiBubbleData> mMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMultiChart = findViewById(R.id.multChart);
        mBubbleData = new ArrayList<>();
        for (int i=0;i<48;i++){
            MultiBubbleData multiBubbleData = new MultiBubbleData();
            ArrayList<BubbleData> bubbleData = new ArrayList<>();
            bubbleData.add(new BubbleData((float) (1*Math.random()),"00:0"+i, Color.RED));
            bubbleData.add(new BubbleData((float) (1*Math.random()),"00:0"+i, Color.RED));
            bubbleData.add(new BubbleData((float) (1*Math.random()),"00:0"+i, Color.RED));
            multiBubbleData.values =bubbleData;
            mMap.put("00:0"+i,multiBubbleData);
        }
        mMultiChart.setBubbleData(mMap);
    }
}
