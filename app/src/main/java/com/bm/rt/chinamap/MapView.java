package com.bm.rt.chinamap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by youtl on 2017/6/18.
 */

public class MapView extends View {

    private Context mContext;
    private List<ProviceItem> list;
    private Paint mPaint;
    private float scale = 1.3f;
    private int mSelectPos = 0;

    public MapView(Context context) {
        this(context,null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 在构造函数中开启一个线程解析svg文件
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        list = new ArrayList<>();
        loadThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (list.size() > 0){
            canvas.save();
            canvas.scale(scale,scale);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).draw(canvas,mPaint,i == mSelectPos);
            }
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isTouch((int)(event.getX() / scale),(int)(event.getY() / scale))){
                mSelectPos = i;
                break;
            }
        }
        postInvalidate();
        return true;
    }

    /**
     * 将svg文件解析，将每个省份的path封装成ProviceItem
     */
    private Thread loadThread = new Thread(){
        @Override
        public void run() {
            list.clear();
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.chinahigh);
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    Path path = PathParser.createPathFromPathData(pathData);
                    ProviceItem proviceItem = new ProviceItem(path);
                    list.add(proviceItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 解析完开始绘制
            mHandler.sendEmptyMessage(1);
        }
    };

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if (list.size() == 0){
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                if (i % 6  == 0){
                    list.get(i).setDrawColor(Color.RED);
                }else if (i % 6 == 1){
                    list.get(i).setDrawColor(Color.GREEN);
                }else if (i % 6 == 2){
                    list.get(i).setDrawColor(Color.BLUE);
                }else if (i % 6 == 3){
                    list.get(i).setDrawColor(Color.YELLOW);
                }else if (i % 6 == 4){
                    list.get(i).setDrawColor(Color.CYAN);
                }else if (i % 6 == 5){
                    list.get(i).setDrawColor(Color.MAGENTA);
                }
            }
            postInvalidate();
        }
    };
}
