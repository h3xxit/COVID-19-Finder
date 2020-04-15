package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordEmailOrPhoneDialog extends DialogFragment {
    private TextView editTextEmail;
    private TextView editTextPhoneNumber;
    private ProgressBar progressBar;

    private FragmentManager frgManager;
    private UserAPI userAPI;
    private static final int INTERNET_PERMISSION = 103;
    private Context LoginRegisterContext;
    private Activity LoginRegisterActivity;
    private AlertDialog globalDialog;
    private TextView emailTextView;
    private Button infectedBtn;

    private String email;

    public ForgotPasswordEmailOrPhoneDialog(FragmentManager fragmentManager, UserAPI userAPI, Context context, Activity activity, TextView emailTextView, Button infectedBtn) {
        this.frgManager=fragmentManager;
        this.userAPI=userAPI;
        this.LoginRegisterContext=context;
        this.LoginRegisterActivity=activity;
        this.emailTextView=emailTextView;
        this.infectedBtn=infectedBtn;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            LayoutInflater layoutInflater = LoginRegisterActivity.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.layout_register_email_phone, null);

            editTextEmail = view.findViewById(R.id.editTextEmail);
            editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
            progressBar = view.findViewById(R.id.progressBar);

            progressBar.setVisibility(View.INVISIBLE);

            builder.setView(view)
                    .setTitle(R.string.change_password)
                    .setNeutralButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            //Login login = new Login(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn);
                            //login.show(frgManager, "Login");
                            //login.setCancelable(false);
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
            Toast toast=Toast.makeText(LoginRegisterContext, R.string.problem_try_again_restart, Toast.LENGTH_SHORT);
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
        email = editTextEmail.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        if(isEmailValid(email)){
            sendForgotPasswordEmail(email);
            return;
        } else if(isPhoneNumberValid(phoneNumber)){
            sendForgotPasswordSms(phoneNumber);
            return;
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginRegisterContext, getResources().getString(R.string.email_not_valid), Toast.LENGTH_LONG).show();
            editTextPhoneNumber.setText(null);
            editTextEmail.setText(null);
        }
    }

    private void sendForgotPasswordEmail(String email){
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
                EmailOrSmsDto emailOrSmsDto = new EmailOrSmsDto(email);
                userAPI.sendForgotPasswordEmail(emailOrSmsDto).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast toast=Toast.makeText(LoginRegisterContext, R.string.resend_ok, Toast.LENGTH_LONG);
                            toast.show();

                            ForgotPasswordNewPasswordWithCode passwordAndCode = new ForgotPasswordNewPasswordWithCode(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn, email);
                            passwordAndCode.show(passwordAndCode.getFrgManager(), "PassAndCode");
                            passwordAndCode.setCancelable(false);

                            globalDialog.dismiss();
                        } else {
                            if(response.code()==500) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginRegisterContext, getResources().getString(R.string.email_error), Toast.LENGTH_LONG).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(LoginRegisterContext, R.string.email_phone_not_valid, Toast.LENGTH_LONG);
                                toast.show();
                                editTextEmail.setText(null);
                                editTextPhoneNumber.setText(null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast toast=Toast.makeText(LoginRegisterContext, R.string.network_failed, Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println("Network failed");
                        System.out.println(t.getMessage());

                        editTextEmail.setText(null);
                        editTextPhoneNumber.setText(null);
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void sendForgotPasswordSms(String phone){
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
                EmailOrSmsDto emailOrSmsDto = new EmailOrSmsDto(phone);
                userAPI.sendForgotPasswordSms(emailOrSmsDto).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast toast=Toast.makeText(LoginRegisterContext, R.string.sms_ok, Toast.LENGTH_LONG);
                            toast.show();

                            ForgotPasswordNewPasswordWithCode passwordAndCode = new ForgotPasswordNewPasswordWithCode(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn, phone);
                            passwordAndCode.show(passwordAndCode.getFrgManager(), "PassAndCode");
                            passwordAndCode.setCancelable(false);

                            globalDialog.dismiss();
                        } else {
                            if(response.code()==500) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginRegisterContext, getResources().getString(R.string.sms_error), Toast.LENGTH_LONG).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(LoginRegisterContext, R.string.email_phone_not_valid, Toast.LENGTH_LONG);
                                toast.show();
                                editTextEmail.setText(null);
                                editTextPhoneNumber.setText(null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast toast=Toast.makeText(LoginRegisterContext, R.string.network_failed, Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println("Network failed");
                        System.out.println(t.getMessage());

                        editTextEmail.setText(null);
                        editTextPhoneNumber.setText(null);
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.INTERNET_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendForgotPasswordEmail(email);
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

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }
}
