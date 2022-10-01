package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class DeviceAuthJson {
    @SerializedName("user_code")
    private String userCode;
    @SerializedName("device_code")
    private String deviceCode;
    @SerializedName("verification_uri")
    private String verificationURI;
    @SerializedName("expires_in")
    private int expiresIn;
    private int interval;
    private String message;

    public String getUserCode() {
        return this.userCode;
    }

    public String getDeviceCode() {
        return this.deviceCode;
    }

    public String getVerificationURI() {
        return this.verificationURI;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public int getInterval() {
        return this.interval;
    }

    public String getMessage() {
        return this.message;
    }
}
