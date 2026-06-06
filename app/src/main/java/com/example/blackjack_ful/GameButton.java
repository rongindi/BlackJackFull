package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

/**
 * מחלקה כללית המייצגת כפתור במשחק.
 * מחזיקה תמונה, מיקום ומאפשרת ציור ובדיקת לחיצה.
 */
public class GameButton {
    private Bitmap bitmap;      // התמונה של הכפתור
    private float x, y;         // מיקום הכפתור על המסך

    public GameButton(float x, float y, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    /**
     * ציור הכפתור על גבי ה-Canvas.
     */
    public void draw(@NonNull Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    /**
     * בדיקה האם לחיצה במיקום (x,y) נמצאת בתוך גבולות הכפתור.
     * @return true אם הלחיצה היא על הכפתור, אחרת false.
     */
    public boolean contains(float x, float y) {
        if (bitmap == null) return false;
        return x >= this.x && x <= this.x + bitmap.getWidth() &&
               y >= this.y && y <= this.y + bitmap.getHeight();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
