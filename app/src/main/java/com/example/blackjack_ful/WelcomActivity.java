package com.example.blackjack_ful;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

/**
 * מסך הבית (Welcome Screen) המוצג למשתמש לאחר התחברות מוצלחת.
 * מאפשר ניווט למשחק, צפייה בטבלת שיאים או התנתקות מהחשבון.
 */
public class WelcomActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogout, btnPlay, btnRecords;
    private TextView tvMyName;

    private FirebaseAuth mAuth;
    com.example.blackjack_ful.FBsingelton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcom);
        
        // החלפת למבדה במחלקה אנונימית עבור WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });
        initialization();
    }

    /**
     * אתחול רכיבי הממשק והקישורים ל-Firebase.
     */
    private void initialization() {
        fb = com.example.blackjack_ful.FBsingelton.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnRecords = findViewById(R.id.btnRecords);
        btnRecords.setOnClickListener(this);

        tvMyName = findViewById(R.id.tvMyName);
    }

    /**
     * ניהול לחיצות על הכפתורים במסך.
     */
    @Override
    public void onClick(View v) {
        if(v == btnLogout)
        {
            // התנתקות מהחשבון וחזרה למסך ההתחברות
            FirebaseAuth.getInstance().signOut();
            finish(); 
        }
        if (v == btnPlay)
        {
            // מעבר למסך המשחק (GameActivity)
            Intent intent = new Intent(WelcomActivity.this, GameActivity.class);
            startActivity(intent);
        }

        if (v == btnRecords)
        {
            // מעבר למסך טבלת השיאים
            Intent intent = new Intent(WelcomActivity.this, RecordsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * פונקציה לעדכון שם המשתמש בתצוגה (אם נדרש).
     */
    public void userDataChange(String name) {
        System.out.println(name);
        tvMyName.setText("שלום: " + name);
    }
}
