package com.minicreate.online_taxi.transmission;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Entity mapped to table WIFI_DEVICE.
 */
public class WifiDevice implements Parcelable {

    /**
     * Not-null value.
     */
    private String ssid = "";
    /**
     * Not-null value.
     */
    private String mac = "0:0:0:0:0:0";
    private String name;
    private String pw;

    public WifiDevice() {
    }


    public WifiDevice(String ssid, String mac, String name) {
        this.ssid = ssid;
        this.mac = mac;
        this.name = name;
    }


    /**
     * Not-null value.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Not-null value.
     */
    public String getMac() {
        return mac;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameIfEmptyReturnSsid() {

        return TextUtils.isEmpty(name) ? ssid : name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof WifiDevice)) return false;

        WifiDevice that = (WifiDevice) o;

        if (!mac.equals(that.mac)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mac.hashCode();
    }

    public WifiDevice(Parcel parcel) {
        ssid = parcel.readString();
        mac = parcel.readString();
        name = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ssid);
        parcel.writeString(mac);
        parcel.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<WifiDevice> CREATOR = new Parcelable.Creator<WifiDevice>() {
        @Override
        public WifiDevice createFromParcel(Parcel parcel) {
            return new WifiDevice(parcel);
        }

        @Override
        public WifiDevice[] newArray(int i) {
            return new WifiDevice[i];
        }
    };

    @Override
    public String toString() {
        return "WifiDevice [ ssid=" + ssid + ", mac=" + mac
                + ", name=" + name + "]";
    }


    public String getPw() {
        return pw;
    }


    public void setPw(String pw) {
        this.pw = pw;
    }

}
