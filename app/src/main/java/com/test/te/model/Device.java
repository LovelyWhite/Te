package com.test.te.model;

import java.net.Socket;

public class Device {
    Socket socket;
    String deviceID;
    String devicePW;
    String position;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDevicePW() {
        return devicePW;
    }

    public void setDevicePW(String devicePW) {
        this.devicePW = devicePW;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
