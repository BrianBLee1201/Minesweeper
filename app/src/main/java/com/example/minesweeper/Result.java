package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String message = intent.getStringExtra("com.example.sendmessage.MESSAGE");
        message = "Used " + message + " seconds.";

        TextView textView = findViewById(R.id.seconds_used);
        //you get object by id
        //each activity corresponds to a single HTML page
        textView.setText(message);

        String status = intent.getStringExtra("com.example.sendmessage.STATUS");
        if (status.equals("win")){
            TextView textView2 = findViewById(R.id.won);
            textView2.setText("You won.");
            TextView textView3 = findViewById(R.id.compliment);
            textView3.setText("Good Job!");
        }
        else if (status.equals("lose")){
            TextView textView2 = findViewById(R.id.won);
            textView2.setText("You lost.");
            TextView textView3 = findViewById(R.id.compliment);
            textView3.setText("Nice Try!");
        }
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
