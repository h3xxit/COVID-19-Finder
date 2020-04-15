package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.app.AlertDialog;
import android.app.Dialog;

public class CustomRunnable implements Runnable{

    private SelectPeriod dialog;
    private Long time;

    @Override
    public void run() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setDialog(SelectPeriod dialog){
        this.dialog=dialog;
    }

    public SelectPeriod getDialog() {
        return dialog;
    }
}
