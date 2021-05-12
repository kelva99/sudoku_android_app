package com.minigame.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

    private int clr_selectedCell;
    private int clr_incorrectNumber;
    private int clr_correctNumber;
    private int clr_cannotEdit;

    private final int history_max_size = 20;
    private List<Step> history = new ArrayList<>();

    public BoardAdapter(Context context, List<List<Integer>> gameboard) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.board = SudokuUtils.DeepCopyList(gameboard);
        this.cell_background = context.getDrawable(R.drawable.cell_background);

        history.clear();

        // create nested list for viewholder
        boardViews = new ArrayList<List<ViewHolder>>();
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            List<ViewHolder> l = new ArrayList<ViewHolder>();
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++){
                l.add(null);
            }
            boardViews.add(l);
        }

        clr_selectedCell = context.getColor(R.color.colorPrimaryLight);
        clr_incorrectNumber = context.getColor(R.color.red);
        clr_correctNumber = context.getColor(R.color.darkgrey);
        clr_cannotEdit = context.getColor(R.color.grey);
        selectedRow = -1;
        clr_selectedCell = -1;
    }



    public void UpdateBoard(List<List<Integer>> newboard){
        board.clear();
        board = SudokuUtils.DeepCopyList(newboard);
        history.clear();
        notifyDataSetChanged();
    }

    public void EraseBoard(){
        board = new ArrayList<List<Integer>>();
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            List<Integer> l = new ArrayList<Integer>();
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++){
                l.add(0);
            }
            board.add(l);
        }
        history.clear();
        notifyDataSetChanged();
    }

    public List<List<Integer>> GetBoard() {
        return board;
    }

    public void FillNumber(boolean isNote, int whichNumber){
        if (selectedRow < 0 || selectedCol < 0){
            return;
        }
        ViewHolder view = boardViews.get(selectedRow).get(selectedCol);
        if (!view.canUpdate) return;
        if (isNote){
            if (whichNumber < 1 || whichNumber > SudokuUtils.EDGE_SIZE || !view.selectedNumber.getText().equals("")){
                return;
            }
            AddToHistory(view);
            view.SetNumber(0);
            float alpha = ((int) view.candidates[whichNumber- 1].getAlpha()) ^ 1;
            view.candidates[whichNumber- 1].setAlpha(alpha);
            // Log.d("fill_number", "set text for note");
        }
        else {
            AddToHistory(view);
            for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
                view.candidates[i].setAlpha(0);
            }
            view.SetNumber(whichNumber);
        }
        if (!SudokuUtils.ValidEntry(board, selectedRow, selectedCol, whichNumber)){
            view.selectedNumber.setTextColor(clr_incorrectNumber);
        }
        else {
            view.selectedNumber.setTextColor(clr_correctNumber);
        }
    }

    public void ClearNumber(){
        if (selectedRow < 0 || selectedCol < 0){
            return;
        }
        ViewHolder view = boardViews.get(selectedRow).get(selectedCol);
        if (!view.canUpdate) return;

        AddToHistory(view);
        view.SetNumber(0);
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            view.candidates[i].setAlpha(0);
        }
        board.get(selectedRow).set(selectedCol, 0);
    }

    public void SetHint(List<List<Integer>> solution){
        ViewHolder view = boardViews.get(selectedRow).get(selectedCol);
        AddToHistory(view);
        if (view.canUpdate) {
            view.SetNumber(solution.get(selectedRow).get(selectedCol));
            view.selectedNumber.setTextColor(clr_cannotEdit);
            view.canUpdate = false;
            // TODO: Make hint more clear by animation?
        }
    }

    private void AddToHistory(ViewHolder view) {
        history.add(new Step(selectedRow, selectedCol, view));
        if (history.size() > history_max_size) {
            history.remove(0);
        }
    }

    public void Revert(){
        if (history.size() <= 0) return;
        selectedRow = selectedCol = -1;
        Step lastStep = history.remove(history.size() - 1);
        ViewHolder view = boardViews.get(lastStep.row).get(lastStep.col);

        Log.d("revert", lastStep.toString());
        view.selectedNumber.setText(lastStep.previousNum);
        view.selectedNumber.setTextColor(lastStep.numColor);
        for (int i = 0; i < SudokuUtils.EDGE_SIZE; i++) {
            view.candidates[i].setAlpha(lastStep.previousHints.get(i));
        }
        view.shell.setBackgroundColor(Color.TRANSPARENT);
        view.shell.setBackground(cell_background);
    }

    public boolean isFinished(List<List<Integer>> solution) {
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++) {
            if (board.get(i).contains(0)) return false;
        }
        return solution.equals(board);
    }



    private String GetBoardAsPrettyString(){
        String s = "";
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++){
                s += board.get(i).get(j);
                if ((j + 1) % SudokuUtils.BOX_SIZE == 0 && (j+1) != SudokuUtils.EDGE_SIZE){
                    s += "  ";
                }
            }
            s += "\n";
            if ((i + 1) % SudokuUtils.BOX_SIZE == 0 && (i+1) != SudokuUtils.EDGE_SIZE){
                s += "\n";
            }
        }
        return s;
    }

    final int[] candidates_id = new int[]{R.id.hint1, R.id.hint2, R.id.hint3, R.id.hint4, R.id.hint5,
            R.id.hint6, R.id.hint7, R.id.hint8, R.id.hint9};

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TextView hint1, hint2, hint3, hint4, hint5, hint6, hint7, hint8, hint9;
        ConstraintLayout shell;
        TextView selectedNumber;
        // TextView row, column;
        int row, column;
        boolean canUpdate = true;
        TextView[] candidates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shell = itemView.findViewById(R.id.shell);
            candidates = new TextView[SudokuUtils.EDGE_SIZE];

            for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
                candidates[i] = itemView.findViewById(candidates_id[i]);
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
            shell.setBackgroundColor(clr_selectedCell);
            selectedRow = row;
            selectedCol = column;
            }
        };

        public void SetNumber(int num){
            if (num < 0 || num > SudokuUtils.EDGE_SIZE) return;
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
        vh.row = position/ SudokuUtils.EDGE_SIZE;
        vh.column = position% SudokuUtils.EDGE_SIZE;
        int val = getItem(position);
        vh.SetNumber(val);
        if (val >= 1 && val <= SudokuUtils.EDGE_SIZE){
            vh.canUpdate = false;
            vh.selectedNumber.setTextColor(clr_cannotEdit);
        }

        boardViews.get(vh.row).set(vh.column, vh);
        // Log.d("soln - posn", vh.row + " " + vh.column + " " + vh.selectedNumber.getText());
    }

    @Override
    public int getItemCount() {
        return SudokuUtils.TOTAL_CELL_COUNT;
    }


    int getItem(int pos) {
        return board.get(pos/ SudokuUtils.EDGE_SIZE).get(pos% SudokuUtils.EDGE_SIZE);
    }

    void setItem(int pos, int i) {
        board.get(pos/ SudokuUtils.EDGE_SIZE).set(pos% SudokuUtils.EDGE_SIZE, i);
    }

}

class Step {
    public final int row, col;
    public final String previousNum;
    public final List<Float> previousHints;
    public final int numColor;
    public Step(int row, int col, BoardAdapter.ViewHolder vh) {
        this.row = row;
        this.col = col;
        List<Float> visibleNotes = new ArrayList<>();
        previousNum = vh.selectedNumber.getText().toString();
        for (int i = 0; i < SudokuUtils.EDGE_SIZE; i++) {
            visibleNotes.add(vh.candidates[i].getAlpha());
        }
        previousHints = visibleNotes;
        numColor   = vh.selectedNumber.getCurrentTextColor();
    }

    @Override
    public String toString() {
        return "Row: " + row + "; Col: " + col + "; Previous Hint: " + previousNum + "; PreviousHints" + previousHints.toString();
    }
}