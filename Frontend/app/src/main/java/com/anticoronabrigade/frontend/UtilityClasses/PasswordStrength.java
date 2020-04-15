package com.anticoronabrigade.frontend.UtilityClasses;

import android.graphics.Color;

public enum PasswordStrength {
    WEAK("Weak", Color.parseColor("#61ad85")),
    MEDIUM("Medium", Color.parseColor("#4d8a6a")),
    STRONG("Strong", Color.parseColor("#3a674f")),
    VERY_STRONG("Very_strong", Color.parseColor("#264535"));

    public String msg;
    public int color;

    PasswordStrength(String msg, int color) {
        this.msg = msg;
        this.color = color;
    }

    public static PasswordStrength calculate(String password) {
        int score=0;
        boolean upper=false;
        boolean lower=false;
        boolean digit=false;
        boolean specialChar=false;

        int length=password.length();

        for (int i=0; i<length; i++) {
            char c=password.charAt(i);

            if(!specialChar && !Character.isLetterOrDigit(c)) {
                score++;
                specialChar=true;
            } else {
                if(!digit && Character.isDigit(c)) {
                    score++;
                    digit=true;
                } else {
                    if(!upper || !lower) {
                        if(Character.isUpperCase(c)) {
                            upper=true;
                        } else {
                            lower=true;
                        }

                        if(upper && lower) {
                            score++;
                        }
                    }
                }
            }
        }

        if(length>Const.MAX_LENGTH) {
            score++;
        } else if(length<Const.MIN_LENGTH) {
            score=0;
        }

        switch(score) {
            case 0: return WEAK;
            case 1: return MEDIUM;
            case 2: return STRONG;
            case 3: return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }
}
