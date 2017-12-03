package br.usp.trabalhoandroid;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TrainingActivity extends AppCompatActivity {

    private Button bt_capture;
    private Exercise userExercise, professionalExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.training));
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        professionalExercise = (Exercise) getIntent().getExtras().getSerializable("exercise");
        professionalExercise.printSeries();
        Log.d("debug", professionalExercise.getName());

        bt_capture = findViewById(R.id.bt_capture);
        bt_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
