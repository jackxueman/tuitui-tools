package com.tuitui.tool.entity;


/**
 * 位置信息DTO
 *
 * @author liujianxue
 * @since 2018/5/18
 */
public class LocationDTO {

    private CoordDTO location;

    private String address;

    private String geohash;

    public CoordDTO getLocation() {
        return location;
    }

    public void setLocation(CoordDTO location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }
}
