package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.ReadWriteFileHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends DialogFragment {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private TextView textViewForgotPassword;
    private TextInputLayout editTextPasswordLayout;

    private FragmentManager frgManager;
    UserAPI userAPI;
    private Context LoginRegisterContext;
    private Activity LoginRegisterActivity;
    private AlertDialog globalDialog;
    private TextView emailTextView;
    private CardView infectedCV;
    private TextView infectedTV;

    public Login(FragmentManager fragmentManager, UserAPI userAPI, Context context, Activity activity, TextView emailTextView, CardView infectedCV, TextView infectedTV)
    {
        this.userAPI=userAPI;
        this.frgManager=fragmentManager;
        this.LoginRegisterContext=context;
        this.LoginRegisterActivity=activity;
        this.emailTextView=emailTextView;
        this.infectedCV=infectedCV;
        this.infectedTV=infectedTV;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.layout_login, null);

            progressBar = view.findViewById(R.id.progressBar);
            editTextEmail = view.findViewById(R.id.editTextEmail);
            editTextPassword = view.findViewById(R.id.editTextPassword);
            textViewForgotPassword = view.findViewById(R.id.textViewForgotPassword);

            editTextPasswordLayout = view.findViewById(R.id.editTextPasswordLayout);
            editTextPasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editTextPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()){
                        editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });

            textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ForgotPasswordEmailOrPhoneDialog forgotPasswordEmailOrPhoneDialog = new ForgotPasswordEmailOrPhoneDialog(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedCV);
                    //forgotPasswordEmailOrPhoneDialog.show(frgManager, "Forgot password email dialog");
                    //forgotPasswordEmailOrPhoneDialog.setCancelable(false);

                    globalDialog.dismiss();
                }
            });

            progressBar.setVisibility(View.INVISIBLE);

            builder.setView(view)
                    .setTitle(R.string.login)
                    .setNeutralButton(R.string.register, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);

                            //RegisterEmailOrPhone registerEmailOrPhone = new RegisterEmailOrPhone(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedCV);
                            //registerEmailOrPhone.show(frgManager, "RegisterEmailAndPhoneNumber");
                            //registerEmailOrPhone.setCancelable(false);
                            globalDialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setPositiveButton(R.string.ok, null);


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
        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        login(email, password);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.INTERNET_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    Context context = getContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, R.string.internet_permission, duration);
                    toast.show();
                }
                return;
            }
        }
    }

    private void login(final String email, final String password)
    {
        if(getContext()!=null && getActivity()!=null)
        {
            if (ContextCompat.checkSelfPermission(LoginRegisterContext,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginRegisterActivity,
                        new String[]{Manifest.permission.INTERNET},
                        Const.INTERNET_PERMISSION);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                User user = new User(email, password);
                userAPI.findUser(user).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.isSuccessful() && response.body() != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast toast=Toast.makeText(LoginRegisterContext, R.string.welcome, Toast.LENGTH_LONG);
                            toast.show();
                            Integer isInfected = response.body() ? 1 : 0;
                            ReadWriteFileHandler readWriteFileHandler = new ReadWriteFileHandler(LoginRegisterContext);
                            readWriteFileHandler.protectedWriteToFile(Const.USER_FILE_NAME, new UserDto(email, password, isInfected));
                            DatabaseSimulator.currentUser = new UserDto(email, password, isInfected);
                            if(isInfected==1) {
                                infectedTV.setText(R.string.report_not_infected);
                                infectedCV.setBackground(getResources().getDrawable(R.drawable.btn_rounded_complement));
                            } else {
                                infectedTV.setText(R.string.report_infected);
                                infectedCV.setBackground(getResources().getDrawable(R.drawable.btn_rounded));
                            }
                            //emailTextView.setText(DatabaseSimulator.currentUser.getEmail());

                            globalDialog.dismiss();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast toast=Toast.makeText(LoginRegisterContext, R.string.email_password_incorrect, Toast.LENGTH_LONG);
                            toast.show();
                            editTextEmail.setText(null);
                            editTextPassword.setText(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast toast=Toast.makeText(LoginRegisterContext, R.string.network_failed, Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println(getResources().getString(R.string.network_failed));
                        System.out.println(t.getMessage());

                        editTextEmail.setText(null);
                        editTextPassword.setText(null);
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public FragmentManager getFrgManager() {
        return frgManager;
    }
}
