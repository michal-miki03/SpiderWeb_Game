package com.example.simple_sensors_game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public final void startGame(final View v){
        startActivity(new Intent(this, Game.class));
    }

    public final void instruction(final View v){
        startActivity(new Intent(this, Instruction.class));
    }
}
