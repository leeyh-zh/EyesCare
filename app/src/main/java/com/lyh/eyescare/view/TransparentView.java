package com.lyh.eyescare.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lyh.eyescare.R;

/**
 * Created by lyh on 2017/3/26 0026.
 */

public class TransparentView extends View {
    private Bitmap bitmap;
    public TransparentView(Context context) {
        this(context, null);

    }

    public TransparentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransparentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap vBitmap = BitmapFactory.decodeResource( this.getResources()
                , R.mipmap.ic_launcher);
        // 建立Paint 物件
        Paint vPaint = new Paint();
        vPaint .setStyle( Paint.Style.STROKE );   //空心
        vPaint .setAlpha(75);   //

        //canvas.drawBitmap ( vBitmap , 50, 100, null );  //无透明
        canvas.drawBitmap ( vBitmap , 50, 200, vPaint );  //有透明
        //Paint paint = new Paint();
        //paint.setAlpha(alpha);
        //canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(10, 10, 310, 235), paint);
    }
}
