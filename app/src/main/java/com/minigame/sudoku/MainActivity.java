package com.minigame.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int edgeSize = BoardAdapter.edgeSize;
    private BoardAdapter adapter;
    private Button[] numButtons;
    private Button btnNote, btnErase, btnUndo, btnDemo;

    private boolean isNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rvGameBoard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, edgeSize));
        adapter = new BoardAdapter(this, null);
        recyclerView.setAdapter(adapter);

        numButtons = new Button[edgeSize];
        int buttonId[] = new int[]{R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for(int i = 0; i < edgeSize; i++){
            numButtons[i] = findViewById(buttonId[i]);
            numButtons[i].setOnClickListener(numButtonListener);
        }

        btnNote = findViewById(R.id.btnPen);
        btnNote.setOnClickListener(noteListener);

        btnErase = findViewById(R.id.btnErase);
        btnErase.setOnClickListener(eraseListener);

        btnUndo = findViewById(R.id.btnUndo);
        btnUndo.setOnClickListener(undoListener);

        btnDemo = findViewById(R.id.btnAIDemo);
        btnDemo.setOnClickListener(demoListener);

        isNote = false;
    }

    private View.OnClickListener numButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int whichNumber = Integer.parseInt(((TextView) v).getText().toString());
            adapter.FillNumber(isNote, whichNumber);
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
            Toast.makeText(getApplicationContext(), "UNDO NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener demoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "DEMO NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menuRestart:
                Toast.makeText(getApplicationContext(), "RESTART NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuNewGame:
                Toast.makeText(getApplicationContext(), "NEW GAME NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuStatistics:
                Toast.makeText(getApplicationContext(), "STAT NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSettings:
                Toast.makeText(getApplicationContext(), "SETTINGS NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}