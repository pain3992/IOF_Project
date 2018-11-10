package com.example.ryu_w.calendar;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

@DynamoDBTable(tableName = "farms_diary")
public class DailyLogDO {
    static private String _id;
    private String _time;
    private String _content;
    private Double _CO2;
    private Double _bat;
    private Double _channel;
    private Double _humidity;
    private Double _rootT;
    private Double _soil;
    private Double _temperature;

    @DynamoDBHashKey(attributeName = "dev_id")
    @DynamoDBAttribute(attributeName = "dev_id")
    public String getId() {
        return _id;
    }
    public void setId(final String _id) {
        this._id = _id;
    }

    @DynamoDBRangeKey(attributeName = "time")
    @DynamoDBAttribute(attributeName = "time")
    public String getTime() {
        return _time;
    }
    public void setTime(final String _time) {
        this._time= _time;
    }

//    static Condition rangeKeyCondition = new Condition()
//            .withComparisonOperator(ComparisonOperator.GT.toString())
//            .withAttributeValueList(new AttributeValue().withS("1"));
//
//    static DynamoDBQueryExpression dbQueryExpression = new DynamoDBQueryExpression()
//            .withHashKeyValues(_id)
//            .withRangeKeyCondition("time", rangeKeyCondition);

    @DynamoDBAttribute(attributeName = "content")
    public String getContent(){
        return _content;
    }
    public void setContent(final String _content){
        this._content = _content;
    }


    @DynamoDBIndexHashKey(attributeName = "CO2", globalSecondaryIndexName = "CO2")
    public Double getCO2() {
        return _CO2;
    }
    public void setCO2(final Double _CO2) {
        this._CO2 = _CO2;
    }


    @DynamoDBIndexRangeKey(attributeName = "battery", globalSecondaryIndexName = "battery")
    public Double getbat() {
        return _bat;
    }
    public void setbat(final Double _bat) {
        this._bat= _bat;
    }

    // 내가 추가
    @DynamoDBAttribute(attributeName = "channel")
    public Double getchannel(){
        return _channel;
    }
    public void setchannel(final Double _channel){
        this._channel = _channel;
    }

    @DynamoDBAttribute(attributeName = "humidity")
    public Double gethumi(){
        return _humidity;
    }
    public void sethumi(final Double _humidity){
        this._humidity = _humidity;
    }


    @DynamoDBAttribute(attributeName = "rootT")
    public Double getrootT(){
        return _rootT;
    }
    public void setrootT(final Double _rootT){
        this._rootT = _rootT;
    }


    @DynamoDBAttribute(attributeName = "soil")
    public Double getsoil(){
        return _soil;
    }
    public void setsoil(final Double _soil){
        this._soil = _soil;
    }


    @DynamoDBAttribute(attributeName = "temperature")
    public Double gettemp(){
        return _temperature;
    }
    public void settemp(final Double _temperature){
        this._temperature = _temperature;
    }


    public String toString(){
        return _bat+ "," + _channel + "," + _temperature + ","+ _rootT + "," + _soil + ","+_humidity + "," + _CO2 +"," + _content;
//        return "iof[장치="+_id+",시간="+_time+",CO2="+_CO2+",BAT="+_bat+"]";
    }
}
