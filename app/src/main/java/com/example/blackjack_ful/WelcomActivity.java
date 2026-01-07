package com.example.blackjack_ful;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class WelcomActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogout, btnSave, btnPlay;
    private EditText etTokens;
    private TextView tvMyName;

    private FirebaseAuth mAuth;
    com.example.blackjack_ful.FBsingelton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialization();
    }

    private void initialization() {
        // initialize

        fb = com.example.blackjack_ful.FBsingelton.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etTokens = findViewById(R.id.etTokens);


        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        tvMyName = findViewById(R.id.tvMyName);

/*        if(mAuth != null)
        {
            tvMyName.setText(mAuth.getCurrentUser().getEmail());
        }*/

    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            int tokens = Integer.parseInt(etTokens.getText().toString());

            fb.setDetails(tokens);
        }
        if(v == btnLogout)
        {
            FirebaseAuth.getInstance().signOut();
            finish(); // close the activity
        }
        if (v == btnPlay)
        {
            Intent intent = new Intent(WelcomActivity.this, GameActivity.class);
            startActivity(intent);
        }

    }

    public void userDataChange(String name) {
        System.out.println(name);
        tvMyName.setText("my Name: " + name);

    }
}