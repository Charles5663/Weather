package com.neko642.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView cityName,date,weatherText,currentTemp,lowTemp,highTemp,windText,windLevel;
    Button selectRegionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化各控件
        cityName = (TextView)findViewById(R.id.city_name);
        date = (TextView)findViewById(R.id.current_date);
        weatherText = (TextView)findViewById(R.id.weather_text);
        currentTemp = (TextView)findViewById(R.id.current_temp);
        lowTemp = (TextView)findViewById(R.id.temp_low);
        highTemp = (TextView)findViewById(R.id.temp_high);
        windLevel = (TextView)findViewById(R.id.wind_level);
        windText = (TextView)findViewById(R.id.wind_text);
        selectRegionButton = (Button)findViewById(R.id.choose_city);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getBoolean("havecity",false)){
            //如果已经存储城市信息，从SharedPref加载
            updateWeatherUI();
        }else{
            //第一次运行加载默认信息
            getJsonStringFromServer("101030100", new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    readJsonString(MainActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateWeatherUI();
                        }
                    });

                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            });

        }

        selectRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SelectCityActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String regionCode = data.getStringExtra("regionCode");
            getJsonStringFromServer(regionCode, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    readJsonString(MainActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateWeatherUI();
                        }
                    });

                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            });

        }
    }

    private void updateWeatherUI(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(sharedPreferences.getString("cityName",null));
        date.setText(sharedPreferences.getString("date",null));
        weatherText.setText(sharedPreferences.getString("weatherText",null));
        currentTemp.setText(sharedPreferences.getString("currentTemp",null));
        lowTemp.setText(sharedPreferences.getString("lowTemp",null));
        highTemp.setText(sharedPreferences.getString("highTemp",null));
        windLevel.setText(sharedPreferences.getString("windLevel",null));
        windText.setText(sharedPreferences.getString("windText",null));
    }



    public void readJsonString(Context context , String jsonString){
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject weatherInfoObj = jsonObject.getJSONObject("retData");
            String cityName = weatherInfoObj.getString("city");
            JSONObject weatherInfo = weatherInfoObj.getJSONObject("today");
            String date = weatherInfo.getString("date");
            String lowTemp = weatherInfo.getString("lowtemp");
            String highTemp = weatherInfo.getString("hightemp");
            String weatherText = weatherInfo.getString("type");
            String windText = weatherInfo.getString("fengxiang");
            String windLevel = weatherInfo.getString("fengli");
            String currentTemp = weatherInfo.getString("curTemp");
            saveWeatherInfoToSharedPref(context,cityName,date,currentTemp,lowTemp,highTemp,windText,windLevel,weatherText);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void saveWeatherInfoToSharedPref(Context context,String cityName,String date,String currentTemp, String lowTemp,
                                       String highTemp,String windText,String windLevel,String weatherText){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("cityName",cityName);
        editor.putString("date",date);
        editor.putString("currentTemp",currentTemp);
        editor.putString("lowTemp",lowTemp);
        editor.putString("highTemp",highTemp);
        editor.putString("windText",windText);
        editor.putString("windLevel",windLevel);
        editor.putString("weatherText",weatherText);
        editor.putBoolean("havecity",true);
        editor.apply();
    }

    public void getJsonStringFromServer(final String cityCode , final HttpCallBackListener httpCallBackListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader;
                String result ;
                StringBuilder stringBuilder = new StringBuilder();
                String httpUrl = "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityid=" + cityCode;

                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("apikey", "ae9479763deb9adee32190fc34dde7f1");
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String strRead;
                    while ((strRead = reader.readLine()) != null) {
                        stringBuilder.append(strRead);
                    }
                    reader.close();
                    result = stringBuilder.toString();
                    httpCallBackListener.onFinish(result);
                } catch (Exception e) {
                    httpCallBackListener.onError(e);
                }
            }
        }).start();

    }
}
