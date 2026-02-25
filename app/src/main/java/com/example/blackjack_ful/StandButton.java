package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

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
    public void drawStand(@NonNull Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }
    public boolean contains(float x, float y)
    {
        return x >= this.x && x <= this.x + bitmap.getWidth() &&
                y >= this.y && y <= this.y + bitmap.getHeight();
    }
}
