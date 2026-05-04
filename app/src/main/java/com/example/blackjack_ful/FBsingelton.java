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
        //Query myQuery = database.getReference("details").orderByChild("chips").limitToLast(10);
        Query myQuery = database.getReference("details");

        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                database = FirebaseDatabase.getInstance();
                MainActivity.records.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    //String str =userSnapshot.child()  .getValue(Record.class);
                    MyDetailsInFb myDetailsInFb =userSnapshot.getValue(MyDetailsInFb.class);
                    MainActivity.records.add(0, myDetailsInFb);
                }
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

/*    public void setName(String name)
    {
        // Write a message to the database
        //DatabaseReference myRef = database.getReference("records").push(); // push adds new node with unique value

        DatabaseReference myRef = database.getReference("details/" + FirebaseAuth.getInstance().getUid());

        myRef.setValue(name);
    }*/

    public void setDetails(String name, int chips)
    {

        DatabaseReference myRef = database.getReference("details/" + FirebaseAuth.getInstance().getUid());

       // com.example.blackjack_ful.MyDetailsInFb rec = new com.example.blackjack_ful.MyDetailsInFb(tokens);
        MyDetailsInFb myDetailsInFb = new MyDetailsInFb(name,chips);
        myRef.setValue(myDetailsInFb);
    }
}

