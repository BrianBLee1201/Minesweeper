package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //layout and mines
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int MINES_COUNT = 4;
    //clock
    private int clock = 0;
    private boolean running = false;

    //extra variables, very important for minesweeper layout!!

    //determining if winning or losing

    private int total_squares = COLUMN_COUNT * ROW_COUNT;

    //this one is important for initializing text upon creation near the mines.

    //second dfs run automatically within mines. But is dfs necessary, or just use for loop and surround??
    //backtracking and fjs
    //mine, surrounding the text

    //creating mine, use DFS to set numbers.
    //but upon playing, use DFS to expand on click.


    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //allocation

        cell_tvs = new ArrayList<TextView>();

        // Method (2): add four dynamically created cells. Used in lecture
        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
        running = true;

        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j=0; j<COLUMN_COUNT; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = androidx.gridlayout.widget.GridLayout.spec(j);

                grid.addView(tv, lp);

                //extra

                cell_tvs.add(tv);
            }
        }
        runTimer(); //run time, unless a player clicked a mine.
    }

    private void runTimer() { //used in lecture; not plagiarized.
        final TextView timeView = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock%60;
                int minutes = (clock % 3600) / 60;
                int total = seconds + minutes * 60;
                //implement later
                if (total >= 1000){
                    total = 99;
                }
                String time = String.valueOf(total);
                if (total < 10){
                    time = "0" + time;
                }
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
    private int findIndexOfCellTextView(TextView tv) { //helper function; not plagiarized.
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    // This part is tricky.

    //During clicking
    //setOnClickListener(this::onClickNode);??

    public void onClickTV(View view){
        TextView tv = (TextView) view;

        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);

        //detecting mines

        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
    }
}