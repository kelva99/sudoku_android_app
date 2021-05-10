package com.minigame.sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SudokuUtils {
    public static final int BOX_SIZE = 3;
    public static final int EDGE_SIZE = BOX_SIZE *BOX_SIZE;
    public static final int TOTAL_CELL_COUNT = EDGE_SIZE * EDGE_SIZE;
    public static final String[] LEVELS = {"EASY", "MEDIUM", "HARD", "MASTER+"};

    // https://www.fi.muni.cz/~xpelanek/publications/sudoku-arxiv.pdf
    /*
    public static final HashMap<String, Double> TECHNIQUE_RATING = new HashMap<String, Double>(){
        {
            TECHNIQUE_RATING.put("Hidden Single", 1.2); // easy
            TECHNIQUE_RATING.put("Direct Pointing", 1.7); // easy
            TECHNIQUE_RATING.put("Direct Claiming", 1.9); // easy
            TECHNIQUE_RATING.put("Direct Hidden Pair", 2.0);
            TECHNIQUE_RATING.put("Naked Single", 2.3);
            TECHNIQUE_RATING.put("Direct Hidden Triple", 2.5);
            // TECHNIQUE_RATING.put("Pointing", 2.6);
            // TECHNIQUE_RATING.put("Claiming", 2.8);
        }
    };
     */

    public static List<List<Integer>> GetEmptyBoard(){
        List<List<Integer>> currentBoard = new ArrayList<List<Integer>>();
        for(int i = 0; i < EDGE_SIZE; i++){
            List<Integer> row = new ArrayList<>();
            for(int j = 0; j < EDGE_SIZE; j++){
                row.add(0);
            }
            currentBoard.add(row);
        }
        return currentBoard;
    }

    public static List<List<Integer>> DeepCopyList(List<List<Integer>> oldList) {
        if (oldList == null){
            return GetEmptyBoard();
        }
        List<List<Integer>> currentBoard = new ArrayList<>();
        for(int i = 0; i < EDGE_SIZE; i++){
            List<Integer> row = new ArrayList<>();
            for(int j = 0; j < EDGE_SIZE; j++){
                row.add(oldList.get(i).get(j));
            }
            currentBoard.add(row);
        }
        return currentBoard;
    }

    public static boolean ValidEntry(List<List<Integer>> board, int row, int col, int val) {
        for(int c = 0; c < EDGE_SIZE; c++){
            if (c == col) continue;
            if (board.get(row).get(c).equals(val)) return false;
        }

        for(int r = 0; r < EDGE_SIZE; r++){
            if (r == row) continue;
            if (board.get(r).get(col).equals(val)) return false;
        }

        for(int r = 0; r < BOX_SIZE; r++){
            for(int c = 0; c < BOX_SIZE; c++) {
                int newRow = row - row % 3 + r;
                int newCol = col - col % 3 + c;
                if (newRow == row && newCol == col) continue;
                if (board.get(newRow).get(newCol).equals(val)) return false;
            }
        }

        return true;
    }
}
