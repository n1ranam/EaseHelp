package com.example.easehelp.model;

public class ActiveRequests {
    String userid;
    String acceptedVolunteer;
    String requestType;
    String requestStatus;
    String OTP;

    String  recordingUrl;
    String Location;
    public ActiveRequests() {
    }

    public ActiveRequests(String userid, String requestType, String recordingUrl, String Location,String otp) {
        this.userid = userid;
        this.requestType = requestType;
        this.recordingUrl= recordingUrl;
        this.requestStatus = "pending";
        this.OTP = otp;
        this.acceptedVolunteer="none";
        this.Location = Location;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public String getAcceptedVolunteer() {
        return acceptedVolunteer;
    }

    public void setAcceptedVolunteer(String acceptedVolunteer) {
        this.acceptedVolunteer = acceptedVolunteer;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }
}
