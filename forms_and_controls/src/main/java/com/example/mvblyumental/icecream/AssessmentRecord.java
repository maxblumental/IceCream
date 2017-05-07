package com.example.mvblyumental.icecream;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

class AssessmentRecord implements Parcelable {

    String stationId;

    int target;

    Date date;

    int actual;

    int variance;

    public AssessmentRecord() {
    }

    AssessmentRecord(String stationId, int actual, int variance) {
        this.stationId = stationId;
        this.target = 42;
        this.date = new Date();
        this.actual = actual;
        this.variance = variance;
    }

    private AssessmentRecord(Parcel in) {
        stationId = in.readString();
        target = in.readInt();
        date = new Date(in.readLong());
        actual = in.readInt();
        variance = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stationId);
        dest.writeInt(target);
        dest.writeLong(date.getTime());
        dest.writeInt(actual);
        dest.writeInt(variance);
    }

    public static final Creator<AssessmentRecord> CREATOR = new Creator<AssessmentRecord>() {
        @Override
        public AssessmentRecord createFromParcel(Parcel in) {
            return new AssessmentRecord(in);
        }

        @Override
        public AssessmentRecord[] newArray(int size) {
            return new AssessmentRecord[size];
        }
    };
}