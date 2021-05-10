package com.minigame.sudoku.GameSolver;

import android.util.Log;
import android.util.Pair;

import com.minigame.sudoku.SudokuUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameSolverCSP extends ABCGameSolver {
    private List<List<Integer>> board;
    // Row<Col<List of poss val for cell>>>
    private List<List<List<Integer>>> candidates;
    // Pair(<r, c>, <r, c>)
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> queue;

    public GameSolverCSP(){
        board = SudokuUtils.GetEmptyBoard();
        queue = new ArrayList<>();
    }

    public List<List<List<Integer>>> AC3(List<List<Integer>> newboard) {
        candidates = new ArrayList<>();
        for(int row = 0; row < SudokuUtils.EDGE_SIZE; row++){
            List<List<Integer>> newrow = new ArrayList<>();
            for(int col = 0; col < SudokuUtils.EDGE_SIZE; col++){
                int val = newboard.get(row).get(col);
                if (val == 0){
                    newrow.add(IntStream.range(1, SudokuUtils.EDGE_SIZE + 1).boxed().collect(Collectors.toList()));

                    for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                        if (c == col) continue;
                        queue.add(new Pair<>(new Pair<>(row, col), new Pair<>(row, c)));
                    }

                    for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++){
                        if (r == row) continue;
                        queue.add(new Pair<>(new Pair<>(row, col), new Pair<>(r, col)));
                    }

                    for(int r = 0; r < SudokuUtils.BOX_SIZE; r++){
                        for(int c = 0; c < SudokuUtils.BOX_SIZE; c++) {
                            int newRow = row - row % 3 + r;
                            int newCol = col - col % 3 + c;
                            if (newRow == row && newCol == col) continue;
                            queue.add(new Pair<>(new Pair<>(row, col), new Pair<>(newRow, newCol)));
                        }
                    }

                }
                else {
                    newrow.add(Arrays.asList(val));
                }
            }
            candidates.add(newrow);
        }

        // Log.d("csp", candidates.toString());
        int size = queue.size();

        while(size > 0){
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> nextToCheck = queue.remove(0);
            size--;
            // Log.d("csp size", Integer.toString(size));
            boolean reduced = false;
            int row = nextToCheck.first.first;
            int col = nextToCheck.first.second;
            List<Integer> valuesToCheck_x = new ArrayList<>(candidates.get(row).get(col));
            List<Integer> valuesToCheck_y = candidates.get(nextToCheck.second.first).get(nextToCheck.second.second);
            for(int i : valuesToCheck_x){
                if (valuesToCheck_y.size() == 1 && valuesToCheck_y.contains(i)) {
                    int index = candidates.get(row).get(col).indexOf(i);
                    candidates.get(row).get(col).remove(index);
                    reduced = true;
                }
            }

            if (reduced){
                // Log.d("csp",  "reduced " + nextToCheck.toString());
                // Log.d("csp", valuesToCheck_x.toString() + " -> " + candidates.get(row).get(col).toString());
                if (candidates.get(row).get(col).size() == 0) {
                    return null;
                }
                else {
                    for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                        if (new Pair<>(row, c).equals(nextToCheck.second) || c == col) continue;
                        queue.add(new Pair<>(new Pair<>(row, c), new Pair<>(row, col)));
                        size++;
                        // Log.d("csp", "add "+ new Pair<>(new Pair<>(row, c), new Pair<>(row, col)));
                    }

                    for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++){
                        if (new Pair<>(r, col).equals(nextToCheck.second) || r == row) continue;
                        queue.add(new Pair<>(new Pair<>(r, col), new Pair<>(row, col)));
                        size++;
                        // Log.d("csp", "add "+ new Pair<>(new Pair<>(r, col), new Pair<>(row, col)));
                    }

                    for(int r = 0; r < SudokuUtils.BOX_SIZE; r++){
                        for(int c = 0; c < SudokuUtils.BOX_SIZE; c++) {
                            int newRow = row - row % 3 + r;
                            int newCol = col - col % 3 + c;
                            if (new Pair<>(r, col).equals(nextToCheck.second) || (newRow == row && newCol == col)) continue;
                            queue.add(new Pair<>(new Pair<>(newRow, newCol), new Pair<>(row, col)));
                            size++;
                            // Log.d("csp", "add "+ new Pair<>(new Pair<>(newRow, newCol), new Pair<>(row, col)));
                        }
                    }
                }
            }
        }
        Log.d("csp", candidates.toString());
        return candidates;
    }

    public boolean SolveSudoku(List<List<Integer>> newboard) {
        // ac3
        if (AC3(newboard) == null) return false;

        // backtracing
        board = SudokuUtils.DeepCopyList(newboard);
        return SolveRecursive();
    }

    private boolean SolveRecursive() {
        int row = -1, col = -1;
        for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++) {
            for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                if (board.get(r).get(c).equals(0)) {
                    if (candidates.get(r).get(c).size() == 1) {
                        board.get(r).set(c, candidates.get(r).get(c).get(0));
                    }
                    else {
                        row = r;
                        col = c;
                        break;
                    }

                }
            }
            if (row >= 0 && col >= 0){
                break;
            }
        }
        if (row == -1 && col == -1){
            return true;
        }

        for(int n : candidates.get(row).get(col)){
            board.get(row).set(col, n);
            if (!SudokuUtils.ValidEntry(board, row, col, n)){
                board.get(row).set(col, 0);
                continue;
            }
            if (SolveRecursive()) {
                return true;
            }
            else {
                board.get(row).set(col, 0);
            }
        }
        return false;
    }




    public List<List<Integer>> GetCurrentBoard() {
        return board;
    }
}
