/**
 * Widget Related packages:
 * - AppWidgetProvider
 * - WidgetUpdater
 * - WidgetConfig
 * - WidgetUpdateService
 */
package ru.elifantiev.fga.widget;

import ru.elifantiev.fga.FuckinGreatAdvice;
import ru.elifantiev.fga.R;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Widget Updater. Does call to Fucking Great Advice class and pulls current
 * advice
 * 
 * @author zoxa
 */
public class WidgetUpdater implements Runnable
{
    private final Context context;
    private final AppWidgetManager appWidgetManager;
    private final int widgetID;

    public final static int WIDGET_UPDATE_MAIN = 0;
    public final static int WIDGET_UPDATE_RND = WIDGET_UPDATE_MAIN + 1;

    public WidgetUpdater( final Context context, final AppWidgetManager appWidgetManager,
            final int id )
    {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.widgetID = id;
    }

    @Override
    public void run()
    {
        Log.v( "FGA", "fetching data for appWidgetId " + widgetID );

        // show loading message
        appWidgetManager.updateAppWidget( widgetID, buildWidget( context
                .getString( R.string.gettingAdvice ) ) );

        // get URL and TYPE of the widget
        SQLiteDatabase db = new DBHelper( context ).getReadableDatabase();
        Cursor cur = db.query( DBHelper.WIDGET_TABLE, new String[] { DBHelper.WIDGET_TYPE,
                DBHelper.WIDGET_REFRESH, DBHelper.WIDGET_URL }, DBHelper.WIDGET_ID + " = "
                + widgetID, null, null, null, null );

        Log.v( "FGA", "cursor count" + cur.getCount() );

        if ( cur.getCount() == 0 )
        {
            cur.close();
            db.close();

            // widget data not found
            appWidgetManager.updateAppWidget( widgetID, buildWidget( context
                    .getString( R.string.noWidgetData ) ) );
        }
        else
        {
            cur.moveToFirst();
            
            final String WIDGET_URL = cur.getString( cur.getColumnIndex( DBHelper.WIDGET_URL ) );
            Log.v( "FGA", "Widget URL" + WIDGET_URL );

            final int WIDGET_TYPE = cur.getInt( cur.getColumnIndex( DBHelper.WIDGET_TYPE ) );
            Log.v( "FGA", "Widget TYPE" + WIDGET_TYPE );

            final int WIDGET_REFRESH = cur.getInt( cur.getColumnIndex( DBHelper.WIDGET_REFRESH ) );
            Log.v( "FGA", "Widget REFRESH" + WIDGET_REFRESH );

            cur.close();
            db.close();

            // fetch advice
            FuckinGreatAdvice advice = new FuckinGreatAdvice( WIDGET_URL, context
                    .getString( R.string.error ) );

            // update widget with advice
            appWidgetManager.updateAppWidget( widgetID, buildWidget( advice.getAdvice() ) );

            // TYPE is random save next url
            if ( WIDGET_TYPE == WIDGET_UPDATE_RND )
            {
                ContentValues values = new ContentValues();
                values.put( DBHelper.WIDGET_URL, advice.getNextURL() );

                SQLiteDatabase dbW = new DBHelper( context ).getWritableDatabase();
                dbW.update( DBHelper.WIDGET_TABLE, values, DBHelper.WIDGET_ID + " = " + widgetID,
                        null );
                values.clear();
                dbW.close();
            }

            // set Alarm
        }
    }

    private RemoteViews buildWidget( final String advice )
    {
        // get widget layout
        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget );

        // set text
        views.setTextViewText( R.id.widget_text, advice );
        return views;
    }

}
