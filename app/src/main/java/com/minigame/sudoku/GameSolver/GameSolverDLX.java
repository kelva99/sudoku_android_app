package com.minigame.sudoku.GameSolver;

import com.minigame.sudoku.SudokuUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Reference: https://garethrees.org/2007/06/10/zendoku-generation/#section-3
// https://www.youtube.com/watch?v=_cR9zDlvP88&t=772s

public class GameSolverDLX {
    private Node root;
    List<List<Integer>> currentBoard;
    private final int NUM_CONSTRAINT = 4;
    private final int NUM_COLUMN_SIZE_MATRIX = SudokuUtils.TOTAL_CELL_COUNT * NUM_CONSTRAINT;
    private final int MAX_NUM_ROW_SIZE_MATRIX = SudokuUtils.TOTAL_CELL_COUNT * SudokuUtils.EDGE_SIZE;
    private Random rand;
    public GameSolverDLX() {
        SetCurrentBoardEmpty();
        rand = new Random();
        results = new ArrayList<>();
    }

    public List<List<Integer>> GetCurrentBoard() {
        return currentBoard;
    }

    private void SetCurrentBoardEmpty(){
        currentBoard = SudokuUtils.GetEmptyBoard();
    }

    public List<List<Integer>> GenerateNewBoard(){
        SetCurrentBoardEmpty();
        // select a random row to fill
        int randRow = rand.nextInt(SudokuUtils.EDGE_SIZE);
        List<Integer> number = new ArrayList<>();
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            number.add(i+1);
        }
        Collections.shuffle(number);
        for(int i = 0; i < SudokuUtils.EDGE_SIZE; i++){
            currentBoard.get(randRow).set(i, number.get(i));
        }
        Solve();
        return currentBoard;
    }

    public boolean SolveSudoku(List<List<Integer>> board){
        if (board != null){
            currentBoard = SudokuUtils.DeepCopyList(board);
        }
        return Solve();
    }

    private boolean Solve(){
        LinkMatrix();
        results = new ArrayList<>();
        boolean hasSoln = Fill();
        // Log.d("soln", currentBoard.toString());
        if (hasSoln) {
            for(Node n : results){
                if (currentBoard.get(n.row).get(n.col).equals(0)){
                    currentBoard.get(n.row).set(n.col, n.val);
                }

            }
        }
        return hasSoln;
        // Log.d("soln2", currentBoard.toString());
    }

    public Node SelectRandomColNode(){
        int r = rand.nextInt(NUM_COLUMN_SIZE_MATRIX);
        Node n = root;
        for(int i = 0; i < r; i++){
            n = n.right;
        }
        if (n == root){
            n = n.right;
        }
        if (n == root){
            n = null;
        }
        return n;
    }

    private List<Node> results;
    public List<Node> GetResults(){
        return  results;
    }
    private boolean Fill(){
        if (root.right == root){
            return true;
        }
        // randomly choose a node to remove
        List<Node> node_smallestColSize = new ArrayList<>();
        int minSize = MAX_NUM_ROW_SIZE_MATRIX;
        for(Node n = root.right; n != root; n = n.right){
            if (n.columnSize < minSize){
                minSize = n.columnSize;
                node_smallestColSize.clear();
                node_smallestColSize.add(n);
                if (minSize == 0){
                    // empty column
                    return false;
                }
            }
            else if (n.columnSize == minSize) {
                node_smallestColSize.add(n);
            }
        }
        Node selected = node_smallestColSize.get(rand.nextInt(node_smallestColSize.size()));
        node_smallestColSize.clear();
        // Node selected = SelectRandomColNode();

        selected.Cover();
        for(Node m = selected.down; m != selected; m = m.down) {
            results.add(m);
            // Log.d("board", "r: " + m.row + " c: " + m.col + " val: " + m.val + " constr: " + m.GetValConstraint() + " isColHead: " + (m.colRoot == m));
            for(Node n = m.right; m != n; n = n.right){
                n.colRoot.Cover();
            }
            if (Fill()){
                return true;
            }
            // revert
            results.remove(results.size() - 1);
            // Log.d("board revert", "r: " + m.row + " c: " + m.col + " val: " + m.val + " constr: " + m.GetValConstraint() + " isColHead: " + (m.colRoot == m));
            for(Node n = m.left; m != n; n = n.left){
                n.colRoot.Uncover();
            }

        }
        selected.Uncover();
        return false;
    }

    private void LinkMatrix() {
        List<List<Integer>> lst_constraints = GenerateConstraints();
        List<Node> columnNodes = new ArrayList<>();
        Node rootnode = new Node(-1);
        for(int i = 0; i < NUM_COLUMN_SIZE_MATRIX; i++){
            Node n = new Node(i);
            columnNodes.add(n);

            rootnode.LinkRight(n);
            rootnode = n;
            // rootnode.LinkLeft(n);
        }
        rootnode = rootnode.right;

        for(List<Integer> row_constraints : lst_constraints ){
            Node cur = null;
            int row = row_constraints.get(0) / SudokuUtils.EDGE_SIZE;
            int col = row_constraints.get(0) % SudokuUtils.EDGE_SIZE;
            int value = row_constraints.get(1) - SudokuUtils.TOTAL_CELL_COUNT - row * SudokuUtils.EDGE_SIZE +1;
            for(Integer c : row_constraints){
                Node newNode = new Node(c, row, col, value);
                newNode.colRoot = columnNodes.get(c);
                // columnNodes.get(c).LinkUp(newNode);
                newNode.colRoot.up.LinkDown(newNode);

                newNode.colRoot.columnSize++;
                if (cur == null){
                    cur = newNode;
                }
                // cur.LinkLeft(newNode);
                cur.LinkRight(newNode);
                cur = newNode;
            }

        }
        // clear columns that is empty
        for(int i = NUM_COLUMN_SIZE_MATRIX - 1; i >= 0; i--){
            if (columnNodes.get(i).columnSize == 0){
                columnNodes.get(i).RemoveColumn();
            }
        }

        root = rootnode;
    }

    private List<List<Integer>> GenerateConstraints(){
        List<List<Integer>> lst_constraints = new ArrayList<>();
        for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++){
            for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                if (currentBoard != null && currentBoard.get(r).get(c) > 0){
                    lst_constraints.add(GenerateConstraint_Row(r, c, currentBoard.get(r).get(c)));
                }
                else {
                    for(int i = 1; i <= SudokuUtils.EDGE_SIZE; i++){
                        lst_constraints.add(GenerateConstraint_Row(r, c, i));
                    }
                }
            }
        }
        return lst_constraints;
    }

    private List<Integer> GenerateConstraint_Row(int row, int col, int val_real){
        List<Integer> res = new ArrayList<>();
        int val_index = val_real - 1;
        // constraint 1: each cell must filled [0-80]
        res.add(row * SudokuUtils.EDGE_SIZE + col);
        // constraint 2: each row has one occurrence [81- 161]
        res.add(SudokuUtils.TOTAL_CELL_COUNT + row * SudokuUtils.EDGE_SIZE + val_index);
        // constraint 3: each column has one occurrence [162- 242]
        res.add(SudokuUtils.TOTAL_CELL_COUNT * 2 + col * SudokuUtils.EDGE_SIZE + val_index);
        // constraint 4: each BOX_SIZE * BOX_SIZE has one occurrence [243-323]
        res.add(SudokuUtils.TOTAL_CELL_COUNT * 3 + ((row / 3) * SudokuUtils.BOX_SIZE+ (col/3))* SudokuUtils.EDGE_SIZE+ val_index);
        return  res;
    }

    @Deprecated
    private List<List<Integer>> GenerateConstraintsBin(){
        List<List<Integer>> matrix_bin = new ArrayList<>();
        for(int r = 0; r < SudokuUtils.EDGE_SIZE; r++){
            for(int c = 0; c < SudokuUtils.EDGE_SIZE; c++){
                if (currentBoard != null && currentBoard.get(r).get(c) > 0){
                    matrix_bin.add(GenerateConstraintBin_Row(r, c, currentBoard.get(r).get(c)));
                }
                else {
                    for(int i = 1; i <= SudokuUtils.EDGE_SIZE; i++){
                        matrix_bin.add(GenerateConstraintBin_Row(r, c, i));
                    }
                }
            }
        }
        return matrix_bin;
    }

    @Deprecated
    private List<Integer> GenerateConstraintBin_Row(int row, int col, int val_real){
        List<Integer> res = Arrays.asList(new Integer[NUM_COLUMN_SIZE_MATRIX]);
        int val_index = val_real - 1;
        // constraint 1: each cell must filled [1-81]
        res.set(row * SudokuUtils.EDGE_SIZE + col, 1);
        // constraint 2: each row has one occurrence [82- 162]
        res.set(SudokuUtils.TOTAL_CELL_COUNT + row * SudokuUtils.EDGE_SIZE + val_index, 1);
        // constraint 3: each column has one occurrence [163- 243]
        res.set(SudokuUtils.TOTAL_CELL_COUNT * 2 + col * SudokuUtils.EDGE_SIZE + val_index, 1);
        // constraint 4: each BOX_SIZE * BOX_SIZE has one occurrence [244-324]
        res.set(SudokuUtils.TOTAL_CELL_COUNT * 3 + ((row / 3) * SudokuUtils.BOX_SIZE+ (col/3))* SudokuUtils.EDGE_SIZE+ val_index , 1);
        return  res;
    }


}

