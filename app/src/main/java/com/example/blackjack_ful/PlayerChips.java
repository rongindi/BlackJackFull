package com.example.blackjack_ful;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class PlayerChips {

    private static final String CHIPS_PATH = "users";
    private static final int COST_PER_ROUND = 10;

    private DatabaseReference chipsRef;
    private int currentChips = 1000; // ברירת מחדל אם אין נתונים
    private ChipsListener listener;

    public interface ChipsListener {
        void onChipsUpdated(int chips);
    }

    public PlayerChips(Context context, ChipsListener listener) {
        this.listener = listener;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "יש להתחבר כדי לשחק על גטונים", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = user.getUid();
        chipsRef = FirebaseDatabase.getInstance().getReference(CHIPS_PATH).child(userId).child("chips");

        loadChipsFromFirebase();
    }

    private void loadChipsFromFirebase() {
        chipsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        currentChips = value;
                    }
                } else {
                    // יצירת גטונים ראשוניים
                    chipsRef.setValue(currentChips);
                }
                if (listener != null) listener.onChipsUpdated(currentChips);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ניתן להוסיף Toast אם רוצים
            }
        });
    }

    // מחסיר 10 גטונים לתחילת סיבוב
    public boolean deductRoundCost(Context context) {
        if (currentChips < COST_PER_ROUND) {
            Toast.makeText(context, "אין לך מספיק גטונים! (צריך 10)", Toast.LENGTH_SHORT).show();
            return false;
        }

        currentChips -= COST_PER_ROUND;
        chipsRef.setValue(currentChips);
        if (listener != null) listener.onChipsUpdated(currentChips);
        return true;
    }

    // להוסיף גטונים (למשל אם רוצים פרס)
    public void addChips(int amount) {
        currentChips += amount;
        chipsRef.setValue(currentChips);
        if (listener != null) listener.onChipsUpdated(currentChips);
    }

    public int getCurrentChips() {
        return currentChips;
    }
    public void winRound(boolean isBlackjack) {
        if (isBlackjack) {
            currentChips += 25;     // ×2.5 = החזר 10 + רווח 15
        } else {
            currentChips += 20;     // ×2 = החזר 10 + רווח 10
        }
        chipsRef.setValue(currentChips);
        if (listener != null) listener.onChipsUpdated(currentChips);
    }

    public void pushRound() {
        currentChips += 10;         // החזר ההימור
        chipsRef.setValue(currentChips);
        if (listener != null) listener.onChipsUpdated(currentChips);
    }

    public void loseRound() {
        // כבר חויב 10 בתחילת הסיבוב - אין שינוי נוסף
        if (listener != null) listener.onChipsUpdated(currentChips);
    }
}
