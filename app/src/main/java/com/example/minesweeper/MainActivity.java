package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //***INITIALIZATION***

    //layout and mines. Feel Free to change it, but make sure that it makes sense.
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int MINES_COUNT = 4;
    //clock.
    private int clock = 0;
    private boolean running = false;
    //relevant variables, very important for minesweeper layout!!
    private boolean mining = true; //determines if a player is in a mining style, or flagging style.
    private final int[][] mine_location = new int[ROW_COUNT][COLUMN_COUNT]; //creates a table that shows which location has mines.
    private final int[][] surroundings = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private final ArrayList<Pair<Integer, Integer>> locations = new ArrayList<>();
    private final boolean[][] visited = new boolean[ROW_COUNT][COLUMN_COUNT]; //very important for DFS.
    private int total_squares = COLUMN_COUNT * ROW_COUNT;

    //***END OF INITIALIZATION***

    private ArrayList<TextView> cell_tvs;
    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cell_tvs = new ArrayList<>();

        // Method (2): add four dynamically created cells. Used in lecture.
        // However, this is modified since I need to also initialize mine_location and visited.
        androidx.gridlayout.widget.GridLayout grid = findViewById(R.id.gridLayout01);
        running = true;
        TextView flag = findViewById(R.id.flagcount);
        flag.setText(String.valueOf(MINES_COUNT));

        for (int i = 0; i < ROW_COUNT; i++){
            for (int j = 0; j < COLUMN_COUNT; j++){
                mine_location[i][j] = 0;
                visited[i][j] = false;
            }
        }
        generate_mines();

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

                if (mine_location[i][j] > 0 && mine_location[i][j] < 9){
                    //After generating mines, set text.
                    tv.setText(String.valueOf(mine_location[i][j]));
                }
                cell_tvs.add(tv);
            }
        }

        TextView mimetype = findViewById(R.id.modes);
        mimetype.setOnClickListener(this::onClickMode); //allows users to change modes.

        runTimer(); //run time, unless a player clicked a mine.
    }


    private void runTimer() { //used in lecture
        final TextView timeView = findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock%60;
                int minutes = (clock % 3600) / 60;
                int total = seconds + minutes * 60;

                //This part is where I modify.

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

    //***IMPORTANT FUNCTIONS LISTED HERE FOR GAMEPLAY***

    public void generate_mines(){ //generate mines, but not surrounding it.
        Random rand = new Random();

        //GENERATE MINES

        for (int i = 0; i < MINES_COUNT; i++){
            int x1 = rand.nextInt(ROW_COUNT);
            int x2 = rand.nextInt(COLUMN_COUNT);
            while (mine_location[x1][x2] == 1000000){ //contains mine. Need to avoid duplicates.
                x1 = rand.nextInt(ROW_COUNT);
                x2 = rand.nextInt(COLUMN_COUNT);
            }
            Pair<Integer, Integer> loc = new Pair<>(x1, x2);
            locations.add(loc);

            mine_location[x1][x2] = 1000000;
            visited[x1][x2] = true; //there is no point visiting a mine exactly.
            total_squares--; //Do not include squares that are occupied by mines.
        }

        //GENERATE NUMBERS SURROUNDING THE MINES

        for (Pair<Integer, Integer> itr: locations){
            surround_mines_helper(itr.first, itr.second);
        }
    }

    public void surround_mines_helper(int x, int y){
        //get numbers surrounding a mine. There are 8 directions total.
        for (int[] itr2: surroundings){
            int x1 = x;
            int y1 = y;
            int temp1 = itr2[0];
            int temp2 = itr2[1];
            x1 += temp1;
            y1 += temp2;
            if ((x1 >= 0 && x1 < ROW_COUNT) && (y1 >=0 && y1 < COLUMN_COUNT)){//legal range
                if (mine_location[x1][y1]!=1000000){ //we do not see a mine
                    mine_location[x1][y1]++;
                }
            }
        }
    }
    public void onClickMode(View view){ //CHANGE MODE TYPE
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
        TextView flag = findViewById(R.id.flagcount);
        int flag_counter = Integer.parseInt(flag.getText().toString()); //grab an integer.

        if (tv.getCurrentTextColor() == Color.GREEN){ //You only click green squares. Clicking gray does nothing.
            if (mining){ //mining mode
                if (!tv.getText().toString().equals("\uD83D\uDEA9")){
                    //checks to see if there is no flag
                    //detecting mines

                    int n = findIndexOfCellTextView(tv);
                    int i = n/COLUMN_COUNT;
                    int j = n%COLUMN_COUNT;
                    if (mine_location[i][j] != 1000000){ //a user does not hit a mine But we will run DFS
                        DFS(i, j);

                        if (total_squares<=0){ //completes a game and finishes
                            final TextView timeView = findViewById(R.id.timer);
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

                        //Send to the results screen.

                        final TextView timeView = findViewById(R.id.timer);
                        String message = timeView.getText().toString();
                        String dummyresult = "lose";

                        Intent intent = new Intent(this, Result.class);
                        intent.putExtra("com.example.sendmessage.MESSAGE", message);
                        intent.putExtra("com.example.sendmessage.STATUS", dummyresult);
                        startActivity(intent);
                    }
                }
            }
            else{//we only place flags.
                int n = findIndexOfCellTextView(tv);
                int i = n/COLUMN_COUNT;
                int j = n%COLUMN_COUNT;
                TextView tv2 = cell_tvs.get(COLUMN_COUNT * i +j); //specify which cell.

                if (tv.getText().toString().equals("\uD83D\uDEA9")){ //remove a flag
                    if (mine_location[i][j] != 1000000 && mine_location[i][j] != 0){ //has a mine near it
                        tv.setText(String.valueOf(mine_location[i][j]));
                        tv2.setText(String.valueOf(mine_location[i][j]));
                    }
                    else{ //exclude it, but prevent a square mine from having a number.
                        tv.setText("");
                        tv2.setText("");
                    }
                    flag_counter++;
                }
                else { //set a flag
                    tv.setText("\uD83D\uDEA9");
                    tv2.setText("\uD83D\uDEA9");
                    flag_counter--;
                }
                flag.setText(String.valueOf(flag_counter)); //update counter
            }
        }
    }

    private void DFS(int i, int j){
        TextView tv = cell_tvs.get(COLUMN_COUNT * i + j); //get a specified cell.
        if (tv.getText().toString().equals("\uD83D\uDEA9")){ //if we are at a flag, they do not count.
            visited[i][j] = false;
            return;
        }
        //change square color and updating it.
        total_squares--;
        visited[i][j] = true;
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);
        if ((mine_location[i][j] > 0 && mine_location[i][j] < 1000000) && !tv.getText().toString().equals("\uD83D\uDEA9")) {
            return;
            //do not expand if we are in one square with a number.
        }
        //search time
        for (int[] itr2: surroundings){
            int x1 = i;
            int y1 = j;
            x1 += itr2[0];
            y1 += itr2[1];

            if ((x1 >= 0 && x1 < ROW_COUNT) && (y1 >=0 && y1 < COLUMN_COUNT)){//legal range
                if (!visited[x1][y1]){
                    //we can only expand if a square is not visited.
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.LTGRAY);
                    DFS(x1, y1);
                }
            }
        }
    }

    //***END OF INITIALIZING IMPORTANT FUNCTIONS***
}