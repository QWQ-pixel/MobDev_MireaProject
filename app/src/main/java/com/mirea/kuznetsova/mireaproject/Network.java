package com.mirea.kuznetsova.mireaproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Network extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int REQUEST_CODE_PERMISSION_ACCESS_NETWORK_STATE = 1;
    private TextView ip;
    private TextView county;
    private TextView region;
    private TextView zip;
    private Button get_ip;
    private final String url = "http://ip-api.com/json/";

    private String mParam1;
    private String mParam2;

    public Network() {}

    public static Network newInstance(String param1, String param2) {
        Network fragment = new Network();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        int permissionStatus = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET);

        int permissionStatusInt = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED && permissionStatusInt != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET},
                    REQUEST_CODE_PERMISSION_ACCESS_NETWORK_STATE);
        }
        ip = view.findViewById(R.id.ip);

        county = view.findViewById(R.id.country);

        region = view.findViewById(R.id.region);

        zip = view.findViewById(R.id.zip);

        get_ip = view.findViewById(R.id.get_ip);

        get_ip.setOnClickListener(v -> Network.this.onClick());


        return view;
    }
    public void onClick() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkinfo = null;

        if (connectivityManager != null) {

            networkinfo = connectivityManager.getActiveNetworkInfo();
        }


        if (networkinfo != null && networkinfo.isConnected()) {

            new DownloadPageTask().execute(url);

        } else {

            Toast.makeText(getContext(), "Нет интернета", Toast.LENGTH_SHORT).show();

        }
    }
    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            ip.setText("Загружаем...");

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                return downloadIpInfo(urls[0]);

            } catch (IOException e) {

                e.printStackTrace();

                return "error";

            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(MainActivity.class.getSimpleName(), result);

            try {

                JSONObject responseJson = new JSONObject(result);

                String ipStr = responseJson.getString("query");

                String countyStr = responseJson.getString("country");

                String regionStr = responseJson.getString("regionName");

                String zipStr = responseJson.getString("zip");

                ip.setText(ipStr);

                county.setText(countyStr);

                region.setText(regionStr);

                zip.setText(zipStr);

                Log.d(MainActivity.class.getSimpleName(), ipStr);

            } catch (JSONException e) {

                e.printStackTrace();

            }

            super.onPostExecute(result);
        }
    }

    private String downloadIpInfo(String address) throws IOException {

        InputStream inputStream = null;

        String data = "";

        try {

            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(100000);

            connection.setConnectTimeout(100000);

            connection.setRequestMethod("GET");

            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches(false);

            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                inputStream = connection.getInputStream();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int read = 0;

                while ((read = inputStream.read()) != -1) {

                    bos.write(read);

                }

                byte[] result = bos.toByteArray();

                bos.close();

                data = new String(result);

            } else {

                data = connection.getResponseMessage() + " . Error Code : " + responseCode;

            }

            connection.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (inputStream != null) {

                inputStream.close();

            }

        }

        return data;
    }
}