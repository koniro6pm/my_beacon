package com.example.emily.beaconside;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationHistory extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String JSON_STRING; //用來接收php檔傳回的json
    private String macAddress, bName, bPic; // 指定的藍牙裝置
//    private String uEmail, get_uEmail;
    private String latitude, longitude, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        // 設置bar內容
        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable
        // 取得傳來的資訊
        Bundle bundle = this.getIntent().getExtras();
        bName = bundle.getString("itemName"); // 接受要搜尋的藍牙裝置名稱
        macAddress = bundle.getString("itemAddress"); // 接受要搜尋的藍牙裝置地址
        bPic = bundle.getString("bPic"); // 藍牙裝置icon
        // 獲取藍牙裝置資料庫中的資訊
        getBeacon();
        // 建立地圖
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeacon();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeacon() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                // 取得資料庫中的經緯度
                latitude = jo.getString("latitude");
                longitude = jo.getString("longitude");
                time = jo.getString("time");
            }
            // 顯示裝置位置在地圖上
            // 設置圖片icon
            String uri = "@drawable/" + bPic; //圖片路徑和名稱
            int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
            LatLng location = new LatLng(Double.parseDouble(latitude) , Double.parseDouble(longitude));
            Marker item;
            item = mMap.addMarker(new MarkerOptions().position(location).title(bName).snippet(time).icon(BitmapDescriptorFactory.fromResource(R.drawable.logox50)));
            item.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
            // 顯示我目前的位置，先判斷是否有權限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Show rationale and request permission.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
