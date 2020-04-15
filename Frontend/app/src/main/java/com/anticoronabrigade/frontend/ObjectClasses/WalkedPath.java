package com.anticoronabrigade.frontend.ObjectClasses;

import android.app.ListActivity;
import android.os.Parcel;
import android.os.Parcelable;

import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class WalkedPath implements Parcelable {
    @SerializedName("date")
    @Expose
    private Long date;
    @SerializedName("points")
    @Expose
    private List<MapLocation> points;

    public WalkedPath()
    {
        date = 0L;
        points = new ArrayList<>();
    }

    public WalkedPath(Parcel in)
    {
        points = new ArrayList<>();
        date = in.readLong();
        int size = in.readInt();
        for (int i = 0; i<size; ++i) {
            Double lat = in.readDouble();
            Double lon = in.readDouble();
            points.add(new MapLocation(lat, lon));
        }
    }

    public static final Parcelable.Creator<WalkedPath> CREATOR =
            new Parcelable.Creator<WalkedPath>() {

                @Override
                public WalkedPath createFromParcel(Parcel source) {
                    return new WalkedPath(source);
                }

                @Override
                public WalkedPath[] newArray(int size) {
                    return new WalkedPath[size];
                }

            };

    public WalkedPath(Long date, List<MapLocation> points) {
        this.date = date;
        this.points = points;
    }

    public void addPath(WalkedPath pathToAdd){
        this.points.addAll(pathToAdd.points);
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<MapLocation> getPoints() {
        return points;
    }

    public void setPoints(List<MapLocation> points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int i;
        for(i = 0; i<points.size() && points.get(i).latitude == 1000d; ++i) {
            date+= Const.TICK_RATE * 1000;
        }

        dest.writeLong(date);
        dest.writeInt(points.size()-i);

        for (; i<points.size(); ++i) {
            dest.writeDouble(points.get(i).latitude);
            dest.writeDouble(points.get(i).longitude);
        }
    }
}
