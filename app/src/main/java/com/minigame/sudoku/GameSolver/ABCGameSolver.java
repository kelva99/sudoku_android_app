package com.minigame.sudoku.GameSolver;

import com.minigame.sudoku.SudokuUtils;

import java.util.List;

public abstract class ABCGameSolver {
    public abstract boolean SolveSudoku(List<List<Integer>> newboard);
    public abstract List<List<Integer>> GetCurrentBoard();

}
