package com.jagtar.animalvsfruit;

import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FinalScore extends AppCompatActivity {
    TextView winn;
    TextView scr;
    String name;
    String score;
    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_score);

        winn = (TextView) findViewById(R.id.winner);
        scr = (TextView) findViewById(R.id.score);

        database = FirebaseDatabase.getInstance();
            myRef = database.getReference("device");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);
                    Log.d("fbdb", name);
                    winn.setText("Winner is " + name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        myRef2 = database.getReference("score");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                score = dataSnapshot.getValue(String.class);
                scr.setText("Score " + score);
                Log.d("fbdb", score);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void restart(View view) {
        Intent Mainac = new Intent(this, MainActivity.class);
        //Optional parameters
        startActivity(Mainac);
    }
}
