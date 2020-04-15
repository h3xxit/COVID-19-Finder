package com.anticoronabrigade.backend.customClasses;

public class Constants {
    public static final Long TEN_DAYS_IN_MILLISECONDS = 864000000L;
    public static final Long FIVE_DAYS_IN_MILLISECONDS = 432000000L;
    public static final int TICK_RATE = 10; //we get the location once every TICK_RATE seconds;
    public static final long LIFESPAN_IN_AIR = 300000;
    public static final double MIN_DIST_FOR_INFECTION = 10; //face to face infection
    public static final double MIN_DIST_TO_CONSIDER_PATH = 10000; //distance for path starting points
    public static final Long FIVE_MINUTES_IN_MILLISECONDS = 300000L;
    public static final String ACCOUNT_SID=""; //Twilio account SID
    public static final String AUTH_TOKEN=""; //Twilio account auth token
    public static final String TWILIO_PHONE_NUMBER=""; //Twilio phone number
}
