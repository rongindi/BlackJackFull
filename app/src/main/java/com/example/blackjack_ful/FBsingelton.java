package com.example.blackjack_ful;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// google explanations
// https://firebase.google.com/docs/database/android/lists-of-data#java_1


public class FBsingelton {
    private static FBsingelton instance;

    FirebaseDatabase database;

    private FBsingelton() {
        database = FirebaseDatabase.getInstance();

        // read the records from the Firebase and order them by the record from highest to lowest
        // limit to only 8 items
        Query myQuery = database.getReference("records").orderByChild("score").limitToLast(10);

        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                database = FirebaseDatabase.getInstance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public static FBsingelton getInstance() {
        if (null == instance) {
            instance = new FBsingelton();
        }
        return instance;
    }

    public void setName(String name)
    {
        // Write a message to the database
        //DatabaseReference myRef = database.getReference("records").push(); // push adds new node with unique value

        DatabaseReference myRef = database.getReference("records/" + FirebaseAuth.getInstance().getUid() + "/MyName");

        myRef.setValue(name);
    }

    public void setDetails(int tokens)
    {
        // Write a message to the database
        //DatabaseReference myRef = database.getReference("records").push(); // push adds new node with unique value

        DatabaseReference myRef = database.getReference("records/" + FirebaseAuth.getInstance().getUid() + "/MyDetails");

        com.example.blackjack_ful.MyDetailsInFb rec = new com.example.blackjack_ful.MyDetailsInFb(tokens);
        myRef.setValue(rec);
    }
}

