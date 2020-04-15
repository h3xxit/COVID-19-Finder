package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.Manifest;
import android.app.Activity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.PasswordStrength;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordNewPasswordWithCode extends AppCompatDialogFragment {
    private EditText editTextCode;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private ProgressBar progressBar;
    private TextInputLayout editTextPasswordLayout;
    private TextInputLayout editTextConfirmPasswordLayout;

    private FragmentManager frgManager;
    private UserAPI userAPI;
    private static final int INTERNET_PERMISSION = 103;
    private Context LoginRegisterContext;
    private Activity LoginRegisterActivity;
    private android.app.AlertDialog globalDialog;
    private TextView emailTextView;
    private Button infectedBtn;
    private String emailOrPhone;

    public ForgotPasswordNewPasswordWithCode(FragmentManager fragmentManager, UserAPI userAPI, Context context, Activity activity, TextView emailTextView, Button infectedBtn, String emailOrPhone) {
        this.frgManager=fragmentManager;
        this.userAPI=userAPI;
        this.LoginRegisterContext=context;
        this.LoginRegisterActivity=activity;
        this.emailTextView=emailTextView;
        this.infectedBtn=infectedBtn;
        this.emailOrPhone=emailOrPhone;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        try {
            LayoutInflater layoutInflater = LoginRegisterActivity.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.layout_password_code, null);

            editTextCode = view.findViewById(R.id.editTextCode);
            editTextPassword = view.findViewById(R.id.editTextPassword);
            editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
            progressBar = view.findViewById(R.id.progressBar);

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

            editTextConfirmPasswordLayout = view.findViewById(R.id.editTextConfirmPasswordLayout);
            editTextConfirmPasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editTextPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()){
                        editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });

            builder.setView(view)
                    .setTitle(R.string.change_password)
                    .setNeutralButton(R.string.resend, null)
                    .setPositiveButton(R.string.ok, null)
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ForgotPasswordEmailOrPhoneDialog registerEmailOrPhone = new ForgotPasswordEmailOrPhoneDialog(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn);
                            registerEmailOrPhone.show(frgManager, "RegisterEmailAndPhoneNumber");
                            registerEmailOrPhone.setCancelable(false);
                            globalDialog.dismiss();
                        }
                    });

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
        Button okButton = globalDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                performOkButtonAction();
            }
        });

        Button resendButton = globalDialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performResendButton();
            }
        });
    }

    private void performResendButton() {
        progressBar.setVisibility(View.VISIBLE);

        if(emailOrPhone.contains("@"))
            sendForgotPasswordEmail(emailOrPhone);
        else
            sendForgotPasswordSms(emailOrPhone);
    }

    private void performOkButtonAction() {
        String codeString = editTextCode.getText().toString();
        if("".equals(codeString))
            return;
        Integer code = Integer.valueOf(codeString);
        String password = editTextPassword.getText().toString();
        String passwordConfirmation = editTextConfirmPassword.getText().toString();

        if("".equals(password) || "".equals(passwordConfirmation))
            return;
        progressBar.setVisibility(View.VISIBLE);

        if(checkPassword(password, passwordConfirmation)){
            if(PasswordStrength.WEAK != PasswordStrength.calculate(password)) {
                changePassword(code, password, emailOrPhone);
            }
            else {
                Toast.makeText(getContext(), R.string.weak_password, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
        else {
            Toast.makeText(getContext(), R.string.password_missmatch, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void changePassword(Integer code, String password, String email) {
        if (getContext() != null && getActivity() != null) {
            if (ContextCompat.checkSelfPermission(LoginRegisterContext,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginRegisterActivity,
                        new String[]{Manifest.permission.INTERNET},
                        INTERNET_PERMISSION);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                ChangePasswordDto registerWithCodeDto = new ChangePasswordDto(email, password, code);
                userAPI.changePassword(registerWithCodeDto).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);

                            //Login login = new Login(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn);
                            //login.show(login.getFrgManager(), "Login");
                            //login.setCancelable(false);
                            Toast.makeText(LoginRegisterContext, getResources().getString(R.string.register_successful), Toast.LENGTH_LONG).show();

                            globalDialog.dismiss();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginRegisterContext, getResources().getString(R.string.code_error), Toast.LENGTH_LONG).show();

                            editTextPassword.setText(null);
                            editTextCode.setText(null);
                            editTextConfirmPassword.setText(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginRegisterContext, getResources().getString(R.string.network_failed), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
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
                        INTERNET_PERMISSION);
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
                        } else {
                            if(response.code()==500) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginRegisterContext, getResources().getString(R.string.email_error), Toast.LENGTH_LONG).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast=Toast.makeText(LoginRegisterContext, R.string.resend_error, Toast.LENGTH_LONG);
                                toast.show();
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
                    }
                });
                return;
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
                            Toast toast=Toast.makeText(LoginRegisterContext, R.string.resend_ok, Toast.LENGTH_LONG);
                            toast.show();

                            PasswordAndCode passwordAndCode = new PasswordAndCode(frgManager, userAPI, LoginRegisterContext, LoginRegisterActivity, emailTextView, infectedBtn, phone);
                            passwordAndCode.show(passwordAndCode.getFrgManager(), "PassAndCode");
                            passwordAndCode.setCancelable(false);

                            globalDialog.dismiss();
                        } else {
                            if(response.code()==500){
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginRegisterContext, R.string.sms_error, Toast.LENGTH_LONG).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(LoginRegisterContext, R.string.email_already_registered, Toast.LENGTH_LONG);
                                toast.show();
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
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkPassword(String password, String passwordConfirmation){
        return password.equals(passwordConfirmation);
    }

    public FragmentManager getFrgManager() {
        return frgManager;
    }
}
