package com.app;

public class History {

    private String _id;
    private String number;
    private String date;
    private int triangleCount;

    public History(String number, String date, int triangleCount, String uuid) {
        this._id = uuid;
        this.number = number;
        this.date = date;
        this.triangleCount = triangleCount;
    }

    public String getNumber() {
        return this.number;
    }

    public String getDate() {
        return this.date;
    }

    public int getTriangleCount() {
        return this.triangleCount;
    }

    public String getUUID() {
        return this._id;
    }

    public String setNumber(String number) {
        return this.number = number;
    }

    public String setDate(String date) {
        return this.date = date;
    }

    public int setTriangleCount(int triangleCount) {
        return this.triangleCount = triangleCount;
    }

}
