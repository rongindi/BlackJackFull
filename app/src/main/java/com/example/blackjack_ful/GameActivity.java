package com.example.blackjack_ful;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * הפעילות המארחת את המשחק עצמו.
 * בניגוד לפעילויות רגילות המשתמשות ב-XML (setContentView(R.layout...)),
 * פעילות זו טוענת ישירות את ה-Custom View שיצרנו - BoardGame.
 */
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // יצירת מופע של מחלקת המשחק והצגתה על כל המסך
        BoardGame boardGame = new BoardGame(this);
        setContentView(boardGame);
    }
}
