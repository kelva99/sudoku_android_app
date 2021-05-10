package com.minigame.sudoku.GameSolver;

import android.widget.Toast;

import java.util.List;

public class GameSolver {
    enum solver_algorithm {BACKTRACING("BACKTRACING"), DLX("DLX"), CSP("CSP");
        private final String text;
        solver_algorithm(String text) {
            this.text = text;
        }

        @Override
        public String toString(){
            return text;
        }
    }

    private GameSolverDLX dlx;
    private GameSolverBacktracing backtracing;
    private GameSolverCSP csp;

    public static final String[] SOLVER_ALGORITHM = {
            solver_algorithm.BACKTRACING.toString(),
            solver_algorithm.DLX.toString(),
            solver_algorithm.CSP.toString()
    };

    public GameSolver(){
        dlx = new GameSolverDLX();
        backtracing = new GameSolverBacktracing();
        csp = new GameSolverCSP();
    }

    public List<List<Integer>> Solve(String which, List<List<Integer>> board){
        List<List<Integer>> newBoard = null;
        boolean result  = false;
        if (which.equals(solver_algorithm.BACKTRACING.toString())) {
            result = backtracing.SolveSudoku(board);
            if (result) {
                newBoard = backtracing.GetCurrentBoard();
            }
        }
        else if (which.equals(solver_algorithm.DLX.toString())){
            result = dlx.SolveSudoku(board);
            if (result) {
                newBoard = dlx.GetCurrentBoard();
            }
        }
        else if (which.equals(solver_algorithm.CSP.toString())) {
            result = csp.SolveSudoku(board);
            if (result) {
                newBoard = csp.GetCurrentBoard();
            }
        }
        return newBoard;
    }

}
