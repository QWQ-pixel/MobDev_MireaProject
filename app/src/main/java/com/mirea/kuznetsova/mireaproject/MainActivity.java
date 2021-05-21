package com.mirea.kuznetsova.mireaproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView textView;
    private String  operation;
    private SensorManager pSensorManager;
    private Sensor mPressure;
    private Sensor mTemperature;
    private Sensor mLight;

    private TextView pressure;

    private TextView temperature;

    private TextView textLIGHT_available, textLIGHT_reading;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_brouser, R.id.calculateFragment, R.id.music)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        pSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mPressure = pSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        mTemperature = pSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        mLight = pSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (mTemperature == null) {
            temperature.setText("Sensor not avalible");
        }
        if (mLight == null) {
            textLIGHT_available.setText("Sensor not avalible");
        }

        pressure = (TextView) findViewById(R.id.pressure);

        temperature = (TextView) findViewById(R.id.temperatureValue);

        textLIGHT_available = (TextView) findViewById(R.id.LIGHT_available);

        textLIGHT_reading = (TextView) findViewById(R.id.LIGHT_reading);
    }

    public void OpenBrows(View view){
        WebView webView = (WebView) findViewById(R.id.webView);

        webView.setInitialScale(1);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("https://www.google.ru/");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onSensorChanged(SensorEvent event) {

        float millibars_of_pressure = event.values[0];

        pressure.setText(String.valueOf(millibars_of_pressure));

        float ambient_temperature = event.values[0];

        temperature.setText(String.valueOf(ambient_temperature));

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            textLIGHT_reading.setText("LIGHT: " + event.values[0]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onClick(View view){
        textView = findViewById(R.id.tv);
        Button button = (Button) view;
        String butt = (String) button.getText();
        if (butt.equals("C"))
            textView.setText(null);
        else {
            if (butt.equals("=")) {
                Runnable r = () -> {
                    textView.setText(Ope(textView.getText().toString()));
                    operation = null;
                };
                Thread thread = new Thread(r);
                thread.start();

            } else {
                if (butt.equals("/") || butt.equals("*") || butt.equals("-") || butt.equals("+"))
                    operation = butt.trim();
                textView.append(butt);
            }
        }
    }

    private String Ope(String str){
        str = str.replace(operation, " ");
        String [] numbers = str.split(" ");
        return Calc(numbers[0], numbers[1], operation);
    }

    @SuppressLint("DefaultLocale")
    private String Calc(String a, String b, String oper) {
        if (a.contains(".") || b.contains(".")){
            switch (oper) {
                case "+":
                    return String.format("%.1f",(Float.parseFloat(a) + Float.parseFloat(b)));
                case "-":
                    return String.format("%.3f",(Float.parseFloat(a) - Float.parseFloat(b)));
                case "*":
                    return String.format("%.3f",(Float.parseFloat(a) * Float.parseFloat(b)));
                case "/":
                    if (!b.equals("0")) {
                        return String.format("%.3f",(Float.parseFloat(a) / Float.parseFloat(b)));
                    } else {
                        return "Division by zero";
                    }
                }
        }else {
            switch (oper) {
                case "+":
                    return Integer.toString((Integer.parseInt(a) + Integer.parseInt(b)));
                case "-":
                    return Integer.toString((Integer.parseInt(a) - Integer.parseInt(b)));
                case "*":
                    return Integer.toString((Integer.parseInt(a) * Integer.parseInt(b)));
                case "/":
                    if (!b.equals("0")) {
                        return Integer.toString((Integer.parseInt(a) / Integer.parseInt(b)));
                    } else {
                        return "Division by zero";
                    }
                }
            }
        return "Something went wrong! :(";
    }

    public void onClickPlayMusic(View view){
        startService(new Intent(MainActivity.this, PlayerService.class));
    }

    public void onClickStopMusic(View view){
        stopService(new Intent(MainActivity.this, PlayerService.class));
    }
}