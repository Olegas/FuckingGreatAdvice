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

/**
 * App Widget Provider. Calls Widget Updater
 * @author zoxa
 */
public class FuckingWidget extends AppWidgetProvider
{

    public void onUpdate( final Context context, final AppWidgetManager appWidgetManager,
            final int[] appWidgetIds )
    {
        final int N = appWidgetIds.length; 

        for ( int i = 0; i < N; i++ )
        {
            WidgetUpdater wUpdater = new WidgetUpdater( context, appWidgetManager,
                    appWidgetIds[i] );
            new Thread( wUpdater ).start();
        } 
        
        super.onUpdate( context, appWidgetManager, appWidgetIds );
    }
}
