package com.jagtar.animalvsfruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class FinalScore extends AppCompatActivity {


    int Score = 0;
    String Winner;
    TextView winn;
    TextView scr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_score);

        winn = (TextView) findViewById(R.id.winner);
        scr = (TextView) findViewById(R.id.score);


        Intent intent = getIntent();
        String name = intent.getStringExtra("device");
        String score = intent.getStringExtra("score");

        winn.setText("Winner is " + name);
           scr.setText("Score " + score);


    }

    public void restart(View view) {
        Intent Mainac = new Intent(this, MainActivity.class);
        //Optional parameters
        startActivity(Mainac);
    }
}
