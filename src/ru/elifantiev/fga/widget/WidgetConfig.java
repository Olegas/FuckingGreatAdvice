package ru.elifantiev.fga.widget;

import ru.elifantiev.fga.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class WidgetConfig extends Activity
{
    private SeekBar refreshBar;
    private TextView refreshLbl;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.config );

        /* find refresh Seek Bar & refresh LBL */
        refreshBar = (SeekBar) findViewById( R.id.config_refresh_rate );
        refreshLbl = (TextView) findViewById( R.id.config_refresh_lbl );
        refreshBar.setOnSeekBarChangeListener( new seekListener() );
        
        Button setBtn = (Button) findViewById( R.id.config_set );
        setBtn.setOnClickListener( new btnClickListener() );        
    }

    private class btnClickListener implements OnClickListener
    {
        @Override
        public void onClick( View v )
        {
           RadioGroup rg = (RadioGroup) findViewById( R.id.config_type );
           RadioButton rb1 = (RadioButton) findViewById( R.id.config_type_main );
           RadioButton rb2 = (RadioButton) findViewById( R.id.config_type_random );
           
           Log.v( "FGA", "Radio Group Btn ID: " + rg.getCheckedRadioButtonId() );
           //Log.v( "FGA", "Radio Btn 1: " + rb1. );
           Log.v( "FGA", "Radio Btn 2: " + rg.getCheckedRadioButtonId() );
           Log.v( "FGA", "Refresh Rate: " + refreshBar.getProgress() );
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
