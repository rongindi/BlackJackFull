package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class StandButton {
    Bitmap bitmap;
    float x, y;
    private float canvasWidth;
    private float canvasHeight;

    public StandButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }
    public void drawStand(Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }
}
