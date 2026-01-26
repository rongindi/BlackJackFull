package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ReturnButton {
    Bitmap bitmap;
    float x, y;
    private float canvasWidth;
    private float canvasHeight;

    public ReturnButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }
    public void drawReturn(Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }
}
