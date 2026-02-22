package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

public class StartButton {
    Bitmap bitmap;
    float x, y;
    private float canvasWidth;
    private float canvasHeight;

    public StartButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }
    public void drawStart(@NonNull Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }

}
