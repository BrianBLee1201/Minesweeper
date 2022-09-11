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

        //Intent intent = getIntent();
        //String message = intent.getStringExtra("com.example.sendmessage.MESSAGE");
        String message = "Tell me!";

        TextView textView = (TextView) findViewById(R.id.secondsused);
        //you get object by id
        //each activity corresponds to a single HTML page
        textView.setText(message);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
