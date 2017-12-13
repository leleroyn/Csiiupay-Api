package com.ucs.xcbank.csiiupay.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResponse {
    @JsonProperty(value = "SerialNumber")
    private  String serialNumber;
    @JsonProperty(value = "Code")
    private  String code;
    @JsonProperty(value = "Message")
    private  String message;
    @JsonProperty(value = "ResponseTime")
    private String responseTime;
    @JsonProperty(value = "Data")
    private Map<String,Object> data;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ApiResponse(){
        this.code = "00";
        this.message ="";
        this.responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        data = new LinkedHashMap<>();
    }

    public ApiResponse(String _serialNumber){
        this.code = "00";
        this.message ="";
        this.serialNumber = _serialNumber;
        this.responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        data = new LinkedHashMap<>();
    }


}
