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
    private static final int COLUMN_COUNT = 4;
    private static final int ROW_COUNT = 4;
    private static final int MINES_COUNT = 1;
    //clock
    private int clock = 0;
    private boolean running = false;
    //extra variables
    private HashMap<TextView, int[]> map;
    private TextView[][] array_map;
    private HashSet<TextView> mines;
    private int[][] mines_map; //important for DFS. you stop expanding if you see a number.
    private HashSet<TextView> visited; //part of DFS
    private int[][] surroundings = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

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
        array_map = new TextView[ROW_COUNT][COLUMN_COUNT];
        mines_map = new int[ROW_COUNT][COLUMN_COUNT];

        // Method (2): add four dynamically created cells
        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
        running = true;

        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j=0; j<COLUMN_COUNT; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = androidx.gridlayout.widget.GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
                array_map[i][j] = tv;
            }
        }
        gen_mines();
        runTimer();
    }

    private void runTimer() {
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

    //During clicking
    //setOnClickListener(this::onClickNode);

    public void changetype(View view){

    }

    private int findIndexOfCellTextView(TextView tv) { //helper function
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }
    public void onClickTV(View view){
        TextView tv = (TextView) view;
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);

        //detecting mines

        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        if (mines.contains(array_map[i][j])){ //you hit a mine
            running = false;
            tv.setTextColor(Color.RED);
            tv.setBackgroundColor(Color.parseColor("red"));
        }

        else{
            int totalmine = mine_surround_helper(i, j);
            if (totalmine != 0){
                tv.setText(String.valueOf(totalmine)); //do not expand
            }
            else{ //empty square with no mines
                visited = new HashSet<TextView>();
                DFS(i, j, view);
            }
        }
    }

    //FIGURE OUT WHAT HAPPENS ON THE BOTTOM

    public void gen_mines(){ //randomly generate mines
        mines = new HashSet<TextView>();
        Random rand = new Random();
        for (int i = 0; i < MINES_COUNT; i++){
            int num1 = rand.nextInt((ROW_COUNT-1));
            int num2 = rand.nextInt((COLUMN_COUNT-1));
            if (!mines.isEmpty()){
                if (mines.contains(array_map[num1][num2])) {
                    num1 = rand.nextInt((ROW_COUNT-1));
                    num2 = rand.nextInt((COLUMN_COUNT-1));
                }
            }
            mines.add(array_map[num1][num2]);
            //debug purposes
            Log.d("Number Generated",String.valueOf(num1) + " and " + String.valueOf(num2));

        }
        for (int i = 0; i < MINES_COUNT; i++){
            for (int j = 0; j < COLUMN_COUNT; j++){
                if (mines.contains(array_map[i][j])){
                    mine_surround_helper(i,j);
                }
            }
        }
    }

    private int mine_surround_helper (int i, int j){
        int minesme = 0;
        for (int[] temp: surroundings){
            int dummyx = i + temp[0];
            int dummyy = j + temp[1];
            if ((dummyx >= 0 & dummyy >= 0) & (dummyx < ROW_COUNT & dummyy < COLUMN_COUNT)){
                if (mines.contains(array_map[dummyx][dummyy])){
                    minesme++;
                }
            }
        }
        mines_map[i][j] = minesme;
        //debug purposes
        Log.d("Neighbors Generated in " + String.valueOf(i) + " and " + String.valueOf(j), String.valueOf(minesme));
        return minesme;
    }



    public void DFS(int i, int j, View view){
        //another data structure. Handle bacnkend. You can store number and grab grid number.
        //GUI. Create another data structure.
        TextView tv = (TextView) view;
        if ((i >= 0 && i < ROW_COUNT) && (j >= 0 && j < COLUMN_COUNT)){ //valid range
            if (!visited.contains(array_map[i][j])){ //expand if we did not discover and is an empty square
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.LTGRAY);
                visited.add(array_map[i][j]);
                if (mines_map[i][j]==0){
                    for (int[] temp: surroundings){
                        DFS((i + temp[0]), (j + temp[1]), view);
                    }
                }
            }
        }
    }
    //settext, gettext.
}