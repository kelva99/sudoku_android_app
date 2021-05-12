package com.minigame.sudoku;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuUtils {
    public static final int BOX_SIZE = 3;
    public static final int EDGE_SIZE = BOX_SIZE *BOX_SIZE;
    public static final int TOTAL_CELL_COUNT = EDGE_SIZE * EDGE_SIZE;

    public static final HashMap<String, Pair<Integer, Integer>> LEVEL;
    static {
        LEVEL = new HashMap<>();
        LEVEL.put("EASY",    new Pair<>(20, 2));
        LEVEL.put("MEDIUM",  new Pair<>(34, 3));
        LEVEL.put("HARD",    new Pair<>(45, 4));
        LEVEL.put("MASTER+", new Pair<>(81, 9)); // remove as many as possible
    }

    // public static String[] LEVELS = (String[]) LEVEL.keySet().toArray();
    public final static String[] LEVELS = {"EASY", "MEDIUM", "HARD", "MASTER+"};

    /*
    // https://www.fi.muni.cz/~xpelanek/publications/sudoku-arxiv.pdf
    public static final HashMap<String, Double> TECHNIQUE_RATING;
    static {
        TECHNIQUE_RATING = new HashMap<>();
        TECHNIQUE_RATING.put("Hidden Single", 1.2); // easy
        TECHNIQUE_RATING.put("Direct Pointing", 1.7); // easy
        TECHNIQUE_RATING.put("Direct Claiming", 1.9); // easy
        TECHNIQUE_RATING.put("Direct Hidden Pair", 2.0);
        TECHNIQUE_RATING.put("Naked Single", 2.3);
        TECHNIQUE_RATING.put("Direct Hidden Triple", 2.5);
        TECHNIQUE_RATING.put("Pointing", 2.6);
        TECHNIQUE_RATING.put("Claiming", 2.8);
    }
     */

    public static List<List<Integer>> GetEmptyBoard(){
        List<List<Integer>> currentBoard = new ArrayList<>();
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

    // no solution version
    public boolean isFinished(List<List<Integer>> board) {
        List<Integer> numsAvailable;
        int n = 0;

        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++) {
            numsAvailable = IntStream.range(1,  SudokuUtils.EDGE_SIZE+1).boxed().collect(Collectors.toList());

            // ith row and jth column
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++) {
                n = board.get(i).get(j);
                if (!numsAvailable.contains(n) || n == 0) {
                    return false;
                }
                numsAvailable.remove(numsAvailable.indexOf(n));
            }

            numsAvailable = IntStream.range(1, SudokuUtils.EDGE_SIZE+1).boxed().collect(Collectors.toList());
            // jth row and ith column
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++) {
                n = board.get(j).get(i);
                if (!numsAvailable.contains(n) || n == 0) {
                    return false;
                }
                numsAvailable.remove(numsAvailable.indexOf(n));
            }

            numsAvailable = IntStream.range(1, SudokuUtils.EDGE_SIZE+1).boxed().collect(Collectors.toList());
            // ith box
            for(int j = 0; j < SudokuUtils.EDGE_SIZE; j++) {
                n = board.get(i - i%SudokuUtils.BOX_SIZE + j/SudokuUtils.BOX_SIZE)
                        .get(i%SudokuUtils.BOX_SIZE * SudokuUtils.BOX_SIZE+j%SudokuUtils.BOX_SIZE);
                if (!numsAvailable.contains(n) || n == 0) {
                    return false;
                }
                numsAvailable.remove(numsAvailable.indexOf(n));
            }
        }
        return true;
    }
}
