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

    public Deck(Context context1) {
        cards = new ArrayList<Card>();
        context = context1;
        createDeck();
    }

    /**
     * יצירת חפיסה סטנדרטית של 52 קלפים.
     * הפונקציה טוענת את התמונות מתיקיית ה-drawable לפי שמות הקבצים (c1, d1, h1, s1 וכו').
     */
    public void createDeck() {
        // לולאות עבור כל סוג (Hearts, Diamonds, Clubs, Spades)
        // c = clubs, d = diamonds, h = hearts, s = spades
        
        String[] types = {"c", "d", "h", "s"};
        
        for (String type : types) {
            for (int val = 1; val <= 13; val++) {
                // מציאת מזהה המשאב (ID) לפי שם הקובץ בצורה דינמית
                int resId = context.getResources()
                        .getIdentifier(type + val, "drawable", context.getPackageName());

                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                
                // לוגיקת קביעת הערך לפי חוקי בלאק ג'ק:
                if (val > 10) {
                    // נסיך (11), מלכה (12), מלך (13) שווים כולם 10 נקודות
                    cards.add(new Card(0, 0, bitmap, 10));
                } else if (val == 1) {
                    // אס (Ace) מקבל ערך התחלתי של 11 (יכול להשתנות ל-1 בחישוב היד)
                    cards.add(new Card(0, 0, bitmap, 11));
                } else {
                    // קלפי מספרים (2-10) שווים את ערכם הנקוב
                    cards.add(new Card(0, 0, bitmap, val));
                }
            }
        }
    }

    /**
     * ערבוב אקראי של רשימת הקלפים.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * שליפת הקלף העליון מהחפיסה והסרתו ממנה.
     * @return אובייקט Card או null אם החפיסה ריקה
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            // אם החפיסה נגמרה, אפשר ליצור אותה מחדש ולערבב
            createDeck();
            shuffle();
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
}
