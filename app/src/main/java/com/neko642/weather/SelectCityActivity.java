package com.neko642.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {
    EditText searchBox;
    Button searchCityButton;
    ListView cityList;
    List<Region> regionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        searchBox = (EditText)findViewById(R.id.search_box);
        searchCityButton = (Button)findViewById(R.id.searchCity_button);
        cityList = (ListView)findViewById(R.id.city_list);
        searchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encodedName = "";
                try{
                    encodedName = URLEncoder.encode(searchBox.getText().toString(),"UTF-8");
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                getJsonString(encodedName, new HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
                        List<Region> regionListTemp = resolveRegionJsonData(response);
                        if (regionListTemp != null) {
                            regionList = regionListTemp;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateRegionListView(regionList);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
            }
        });
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Region region = regionList.get(position);
                String regionCode = region.getRegionId();
                Intent intent = new Intent();
                intent.putExtra("regionCode", regionCode);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



    public void updateRegionListView(List<Region> regionList){
        RegionAdapter regionAdapter = new RegionAdapter(SelectCityActivity.this,R.layout.region_list_layout,regionList);
        cityList.setAdapter(regionAdapter);
        regionAdapter.notifyDataSetChanged();
    }

    public List<Region> resolveRegionJsonData(String regionJsonString){
        List<Region> regionList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(regionJsonString);
            String errorMsg = jsonObject.getString("errMsg");
            if(errorMsg.equals("success")){
                JSONArray regionArray = jsonObject.getJSONArray("retData");
                for(int i = 0 ; i < regionArray.length() ; i++){
                    JSONObject regionJsonObj = regionArray.getJSONObject(i);
                    Region region = new Region();
                    region.setProvince(regionJsonObj.getString("province_cn"));
                    region.setCity(regionJsonObj.getString("district_cn"));
                    region.setCounty(regionJsonObj.getString("name_cn"));
                    region.setRegionId(regionJsonObj.getString("area_id"));
                    regionList.add(region);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return  regionList;
    }



    private void getJsonString(final String regionName,final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader;
                String result ;
                StringBuilder stringBuilder = new StringBuilder();
                String httpUrl = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + regionName;

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
                    listener.onFinish(result);
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        }).start();
    }
}
