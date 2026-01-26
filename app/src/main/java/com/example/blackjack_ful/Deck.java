package com.example.blackjack_ful;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        createDeck();
    }

    private void createDeck() {
        String[] shapes = {"h", "d", "c", "s"};

        for (int val = 1; val <= 13; val++) {

            cards.add(new Card(0, 0, "c"+val, shapes[0], val));
        }
        for (int val = 1; val <= 13; val++) {
            cards.add(new Card(val, shape));
        }
        for (int val = 1; val <= 13; val++) {
            cards.add(new Card(val, shape));
        }
        for (int val = 1; val <= 13; val++) {
            cards.add(new Card(val, shape));
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
