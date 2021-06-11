package com.hello.itsme.WeatherToday;


import com.hello.itsme.WeatherToday.R;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.HourlyWeatherForecast;
import net.aksingh.owmjapis.model.param.WeatherData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WeatherModel implements IWeather {
    private static final String TAG = WeatherModel.class.toString();

    private static WeatherModel instance = null;
    // Задаем ключ api
    private final String API_KEY = "929b3b74bc0c3c4d83aef926f8d76969";
    private OWM weatherApp = new OWM(API_KEY);

    private String city = "zaporizhzhia";
    private CurrentWeather currentWeather;
    private OWM.Unit unit = OWM.Unit.METRIC;

    private WeatherModel() {
        weatherApp.setUnit(unit);
        weatherApp.setLanguage(OWM.Language.RUSSIAN);
    }

    public static WeatherModel getInstance(){
        if (instance == null){
            instance = new WeatherModel();
        }
        return instance;
    }


    // Возвращает массив с данныыми относительно выбранного города
    @Override
    public ArrayList<String> getWeatherInfo() throws APIException {
        ArrayList<String> info = new ArrayList<>();
        currentWeather = weatherApp.currentWeatherByCityName(city);

        info.add(getCityName());
        info.add(getCityTemp());
        info.add(getCityHumidity());
        info.add(getCityWindSpeed());
        info.add(getWeatherDescription());
        info.add(String.valueOf(getConditionId()));

        return info;
    }

    // Получаем погодные данные с нашего api
    // Пустой массив который к нам потом вернется уже с данными
    @Override
    public ArrayList<String> getForecastInfo() throws APIException{
        HourlyWeatherForecast hourlyWeatherForecast = weatherApp.hourlyWeatherForecastByCityName(city);
        if (hourlyWeatherForecast.hasDataList()) {
            ArrayList<String> info = new ArrayList<>();
            // Делаем цикл на 3 захода для данных на 3 временных промежутка
            for (int i = 0; i < 3; i++) {
                // Получаем данные про погоду и время
                WeatherData wd = Objects.requireNonNull(hourlyWeatherForecast.getDataList()).get(i);
                String time = hourlyWeatherForecast.getDataList().get(i).getDateTimeText();

                // Делаем отображение даты
                try{
                    Date d = new SimpleDateFormat("dd-mm-yyyy h:mm:ss", Locale.UK).parse(time);
                    info.add(new SimpleDateFormat("kk:mm ", Locale.UK).format(d.getTime()));
                } catch (ParseException e){
                    e.printStackTrace();
                }

                // Для
                double temp = wd.getMainData().getTemp();
                String deg;
                if (unit == OWM.Unit.METRIC)
                    deg = "°C";
                else deg = "°F";
                info.add(String.format(Locale.ENGLISH, "%.0f%s", temp, deg));
            }
            return info;
        }
        return null;
    }

    // Возвращает нам данные о названии города
    @Override
    public String getCityName() {
        if (currentWeather.hasMainData())
        return currentWeather.getCityName() + ", " +
                currentWeather.getSystemData().getCountryCode();
        else return "Не найден город с таким названием( Нужно вводить киррилицей ;( )";
    }


    // Возвращает нам данные о температуре
    @Override
    public String getCityTemp() {
        if (currentWeather.hasMainData()) {
            double temp = currentWeather.getMainData().getTemp();
            String deg;
            if (unit == OWM.Unit.METRIC)
                deg = "°C";
            else deg = "°F";
            return String.format(Locale.ENGLISH, "%.0f%s", temp, deg);
        }
        else return "Ошибка";
    }


    // Возвращает нам данные о облачности
    @Override
    public String getCityHumidity(){
        if(currentWeather.hasMainData()) {
            int humidity = currentWeather.getMainData().getHumidity();
            return String.format(Locale.ENGLISH, "%s%s", humidity,"%");
        }
        return null;
    }

    // Возвращает нам данные о скорости ветра
    @Override
    public String getCityWindSpeed() {
        if(currentWeather.hasMainData()){
            double speed = currentWeather.getWindData().getSpeed();
            String ut;
            if (unit == OWM.Unit.METRIC)
                ut = "М/с";
            else ut = "Миля/ч";
            return String.format(Locale.ENGLISH, "%s %s", speed, ut);
        }
        return null;
    }

    // Возвращаем описание погоды(онли инглиш)
    @Override
    public String getWeatherDescription() {
        if (currentWeather.hasWeatherList())
        return currentWeather.getWeatherList().get(0).getDescription();
        return "Ты чо дурак?";
    }


    // Возвращает id для отображения нужного значка.
    @Override
    public int getConditionId() {
        if (currentWeather.hasWeatherList()) {
            String code = currentWeather.getWeatherList().get(0).getIconCode();
            int id;
            switch(code){
                case "01d": id = R.drawable.sunny;
                    break;
                case "01n": id = R.drawable.night_clear;
                    break;
                case "02d": id = R.drawable.cloudy;
                    break;
                case "02n": id = R.drawable.night_cloudy;
                    break;
                case "03d": id = R.drawable.cloudy_2;
                    break;
                case "03n": id = R.drawable.night_cloudy_2;
                    break;
                case "04d": id = R.drawable.cloudy_3;
                    break;
                case "04n": id = R.drawable.night_cloudy_3;
                    break;
                case "09d": id = R.drawable.rainy_2;
                    break;
                case "09n": id = R.drawable.night_rainy_2;
                    break;
                case "10d": id = R.drawable.rainy;
                    break;
                case "10n": id = R.drawable.night_rainy;
                    break;
                case "11d": id = R.drawable.stormy;
                    break;
                case "11n": id = R.drawable.night_stormy;
                    break;
                default: id = R.drawable.current_icon;
                    break;
            }
            return id;
        }
        else return R.drawable.current_icon;
    }


    //
    @Override
    public void setCity(String name) {
        this.city = name;
    }

    // Изменение сисетмы измерения
    @Override
    public void changeUnit() {
        if (unit == OWM.Unit.METRIC)
            unit = OWM.Unit.IMPERIAL;
        else unit = OWM.Unit.METRIC;

        weatherApp.setUnit(unit);

    }
}
