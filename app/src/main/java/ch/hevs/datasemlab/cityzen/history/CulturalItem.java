package ch.hevs.datasemlab.cityzen.history;

import android.os.Parcel;
import android.os.Parcelable;

public class CulturalItem implements Parcelable{

    public static final String CULTURALITEMS = "culturalItems";

    private String title;
    private byte[] image;
    private String year;
    private String description;

    public CulturalItem (String title, byte[] image, String year, String description) {
        this.title = title;
        this.image = image;
        this.year = year;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public byte[] getImage() {
        return this.image;
    }

    public String getYear() { return this.year; }

    public String getDescription() { return this.description; }

    public CulturalItem(Parcel in){
        this.title = in.readString();
        in.readByteArray(this.image);
        this.year = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeByteArray(this.image);
        dest.writeString(this.year);
        dest.writeString(this.description);
    }

    public static final Parcelable.Creator<CulturalItem> CREATOR = new Parcelable.Creator<CulturalItem>(){

        public CulturalItem createFromParcel(Parcel source){
            return new CulturalItem(source);
        }

        public CulturalItem[] newArray(int size){
            return new CulturalItem[size];
        }
    };
}
