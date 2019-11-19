package com.a1anwang.lib_radarview;

/**
 * Created by a1anwang.com on 2019/11/19.
 */
public class RadarItem {
    String labelName;//标签名称
    String value;//数值
    float progress;//0-1;

    public RadarItem(String labelName, String value, float progress) {
        this.labelName = labelName;
        this.value = value;
        this.progress = progress;
    }
}
