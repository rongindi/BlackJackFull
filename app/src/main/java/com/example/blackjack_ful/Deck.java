package com.example.blackjack_ful;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * מחלקה המייצגת חפיסת קלפים מלאה (52 קלפים).
 * אחראית על יצירת הקלפים מהמשאבים (Resources), ערבובם ושליפת קלף מהחפיסה.
 */
public class Deck {

    private ArrayList<Card> cards; // רשימת הקלפים בחפיסה
    Context context;
    
    // גודל קבוע לקלפים כדי למנוע תמונות ענקיות
    private final int CARD_WIDTH = 220;
    private final int CARD_HEIGHT = 330;

    public Deck(Context context1) {
        cards = new ArrayList<Card>();
        context = context1;
        createDeck();
    }

    /**
     * יצירת חפיסה סטנדרטית של 52 קלפים.
     * הפונקציה טוענת את התמונות מתיקיית ה-drawable לפי שמות הקבצים ומקטינה אותן לגודל אחיד.
     */
    public void createDeck() {
        String[] types = {"c", "d", "h", "s"};

        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            for (int val = 1; val <= 13; val++) {
                int resId = context.getResources()
                        .getIdentifier(type + val, "drawable", context.getPackageName());

                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                
                // שינוי גודל הקלף לגודל אחיד
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, CARD_WIDTH, CARD_HEIGHT, true);
                }
                
                // לוגיקת קביעת הערך לפי חוקי בלאק ג'ק:
                if (val > 10) {
                    cards.add(new Card(0, 0, bitmap, 10));
                } else if (val == 1) {
                    cards.add(new Card(0, 0, bitmap, 11));
                } else {
                    cards.add(new Card(0, 0, bitmap, val));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            createDeck();
            shuffle();
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
}
