package com.minigame.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minigame.sudoku.GameSolver.GameSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: add step by step solve visualizer

public class Playground extends AppCompatActivity {
    private Button[] numButtons;
    private BoardAdapter adapter;
    private Button btnSolver, btnErase, btnDemo, btnClear;

    private GameSolver solver;

    private AlertDialog.Builder demoAlgoSelector;

    private List<List<Integer>> BOARD_DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        setTitle("Playground");

        solver = new GameSolver();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // debug purpose
        BOARD_DEBUG = new ArrayList<List<Integer>>(){};
        BOARD_DEBUG.add(Arrays.asList(6, 0, 1, 0, 0, 2, 0, 8, 0));
        BOARD_DEBUG.add(Arrays.asList(9, 0, 0, 0, 4, 0, 1, 0, 7));
        BOARD_DEBUG.add(Arrays.asList(0, 8, 0, 0, 0, 0, 0, 0, 0));
        BOARD_DEBUG.add(Arrays.asList(0, 2, 0, 9, 0, 0, 0, 0, 0));
        BOARD_DEBUG.add(Arrays.asList(0, 0, 5, 6, 0, 8, 3, 0, 0));
        BOARD_DEBUG.add(Arrays.asList(0, 0, 0, 0, 0, 4, 0, 5, 0));
        BOARD_DEBUG.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 3, 0));
        BOARD_DEBUG.add(Arrays.asList(5, 0, 8, 0, 2, 0, 0, 0, 1));
        BOARD_DEBUG.add(Arrays.asList(0, 4, 0, 7, 0, 0, 8, 0, 6));

        RecyclerView recyclerView = findViewById(R.id.pg_rvGameBoard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SudokuUtils.EDGE_SIZE));
        adapter = new BoardAdapter(this, null);
        recyclerView.setAdapter(adapter);

        numButtons = new Button[SudokuUtils.EDGE_SIZE];
        int buttonId[] = new int[]{R.id.pg_btn1, R.id.pg_btn2, R.id.pg_btn3, R.id.pg_btn4, R.id.pg_btn5, R.id.pg_btn6, R.id.pg_btn7, R.id.pg_btn8, R.id.pg_btn9};
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            numButtons[i] = findViewById(buttonId[i]);
            numButtons[i].setOnClickListener(numButtonListener);
        }

        demoAlgoSelector = new AlertDialog.Builder(this);
        demoAlgoSelector.setTitle("Choose an algorithm:");
        demoAlgoSelector.setSingleChoiceItems(GameSolver.SOLVER_ALGORITHM, GameSolver.SOLVER_ALGORITHM.length - 1, null);
        demoAlgoSelector.setPositiveButton("OK", (dialog, which) -> {
            ListView listView = ((AlertDialog) dialog).getListView();
            String selectedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition()).toString();
            List<List<Integer>> solution = solver.Solve(selectedItem, adapter.GetBoard());
            Log.d("playground", "board solved");
            if (solution != null){
                adapter.UpdateBoard(solution);
            }
            else {
                Toast.makeText(getApplicationContext(), "No solution", Toast.LENGTH_SHORT).show();
            }
            Log.d("playground", "board updated");

        });
        demoAlgoSelector.setNegativeButton("Cancel", null);

        btnSolver = findViewById(R.id.pg_btnSolve);
        btnSolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoAlgoSelector.show();
            }
        });


        btnErase = findViewById(R.id.pg_btnErase);
        btnErase.setOnClickListener(eraseListener);

        btnDemo = findViewById(R.id.pg_btnDemo);
        btnDemo.setOnClickListener(demoListener);

        btnClear = findViewById(R.id.pg_btnClear);
        btnClear.setOnClickListener(clearListener);

    }

    private View.OnClickListener numButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int whichNumber = Integer.parseInt(((TextView) v).getText().toString());
            adapter.FillNumber(false, whichNumber);
        }
    };

    private View.OnClickListener demoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.UpdateBoard(BOARD_DEBUG);
        }
    };
    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.UpdateBoard(null);
        }
    };

    private View.OnClickListener eraseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.ClearNumber();
        }
    };

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else {
            ret = super.onOptionsItemSelected(item);
        }
        return ret;
    }
}