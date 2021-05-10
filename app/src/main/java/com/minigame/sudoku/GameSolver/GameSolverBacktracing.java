package com.minigame.sudoku.GameSolver;

import com.minigame.sudoku.SudokuUtils;

import java.util.List;

public class GameSolverBacktracing {

    private List<List<Integer>> board;
    public GameSolverBacktracing(){
        board = SudokuUtils.GetEmptyBoard();
    }

    public boolean SolveSudoku(List<List<Integer>> newboard) {
        board = SudokuUtils.DeepCopyList(newboard);
        return SolveRecursive();
    }

    private boolean SolveRecursive() {
        int row = -1, col = -1;
        for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++) {
            for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                if (board.get(r).get(c).equals(0)) {
                    row = r;
                    col = c;
                    break;
                }
            }
            if (row >= 0 && col >= 0){
                break;
            }
        }
        if (row == -1 && col == -1){
            return true;
        }

        for(int n = 1; n <= SudokuUtils.EDGE_SIZE; n++){
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
