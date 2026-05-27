package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

/**
 * מחלקה המייצגת את כפתור ה-"Stand" (עצירה והעברת התור לדילר).
 */
public class StandButton {
    Bitmap bitmap;      // התמונה של הכפתור
    float x, y;         // מיקום הכפתור על המסך
    private float canvasWidth;
    private float canvasHeight;

    public StandButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    /**
     * ציור הכפתור על גבי ה-Canvas.
     */
    public void drawStand(@NonNull Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }

    /**
     * בדיקה האם לחיצה במיקום (x,y) נמצאת בתוך גבולות הכפתור.
     */
    public boolean contains(float x, float y)
    {
        return x >= this.x && x <= this.x + bitmap.getWidth() &&
                y >= this.y && y <= this.y + bitmap.getHeight();
    }
}
