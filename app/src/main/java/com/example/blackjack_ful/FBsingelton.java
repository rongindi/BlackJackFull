package com.example.blackjack_ful;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * מחלקת Singleton לניהול הקשר מול Firebase Realtime Database.
 * תבנית ה-Singleton מבטיחה שיהיה רק מופע אחד של המחלקה בכל האפליקציה,
 * מה שמונע כפילות בחיבורים ומאפשר גישה ריכוזית לנתונים.
 */
public class FBsingelton {
    private static FBsingelton instance;

    FirebaseDatabase database;

    private FBsingelton() {
        database = FirebaseDatabase.getInstance();

        // הגדרת שאילתה לקריאת נתוני השחקנים מנתיב "details"
        Query myQuery = database.getReference("details");

        // האזנה לשינויים בנתונים בזמן אמת (Realtime)
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // בכל פעם שיש שינוי בבסיס הנתונים, הרשימה ב-MainActivity מתעדכנת
                MainActivity.records.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    // המרה של נתוני ה-JSON מה-Firebase לאובייקט Java מסוג MyDetailsInFb
                    MyDetailsInFb myDetailsInFb = userSnapshot.getValue(MyDetailsInFb.class);
                    if (myDetailsInFb != null) {
                        MainActivity.records.add(0, myDetailsInFb);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // טיפול בשגיאות קריאה (אם יש)
            }
        });
    }

    /**
     * פונקציה סטטית המעניקה גישה למופע היחיד של המחלקה.
     */
    public static FBsingelton getInstance() {
        if (null == instance) {
            instance = new FBsingelton();
        }
        return instance;
    }

    /**
     * שמירת או עדכון נתוני משתמש ב-Firebase.
     * @param name שם השחקן
     * @param chips כמות הגטונים הנוכחית
     */
    public void setDetails(String name, int chips) {
        // קבלת ה-UID הייחודי של המשתמש שמחובר כרגע (דרך Firebase Auth)
        String uid = FirebaseAuth.getInstance().getUid(); 
        if (uid != null) {
            // יצירת נתיב ייחודי לכל משתמש: details/UID
            DatabaseReference myRef = database.getReference("details/" + uid);
            
            // יצירת אובייקט הנתונים ושמירתו
            MyDetailsInFb myDetailsInFb = new MyDetailsInFb(name, chips);
            myRef.setValue(myDetailsInFb); 
        }
    }
}
