package com.example.simple_sensors_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;


public class Game extends Activity implements SensorEventListener{

    ImageView ball;
    ImageView purpose;
    ImageView endpoint;
    TextView resultTxt;

    private float xEndPointPosition, xPurposePosition, xBallPosition, xAcceleration, xVelocity = 0f;
    private float yEndPointPosition, yPurposePosition, yBallPosition, yAcceleration, yVelocity  = 0f;
    private float xMaxCord, yMaxCord;
    private int result = 0;
    private SensorManager sensorManager;
    SharedPreferences.Editor editor;

    final static int VALUE_OF_ERROR = 50;
    final static int VALUE_OF_MISTAKE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences myPreferences;
        ball = findViewById(R.id.ball);
        purpose = findViewById(R.id.purpose);
        endpoint = findViewById(R.id.endpoint);
        resultTxt = findViewById(R.id.result);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        xMaxCord = (float) size.x - 80;
        yMaxCord = (float) size.y - 275;

        updateEndPointPosition();
        updatePurposePosition();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = myPreferences.edit();
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAcceleration = sensorEvent.values[0];
            yAcceleration = -sensorEvent.values[1];
            updateBallProperties();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateBallProperties() {
        float frameTime = 0.66f;
        xVelocity += (xAcceleration * frameTime);
        yVelocity += (yAcceleration * frameTime);

        float xS = (float) (((xVelocity / 12) * frameTime) + (xAcceleration * Math.pow(frameTime,2)/2));
        float yS = (float) (((yVelocity / 12) * frameTime) + (yAcceleration * Math.pow(frameTime,2)/2));

        xBallPosition -= xS;
        yBallPosition -= yS;

        if (xBallPosition > xMaxCord) {
            xBallPosition = xMaxCord;
            xVelocity = 0;
        } else if (xBallPosition < 0) {
            xBallPosition = 0;
            xVelocity = 0;
        }

        if (yBallPosition > yMaxCord) {
            yBallPosition = yMaxCord;
            yVelocity = 0;
        } else if (yBallPosition < 0) {
            yBallPosition = 0;
            yVelocity = 0;
        }

        ball.setX(xBallPosition);
        ball.setY(yBallPosition);

        if (xBallPosition >= xPurposePosition - VALUE_OF_ERROR && xBallPosition <= xPurposePosition + VALUE_OF_ERROR) {
            if (yBallPosition >= yPurposePosition - VALUE_OF_ERROR && yBallPosition <= yPurposePosition + VALUE_OF_ERROR) {
                updatePurposePosition();
                updateEndPointPosition();
            }
        }

        if (xBallPosition >= xEndPointPosition - VALUE_OF_ERROR && xBallPosition <= xEndPointPosition + VALUE_OF_ERROR) {
            if (yBallPosition >= yEndPointPosition - VALUE_OF_ERROR && yBallPosition <= yEndPointPosition + VALUE_OF_ERROR) {
                endGame();
            }
        }
    }

    private void updatePurposePosition() {

        updateResult();
        xPurposePosition = (float) (Math.random() * xMaxCord);
        yPurposePosition = (float) (Math.random() * yMaxCord);
        purpose.setX(xPurposePosition);
        purpose.setY(yPurposePosition);
    }

    private void updateResult(){

        resultTxt.setText(getString(R.string.result) + result++);
    }

    private void updateEndPointPosition() {

        do{
            xEndPointPosition = (float) (Math.random() * xMaxCord);
            yEndPointPosition = (float) (Math.random() * yMaxCord);
        }
        while((xPurposePosition - VALUE_OF_MISTAKE <= xEndPointPosition && xEndPointPosition <= xPurposePosition + VALUE_OF_MISTAKE)
                || (yPurposePosition - VALUE_OF_MISTAKE <= yEndPointPosition && yEndPointPosition <= yPurposePosition + VALUE_OF_MISTAKE)
                || (xBallPosition >= xEndPointPosition - VALUE_OF_ERROR && xBallPosition <= xEndPointPosition + VALUE_OF_ERROR)
                || (yBallPosition >= yEndPointPosition - VALUE_OF_ERROR && yBallPosition <= yEndPointPosition + VALUE_OF_ERROR));

        endpoint.setX(xEndPointPosition);
        endpoint.setY(yEndPointPosition);
    }

    private void endGame(){

        ball.setX(xEndPointPosition);
        ball.setY(yEndPointPosition);
        sensorManager.unregisterListener(this);

        editor.putInt("SCORE", result-1);

        editor.commit();

        startActivity(new Intent(this, Summary.class));
        finish();
    }
}
