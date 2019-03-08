package com.example.jiac.chartdemo;

import android.support.annotation.NonNull;

public class BubbleData implements Comparable<BubbleData> {
    public float value;
    public String xText;
    public float x;
    public float y;
    public int pointRaduis;
    public int color;

    public BubbleData(float value) {
        this.value = value;
    }

    public BubbleData(float value, int color) {
        this.value = value;
        this.color = color;
    }

    public BubbleData(float value, String xText, int color) {
        this.value = value;
        this.xText = xText;
        this.color = color;
    }

    @Override
    public int compareTo(@NonNull BubbleData o) {
        if (this.value > o.value) {
            return 1;

        } else {
            return -1;
        }
    }

    public boolean isInside(float x, float y, float raduis) {
        return x>(this.x-raduis)&&x<(this.x+raduis);
//        return x>(this.x-raduis)&&x<(this.x+raduis)&&y>(this.y-raduis)&&y<(this.y+raduis);
    }
}
