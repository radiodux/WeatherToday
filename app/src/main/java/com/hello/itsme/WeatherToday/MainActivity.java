package com.hello.itsme.WeatherToday;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.hello.itsme.WeatherToday.R;

import net.aksingh.owmjapis.api.APIException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Все наши переменные и массиваы для хранения данных
    private static final String TAG = MainActivity.class.toString();

    private ArrayList<String> weatherData = new ArrayList<>();
    private ArrayList<String> forecastData = new ArrayList<>();

    private TextView cityTemp, cityHumidity;
    private TextView windSpeed, conditionDescription, lastUpdate;
    private TextView forecastOne, fOneTemp, forecastTwo, fTwoTemp, forecastThree, fThreeTemp;
    private EditText weatherLocation;
    private ImageView weatherIcon;

    private String ERR_MSG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Для обнов
        lastUpdate = findViewById(R.id.lastUpdate);
        cityTemp = findViewById(R.id.currentTemp);
        cityHumidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.windSpeed);
        weatherLocation = findViewById(R.id.inputLocation);
        conditionDescription = findViewById(R.id.conditionDescription);
        weatherIcon = findViewById(R.id.weatherIcon);

        forecastOne = findViewById(R.id.forecastOne);
        forecastTwo = findViewById(R.id.forecastTwo);
        forecastThree = findViewById(R.id.forecastThree);
        fOneTemp = findViewById(R.id.fOneTemp);
        fTwoTemp = findViewById(R.id.fTwoTemp);
        fThreeTemp = findViewById(R.id.fThreeTemp);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button change = findViewById(R.id.tempSwitch);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherModel.getInstance().changeUnit();
                new Connection().execute();
            }
        });


        // Позволяет при нажатии на ентер в виртулаьной клавиатуре подтвердить действие
        weatherLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)){
                    WeatherModel.getInstance().setCity(weatherLocation.getText().toString());
                    new Connection().execute();
                    return true;
                }
                return false;
            }
        });
        new Connection().execute();
    }


    //  Этот метод  обновляет все поля
    protected void updateMainInfo(){
        try {
            // Получаем нужную нам информацию про погоду
            weatherData = WeatherModel.getInstance().getWeatherInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    weatherLocation.setText(weatherData.get(0));
                    cityTemp.setText(weatherData.get(1));
                    cityHumidity.setText(weatherData.get(2));
                    windSpeed.setText(weatherData.get(3));
                    String description = weatherData.get(4);
                    conditionDescription.setText(description.toUpperCase());

                    // Обновляем значок подходящий текущим погодным условиям
                    try {
                        int id = Integer.parseInt(weatherData.get(5));
                        weatherIcon.setImageResource(id);
                    } catch (Exception e){
                        e.printStackTrace();
                        weatherIcon.setImageResource(R.drawable.current_icon);
                    }

                    // Время последнего обновление погоды
                    SimpleDateFormat sdf =
                            new SimpleDateFormat("d MMM EEE hh:mm",
                                    Locale.UK);
                    lastUpdate.setText(sdf.format(Calendar.getInstance().getTime()));
                }
            });

        } catch (APIException e){
            ERR_MSG = e.getMessage();
            Log.e(TAG, "updateMainInfo(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void updateForecastInfo(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    forecastData = WeatherModel.getInstance().getForecastInfo();
                    if(forecastData != null){
                        forecastOne.setText(forecastData.get(0));
                        fOneTemp.setText(forecastData.get(1));

                        forecastTwo.setText(forecastData.get(2));
                        fTwoTemp.setText(forecastData.get(3));

                        forecastThree.setText(forecastData.get(4));
                        fThreeTemp.setText(forecastData.get(5));
                    }
                } catch  (APIException e){
                    if (!ERR_MSG.equals(""))
                        ERR_MSG = e.getMessage();
                    Log.e(TAG, "updateForecastInfo(): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    // Какая-то белиберда с стаковерфлоу
    // для устранения проблем с android.os.NetworkOnMainThreadException.
    private class Connection extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            updateMainInfo();
            updateForecastInfo();
            if (!ERR_MSG.equals("")) {
                showToast(ERR_MSG);
                ERR_MSG = "";
            }
            return null;
        }
    }


    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.setGravity(Gravity.BOTTOM, 0, 200);
                toast.show();
            }
        });
    }




}
