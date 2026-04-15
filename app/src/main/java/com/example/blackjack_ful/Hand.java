package com.example.blackjack_ful;

import android.graphics.Canvas;
import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();
    private float startX;
    private float startY;
    private float maxWidth = 800f;   // רוחב מקסימלי מומלץ ליד (תוכל לשנות)

    public Hand(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public void addCard(Card c) {
        hand.add(c);
        updatePositions();
    }

    public void clear() {
        hand.clear();
    }

    private void updatePositions() {
        int count = hand.size();
        if (count == 0) return;

        // חישוב רווח דינמי – ככל שיש יותר קלפים, הרווח קטן יותר
        float spacing;
        if (count <= 3) {
            spacing = 220f;           // רווח גדול כשיש מעט קלפים
        } else if (count == 4) {
            spacing = 170f;
        } else if (count == 5) {
            spacing = 130f;
        } else {
            spacing = 100f;           // רווח מינימלי
        }

        // אם היד עדיין יוצאת מהמסך – נכווץ עוד יותר
        float totalWidth = (count - 1) * spacing + 120; // 120 = רוחב קלף משוער
        if (totalWidth > maxWidth) {
            spacing = (maxWidth - 120) / (count - 1);
        }

        for (int i = 0; i < count; i++) {
            Card card = hand.get(i);
            float posX = startX + i * spacing;
            float posY = startY;
            card.setPosition(posX, posY);
        }
    }

    public void setPosition(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        updatePositions();
    }

    public void drawAll(Canvas canvas) {
        for (Card card : hand) {
            card.drawCard(canvas);
        }
    }

    public int getValue() {
        int value = 0;
        int aces = 0;
        for (Card card : hand) {
            int rank = card.getVal();
            value += rank;
            if (rank == 11) aces++;
        }
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public ArrayList<Card> getCards() {
        return hand;
    }
    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }
}