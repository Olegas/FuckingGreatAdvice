package ru.elifantiev.fga.widget;

import ru.elifantiev.fga.R;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class WidgetConfig extends Activity
{
    private SeekBar refreshBar;
    private TextView refreshLbl;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        
        // Sent negative result in order to cancel widget placement
        setResult( RESULT_CANCELED );
        
        setContentView( R.layout.config );

        /* find refresh Seek Bar & refresh LBL */
        refreshBar = (SeekBar) findViewById( R.id.config_refresh_rate );
        refreshLbl = (TextView) findViewById( R.id.config_refresh_lbl );
        refreshBar.setOnSeekBarChangeListener( new seekListener() );
        
        // TODO: update refreshLbl with current refreshBar progress 
        

        // set btnAction
        Button setBtn = (Button) findViewById( R.id.config_set );
        setBtn.setOnClickListener( new btnClickListener() );

        // Find the widget id from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if ( extras != null )
        {
            appWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID );
        }

        Log.v( "FGA", "appWidgetId " + appWidgetId );

        // Check if Activity called for widget, else finish
        if ( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID )
        {
            finish();
        }
    }

    private class btnClickListener implements OnClickListener
    {
        @Override
        public void onClick( View v )
        {
            final Context context = WidgetConfig.this;
            
            // save config to db
            RadioButton rb1 = (RadioButton) findViewById( R.id.config_type_main );

            int widgetUpdateType = rb1.isChecked() ? WidgetUpdater.WIDGET_UPDATE_MAIN
                    : WidgetUpdater.WIDGET_UPDATE_RND;

            ContentValues values = new ContentValues();
            values.put( DBHelper.WIDGET_ID, appWidgetId );
            values.put( DBHelper.WIDGET_TYPE, widgetUpdateType );
            values.put( DBHelper.WIDGET_REFRESH, refreshBar.getProgress() );
            values.put( DBHelper.WIDGET_URL, getString( R.string.adviceUrl ) );

            Log.v( "FGA", "Values to save" + values.toString() );
            
            SQLiteDatabase db = new DBHelper( context ).getWritableDatabase();
            db.insert( DBHelper.WIDGET_TABLE, null, values );
            db.close();
            values.clear();
            
            Log.v( "FGA", "get AppWidgetManager" );
            // get AppWidgetManager
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( WidgetConfig.this );

            Log.v( "FGA", "call widget updater to update widget" );
            // call widget updater to update widget
            WidgetUpdater wUpdater = new WidgetUpdater( context, appWidgetManager,
                    appWidgetId );
            new Thread( wUpdater ).start();

            Log.v( "FGA", "Pass back original widgetId" );
            // Pass back original widgetId
            Intent resultValue = new Intent();
            resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
            setResult( RESULT_OK, resultValue );
            
            finish();
        }
    }

    private class seekListener implements OnSeekBarChangeListener
    {
        @Override
        public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
        {
            if ( progress == 0 )
            {
                refreshLbl.setText( getString( R.string.config_refresh_none ) );
            }
            else
            {
                refreshLbl.setText( String.format( getString( R.string.config_refresh_lbl ),
                        progress ) );
            }
        }

        @Override
        public void onStartTrackingTouch( SeekBar seekBar )
        {}

        @Override
        public void onStopTrackingTouch( SeekBar seekBar )
        {}
    }
}
