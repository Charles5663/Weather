package com.neko642.weather;

/**
 * Created by charl on 2016/2/22.
 */
public class Region {
    private String province;
    private String city;
    private String county;
    private String regionId;
    public void setRegionId(String regionId){
        this.regionId = regionId;
    }
    public void setProvince(String province){
        this.province = province;
    }
    public void setCity(String city){
        this.city = city;
    }
    public void setCounty(String county){
        this.county = county;
    }
    public String getProvince(){
        return province;
    }
    public String getCity(){
        return city;
    }
    public String getCounty(){
        return county;
    }
    public String getRegionId(){
        return regionId;
    }
}
