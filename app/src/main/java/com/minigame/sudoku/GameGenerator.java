package com.minigame.sudoku;

import android.util.Log;

import com.minigame.sudoku.GameSolver.GameSolverDLX;

import java.util.ArrayList;
import java.util.List;


public class GameGenerator {
    private GameSolverDLX dlx;
    public GameGenerator(){
        dlx = new GameSolverDLX();
    }

    public List<List<Integer>> Generator_DLX(){
        List<List<Integer>> res = dlx.GenerateNewBoard();
        return res;
    }

    public List<List<Integer>> GenerateSolvableBoard(List<List<Integer>> solution, String level){
        // TODO: implement this - PRIORITY
        List<List<Integer>> starter = SudokuUtils.DeepCopyList(solution);
        Log.d("solution", starter.toString());
        return solution;
    }


}
