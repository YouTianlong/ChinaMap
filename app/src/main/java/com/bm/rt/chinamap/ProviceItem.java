package com.bm.rt.chinamap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by youtl on 2017/6/18.
 */

public class ProviceItem {

    protected Path path;

    private int drawColor;

    public ProviceItem(Path path){
        this.path = path;
    }


    public void setDrawColor(int drawColor){
        this.drawColor = drawColor;
    }

    /**
     *  绘制自己
     * @param canvas
     * @param paint
     * @param isSelect
     */
    public void draw(Canvas canvas, Paint paint,boolean isSelect) {
        if (isSelect){
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setShadowLayer(8,0,0,0xfffff);
            canvas.drawPath(path,paint);

            paint.clearShadowLayer();
            paint.setColor(Color.WHITE);
            canvas.drawPath(path,paint);
        }else{
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setColor(drawColor);
            canvas.drawPath(path,paint);

        }
    }

    /**
     * 判断该点是否在自己path的区域内
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x, int y) {
        RectF rectF = new RectF();
        path.computeBounds(rectF,true);
        Region region = new Region();
        region.setPath(path,new Region((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom));
        return region.contains(x,y);
    }
}
