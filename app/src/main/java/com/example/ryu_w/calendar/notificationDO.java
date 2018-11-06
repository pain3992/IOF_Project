package com.example.ryu_w.calendar;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


@DynamoDBTable(tableName = "notification")

public class notificationDO {
    private String _id;
    private String _time;
    private String _H_temp;
    private String _L_temp;
    private String _H_humi;
    private String _L_humi;
    private String _H_CO2;
    private String _L_CO2;
    private String _H_rootT;
    private String _L_rootT;
    private String _H_soil;
    private String _L_soil;


    @DynamoDBHashKey(attributeName = "dev_id")
    @DynamoDBAttribute(attributeName = "dev_id")
    public String getNId() {
        return _id;
    }
    public void setNId(final String _id) {
        this._id = _id;
    }


    @DynamoDBRangeKey (attributeName = "time")
    @DynamoDBAttribute(attributeName = "time")
    public String getNTime() {
        return _time;
    }
    public void setNTime(final String _time) {
        this._time= _time;
    }


    @DynamoDBIndexHashKey(attributeName = "H_temperature", globalSecondaryIndexName = "H_temperature")
    public String getNtemp() {
        return _H_temp;
    }
    public void setNtemp(final String _H_temp) {
        this._H_temp = _H_temp;
    }


    @DynamoDBIndexRangeKey(attributeName = "L_temperature", globalSecondaryIndexName = "L_temperature")
    public String getNLtemp() {
        return _L_temp;
    }
    public void setNLtemp(final String _L_temp) {
        this._L_temp= _L_temp;
    }


    @DynamoDBAttribute(attributeName = "H_humidity")
    public String getNhumi(){
        return _H_humi;
    }
    public  void setNhumi(final String _H_humi){
        this._H_humi = _H_humi;
    }


    @DynamoDBAttribute(attributeName = "L_humidity")
    public String getNLhumi(){
        return _L_humi;
    }
    public void setNLhumi(final String _L_humi){
        this._L_humi = _L_humi;
    }


    @DynamoDBAttribute(attributeName = "H_CO2")
    public String getNCO2(){
        return _H_CO2;
    }
    public void setNCO2(final String _H_CO2){
        this._H_CO2 = _H_CO2;
    }


    @DynamoDBAttribute(attributeName = "L_CO2")
    public String getNLCO2(){
        return _L_CO2;
    }
    public void setNLCO2(final String _L_CO2){
        this._L_CO2 = _L_CO2;
    }


    @DynamoDBAttribute(attributeName = "H_rootT")
    public String getNrootT(){
        return _H_rootT;
    }
    public  void setNrootT(final String _H_rootT){
        this._H_rootT =_H_rootT;
    }


    @DynamoDBAttribute(attributeName = "L_rootT")
    public String getNLrootT(){
        return _L_rootT;
    }
    public  void setNLrootT(final String _L_rootT){
        this._L_rootT =_L_rootT;
    }


    @DynamoDBAttribute(attributeName = "H_soil")
    public String getNsoil(){
        return _H_soil;
    }
    public  void setNsoil(final String _H_soil){
        this._H_soil =_H_soil;
    }


    @DynamoDBAttribute(attributeName = "L_soil")
    public String getNLsoil(){
        return _L_soil;
    }
    public  void setNLsoil(final String _L_soil){
        this._L_soil =_L_soil;
    }
}

