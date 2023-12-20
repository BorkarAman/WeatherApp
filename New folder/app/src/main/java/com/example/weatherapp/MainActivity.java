package com.example.weatherapp;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    //private static final DecimalFormat decfor = new DecimalFormat("0.00");
    //-------------------------------------------
    String mainss="";
    String description="";
    String temp="";
    String temp_feelsLike="";
    String temp_max = "";
    String temp_min = "";
    String pressure = "";
    String humidity = "";
    String visibility = "";
    String country = "";
    String sunRise = "";
    String sunset = "";
    String nameCity = "";

    ArrayList<String> list = new ArrayList<String>();
    TextView nameOfPlace, tempBig;

    //-------------------------------------------

    Geocoder geocoder;

    //------------------------------------
    LocationManager locationManager;
    LocationListener locationListener;
    ListView listView;

    //-----------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.i("ji", "jiij");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000000, 10000, locationListener);
            }

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameOfPlace = findViewById(R.id.textView);
        tempBig = findViewById(R.id.textView2);

        listView = findViewById(R.id.listViewContainer);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
                String cityName = "";
                String stateName = "";
                String countryName = "";
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);

                    cityName = addresses.get(0).getLocality();
                    getWeather();

                    nameOfPlace.setText(cityName);
                    stateName = addresses.get(0).getAddressLine(1);
                    countryName = addresses.get(0).getAddressLine(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("Location", location.toString());
            }

        };

        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        }else{
            Log.i("from", "Here");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


        long currentTime = Calendar.getInstance().getTimeInMillis();
        Log.i("time", String.valueOf(currentTime));

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.i("ip", ip);

        Log.i("IP4", Utils.getIPAddress(true));
        Log.i("IP6", Utils.getIPAddress(false));


    }

    public void getWeather(){
        try{
            DownloadTask task = new DownloadTask();
            //String encodedName = URLEncoder.encode(nameOfPlace.getText().toString(), "UTF-8");
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + "nagpur" + "&appid=3034187a3f43b36f5d549815ca45036b";

            task.execute(url).toString();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            Log.i("Urll", strings[0]);
            try{
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                    //Log.i("Amanb", "Hello");
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return "Error";
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onPostExecute(String s){
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
               // Log.i("SSSSSSSSSSSSSS", s);
                nameCity = jsonObject.getString("name");
                visibility = jsonObject.getString("visibility");
                String weather = jsonObject.getString("weather");
                //JSONObject p = jsonObject.getString("main");
                String mainA = jsonObject.getString("main");
                JSONObject mainAkaA = new JSONObject(mainA);
                    temp = mainAkaA.getString("temp");
                    Float valueToShow = Float.parseFloat(temp);
                    valueToShow = valueToShow - 273;
                    String sas = String.valueOf(valueToShow);
                    sas = sas.substring(0,2);
                    tempBig.setText(sas);
                    temp_feelsLike = mainAkaA.getString("feels_like");
                    temp_max = mainAkaA.getString("temp_max");
                    temp_min = mainAkaA.getString("temp_min");
                    pressure = mainAkaA.getString("pressure");
                    humidity = mainAkaA.getString("humidity");

                String system = jsonObject.getString("sys");
                JSONObject systemAka = new JSONObject(system);

                    country = systemAka.getString("country");
                    sunRise = systemAka.getString("sunrise");
                    sunset  = systemAka.getString("sunset");
                Integer datetimestamp = Integer.parseInt(sunRise);
                Date date = new Date(datetimestamp);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);
                sunRise = dateFormatted;
                datetimestamp = Integer.parseInt(sunset);
                date = new Date(datetimestamp);
                formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                dateFormatted = formatter.format(date);
                sunset = dateFormatted;
                JSONArray aboutWeather = new JSONArray(weather);
                //JSONArray aboutMain = new JSONArray(mainAkaA);
                //JSONArray aboutSystem = new JSONArray(system);
                //Log.i("PPPPPPP: ", p);
                for(int i=0;i<aboutWeather.length();i++){
                    JSONObject jsonPart = aboutWeather.getJSONObject(i);
                    mainss = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                }

                Double tem ;
                if(Double.parseDouble(temp)>=150){
                    tem= Double.parseDouble(temp);
                    tem -= 273.15;
                    //int valueToShow = Integer.parseInt(temp);
                    temp = String.valueOf(tem);
                    temp = temp.substring(0,5) + " C";
                    //tempBig.setText(valueToShow);
                }

                if(Double.parseDouble(temp_feelsLike)>=150){
                    tem = Double.parseDouble(temp_feelsLike);
                    tem-=273.15;
                    temp_feelsLike = String.valueOf(tem);
                    temp_feelsLike = temp_feelsLike.substring(0,5) + " C";
                }

                if(Double.parseDouble(temp_max)>=150){
                    tem = Double.parseDouble(temp_max);
                    tem -= 273.15;
                    temp_max = String.valueOf(tem);
                    temp_max = temp_max.substring(0,5) + " C";
                }

                if(Double.parseDouble(temp_min)>=100){
                    tem = Double.parseDouble(temp_min);
                    tem -= 273.15;
                    temp_min = String.valueOf(tem);
                    temp_min = temp_min.substring(0,5) + " C";
                }

                list.add("Mains: " +mainss);
                list.add("Description: "+description);
                list.add("Temperature: "+temp);
                list.add("Feels Like: "+temp_feelsLike);
                list.add("Temperature Max: "+temp_max);
                list.add("Temperature Min: "+temp_min);
                list.add("Humidity: "+humidity);
                list.add("Pressure: "+pressure);
                list.add("Country: "+country);
                list.add("Sunrise: "+sunRise);
                list.add("Sunset: "+sunset);
                Log.i("sunset", sunset);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                listView.setAdapter(arrayAdapter);


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}