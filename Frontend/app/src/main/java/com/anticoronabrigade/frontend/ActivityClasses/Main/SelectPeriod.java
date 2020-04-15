package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;

import java.util.Calendar;

public class SelectPeriod extends DialogFragment {

    private AlertDialog globalDialog;
    private FragmentManager frgManager;
    private CustomRunnable customRunnable;
    private Spinner spinner;
    private Long time;
    private Context MainActivityContext;
    private CardView startTrackingCV;
    private CardView reportInfectedCV;
    private CardView testPathsCV;

    public SelectPeriod(FragmentManager manager, Context context, CustomRunnable runnable, CardView startTrackingCV, CardView reportInfectedCV, CardView testPathsCV){
        this.frgManager=manager;
        this.MainActivityContext=context;
        this.customRunnable=runnable;
        this.startTrackingCV = startTrackingCV;
        this.reportInfectedCV = reportInfectedCV;
        this.testPathsCV = testPathsCV;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.layout_period, null);

            spinner = view.findViewById(R.id.periodSpinner);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivityContext,
                    R.array.select_hours_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            builder.setView(view)
                    .setTitle(R.string.period_title)
                    .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startTrackingCV.setEnabled(true);
                            reportInfectedCV.setEnabled(true);
                            testPathsCV.setEnabled(true);
                            globalDialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.ok, null);

        } catch (NullPointerException e) {
            Toast toast=Toast.makeText(getContext(), R.string.problem_try_again_restart, Toast.LENGTH_SHORT);
            toast.show();
        }
        globalDialog = builder.create();

        return globalDialog;
    }

    public FragmentManager getFrgManager() {
        return frgManager;
    }

    @Override
    public void onResume() {
        super.onResume();
        Button okButton = globalDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                performOkButtonAction();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(MainActivity.startedByWidget) {
            MainActivity.startedByWidget = false;
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            ((Activity)MainActivityContext).onBackPressed();
        }
    }

    private void performOkButtonAction() {
        startTrackingCV.setEnabled(true);
        reportInfectedCV.setEnabled(true);
        testPathsCV.setEnabled(true);
        long selectedItem = spinner.getSelectedItemId();
        time = (selectedItem == 0) ? Const.ONE_MINUTE_IN_MILLISECONS * 30
                : (selectedItem == 1) ? Const.ONE_MINUTE_IN_MILLISECONS * 60
                : (selectedItem == 2) ? Const.ONE_MINUTE_IN_MILLISECONS * 90
                : Const.ONE_MINUTE_IN_MILLISECONS * 120;
        customRunnable.setTime(time);
        customRunnable.run();
        globalDialog.dismiss();
    }

    public Long getTime(){
        return time;
    }
}
