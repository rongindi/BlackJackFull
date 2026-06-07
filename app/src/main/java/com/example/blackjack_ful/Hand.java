package com.example.blackjack_ful;

import android.graphics.Canvas;
import java.util.ArrayList;

/**
 * מחלקה המייצגת "יד" של קלפים.
 */
public class Hand {
    private ArrayList<Card> hand = new ArrayList<>(); 
    private float startX; 
    private float startY; 
    private float maxWidth = 800f; 

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

        float spacing;
        if (count <= 3) spacing = 220f;
        else if (count == 4) spacing = 170f;
        else if (count == 5) spacing = 130f;
        else spacing = 100f;

        float totalWidth = (count - 1) * spacing + 120;
        if (totalWidth > maxWidth) {
            spacing = (maxWidth - 120) / (count - 1);
        }

        for (int i = 0; i < count; i++) {
            Card card = hand.get(i);
            card.setPosition(startX + i * spacing, startY); 
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

    public void drawAll(Canvas canvas) {

        for (int i = 0; i < hand.size(); i++) {
            hand.get(i).drawCard(canvas);
        }
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (int i = 0; i < hand.size(); i++) {
            int rank = hand.get(i).getVal();
            value += rank;
            if (rank == 11) aces++; 
        }
        
        for (int i = aces; i > 0; i--) {
            if (value > 21) {
                value -= 10; 
            }
        }
        return value;
    }

    public boolean isBust () {
        return getValue() > 21;
    }

    public ArrayList<Card> getCards () {
        return hand;
    }
    public float getStartX () { return startX; }
    public float getStartY () { return startY; }
}
