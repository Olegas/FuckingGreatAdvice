/**
 * 
 */
package ru.elifantiev.fga.widget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite DB Opener Helper for Widget
 * 
 * @author zoxa
 */
public class DBHelper extends SQLiteOpenHelper
{
    public static String DATABASE_NAME = "fuckinggreateadvice.db";
    private static int DATABASE_VERSION = 2;
    public static String WIDGET_TABLE = "widget";

    public static String WIDGET_ID = "_id";
    public static String WIDGET_TYPE = "type";
    public static String WIDGET_REFRESH = "rate";
    public static String WIDGET_STYLE = "style";
    public static String WIDGET_URL = "url";

    /**
     * Create DB Connection. Overload with custom params
     * 
     * @param context
     */
    public DBHelper( Context context )
    {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * on DB Create
     * 
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     *      .SQLiteDatabase)
     */
    @Override
    public void onCreate( SQLiteDatabase db )
    {
        db.execSQL( createWidgetTable() );
    }

    /**
     * on DB Upgrade: deletes current table and create new one. need to rewrite
     * on future change
     * 
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     *      .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade( SQLiteDatabase db, int arg1, int arg2 )
    {
        try
        {
            db.execSQL( "ALTER TABLE " + WIDGET_TABLE + " ADD COLUMN " + WIDGET_STYLE
                    + " INTEGER DEFAULT 0;" );
        }
        catch ( Exception e )
        {
            db.execSQL( "DROP TABLE IF EXISTS " + WIDGET_TABLE );
            onCreate( db );
        }
    }

    /**
     * Build SQL for Create Table
     * 
     * @return String sql
     */
    private String createWidgetTable()
    {
        StringBuilder sql = new StringBuilder();
        sql.append( "CREATE TABLE " ).append( WIDGET_TABLE ).append( " (" );
        sql.append( WIDGET_ID ).append( " INTEGER PRIMARY KEY, " );
        sql.append( WIDGET_TYPE ).append( " INTEGER DEFAULT 0, " );
        sql.append( WIDGET_REFRESH ).append( " INTEGER DEFAULT 0, " );
        sql.append( WIDGET_STYLE ).append( " INTEGER DEFAULT 0, " );
        sql.append( WIDGET_URL ).append( " TEXT " );
        sql.append( " );" );
        return sql.toString();
    }
}
