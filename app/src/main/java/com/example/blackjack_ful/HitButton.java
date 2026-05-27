package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * מחלקה המייצגת את כפתור ה-"Hit" (בקשת קלף).
 * אחראית על ציור הכפתור וזיהוי לחיצה בתוך הגבולות שלו.
 */
public class HitButton {
    Bitmap bitmap;      // התמונה של הכפתור
    float x, y;         // מיקום הכפתור על המסך
    private float canvasWidth;
    private float canvasHeight;

    public HitButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    /**
     * ציור הכפתור על גבי ה-Canvas.
     */
    public void drawHit(Canvas canvas)
    {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.drawBitmap(bitmap,x,y,null);
    }

    /**
     * בדיקה האם לחיצה במיקום (x,y) נמצאת בתוך גבולות הכפתור.
     * @return true אם הלחיצה היא על הכפתור, אחרת false.
     */
    public boolean contains(float x, float y)
    {
        return x >= this.x && x <= this.x + bitmap.getWidth() && y >= this.y && y <= this.y + bitmap.getHeight();
    }
}
