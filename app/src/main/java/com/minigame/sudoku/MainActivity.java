package com.minigame.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final int edgeSize = BoardAdapter.edgeSize;
    private BoardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rvGameBoard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, edgeSize));
        adapter = new BoardAdapter(this, null);
        recyclerView.setAdapter(adapter);

    }
}