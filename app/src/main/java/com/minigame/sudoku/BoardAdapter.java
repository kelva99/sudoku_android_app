package com.minigame.sudoku;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<List<Integer>> board;
    private List<List<ViewHolder>> boardViews;
    private LayoutInflater layoutInflater;
    private int selectedRow, selectedCol;

    private int selectedColor;
    public static final int edgeSize = 9;
    public BoardAdapter(Context context, List<List<Integer>> gameboard) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.board = gameboard;
        // debug
        if (board == null){
            board = new ArrayList<List<Integer>>();
            for(int i = 0; i < edgeSize; i++){
                List<Integer> l = new ArrayList<Integer>();
                for(int j = 0; j < edgeSize; j++){
                    l.add(0);
                }
                board.add(l);
            }
        }

        // create nested list for viewholder
        boardViews = new ArrayList<List<ViewHolder>>();
        for(int i = 0; i < edgeSize; i++){
            List<ViewHolder> l = new ArrayList<ViewHolder>();
            for(int j = 0; j < edgeSize; j++){
                l.add(null);
            }
            boardViews.add(l);
        }

        selectedColor = context.getColor(R.color.colorPrimaryLight);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TextView hint1, hint2, hint3, hint4, hint5, hint6, hint7, hint8, hint9;
        ConstraintLayout shell;
        TextView selectedNumber;
        // TextView row, column;
        int row, column;
        boolean canUpdate;
        TextView hints[];

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shell = itemView.findViewById(R.id.shell);
            hints = new TextView[9];
            int hints_id[] = new int[]{R.id.hint1, R.id.hint2, R.id.hint3, R.id.hint4, R.id.hint5,
                    R.id.hint6, R.id.hint7, R.id.hint8, R.id.hint9};
            for(int i = 0; i < 9; i++){
                hints[i] = itemView.findViewById(hints_id[i]);
            }
            selectedNumber = itemView.findViewById(R.id.selectedNumber);
            selectedNumber.setOnClickListener(onclicklistener);
        }

        private View.OnClickListener onclicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset background of previous
                boardViews.get(selectedRow).get(selectedCol).shell.setBackgroundColor(Color.TRANSPARENT);

                shell.setBackgroundColor(selectedColor);
                selectedRow = row;
                selectedCol = column;
            }
        };
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.singlecell, parent, false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        int val = getItem(position);
        if (val >= 1 && val <= 9){
            vh.selectedNumber.setText(Integer.toString(val));
            vh.canUpdate = false;
        }
        vh.row = position/edgeSize;
        vh.column = position%edgeSize;
        boardViews.get(vh.row).set(vh.column, vh);
    }

    @Override
    public int getItemCount() {
        return edgeSize * edgeSize;
    }

    // convenience method for getting data at click position
    int getItem(int id) {
        return board.get((int)id/edgeSize).get(id%edgeSize);
    }

    void setItem(int pos, int i) {
        board.get((int)pos/edgeSize).set(pos%edgeSize, i);
    }

}