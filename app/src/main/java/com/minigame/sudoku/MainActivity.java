package com.minigame.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.minigame.sudoku.GameSolver.GameSolver;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private BoardAdapter adapter;
    private Button[] numButtons;
    private Button btnNote, btnErase, btnUndo, btnHint;

    private boolean isNote;

    private GameGenerator generator;
    private GameSolver solver;

    private AlertDialog.Builder levelSelector, demoAlgoSelector, generatorSelector, gameendSelector;

    // https://www.baeldung.com/java-asynchronous-programming
    private ExecutorService tGenerateNewBoard;
    private Future<Long> futureTask;
    private List<List<Integer>> newboard_filled;
    private List<List<Integer>> board_solution;
    private List<List<Integer>> board_tofill;

    private DbHelper database;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generator = new GameGenerator();
        solver = new GameSolver();

        tGenerateNewBoard = Executors.newCachedThreadPool();
        futureTask = null;
        newboard_filled = null;
        Log.d("main", "database");
        database = new DbHelper(this);
        Log.d("main", "database done");

        board_solution = database.LoadRandomBoard();
        if (board_solution == null) {
            board_solution = SudokuUtils.GetEmptyBoard();
            board_tofill = SudokuUtils.GetEmptyBoard();
            database.backgroundExecutor.submit(() ->database.AddDefaultBoards());
        }
        else {
            board_tofill = generator.GenerateSolvableBoard(board_solution, SudokuUtils.LEVELS[1]);
        }

        RecyclerView recyclerView = findViewById(R.id.rvGameBoard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SudokuUtils.EDGE_SIZE));
        adapter = new BoardAdapter(this, board_tofill);
        recyclerView.setAdapter(adapter);

        numButtons = new Button[SudokuUtils.EDGE_SIZE];
        int buttonId[] = new int[]{R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            numButtons[i] = findViewById(buttonId[i]);
            numButtons[i].setOnClickListener(numButtonListener);
        }

        btnNote = findViewById(R.id.btnPen);
        btnNote.setOnClickListener(noteListener);

        btnErase = findViewById(R.id.btnErase);
        btnErase.setOnClickListener(eraseListener);

        btnUndo = findViewById(R.id.btnUndo);
        btnUndo.setOnClickListener(undoListener);

        btnHint = findViewById(R.id.btnHint);
        btnHint.setOnClickListener(hintListener);

        isNote = false;
        // Log.d("main", "level selector");
        levelSelector = new AlertDialog.Builder(this);
        levelSelector.setTitle("Choose a level:");
        levelSelector.setSingleChoiceItems(SudokuUtils.LEVELS, 0, null);
        levelSelector.setPositiveButton("OK", (dialog, which) -> {
            ListView listView = ((AlertDialog) dialog).getListView();
            String selectedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition()).toString();
            board_solution = database.LoadRandomBoard();
            if (board_solution == null){
                Toast.makeText(getApplicationContext(), "Still generating unique boards...", Toast.LENGTH_LONG).show();
            }
            else {
                board_tofill = generator.GenerateSolvableBoard(board_solution, selectedItem);
                adapter.UpdateBoard(board_tofill);
            }

        });
        levelSelector.setNegativeButton("Cancel", null);

        // Log.d("main", "demo algo selector");
        demoAlgoSelector = new AlertDialog.Builder(this);
        demoAlgoSelector.setTitle("Choose an algorithm:");
        demoAlgoSelector.setSingleChoiceItems(GameSolver.SOLVER_ALGORITHM, 0, null);
        demoAlgoSelector.setPositiveButton("OK", (dialog, which) -> {
            ListView listView = ((AlertDialog) dialog).getListView();
            String selectedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition()).toString();
            List<List<Integer>> solution = solver.Solve(selectedItem, adapter.GetBoard());
            if (solution != null){
                adapter.UpdateBoard(solution);
            }
            else {
                Toast.makeText(getApplicationContext(), "No solution", Toast.LENGTH_SHORT).show();
            }
        });
        demoAlgoSelector.setNegativeButton("Cancel", null);

        // Log.d("main", "generator selector");
        generatorSelector = new AlertDialog.Builder(this);
        generatorSelector.setTitle("Load board generating in background?");
        // load background
        generatorSelector.setPositiveButton("Load", (dialog, which) -> {
            // if generated already
            if (newboard_filled != null){
                adapter.UpdateBoard(newboard_filled);
            }
            // if there is no previous generated
            else if (futureTask == null){
                futureTask = (Future<Long>) tGenerateNewBoard.submit(() -> GenerateBoard_NewThread());
                Toast.makeText(getApplicationContext(), "Generate a new board - no request was made", Toast.LENGTH_LONG).show();
            }
            // if it is still generating
            else if (!futureTask.isDone()){
                Toast.makeText(getApplicationContext(), "Still generating", Toast.LENGTH_LONG).show();
            }

            else { // DEBUG
                Log.d("Generator", "should not get into here");
            }

        });
        generatorSelector.setNegativeButton("Generate new", new DialogInterface.OnClickListener() { // generate new
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (futureTask == null || futureTask.isDone()){
                    futureTask = (Future<Long>) tGenerateNewBoard.submit(() -> GenerateBoard_NewThread());
                }
                else {
                    Toast.makeText(getApplicationContext(), "A new board is generating...", Toast.LENGTH_LONG).show();
                }

            }
        });
        generatorSelector.setNeutralButton("Cancel", null);

        // game end message
        gameendSelector = new AlertDialog.Builder(this);
        gameendSelector.setTitle("Congratulation! \nPlease select puzzle difficulty:");
        gameendSelector.setSingleChoiceItems(SudokuUtils.LEVELS, 0, null);
        gameendSelector.setPositiveButton("OK", (dialog, which) -> {
            ListView listView = ((AlertDialog) dialog).getListView();
            String selectedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition()).toString();
            board_solution = database.LoadRandomBoard();
            if (board_solution == null){
                Toast.makeText(getApplicationContext(), "Generating unique boards...", Toast.LENGTH_LONG).show();
            }
            else {
                board_tofill = generator.GenerateSolvableBoard(board_solution, selectedItem);
                adapter.UpdateBoard(board_tofill);
            }

        });
        gameendSelector.setNegativeButton("Cancel", null);


        // TODO: change this to generate a few at set up time?
        // database.backgroundExecutor.submit(() ->database.AddDefaultBoards());
    }

    // created to call in new thread
    private void GenerateBoard_NewThread(){
        newboard_filled = generator.Generator_DLX();
        database.WriteBoardToDb(DbHelper.ListToString(newboard_filled));
        Log.d("Generator", "board generated!");
    }

    private View.OnClickListener numButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int whichNumber = Integer.parseInt(((TextView) v).getText().toString());
            adapter.FillNumber(isNote, whichNumber);
            if (adapter.isFinished(board_solution)) {
                gameendSelector.show();
            }
        }
    };

    private View.OnClickListener noteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isNote = !isNote;
            Button btn = (Button) v;
            if (isNote){
                btn.setTextColor(getColor(R.color.colorPrimary));
            }
            else {
                btn.setTextColor(getColor(R.color.black));
            }
        }
    };

    private View.OnClickListener eraseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.ClearNumber();
        }
    };

    private View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.Revert();
        }
    };

    private View.OnClickListener hintListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "HINT NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
            adapter.SetHint(board_solution);
            if (adapter.isFinished(board_solution)) {
                gameendSelector.show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // TODO: onPause/onResume save state

    // TODO: portrait-landscape views

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menuPlayground:
                intent = new Intent(getApplicationContext(), Playground.class);
                startActivity(intent);
                break;

            case R.id.menuRestart:
                adapter.UpdateBoard(board_tofill);
                break;

            case R.id.menuNewGame:
                levelSelector.show();
                break;

            case R.id.menuGenerate:
                generatorSelector.show();
                break;

            case R.id.menuSolver:
                demoAlgoSelector.show();
                break;

            case R.id.menuStatistics:
                // TODO: stat, sql store how game played/finished/time/etc...
                Toast.makeText(getApplicationContext(), "STAT NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSettings:
                // TODO: settings, change theme, clear data, etc
                Toast.makeText(getApplicationContext(), "SETTINGS NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}