package com.example.project_testapp2;

public class CovidEntry {
    private Double lat;
    private Double lng;
    private Long timestamp;

    public CovidEntry(Double lat, Double lng, Long timestamp){
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;

    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
