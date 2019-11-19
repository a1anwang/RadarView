package com.a1anwang.radarviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.a1anwang.lib_radarview.RadarItem;
import com.a1anwang.lib_radarview.RadarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<RadarItem> radarItemList;

    RadarView radarview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radarview=findViewById(R.id.radarview);

        radarItemList=new ArrayList<>();
        for (int i=0;i<5;i++){
            int value= getRandomValue();
            float progress= value/100.0f;
            radarItemList.add(new RadarItem("标签"+(i+1),""+value,progress));
        }
        radarview.setRadarItemList(radarItemList);
    }

    public void addTag(View view) {
        int value= getRandomValue();
        float progress= value/100.0f;
        radarItemList.add(new RadarItem("标签"+(radarItemList.size()+1),""+value,progress));
        radarview.setRadarItemList(radarItemList);
    }

    public void removeTag(View view) {
        if(radarItemList.size()<=3){
            Toast.makeText(this,"最少3个标签",Toast.LENGTH_SHORT);
            return;
        }
        radarItemList.remove(radarItemList.size()-1);
        radarview.setRadarItemList(radarItemList);
    }


    //随机获取50-100的int
    private int getRandomValue(){
        int num= (int) (Math.random()*50+51);
        return num;
    }

    public void changeColor(View view) {
        int bgColor=getAlphaRandomColor();
        int dotColor=getRandomColor();
        int labelColor=getRandomColor();
        int solidColor=getAlphaRandomColor();
        int strokeColor=getRandomColor();
        int textColor=getRandomColor();
        int netLineColor=getRandomColor();
        int radiantLineColor=getRandomColor();

        radarview.setBgColor(bgColor);
        radarview.setDotColor(dotColor);
        radarview.setLabelColor(labelColor);
        radarview.setSolidColor(solidColor);
        radarview.setStrokeColor(strokeColor);
        radarview.setTextColor(textColor);
        radarview.setNetLineColor(netLineColor);
        radarview.setRadiantLineColor(radiantLineColor);
    }

    private int getRandomColor(){
        int r= (int) Math.floor(Math.random()*256);
        int g= (int) Math.floor(Math.random()*256);
        int b= (int) Math.floor(Math.random()*256);
        return Color.argb(255,r,g,b);
    }

    private int getAlphaRandomColor(){
        int r= (int) Math.floor(Math.random()*256);
        int g= (int) Math.floor(Math.random()*256);
        int b= (int) Math.floor(Math.random()*256);
        return Color.argb(70,r,g,b);
    }

}
