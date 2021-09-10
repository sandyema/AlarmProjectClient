package com.example.stockalarms_client;

import java.io.Serializable;

public class Alarm implements Serializable {

    Integer id;
    String alarm_name;
    Double initial_price;
    Double current_price;
    Double wanted_percent;
    Integer over_price;
    Integer less_price;
    Integer active;

    public Alarm() {
    }

    public Alarm(String alarm_name, Double initial_price,Double current_price, Double wanted_percent, Integer over_price, Integer less_price,Integer active) {
        this.alarm_name = alarm_name;
        this.initial_price = initial_price;
        this.current_price=current_price;
        this.wanted_percent = wanted_percent;
        this.over_price = over_price;
        this.less_price = less_price;
        this.active=active;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlarm_name() {
        return alarm_name;
    }

    public void setAlarm_name(String alarm_name) {
        this.alarm_name = alarm_name;
    }

    public Double getInitial_price() {
        return initial_price;
    }

    public void setInitial_price(Double initial_price) {
        this.initial_price = initial_price;
    }

    public Double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(Double current_price) {
        this.current_price = current_price;
    }

    public Double getWanted_percent() {
        return wanted_percent;
    }

    public void setWanted_percent(Double wanted_percent) {
        this.wanted_percent = wanted_percent;
    }

    public Integer getOver_price() {
        return over_price;
    }

    public void setOver_price(Integer over_price) {
        this.over_price = over_price;
    }

    public Integer getLess_price() {
        return less_price;
    }

    public void setLess_price(Integer less_price) {
        this.less_price = less_price;
    }
}
