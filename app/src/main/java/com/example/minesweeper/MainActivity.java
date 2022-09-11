package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //layout and mines
    private static final int COLUMN_COUNT = 3;
    private static final int ROW_COUNT = 3;
    private static final int MINES_COUNT = 1;
    //clock
    private int clock = 0;
    private boolean running = false;
    //extra variables, very important for minesweeper layout!!

    private boolean mining = true; //determines if a player is in a mining style, or flagging style.
    private int[][] mine_location = new int[ROW_COUNT][COLUMN_COUNT];
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

        TextView mimetype = (TextView) findViewById(R.id.modes);
        mimetype.setOnClickListener(this::onClickMode);
        runTimer(); //run time, unless a player clicked a mine.
        generate_mines();
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

    public void generate_mines(){
        Random rand = new Random();
        for (int i = 0; i < MINES_COUNT; i++){
            int x1 = rand.nextInt(ROW_COUNT);
            int x2 = rand.nextInt(COLUMN_COUNT);
            while (mine_location[x1][x2] == 1000000){ //contains mine
                x1 = rand.nextInt(ROW_COUNT);
                x2 = rand.nextInt(COLUMN_COUNT);
            }
            mine_location[x1][x2] = 1000000;
            total_squares--; //Do not include squares that are occupied by mines.
            Log.d("Mine Location",String.valueOf(x1) + " " + String.valueOf(x2));
        }
    }
    //Specifically clicking on the icon at the bottom.
    //That part is done.
    
    public void onClickMode(View view){
        TextView tv = (TextView) view;
        if (tv.getText().toString().equals("🚩")){
            tv.setText("⛏");
            mining = true;
        }
        else if (tv.getText().toString().equals("⛏")){
            tv.setText("🚩");
            mining = false;
        }
    }
    public void onClickTV(View view){

        //if you click the pickaxe or flag at the bottom, it changes mode.

        TextView tv = (TextView) view;
        TextView flag = (TextView) findViewById(R.id.flagcount);
        int flag_counter = Integer.parseInt(flag.getText().toString()); //grab an integer.
        if (tv.getCurrentTextColor() == Color.GREEN){
            if (mining){

                /*
                This part is where it gets very difficult. Requires to use DFS algorithm.
                We need to know what happens if a player clicked a mine. If that is a case,
                head to the intent.
                We need to deal with the barriers. If we go out of bounds, then that is a problem.
                We will need to use data structures, like a 2D array. Maybe that can help. But we
                also need to store the mines location.
                DFS will be hard. I will need to get very good planning skills, like CSCI 104...
                 */
                //If we are mining, then we color this as gray. Otherwise, do not color but place a flag.

                if (tv.getText().toString().equals("")){
                    //checks to see if there is no flag
                    total_squares--; //we dug a square.
                    //Log.d("Counter",String.valueOf(total_squares) + " squares remaining.");

                    //detecting mines

                    int n = findIndexOfCellTextView(tv);
                    int i = n/COLUMN_COUNT;
                    int j = n%COLUMN_COUNT;
                    if (mine_location[i][j] != 1000000){ //a user does not hit a mine
                        tv.setTextColor(Color.GRAY);
                        tv.setBackgroundColor(Color.LTGRAY);

                        if (total_squares==0){
                            final TextView timeView = (TextView) findViewById(R.id.timer);
                            String message = timeView.getText().toString();

                            Intent intent = new Intent(this, Result.class);
                            intent.putExtra("com.example.sendmessage.MESSAGE", message);
                            startActivity(intent);
                        }
                    }
                    else{ //user hits a mine
                        tv.setTextColor(Color.RED);
                        tv.setBackgroundColor(Color.RED);
                        running = false;

                        final TextView timeView = (TextView) findViewById(R.id.timer);
                        String message = timeView.getText().toString();

                        Intent intent = new Intent(this, Losing.class);
                        intent.putExtra("com.example.sendmessage.MESSAGE", message);
                        startActivity(intent);
                    }
                }
            }
            else{
                //we only place flags. THIS PART IS DONE
                if (tv.getText().toString().equals("")){
                    tv.setText("🚩");
                    flag_counter--;
                }
                else {
                    tv.setText("");
                    flag_counter++;
                }
                flag.setText(String.valueOf(flag_counter));
            }
        }
    }
}