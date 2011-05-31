package ru.elifantiev.fga;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private FuckinGreatAdvice advice;
    private String error;
    private TextView adviceText;
    private ProgressDialog progress;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        error = getString(R.string.error);
        adviceText = (TextView)findViewById(R.id.txtAdvice);
        advice = new FuckinGreatAdvice();

        findViewById(R.id.newAdvice).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                showAdvice();
            }
        });

        showAdvice();
    }

    private void showAdvice() {
        new AdviceGetterTask().execute();
    }

    class AdviceGetterTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String adviceText = advice.getRandomAdvice();
            return adviceText != null ? adviceText : error;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "",
                    getString(R.string.gettingAdvice), true, false);
        }

        @Override
        protected void onPostExecute(String advice) {
            progress.dismiss();
            adviceText.setText(advice != null ? advice : error);
        }
    }

}
