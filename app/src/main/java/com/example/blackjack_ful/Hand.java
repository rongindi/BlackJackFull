package com.example.blackjack_ful;

import android.graphics.Canvas;
import java.util.ArrayList;

/**
 * מחלקה המייצגת "יד" של קלפים (של השחקן או של הדילר).
 * אחראית על ניהול רשימת הקלפים, חישוב הערך הכולל וסידורם הויזואלי על המסך.
 */
public class Hand {
    private ArrayList<Card> hand = new ArrayList<>(); // רשימת הקלפים ביד
    private int val;
    private float startX; // מיקום ה-X ההתחלתי לציור הקלף הראשון
    private float startY; // מיקום ה-Y לציור
    private float maxWidth = 800f; // רוחב מקסימלי שהיד יכולה לתפוס על המסך

    public Hand(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    /**
     * הוספת קלף ליד ועדכון המיקומים של כל הקלפים ביד.
     */
    public void addCard(Card c) {
        hand.add(c);
        updatePositions();
    }

    public void clear() {
        hand.clear();
    }

    /**
     * לוגיקה לסידור הקלפים:
     * ככל שיש יותר קלפים, הרווח ביניהם קטן כדי שלא יצאו מגבולות ה-maxWidth.
     */
    private void updatePositions() {
        int count = hand.size();
        if (count == 0) return;

        float spacing;
        if (count <= 3) {
            spacing = 220f;
        } else if (count == 4) {
            spacing = 170f;
        } else if (count == 5) {
            spacing = 130f;
        } else {
            spacing = 100f;
        }

        // וידוא שהקלפים לא חורגים מהרווח המוקצה
        float totalWidth = (count - 1) * spacing + 120;
        if (totalWidth > maxWidth) {
            spacing = (maxWidth - 120) / (count - 1);
        }

        for (int i = 0; i < count; i++) {
            Card card = hand.get(i);
            float posX = startX + i * spacing;
            float posY = startY;
            card.setPosition(posX, posY); // קביעת המיקום הסופי לציור הקלף
        }
    }

    public void setPosition(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        updatePositions();
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        updatePositions();
    }

    /**
     * ציור כל הקלפים שביד על ה-Canvas.
     */
    public void drawAll(Canvas canvas) {
        for (Card card : hand) {
            card.drawCard(canvas);
        }
    }

    /**
     * לוגיקת חישוב ערך היד בבלאק ג'ק:
     * - קלפי מספר שווים את ערכם.
     * - נסיך, מלכה, מלך שווים 10.
     * - אס (Ace) שווה 11, אך אם השחקן עובר את 21, הוא הופך ל-1.
     */
    public int getValue() {
        int value = 0;
        int aces = 0;
        for (Card card : hand) {
            int rank = card.getVal();
            value += rank;
            if (rank == 11) aces++; // ספירת אסים
        }
        
        // תיקון ערך האסים במידה ועברנו את 21
        for (int i = aces; i > 0; i--) {
            if (value > 21) {
                value -= 10; // הופך אס מ-11 ל-1
            }
        }
        return value;
    }

    /**
     * בדיקה האם היד "שרופה" (מעל 21).
     */
    public boolean isBust () {
        return getValue() > 21;
    }

    public ArrayList<Card> getCards () {
        return hand;
    }
    public float getStartX () {
        return startX;
    }

    public float getStartY () {
        return startY;
    }

}
