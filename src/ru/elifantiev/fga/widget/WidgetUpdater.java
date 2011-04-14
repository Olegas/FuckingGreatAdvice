/**
 * Widget Related packages:
 * - AppWidgetProvider
 * - WidgetUpdater
 * - WidgetConfig
 * - WidgetUpdateService
 */
package ru.elifantiev.fga.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.widget.RemoteViews;
import ru.elifantiev.fga.FuckinGreatAdvice;
import ru.elifantiev.fga.MainActivity;
import ru.elifantiev.fga.R;

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

    public final static int WIDGET_STYLE_WHITE = 0;
    public final static int WIDGET_STYLE_BLACK = WIDGET_STYLE_WHITE + 1;

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
        // get URL, TYPE, and STYLE of the widget
        SQLiteDatabase db = new DBHelper( context ).getReadableDatabase();
        Cursor cur = db.query( DBHelper.WIDGET_TABLE, new String[] { DBHelper.WIDGET_TYPE,
                DBHelper.WIDGET_URL, DBHelper.WIDGET_STYLE },
                DBHelper.WIDGET_ID + " = " + widgetID, null, null, null, null );

        if ( cur.getCount() == 0 )
        {
            cur.close();
            db.close();

            // widget data not found
            appWidgetManager.updateAppWidget(
                    widgetID,
                    buildWidget(
                            context.getString(R.string.noWidgetData),
                            WIDGET_STYLE_WHITE,
                            WidgetUpdater.WIDGET_UPDATE_MAIN));
        }
        else
        {
            cur.moveToFirst();

            final String WIDGET_URL = cur.getString( cur.getColumnIndex( DBHelper.WIDGET_URL ) );
            final int WIDGET_TYPE = cur.getInt( cur.getColumnIndex( DBHelper.WIDGET_TYPE ) );
            final int WIDGET_STYLE = cur.getInt( cur.getColumnIndex( DBHelper.WIDGET_STYLE ) );

            // show loading message
            appWidgetManager.updateAppWidget(
                    widgetID,
                    buildWidget(
                        context.getString(R.string.gettingAdvice),
                        WIDGET_STYLE,
                        WIDGET_TYPE));

            cur.close();
            db.close();

            // fetch advice
            FuckinGreatAdvice advice = new FuckinGreatAdvice( WIDGET_URL, context
                    .getString( R.string.error ) );

            // update widget with advice
            appWidgetManager.updateAppWidget(
                    widgetID,
                    buildWidget(
                        Html.fromHtml("&mdash;").toString() + " " + advice.getAdvice(),
                        WIDGET_STYLE,
                        WIDGET_TYPE));

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
        }
    }

    private RemoteViews buildWidget( final String advice, final int style, final int type )
    {
        // get widget layout based by style
        int layout = (style == WIDGET_STYLE_WHITE) ? R.layout.widget_white : R.layout.widget_black;
        RemoteViews views = new RemoteViews( context.getPackageName(), layout );

        PendingIntent clickRequest;

        if(type == WIDGET_UPDATE_MAIN) 
        {
            clickRequest = PendingIntent.getActivity(
                    context,
                    0,
                    new Intent(context, MainActivity.class),
                    0);
        } 
        else 
        {
            clickRequest =  WidgetConfig.getPendingItent( context, widgetID );
        }

        // set text
        views.setTextViewText( R.id.widget_text, advice );
        views.setOnClickPendingIntent(R.id.widget_text, clickRequest);
        return views;
    }

}
