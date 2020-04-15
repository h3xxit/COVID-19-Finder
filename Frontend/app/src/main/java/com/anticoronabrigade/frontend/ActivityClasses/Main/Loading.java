package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.R;

public class Loading extends DialogFragment {
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private AlertDialog globalDialog;
    private CardView startTrackingCV;
    private CardView reportInfectedCV;
    private CardView testPathsCV;

    public Loading(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.startTrackingCV = null;
        this.reportInfectedCV = null;
        this.testPathsCV = null;
    }

    public Loading(FragmentManager fragmentManager, CardView startTrackingCV, CardView reportInfectedCV, CardView testPathsCV) {
        this.fragmentManager=fragmentManager;
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
            View view = layoutInflater.inflate(R.layout.layout_loading, null);
            progressBar = view.findViewById(R.id.progressBar);
            builder.setView(view)
                    .setTitle(R.string.loading);
        } catch (NullPointerException e) {
            Toast toast=Toast.makeText(getContext(), R.string.problem_try_again_restart, Toast.LENGTH_SHORT);
            toast.show();
        }

        globalDialog = builder.create();

        return globalDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(startTrackingCV != null){
            startTrackingCV.setEnabled(true);
            reportInfectedCV.setEnabled(true);
            testPathsCV.setEnabled(true);
        }
        super.onDismiss(dialog);
    }

    public FragmentManager getFrgManager() {
        return fragmentManager;
    }

    public void closeDialog() {
        globalDialog.dismiss();
    }
}
