package com.anticoronabrigade.frontend.ActivityClasses.Main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.R;

public class AreYouSure extends DialogFragment {
    private FragmentManager fragmentManager;
    private AlertDialog globalDialog;
    private Runnable function;
    private CardView startTrackingCV;
    private CardView reportInfectedCV;
    private CardView testPathsCV;

    public AreYouSure(FragmentManager fragmentManager, Runnable function) {
        this.fragmentManager = fragmentManager;
        this.function = function;
        this.startTrackingCV = null;
        this.reportInfectedCV = null;
        this.testPathsCV = null;
    }

    public AreYouSure(FragmentManager fragmentManager, Runnable function, CardView startTrackingCV, CardView reportInfectedCV, CardView testPathsCV) {
        this.fragmentManager = fragmentManager;
        this.function = function;
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
            View view = layoutInflater.inflate(R.layout.layout_are_you_sure, null);

            builder.setView(view)
                    .setTitle(R.string.are_you_sure)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(startTrackingCV != null) {
                                startTrackingCV.setEnabled(true);
                                reportInfectedCV.setEnabled(true);
                                testPathsCV.setEnabled(true);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            function.run();
                        }
                    });


        } catch(NullPointerException e) {
            Toast toast=Toast.makeText(getContext(), R.string.problem_try_again_restart, Toast.LENGTH_SHORT);
            toast.show();
        }
        globalDialog=builder.create();

        return globalDialog;
    }

    public FragmentManager getFrgManager() {
        return fragmentManager;
    }

    public void closeDialog() {
        globalDialog.dismiss();
    }
}
