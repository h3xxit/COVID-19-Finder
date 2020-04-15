package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.ActivityClasses.ViewInfectedMeetupPoints.ViewInfectedMeetsActivity;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;

public class Tutorial extends DialogFragment {
    private MeetupLocationsDto meetupLocationsDto;
    private AlertDialog globalDialog;
    private FragmentManager frgManager;

    public Tutorial(FragmentManager fragmentManager){
        this.frgManager=fragmentManager;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.layout_tutorial, null);

            builder.setView(view)
                    .setTitle(getResources().getString(R.string.title_tutorial_dialog))
                    .setPositiveButton(getResources().getString(R.string.ok), null);

        } catch(NullPointerException e) {
            Toast toast=Toast.makeText(getContext(), R.string.problem_try_again_restart, Toast.LENGTH_SHORT);
            toast.show();
        }
        globalDialog=builder.create();

        return globalDialog;
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

    private void performOkButtonAction() {
        globalDialog.dismiss();
    }

    public FragmentManager getFrgManager() {
        return frgManager;
    }
}
