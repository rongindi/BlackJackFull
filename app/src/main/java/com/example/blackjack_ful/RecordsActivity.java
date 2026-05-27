package com.example.blackjack_ful;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * מסך טבלת השיאים (Leaderboard).
 * מציג רשימה של כל השחקנים והגטונים שלהם בעזרת RecyclerView.
 */
public class RecordsActivity extends AppCompatActivity {
    private RecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_records);
        
        // הגדרת Padding למניעת חפיפה עם שורת המצב
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialization();
    }

    /**
     * אתחול רכיבי ה-UI והגדרת ה-RecyclerView.
     */
    private void initialization() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        
        // הגדרת מנהל פריסה (Layout Manager) - במקרה זה רשימה אנכית
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // יצירת המתאם (Adapter) וחיבורו לרשימה הסטטית שנמצאת ב-MainActivity
        adapter = new RecordAdapter(this, MainActivity.records);
        recyclerView.setAdapter(adapter);
    }
}