class Node {
    public Node left, right, up, down;
    public int columnSize = 0;
    private int val_constraint = 0;

    public int row, col, val;
    public Node colRoot;


    public Node(){
        left = right = up = down = this;
        val_constraint = row = col = val = -1;
        colRoot = this;
    }
    public Node(int val_constraint){
        this();
        this.val_constraint = val_constraint;
    }

    public Node(int val_constraint, int row, int col, int val){
        this();
        this.val_constraint = val_constraint;
        this.row = row;
        this.col = col;
        this.val = val;
    }

    public int GetValConstraint(){
        return val_constraint;
    }

    public void LinkDown(Node n){
        n.down = down;
        n.down.up = n;
        n.up = this;
        this.down = n;
    }

    public void LinkUp(Node n){
        n.up = up;
        n.up.down = n;
        n.down = this;
        this.up = n;
    }

    public void LinkLeft(Node n){
        n.left = left;
        n.left.right = n;
        n.right = this;
        this.left = n;
    }

    public void LinkRight(Node n){
        n.right = right;
        n.right.left = n;
        n.left = this;
        this.right = n;
    }

    // link left right nodes
    public void RemoveColumn(){
        this.left.right = right;
        this.right.left = left;
    }

    public void RestoreColumn(){
        this.left.right = this;
        this.right.left = this;
    }

    // link up down nodes
    public void RemoveRow(){
        this.up.down = down;
        this.down.up = up;
        columnSize--;
    }

    public void RestoreRow(){
        this.up.down = this;
        this.down.up = this;
        columnSize++;
    }

    public void Cover(){
        RemoveColumn();
        Node m, n;
        m = this.down;
        while(m != this){
            n = m.right;
            while (n != m){
                n.RemoveRow();
                n = n.right;
            }
            m = m.down;
        }
    }

    public void Uncover(){
        Node m , n;
        m = this.up;
        while(m != this){
            n = m.left;
            while(n != m){
                n.RestoreRow();
                n = n.left;
            }
            m = m.up;
        }
        this.RestoreColumn();
    }
}
