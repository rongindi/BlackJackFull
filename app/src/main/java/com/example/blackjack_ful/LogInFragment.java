package com.example.blackjack_ful;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blackjack_ful.WelcomActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * פרגמנט האחראי על תהליך ההתחברות (Login) של משתמש קיים.
 * משתמש ב-Firebase Authentication כדי לאמת את הפרטים.
 */
public class LogInFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טעינת ה-Layout של הפרגמנט (XML)
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. אתחול אובייקט ה-Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. קישור רכיבי הממשק (UI) מה-XML לקוד ה-Java
        etEmail = view.findViewById(R.id.etEmailAddress);
        etPassword = view.findViewById(R.id.etNumberPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        // 3. הגדרת מאזין ללחיצה על כפתור ההתחברות
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    /**
     * פונקציה המבצעת את תהליך ההתחברות מול השרת.
     */
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקה בסיסית שהשדות אינם ריקים
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "נא להזין את כל הפרטים", Toast.LENGTH_SHORT).show();
            return;
        }

        // קריאה לשירות של Firebase לביצוע התחברות עם אימייל וסיסמה
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // אם ההתחברות הצליחה - עוברים למסך המשחק הראשי
                        Intent intent = new Intent(getActivity(), WelcomActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // סגירת מסך ההתחברות
                    } else {
                        // אם ההתחברות נכשלה - מציגים הודעת שגיאה למשתמש
                        Toast.makeText(getContext(), "התחברות נכשלה: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
