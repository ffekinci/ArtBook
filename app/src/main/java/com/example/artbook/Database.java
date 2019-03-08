package com.example.artbook;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

//Singleton Class

public class Database {

    private static Database instance;
    private SQLiteDatabase database;
    //private  SQLiteStatement statement;

    private Database(){

    }
    public SQLiteDatabase getDb(){
        return database;
    }

    public void setDb(SQLiteDatabase database){
        this.database = database;
    }

    public void execSQL(String query){
        database.execSQL(query);
    }
    public void execStatement(SQLiteStatement st){
        st.execute();
    }

    public static Database getInstance() {
        if(instance == null)
            instance = new Database();

        return instance;
    }


}
