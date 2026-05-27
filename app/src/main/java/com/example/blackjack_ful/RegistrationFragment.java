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

import com.google.firebase.auth.FirebaseAuth;

/**
 * פרגמנט האחראי על תהליך ההרשמה (Registration) של משתמש חדש.
 * יוצר חשבון ב-Firebase Auth ומאתחל את נתוני המשתמש ב-Realtime Database.
 */
public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etName;
    private Button btnRegister;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טעינת ה-Layout של הפרגמנט (XML)
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. אתחול אובייקט ה-Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. קישור רכיבי הממשק (UI) מה-XML לקוד ה-Java
        etEmail = view.findViewById(R.id.etEmailAddress);
        etPassword = view.findViewById(R.id.etNumberPassword);
        etName = view.findViewById(R.id.etName);
        btnRegister = view.findViewById(R.id.btnRegister);

        // 3. הגדרת מאזין ללחיצה על כפתור ההרשמה
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });
    }

    /**
     * פונקציה המבצעת את תהליך ההרשמה.
     */
    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();

        // בדיקה שכל השדות מולאו
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // יצירת משתמש חדש ב-Firebase Auth עם אימייל וסיסמה
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "הרשמה הצליחה!", Toast.LENGTH_SHORT).show();

                        // יצירת רשומה ראשונית למשתמש בבסיס הנתונים עם 1000 גטונים
                        FBsingelton.getInstance().setDetails(name, 1000);

                        // מעבר למסך ה-Welcome
                        Intent intent = new Intent(getActivity(), WelcomActivity.class);
                        startActivity(intent);

                        // סגירת ה-Activity המארחת כדי שלא יהיה ניתן לחזור למסך ההרשמה
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        // הצגת סיבת הכישלון (למשל: אימייל כבר קיים או סיסמה חלשה מדי)
                        Toast.makeText(getContext(), "הרשמה נכשלה: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
