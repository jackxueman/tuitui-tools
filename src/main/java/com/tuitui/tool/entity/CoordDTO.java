package com.tuitui.tool.entity;

/**
 * 经纬度DTO
 *
 * @author liujianxue
 * @since 2018/5/20
 */
public class CoordDTO {

    private Double lat;

    private Double lng;

    public CoordDTO() {
    }

    public CoordDTO(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
