package com.example.jiac.chartdemo;

import java.io.Serializable;
import java.util.List;

/**
 * version  0.1
 * date     2019/3/4 14:40
 * author   caojiaxu
 * desc
 */
public class MultiBubbleData implements Serializable {
    public float x;
    public String xText;
    public List<BubbleData> values;
}
