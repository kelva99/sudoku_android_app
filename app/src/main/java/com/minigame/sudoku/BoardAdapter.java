package com.minigame.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Drawable cell_background;

    private int selectedCellColor;
    private int incorrectNumberColor;
    public static final int edgeSize = 9;
    public BoardAdapter(Context context, List<List<Integer>> gameboard) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.board = gameboard;
        this.cell_background = context.getDrawable(R.drawable.cell_background);
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

        selectedCellColor = context.getColor(R.color.colorPrimaryLight);
        incorrectNumberColor = context.getColor(R.color.red);
        selectedRow = -1;
        selectedCellColor = -1;
    }

    public void FillNumber(boolean isNote, int whichNumber){
        if (selectedRow < 0 || selectedCol < 0){
            return;
        }
        ViewHolder view = boardViews.get(selectedRow).get(selectedCol);
        if (!view.canUpdate) return;
        if (isNote){
            if (whichNumber < 1 || whichNumber > 9){
                return;
            }
            view.SetNumber(0);
            float alpha = ((int) view.hints[whichNumber- 1].getAlpha()) ^ 1;
            view.hints[whichNumber- 1].setAlpha(alpha);
            // Log.d("fill_number", "set text for note");
        }
        else {
            for(int i = 0; i < edgeSize; i++){
                view.hints[i].setAlpha(0);
            }
            view.SetNumber(whichNumber);
        }
        if (!ValidEntry()){
            view.selectedNumber.setTextColor(incorrectNumberColor);
        }
    }

    public void ClearNumber(){
        if (selectedRow < 0 || selectedCol < 0){
            return;
        }
        ViewHolder view = boardViews.get(selectedRow).get(selectedCol);
        if (!view.canUpdate) return;

        view.SetNumber(0);
        for(int i = 0; i < edgeSize; i++){
            view.hints[i].setAlpha(0);
        }
        board.get(selectedRow).set(selectedCol, 0);
    }

    public boolean ValidEntry() {
        return true;
    }

    private String GetBoardAsPrettyString(){
        String s = "";
        for(int i = 0; i < edgeSize; i++){
            for(int j = 0; j < edgeSize; j++){
                s += board.get(i).get(j);
                if (j == 2 || j == 5){
                    s += "  ";
                }
            }
            s += "\n";
            if (i == 2 || i == 5){
                s += "\n";
            }
        }
        return s;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TextView hint1, hint2, hint3, hint4, hint5, hint6, hint7, hint8, hint9;
        ConstraintLayout shell;
        TextView selectedNumber;
        // TextView row, column;
        int row, column;
        boolean canUpdate = true;
        TextView hints[];

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shell = itemView.findViewById(R.id.shell);
            hints = new TextView[edgeSize];
            int hints_id[] = new int[]{R.id.hint1, R.id.hint2, R.id.hint3, R.id.hint4, R.id.hint5,
                    R.id.hint6, R.id.hint7, R.id.hint8, R.id.hint9};
            for(int i = 0; i < edgeSize; i++){
                hints[i] = itemView.findViewById(hints_id[i]);
            }
            selectedNumber = itemView.findViewById(R.id.selectedNumber);
            selectedNumber.setOnClickListener(selectCellListener);
        }

        private View.OnClickListener selectCellListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // reset background of previous
            if (selectedCol >= 0 && selectedRow >= 0){
                boardViews.get(selectedRow).get(selectedCol).shell.setBackgroundColor(Color.TRANSPARENT);
                boardViews.get(selectedRow).get(selectedCol).shell.setBackground(cell_background);
            }
            shell.setBackgroundColor(selectedCellColor);
            selectedRow = row;
            selectedCol = column;
            }
        };

        public void SetNumber(int num){
            if (num < 0 || num > 9) return;
            board.get(row).set(column, num);
            selectedNumber.setText(num == 0? "" : Integer.toString(num));
            // Log.d("set number", num + "");
        }
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
        vh.SetNumber(val);
        if (val >= 1 && val <= 9){
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