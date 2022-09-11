package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //layout and mines. Feel Free to change it, but make sure that it makes sense.
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int MINES_COUNT = 4;
    //clock.
    private int clock = 0;
    private boolean running = false;
    //extra variables, very important for minesweeper layout!!

    private boolean mining = true; //determines if a player is in a mining style, or flagging style.
    private int[][] mine_location = new int[ROW_COUNT][COLUMN_COUNT];
    private int[][] surroundings = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private ArrayList<Pair<Integer, Integer>> locations = new ArrayList<Pair<Integer, Integer>>();
    private boolean[][] visited = new boolean[ROW_COUNT][COLUMN_COUNT];

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
        cell_tvs = new ArrayList<TextView>();

        // Method (2): add four dynamically created cells. Used in lecture.
        // However, this is modified since I need to also initialize mine_location and visited.
        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
        running = true;

        for (int i = 0; i < ROW_COUNT; i++){
            for (int j = 0; j < COLUMN_COUNT; j++){
                mine_location[i][j] = 0;
                visited[i][j] = false;
            }
        }
        generate_mines();

        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j=0; j<COLUMN_COUNT; j++) {

                Log.d("Mine Counter square", String.valueOf(i) + " and " + String.valueOf(j) + " with total " + String.valueOf(mine_location[i][j]));
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

                if (mine_location[i][j] > 0 && mine_location[i][j] < 9){
                    tv.setText(String.valueOf(mine_location[i][j]));
                }
                cell_tvs.add(tv);
            }
        }

        TextView mimetype = (TextView) findViewById(R.id.modes);
        mimetype.setOnClickListener(this::onClickMode);
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
    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void generate_mines(){ //generate mines, but not surrounding it.
        Random rand = new Random();
        for (int i = 0; i < MINES_COUNT; i++){
            int x1 = rand.nextInt(ROW_COUNT);
            int x2 = rand.nextInt(COLUMN_COUNT);
            while (mine_location[x1][x2] == 1000000){ //contains mine
                x1 = rand.nextInt(ROW_COUNT);
                x2 = rand.nextInt(COLUMN_COUNT);
            }
            Pair<Integer, Integer> loc = new Pair<Integer, Integer>(x1, x2);
            locations.add(loc);

            mine_location[x1][x2] = 1000000;
            visited[x1][x2] = true; //there is no point visiting a mine exactly.
            total_squares--; //Do not include squares that are occupied by mines.
            //Log.d("Mine Location",String.valueOf(x1) + " " + String.valueOf(x2));
        }

        for (Pair<Integer, Integer> itr: locations){
            surround_mines_helper(itr.first, itr.second);
        }
    }

    public void surround_mines_helper(int x, int y){

        //get numbers surrounding a mine.
        for (int[] itr2: surroundings){
            int x1 = x;
            int y1 = y;
            int temp1 = itr2[0];
            int temp2 = itr2[1];
            //Log.d("x pos: ", String.valueOf(x1) + " + " + String.valueOf(temp1));
            //Log.d("y pos: ", String.valueOf(y1) + " + " + String.valueOf(temp2));
            x1 += temp1;
            y1 += temp2;
            if ((x1 >= 0 & x1 < ROW_COUNT) & (y1 >=0 & y1 < COLUMN_COUNT)){//legal range
                if (mine_location[x1][y1]!=1000000){ //we do not see a mine
                    mine_location[x1][y1]++;
                }
            }
        }
    }
    //Specifically clicking on the icon at the bottom.
    //That part is done.
    
    public void onClickMode(View view){
        TextView tv = (TextView) view;
        if (tv.getText().toString().equals("\uD83D\uDEA9")){
            tv.setText("⛏");
            mining = true;
        }
        else if (tv.getText().toString().equals("⛏")){
            tv.setText("\uD83D\uDEA9");
            mining = false;
        }
    }
    public void onClickTV(View view){

        //if you click the pickaxe or flag at the bottom, it changes mode.

        TextView tv = (TextView) view;
        TextView flag = (TextView) findViewById(R.id.flagcount);
        int flag_counter = Integer.parseInt(flag.getText().toString()); //grab an integer.

        if (tv.getCurrentTextColor() == Color.GREEN){
            //-16711936 for GREEN
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

                if (!tv.getText().toString().equals("\uD83D\uDEA9")){
                    //checks to see if there is no flag
                    //Log.d("Counter",String.valueOf(total_squares) + " squares remaining.");

                    //detecting mines

                    int n = findIndexOfCellTextView(tv);
                    int i = n/COLUMN_COUNT;
                    int j = n%COLUMN_COUNT;
                    if (mine_location[i][j] != 1000000){ //a user does not hit a mine But we will run DFS
                        DFS(i, j);

                        if (total_squares<=0){ //completes a game and finishes
                            final TextView timeView = (TextView) findViewById(R.id.timer);
                            String message = timeView.getText().toString();

                            String dummyresult = "win";

                            Intent intent = new Intent(this, Result.class);
                            intent.putExtra("com.example.sendmessage.MESSAGE", message);
                            intent.putExtra("com.example.sendmessage.STATUS", dummyresult);
                            startActivity(intent);

                        }
                    }
                    else{ //user hits a mine
                        tv.setTextColor(Color.RED);
                        tv.setBackgroundColor(Color.RED);
                        running = false;

                        final TextView timeView = (TextView) findViewById(R.id.timer);
                        String message = timeView.getText().toString();

                        String dummyresult = "lose";

                        Intent intent = new Intent(this, Result.class);
                        intent.putExtra("com.example.sendmessage.MESSAGE", message);
                        intent.putExtra("com.example.sendmessage.STATUS", dummyresult);
                        startActivity(intent);
                    }
                }
            }
            else{
                //we only place flags. THIS PART IS DONE
                if (tv.getText().toString().equals("\uD83D\uDEA9")){
                    int n = findIndexOfCellTextView(tv);
                    int i = n/COLUMN_COUNT;
                    int j = n%COLUMN_COUNT;
                    if (mine_location[i][j] != 1000000 && mine_location[i][j] != 0){ //has a mine near it
                        tv.setText(String.valueOf(mine_location[i][j]));
                    }
                    else{ //exclude it, but prevent a square mine from having a number.
                        tv.setText("");
                    }
                    flag_counter++;
                }
                else {
                    tv.setText("\uD83D\uDEA9");
                    flag_counter--;
                }
                flag.setText(String.valueOf(flag_counter));
            }
        }
    }

    private void DFS(int i, int j){

        //change square color.
        int indexofcell = COLUMN_COUNT * i + j;
        TextView tv = cell_tvs.get(indexofcell); //get a specified cell.
        tv.setTextColor(Color.GRAY);
        //-7829368 for Gray
        //-3355444 for LTgray
        tv.setBackgroundColor(Color.LTGRAY);

        //search

        for (int[] itr2: surroundings){
            int x1 = i;
            int y1 = j;
            int temp1 = itr2[0];
            int temp2 = itr2[1];
            x1 += temp1;
            y1 += temp2;

            if ((x1 >= 0 & x1 < ROW_COUNT) & (y1 >=0 & y1 < COLUMN_COUNT)){//legal range
                if (mine_location[x1][y1]==0 & !visited[x1][y1] & !tv.getText().toString().equals("\uD83D\uDEA9")){
                    tv.setTextColor(Color.GRAY);
                    //-7829368 for Gray
                    //-3355444 for LTgray
                    tv.setBackgroundColor(Color.LTGRAY);
                    //we do not see a mine or a flag.
                    //we can only expand if a square is clean.
                    visited[x1][y1] = true;
                    Log.d("Visited: ", String.valueOf(x1) + " + " + String.valueOf(y1));
                    total_squares--;
                    Log.d("Available: ", String.valueOf(total_squares));
                    DFS(x1, y1);
                }
                else if ((mine_location[x1][y1] > 0 & mine_location[x1][y1] < 1000000) & !visited[x1][y1] & !tv.getText().toString().equals("\uD83D\uDEA9")){
                    //basically we cannot open a square that is occupied by a flag
                    //we cannot open a square that is already visited
                    //we can open a square containing adjacent mine, but do not expand.
                    tv.setTextColor(Color.GRAY);
                    //-7829368 for Gray
                    //-3355444 for LTgray
                    tv.setBackgroundColor(Color.LTGRAY);
                    visited[x1][y1] = true;
                    total_squares--;
                    Log.d("Visited: ", String.valueOf(x1) + " + " + String.valueOf(y1));
                    Log.d("Available: ", String.valueOf(total_squares));
                }
            }
        }
    }
}