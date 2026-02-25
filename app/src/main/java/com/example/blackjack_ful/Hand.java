package com.example.blackjack_ful;

import android.graphics.Canvas;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();
    float startX;
    float startY;
    private float cardSpacing = 250f;  // רווח בין קלפים


    public Hand(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;}

    public void addCard(Card c)
    {
        hand.add(c);
        updatePositions();
    }
    public void clear()
    {
        hand.clear();
    }
    private void updatePositions() {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            float posX = startX + i * cardSpacing;
            float posY = startY;
            card.setPosition(posX, posY);
        }
    }
    public int getValue()
    {
        int value = 0;
        int aces = 0;

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            int rank = card.getVal();
            value += rank;
            if (rank == 11) {
                aces++;
            }

        }
        // אם יש Ace ויש bust → הופכים Ace ל-1
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }
    public void drawAll(Canvas canvas) {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            card.drawCard(canvas);
        }
    }
    // אם תרצה לשנות מיקום אחרי יצירה
    public void setPosition(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        updatePositions();
    }
    public boolean isBust()
    {
        return getValue() > 21;
    }
    public ArrayList<Card> getCards()
    {
        return hand;
    }

}
