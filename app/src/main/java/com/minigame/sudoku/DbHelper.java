package com.minigame.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class DbHelper extends SQLiteOpenHelper {
    public static String dbname = "Sudoku.db";

    public static final String TABLE_NAME_BOARDS ="BuiltInBoards";
    public static final String ID ="ID";
    public static final String BOARD ="board";
    public static final String USED = "used";
    public ExecutorService backgroundExecutor;
    private Random rand;
    private GameGenerator generator;

    public static final String CREATE_TABLE_BOARDS = "CREATE TABLE IF NOT EXISTS " +TABLE_NAME_BOARDS + "( " +
            ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BOARD + " TEXT NOT NULL, " +
            USED + " INTEGER NOT NULL DEFAULT 0 CHECK("+ USED+ " IN (0,1)));";

    public static final String DROP_TABLE_BOARDS = "DROP TABLE IF EXISTS " + TABLE_NAME_BOARDS;

    public DbHelper(Context context) {
        super(context, dbname, null, 1);
        rand = new Random();
        generator = new GameGenerator();
        backgroundExecutor = Executors.newCachedThreadPool();
        AddDefaultBoards();
    }

    public DbHelper(Context context, ExecutorService pool) {
        super(context, dbname, null, 1);
        rand = new Random();
        generator = new GameGenerator();
        if (pool == null){
            backgroundExecutor = Executors.newCachedThreadPool();
        }
        else {
            backgroundExecutor = pool;
        }

    }

    // create the db object
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOARDS);
    }


    // drop and re-create the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_BOARDS);
        onCreate(db);
    }

    public void AddDefaultBoards(){
        if (((ThreadPoolExecutor) backgroundExecutor).getActiveCount() < 2) { // only allow max of 2
            List<List<Integer>> b = generator.Generator_DLX();
            WriteBoardToDb(ListToString(b));
        }
    }

    public static String ListToString(List<List<Integer>> lst) {
        if (lst == null){
            return "";
        }
        StringBuilder ret = new StringBuilder();
        for(List<Integer> row : lst){
            for(Integer i : row){
                ret.append(i.toString());
            }
        }
        return ret.toString();
    }

    public static List<List<Integer>> StringToList(String str) {
        List<List<Integer>> ret = new ArrayList<>();
        if (str.length() != Sudoku_data.TOTAL_CELL_COUNT){
            return null;
        }
        for(int i = 0; i < Sudoku_data.EDGE_SIZE; i++){
            List<Integer> row = new ArrayList<>();
            for(int j = 0; j < Sudoku_data.EDGE_SIZE; j++){
                try {
                    row.add(Integer.parseInt(Character.toString(str.charAt(i * Sudoku_data.EDGE_SIZE + j))));
                }
                catch (Exception e){
                    Log.e("String to List db", e.toString());
                    return null;
                }
            }
            ret.add(row);
        }
        return ret;
    }

    public List<List<Integer>> LoadRandomBoard(){
        List<String> result = new ArrayList<>();
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor;
            String sql = "SELECT * FROM " + TABLE_NAME_BOARDS;
            cursor = db.rawQuery(sql, null);
            //return the cursor instance
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String b = cursor.getString(cursor.getColumnIndex(BOARD));
                        result.add(b);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("Db LoadRandomBoard", e.toString());
        }
        if (result.size() == 0){
            return null;
        }
        return StringToList(result.get(rand.nextInt(result.size())));
    }


    public void WriteBoardToDb(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        // id is a auto increased integer
        content.put(BOARD,s);
        // insert to the db
        db.insert(TABLE_NAME_BOARDS,null,content);
    }

}
