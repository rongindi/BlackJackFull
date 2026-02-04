package com.example.blackjack_ful;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> cards;
    Context context;

    public Deck(Context context1) {
        cards = new ArrayList<>();
        context = context1;
        createDeck();
    }

    public void createDeck() {
        //String[] shapes = {"h", "d", "c", "s"};


        for (int val = 1; val <= 13; val++) {
            int resId = context.getResources()
                    .getIdentifier("c" + val, "drawable", context.getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            cards.add(new Card(0, 0, bitmap, val));
        }
        for (int val = 1; val <= 13; val++) {
            int resId = context.getResources()
                    .getIdentifier("d" + val, "drawable", context.getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            cards.add(new Card(0, 0, bitmap, val));
        }
        for (int val = 1; val <= 13; val++) {
            int resId = context.getResources()
                    .getIdentifier("h" + val, "drawable", context.getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            cards.add(new Card(0, 0, bitmap, val));
        }
        for (int val = 1; val <= 13; val++) {
            int resId = context.getResources()
                    .getIdentifier("s" + val, "drawable", context.getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            cards.add(new Card(0, 0, bitmap, val));
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
}
