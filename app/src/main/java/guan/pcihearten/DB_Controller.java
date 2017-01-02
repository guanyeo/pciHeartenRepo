package guan.pcihearten;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 12/31/2016.
 */

public class DB_Controller extends SQLiteOpenHelper {

    public static final String COL_1 = "INTRO";
    public static final String COL_2 = "PRE";
    public static final String COL_3 = "PROC";
    public static final String COL_4 = "POST";
    public static final String COL_5 = "HEALTH";
    public static final String TABLE_NAME = "DASH";

    public DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "dashboard.db", factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(INTRO TEXT, PRE TEXT, PROC TEXT, POST TEXT, HEALTH TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS DASH");
        onCreate(sqLiteDatabase);
    }

    public void insertItem(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, "0");


        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);

    }


    public void updateItem(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, "0");


        this.getWritableDatabase().execSQL("UPDATE '"+TABLE_NAME+"' SET INTRO = 1 WHERE INTRO = 0");

    }


    public Cursor listText(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT intro FROM dash", null);
        return res;

    }

    public void dropTable(){
        this.getWritableDatabase().execSQL("DROP TABLE sheaffer");
    }








}
