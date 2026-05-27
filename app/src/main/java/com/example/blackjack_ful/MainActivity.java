package com.example.blackjack_ful;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * הפעילות המרכזית (Entry Point) של האפליקציה.
 * אחראית על ניהול מסכי ההתחברות (Login) וההרשמה (Registration) בעזרת Fragments ו-TabLayout.
 */
public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout; // מכולה להצגת הפרגמנטים
    TabLayout tabLayout;     // תפריט לשליטה במעבר בין התחברות להרשמה

    // רשימה סטטית המחזיקה את כל שיאי השחקנים, מתעדכנת בזמן אמת מה-FBsingelton
    public static ArrayList<MyDetailsInFb> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // הגדרת Padding אוטומטי כדי שהתוכן לא יוסתר על ידי ה-System Bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        records = new ArrayList<>();

        frameLayout = findViewById(R.id.frameLayout);
        tabLayout = findViewById(R.id.tabLayout);

        // טעינת פרגמנט ההתחברות כברירת מחדל
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new com.example.blackjack_ful.LogInFragment())
                .addToBackStack(null)
                .commit();

        // האזנה למעבר בין טאבים (התחברות / הרשמה)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0: // טאב התחברות
                        fragment = new com.example.blackjack_ful.LogInFragment();
                        break;
                    case 1: // טאב הרשמה
                        fragment = new RegistrationFragment();
                        break;
                }
                // החלפת הפרגמנט המוצג בהתאם לבחירה
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * בדיקה האם המשתמש כבר מחובר בעת פתיחת האפליקציה.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // אם המשתמש כבר מחובר, מדלגים על מסך ההתחברות ועוברים ישר למסך ה-Welcome
        if(currentUser != null){
            reload();
        }
    }

    /**
     * מעבר למסך הראשי של המשחק וסגירת מסך ההתחברות.
     */
    private void reload() {
        Intent intent = new Intent(MainActivity.this, WelcomActivity.class);
        startActivity(intent);
        finish(); // סגירת ה-Activity הנוכחית כדי שלא יהיה ניתן לחזור אליה עם כפתור "חזור"
    }
}
