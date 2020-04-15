package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.os.Parcel;
import android.os.Parcelable;

import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MeetupLocationsDto implements Parcelable {
    @SerializedName("points")
    @Expose
    private List<MeetupPointDto> points;

    public MeetupLocationsDto() {
        this.points = new ArrayList<>();
    }

    public MeetupLocationsDto(List<MeetupPointDto> points) {
        this.points = points;
    }

    public MeetupLocationsDto(Parcel in)
    {
        points = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i<size; ++i) {
            Double lat = in.readDouble();
            Double lon = in.readDouble();
            Long date = in.readLong();
            points.add(new MeetupPointDto(new PointDto(lat, lon), date));
        }
    }

    public static final Parcelable.Creator<MeetupLocationsDto> CREATOR =
            new Parcelable.Creator<MeetupLocationsDto>() {

                @Override
                public MeetupLocationsDto createFromParcel(Parcel source) {
                    return new MeetupLocationsDto(source);
                }

                @Override
                public MeetupLocationsDto[] newArray(int size) {
                    return new MeetupLocationsDto[size];
                }

            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(points.size());

        for (MeetupPointDto meetupPointDto : points) {
            dest.writeDouble(meetupPointDto.getPoint().getLatitude());
            dest.writeDouble(meetupPointDto.getPoint().getLongitude());
            dest.writeLong(meetupPointDto.getDate());
        }
    }

    public void add(MeetupLocationsDto other)
    {
        points.addAll(other.getPoints());
    }

    public List<MeetupPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<MeetupPointDto> points) {
        this.points = points;
    }
}
