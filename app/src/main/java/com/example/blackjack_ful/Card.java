package com.example.blackjack_ful;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * מחלקה המייצגת קלף בודד במשחק.
 * מחזיקה את התמונה של הקלף, את המיקום שלו על המסך ואת הערך המשחקי שלו.
 */
public class Card {

    Bitmap bitmap;      // התמונה הויזואלית של הקלף (מתיקיית ה-drawable)
    float x, y;         // מיקום הקלף על גבי הלוח (קואורדינטות)
    private int val;    // הערך המספרי של הקלף לצורך חישוב הניקוד בבלאק ג'ק

    public Card(float x, float y, Bitmap bitmap, int val) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.val = val;
    }

    /**
     * עדכון המיקום של הקלף (שימושי כשמסדרים את היד מחדש).
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * ציור הקלף על גבי הקנבס במיקום שנקבע לו.
     */
    public void drawCard(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    /**
     * מחזירה את ערך הקלף.
     */
    public int getVal() {
        return this.val;
    }

    public void setVal(int newVal) {
        this.val = newVal;
    }
}
