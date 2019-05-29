package com.example.simple_sensors_game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Summary extends Activity {

    TextView score;
    TextView bestScore;
    TextView record;
    int iScore;
    int iBestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        score = findViewById(R.id.score);
        bestScore = findViewById(R.id.best_score);
        record = findViewById(R.id.record);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        iScore = myPreferences.getInt("SCORE", 0);
        iBestScore = myPreferences.getInt("BESTSCORE7", 0);

        if(iScore > iBestScore) {
            record.setText(R.string.new_record);
            SharedPreferences.Editor editor = myPreferences.edit();
            editor.putInt("BESTSCORE7", iScore);
            editor.apply();
        }

        score.setText(getString(R.string.score) + String.valueOf(iScore));
        bestScore.setText(getString(R.string.bestScore) + String.valueOf(iBestScore));
    }

    public final void startGame(final View v){
        startActivity(new Intent(this, Game.class));
        finish();
    }

    public final void exit(final View v) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }
}
