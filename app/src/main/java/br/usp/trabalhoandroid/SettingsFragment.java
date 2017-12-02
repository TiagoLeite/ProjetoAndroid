package br.usp.trabalhoandroid;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;


public class SettingsFragment extends Fragment
{
    private View root;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.settings_fragment, container, false);
        getActivity().setTitle("Configurações");

        setup();

        return root;
    }

    private void setup()
    {
        final Switch switchExerciseEnabled = root.findViewById(R.id.switch_exercise_enabled);
        switchExerciseEnabled.setChecked(ExerciseAlarm.getAlarmEnabled(getActivity()));
        switchExerciseEnabled.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                ExerciseAlarm.setAlarmEnabled(checked, getContext());
                ExerciseAlarm.setAlarm(getContext());
            }
        });

        final EditText editExerciseTime = root.findViewById(R.id.alarm_time);
        editExerciseTime.setText(AppUtil.formatTime(ExerciseAlarm.getAlarmHour(getContext()),
                ExerciseAlarm.getAlarmMinute(getContext())));

        editExerciseTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                        R.style.alarm_dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute)
                    {
                        ExerciseAlarm.setAlarmTime(hour, minute, getContext());
                        editExerciseTime.setText(AppUtil.formatTime(hour, minute));
                        ExerciseAlarm.setAlarm(getContext());
                    }
                }, ExerciseAlarm.getAlarmHour(getContext()), ExerciseAlarm.getAlarmMinute(getContext()),
                        true);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }
}
