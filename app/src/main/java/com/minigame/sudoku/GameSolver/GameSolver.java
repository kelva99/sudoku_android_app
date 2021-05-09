package com.minigame.sudoku.GameSolver;

import java.util.List;

public class GameSolver {
    enum solver_algorithm {BACKTRACING("BACKTRACING"), DLX("DLX");
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
    public static final String[] SOLVER_ALGORITHM = {solver_algorithm.BACKTRACING.toString(), solver_algorithm.DLX.toString()};

    public GameSolver(){
        dlx = new GameSolverDLX();
        backtracing = new GameSolverBacktracing();
    }

    public List<List<Integer>> Solve(String which, List<List<Integer>> board){
        List<List<Integer>> newBoard = null;
        if (which.equals(solver_algorithm.BACKTRACING.toString())) {
            // TODO: NOT IMPLEMENTED
        }
        else if (which.equals((solver_algorithm.DLX.toString()))){
            newBoard = dlx.SolveSudoku(board);
        }
        return newBoard;
    }

}
