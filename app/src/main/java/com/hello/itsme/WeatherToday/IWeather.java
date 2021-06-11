package com.hello.itsme.WeatherToday;

import net.aksingh.owmjapis.api.APIException;

import java.util.ArrayList;

public interface IWeather {

    String getCityName() throws APIException;
    String getCityTemp() throws APIException;
    String getCityHumidity() throws APIException;
    String getCityWindSpeed() throws APIException;
    String getWeatherDescription() throws APIException;
    int getConditionId() throws APIException;


    void setCity(String name);
    void changeUnit();
    ArrayList<String> getWeatherInfo() throws APIException;
    ArrayList<String> getForecastInfo() throws APIException;
}
