package com.a1anwang.lib_radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.List;

/**
 * Created by a1anwang.com on 2019/11/18.
 */
public class RadarView extends View {
    private String TAG="RadarView";

    private int textColor=Color.RED; //数值颜色
    private float textSize=14;//数值字体大小，
    private int labelColor=Color.BLACK;//标签颜色
    private float labelSize=16;//标签字体大小，
    private float labelMargin=20;//标签距离最外层网的距离，

    private int netLineColor=Color.BLACK;//网的颜色
    private float netLineWidth=1;//网线宽度

    private int netLineNum=5;//网线层数

    private int radiantLineColor=Color.BLACK;//放射线颜色
    private float radiantLineWidth=1;//放射线宽度

    private float strokeWidth=3;//连接线宽度
    private int strokeColor=Color.YELLOW;//连接线颜色

    private int solidColor=Color.parseColor("#330000FF");//连接区域颜色

    private int dotColor=Color.BLACK;//点的颜色
    private float dotRadius=3;//点的半径

    private int bgColor=Color.parseColor("#22000000");//底色

    private List<RadarItem> radarItemList;


    private TextPaint textPaint;
    private Paint paint;

    private SparseArray<Path> netLinePathList;
    private SparseArray<Point> dotList;
    private SparseArray<Point> topPointList;//各顶点坐标


    private Path strokePath;

    public RadarView(Context context) {
        super(context);
        init(null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs!=null){
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float density=displayMetrics.density;
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadarView);
            textColor = typedArray.getColor(R.styleable.RadarView_textColor, textColor);
            textSize = typedArray.getDimension(R.styleable.RadarView_textSize, textSize*density);
            labelColor = typedArray.getColor(R.styleable.RadarView_labelColor, labelColor);
            labelSize = typedArray.getDimension(R.styleable.RadarView_labelSize, labelSize*density);
            labelMargin = typedArray.getDimension(R.styleable.RadarView_labelMargin, labelMargin*density);
            netLineColor = typedArray.getColor(R.styleable.RadarView_netLineColor, netLineColor);
            netLineWidth = typedArray.getDimension(R.styleable.RadarView_netLineWidth, netLineWidth*density);
            netLineNum=typedArray.getInt(R.styleable.RadarView_netLineNum, netLineNum);
            radiantLineColor = typedArray.getColor(R.styleable.RadarView_radiantLineColor, radiantLineColor);
            radiantLineWidth = typedArray.getDimension(R.styleable.RadarView_radiantLineWidth, radiantLineWidth*density);
            strokeWidth = typedArray.getDimension(R.styleable.RadarView_strokeWidth, strokeWidth*density);
            strokeColor = typedArray.getColor(R.styleable.RadarView_strokeColor, strokeColor);
            solidColor = typedArray.getColor(R.styleable.RadarView_solidColor, solidColor);
            dotColor = typedArray.getColor(R.styleable.RadarView_dotColor, dotColor);
            dotRadius = typedArray.getDimension(R.styleable.RadarView_dotRadius, dotRadius*density);
            bgColor= typedArray.getColor(R.styleable.RadarView_bgColor, bgColor);
            typedArray.recycle();
        }


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(radiantLineColor);
        paint.setStrokeWidth(radiantLineWidth);

        netLinePathList=new SparseArray<>();
        strokePath=new Path();
        dotList=new SparseArray<>();
        topPointList=new SparseArray<>();
    }

    public void setRadarItemList(List<RadarItem> radarItemList) {
        this.radarItemList = radarItemList;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
    }

    public void setLabelSize(float labelSize) {
        this.labelSize = labelSize;
        invalidate();
    }

    public void setLabelMargin(float labelMargin) {
        this.labelMargin = labelMargin;
        invalidate();
    }

    public void setNetLineColor(int netLineColor) {
        this.netLineColor = netLineColor;
        invalidate();
    }

    public void setNetLineWidth(float netLineWidth) {
        this.netLineWidth = netLineWidth;
        invalidate();
    }

    public void setNetLineNum(int netLineNum) {
        this.netLineNum = netLineNum;
        invalidate();
    }

    public void setRadiantLineColor(int radiantLineColor) {
        this.radiantLineColor = radiantLineColor;
        invalidate();
    }

    public void setRadiantLineWidth(float radiantLineWidth) {
        this.radiantLineWidth = radiantLineWidth;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
        invalidate();
    }

    public void setDotColor(int dotColor) {
        this.dotColor = dotColor;
        invalidate();
    }

    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(radarItemList==null||radarItemList.size()<3){
            return;
        }
        strokePath.reset();
        int viewHeight= canvas.getHeight();
        int viewWidth=canvas.getWidth();


        textPaint.setTextSize(labelSize);
        float labelHeight=getTextHeight(textPaint);//标签文字高度

        float topX=viewWidth/2.0f;//顶点x坐标
        float topY=labelHeight+labelMargin;//顶点y坐标
