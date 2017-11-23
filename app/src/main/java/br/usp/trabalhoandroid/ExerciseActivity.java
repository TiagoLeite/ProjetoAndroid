package br.usp.trabalhoandroid;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    TextView tvx, tvy, tvz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager == null)
        {
            error();
            return;
        }
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        tvx = findViewById(R.id.x_value);
        tvy = findViewById(R.id.y_value);
        tvz = findViewById(R.id.z_value);

    }

    private void error()
    {
        //TODO: handle error
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        tvx.setText(x+" ");
        tvy.setText(y+" ");
        tvz.setText(z+" ");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
