package ch.hevs.datasemlab.cityzen.notifications;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alex on 1/24/2017.
 */

public class NearestCulturalInterestInfo implements Parcelable{

    private String description;
    private String latitude;
    private String longitude;

    public NearestCulturalInterestInfo(String description, String latitude, String longitude){
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDescription(){
        return this.description;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public  String getLongitude(){
        return this.longitude;
    }

    public NearestCulturalInterestInfo(Parcel in){

        String[] data = new String[3];
        in.readStringArray(data);
        this.description = data[0];
        this.latitude = data[1];
        this.longitude = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    public static final Parcelable.Creator<NearestCulturalInterestInfo> CREATOR = new Parcelable.Creator<NearestCulturalInterestInfo>() {
        public NearestCulturalInterestInfo createFromParcel(Parcel source) {
            return new NearestCulturalInterestInfo(source);
        }

        public NearestCulturalInterestInfo[] newArray(int size) {
            return new NearestCulturalInterestInfo[size];
        }
    };
}