//        Log.e(TAG,"topY :"+topY+"  labelHeight:"+labelHeight+" labelMargin:"+labelMargin);
        float bottomY=viewHeight-(topY);


        int radarItemSize=radarItemList.size();

        float centerX=viewWidth/2.0f;
        float centerY=viewHeight/2.0f;


        float perDegree=360.0f/radarItemSize;

        float radiantLineLength=centerY-topY;//放射线最大长度
        float netLineInterval=radiantLineLength/netLineNum;//网线之间的间隔

        paint.setStyle(Paint.Style.STROKE);
        for (int i=0;i<radarItemSize; i++){
            RadarItem radarItem=radarItemList.get(i);

            float degree=90-perDegree*i;


            for (int j=0;j<netLineNum;j++){//画网线

                float x= (float) (centerX+(radiantLineLength-j*netLineInterval)*cos(degree));
                float y= (float) (centerY-(radiantLineLength-j*netLineInterval)*sin(degree));

                Path path= netLinePathList.get(j);
                if(path==null){
                    path=new Path();
                    netLinePathList.put(j,path);
                }
                if(i==0){
                    path.reset();
                    path.moveTo(x,y);
                }else{
                    path.lineTo(x,y);
                }
                if(i==radarItemSize-1){
                    path.close();
                }


                if(j==0){
                    if(i==radarItemSize-1){
                            //画底色
                            paint.setStyle(Paint.Style.FILL);
                            paint.setColor(bgColor);
                            canvas.drawPath(path,paint);

                    }
                    Point topPoint=new Point();
                    topPointList.put(i,topPoint);
                    topPoint.x= (int) x;
                    topPoint.y= (int) y;

                    float labelCenterX=(float) (centerX+(radiantLineLength+labelMargin)*cos(degree));
                    float labelCenterY=(float) (centerY-(radiantLineLength+labelMargin)*sin(degree));
                    textPaint.setTextSize(labelSize);
                    textPaint.setColor(labelColor);
                    drawLabel(canvas,textPaint,radarItem.labelName,labelCenterX,labelCenterY,-90+perDegree*i);//画标签

                    float dotCenterX=(float) (centerX+(radiantLineLength*radarItem.progress)*cos(degree));
                    float dotCenterY=(float) (centerY-(radiantLineLength*radarItem.progress)*sin(degree));

                    if(i==0){
                        strokePath.moveTo(dotCenterX,dotCenterY);
                    }else{
                        strokePath.lineTo(dotCenterX,dotCenterY);
                    }
                    Point point= dotList.get(i);
                    if(point==null){
                        point=new Point();
                        dotList.put(i,point);
                    }
                    point.x= (int) dotCenterX;
                    point.y= (int) dotCenterY;




                }


            }
        }

        strokePath.close();



        for (int i=0;i<radarItemSize;i++) {//画放射线
            Point topPoint= topPointList.get(i);
            if(topPoint!=null){
                paint.setColor(radiantLineColor);
                paint.setStrokeWidth(radiantLineWidth);
                canvas.drawLine(centerX,centerY,  topPoint.x,  topPoint.y,paint);
            }
        }

        for (int i=0;i<netLineNum;i++) {//画网线
            Path path= netLinePathList.get(i);
            if(path!=null){
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(netLineColor);
                paint.setStrokeWidth(netLineWidth);
                canvas.drawPath(path,paint);
            }
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(solidColor);
        canvas.drawPath(strokePath,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawPath(strokePath,paint);





        paint.setStyle(Paint.Style.FILL);
        for (int i=0;i<radarItemSize;i++){
            paint.setColor(dotColor);
            Point point=dotList.get(i);
            canvas.drawCircle(point.x,point.y,dotRadius,paint);


            RadarItem radarItem=radarItemList.get(i);

            float degree=90-perDegree*i;
            float valueCenterX=(float) (centerX+(radiantLineLength*radarItem.progress+dotRadius)*cos(degree));
            float valueCenterY=(float) (centerY-(radiantLineLength*radarItem.progress+dotRadius)*sin(degree));
            textPaint.setTextSize(textSize);
            textPaint.setColor(textColor);
            drawText(canvas,textPaint,radarItem.value,valueCenterX,valueCenterY,-90+perDegree*i);//

        }
    }

    private float getTextHeight(Paint paint){
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        return paint.descent() - paint.ascent();
    }

    private float getTextWidth(Paint paint,String text){
        return paint.measureText(text);
    }

    private void drawLabel(Canvas canvas,TextPaint paint,String text,float centerX,float centerY,float degree){
            if(-101<degree&&degree<-89){
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text,centerX,centerY,paint);
            }else if(89<degree&&degree<91){
                paint.setTextAlign(Paint.Align.CENTER);
                float textHeight=getTextHeight(paint);
                canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
            }else if(-1<degree&&degree<1){
                float textWidth=getTextWidth(paint,text);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text,centerX+textWidth/2.0f,centerY,paint);
            }else if(179<degree&&degree<181){
                float textWidth=getTextWidth(paint,text);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text,centerX-textWidth/2.0f,centerY,paint);
            }else if(-90<degree&&degree<0){//第一象限
                paint.setTextAlign(Paint.Align.LEFT);
                float textHeight=getTextHeight(paint);
                canvas.drawText(text,centerX,centerY-textHeight/2.0f,paint);
            }else if(0<degree&&degree<90){//第四象限
                paint.setTextAlign(Paint.Align.LEFT);
                float textHeight=getTextHeight(paint);
                canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
            }else if(90<degree&&degree<180){//第三象限
                paint.setTextAlign(Paint.Align.RIGHT);
                float textHeight=getTextHeight(paint);
                canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
            }else{//第二象限
                paint.setTextAlign(Paint.Align.RIGHT);
                float textHeight=getTextHeight(paint);
                canvas.drawText(text,centerX,centerY-textHeight/2.0f,paint);
            }
    }
    private void drawText(Canvas canvas,TextPaint paint,String text,float centerX,float centerY,float degree){

        if(-101<degree&&degree<-89){
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text,centerX,centerY,paint);
        }else if(89<degree&&degree<91){
            paint.setTextAlign(Paint.Align.CENTER);
            float textHeight=getTextHeight(paint);
            canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
        }else if(-1<degree&&degree<1){
            float textWidth=getTextWidth(paint,text);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text,centerX+textWidth/2.0f,centerY,paint);
        }else if(179<degree&&degree<181){
            float textWidth=getTextWidth(paint,text);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text,centerX-textWidth/2.0f,centerY,paint);
        }else if(-90<degree&&degree<0){//第一象限
            paint.setTextAlign(Paint.Align.LEFT);
            float textHeight=getTextHeight(paint);
            canvas.drawText(text,centerX,centerY,paint);
        }else if(0<degree&&degree<90){//第四象限
            paint.setTextAlign(Paint.Align.LEFT);
            float textHeight=getTextHeight(paint);
            canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
        }else if(90<degree&&degree<180){//第三象限
            paint.setTextAlign(Paint.Align.RIGHT);
            float textHeight=getTextHeight(paint);
            canvas.drawText(text,centerX,centerY+textHeight/2.0f,paint);
        }else{//第二象限
            paint.setTextAlign(Paint.Align.RIGHT);
            float textHeight=getTextHeight(paint);
            canvas.drawText(text,centerX,centerY,paint);
        }
    }


    private double sin(float degree){
        return Math.sin(degree*Math.PI/180);
    }
    private double cos(float degree){
        return Math.cos(degree*Math.PI/180);
    }

}
