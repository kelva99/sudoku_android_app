package com.minigame.sudoku;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.minigame.sudoku.GameSolver.GameSolverBacktracing;
import com.minigame.sudoku.GameSolver.GameSolverCSP;
import com.minigame.sudoku.GameSolver.GameSolverDLX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GameGenerator {
    private GameSolverDLX dlx;
    // private GameSolverBacktracing backtracing;
    private GameSolverCSP csp;
    public GameGenerator(){
        dlx = new GameSolverDLX();
        // backtracing = new GameSolverBacktracing();
        csp = new GameSolverCSP();
    }

    public List<List<Integer>> Generator_DLX(){
        List<List<Integer>> res = dlx.GenerateNewBoard();
        return res;
    }

    public List<List<Integer>> GenerateSolvableBoard(List<List<Integer>> solution, String level) {
        List<List<Integer>> starter = SudokuUtils.DeepCopyList(solution);
        int num_to_remove = SudokuUtils.LEVEL.get(level).first;
        int highest_difficulty = SudokuUtils.LEVEL.get(level).second;

        List<Integer> allPossiblePositions = IntStream.range(0, SudokuUtils.TOTAL_CELL_COUNT).boxed().collect(Collectors.toList()); // 81
        List<Integer> removedPositions = new ArrayList<>();
        Collections.shuffle(allPossiblePositions);

        while(num_to_remove > 0 && allPossiblePositions.size() > 0) {
            int pos = allPossiblePositions.remove(0);
            int originalValue = starter.get(pos/SudokuUtils.EDGE_SIZE).get(pos%SudokuUtils.EDGE_SIZE);
            starter.get(pos/SudokuUtils.EDGE_SIZE).set(pos%SudokuUtils.EDGE_SIZE, 0);
            Pair<Integer, List<List<List<Integer>>>> res = csp.GetBoardUniquenessData(starter);

            if (res == null || res.first != 1) {
                // no (unique) solution, we should not remove this cell..
                starter.get(pos/SudokuUtils.EDGE_SIZE).set(pos%SudokuUtils.EDGE_SIZE,originalValue);
            }
            else {
                // has one solution, we check if it is too difficult to solve...
                removedPositions.add(pos);
                List<List<List<Integer>>> candidates = res.second;
                boolean isTooDifficult = false;
                for(int p : removedPositions) {
                    int row = p / SudokuUtils.EDGE_SIZE;
                    int col = p % SudokuUtils.EDGE_SIZE;
                    if (candidates.get(row).get(col).size() > highest_difficulty) {
                        // too difficult
                        isTooDifficult = true;
                        break;
                    }
                }
                if (isTooDifficult) {
                    starter.get(pos/SudokuUtils.EDGE_SIZE).set(pos%SudokuUtils.EDGE_SIZE,originalValue);
                    removedPositions.remove(removedPositions.size() - 1);
                }
                else{
                    num_to_remove--;
                }
            }
        }
        return starter;
    }


}
