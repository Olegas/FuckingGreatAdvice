/**
 * Widget Related packages:
 * - AppWidgetProvider
 * - WidgetUpdater
 * - WidgetConfig
 * - WidgetUpdateService
 */
package ru.elifantiev.fga.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * App Widget Provider. Calls Widget Updater
 * 
 * @author zoxa
 */
public class FuckingWidget extends AppWidgetProvider
{
    // onEnable have no idea what to do

    // Update current widget using db data
    public void onUpdate( final Context context, final AppWidgetManager appWidgetManager,
            final int[] appWidgetIds )
    {
        Log.v( "FGA", "onUpdate called" );
        final int N = appWidgetIds.length;
        for ( int i = 0; i < N; i++ )
        {
            Log.v( "FGA", "updating widget " + appWidgetIds[i] );
            WidgetUpdater wUpdater = new WidgetUpdater( context, appWidgetManager, appWidgetIds[i] );
            new Thread( wUpdater ).start();
        }

        super.onUpdate( context, appWidgetManager, appWidgetIds );
    }

    /**
     * Deleting one or more widgets (not all)
     */
    public void onDeleted( Context context, int[] appWidgetIds )
    {
        Log.v( "FGA", "onDeleted called" );

        SQLiteDatabase db = new DBHelper( context ).getWritableDatabase();

        final int N = appWidgetIds.length;
        for ( int i = 0; i < N; i++ )
        {
            Log.v( "FGA", "deleting widget " + appWidgetIds[i] );
            db.delete( DBHelper.WIDGET_TABLE, DBHelper.WIDGET_ID + " = " + appWidgetIds[i], null );
            
            // remove alert manager for this widget
        }
        db.close();
        
        super.onDeleted( context, appWidgetIds );
    }
    
    /**
     * All widgets are gone
     */
    public void onDisabled (Context context)
    { 
        Log.v( "FGA", "onDisabled called" );
        
        Log.v( "FGA", "deleting all widget ");
        SQLiteDatabase db = new DBHelper( context ).getWritableDatabase();
        db.delete( DBHelper.WIDGET_TABLE, null, null );
        db.close();
        
        super.onDisabled( context );
    }

}
