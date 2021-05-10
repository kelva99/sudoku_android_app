package com.minigame.sudoku.GameSolver;

import android.util.Pair;

import com.minigame.sudoku.SudokuUtils;

import java.util.List;
import java.util.Queue;

public class GameSolverCSP {
    // TODO

    private List<List<Integer>> board;
    // Row<Col<List of poss val for cell>>>
    private List<List<List<Integer>>> candidates;
    // Pair(cell_1, cell_2)
    private Queue<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> queue;

    public GameSolverCSP(){
        board = SudokuUtils.GetEmptyBoard();
    }

    public boolean SolveSudoku(List<List<Integer>> newboard) {
        // board = SudokuUtils.DeepCopyList(newboard);

        return SolveRecursive();
    }

    private boolean SolveRecursive() {
        return false;
    }

    public List<List<Integer>> GetCurrentBoard() {
        return board;
    }
}
