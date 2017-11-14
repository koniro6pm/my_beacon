package com.example.emily.beaconside;


import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.RoundingMode;


/**
 * Created by jennifer9759 on 2017/8/8.
 */
public class Compass extends AppCompatActivity implements SurfaceHolder.Callback,SensorEventListener {
    /* compass */
    private float currentDegree = 0f;// record the angle turned
    private SensorManager mSensorManager;// device sensor manager

    /* calculate direction */
    private float maxRSSI = -1000000;
    private float turntoTarget = 0;

    /* view component*/
    private ImageView image;
    private TextView itemName;
    private TextView itemDistance;
    private TextView itemDegree;
    private ImageView itemPic;
    /* bluetooth */
    BluetoothMethod bluetooth = new BluetoothMethod();

    String name,address,bPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        /* intent取得傳遞過來的item名稱 */
        Bundle bundle = this.getIntent().getExtras();
        name = bundle.getString("itemName"); // 接受要搜尋的藍牙裝置名稱
        address = bundle.getString("itemAddress"); // 接受要搜尋的藍牙裝置地址
        bPic = bundle.getString("itemPic");
        /* compass */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// initialize your android device sensor capabilities

        /* bluetooth */
        bluetooth.BTinit(this);
        bluetooth.getStartSearchItem(address); // 指定要搜尋的藍牙裝置地址
//        Toast.makeText(getBaseContext(), "開始搜尋"+address, Toast.LENGTH_SHORT).show();
        /* view component */
        image = (ImageView) findViewById(R.id.imageViewCompass);
        // 設置裝置名稱
        itemName = (TextView) findViewById(R.id.itemName);
        itemName.setText("\""+name+"\"");
        itemDistance = (TextView) findViewById(R.id.itemDistance);
        itemDegree = (TextView) findViewById(R.id.itemDegree);
        String uri = "@drawable/" + bPic; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        itemPic = (ImageView) findViewById(R.id.bPic);
        itemPic.setImageResource(imageResource);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        super.onDestroy();
        bluetooth.stopSearch();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        double d = bluetooth.getDistance();
        int resID;
        if(bluetooth.getDistance()<100000){ // 如果有收到藍牙裝置訊號的話
            itemDistance.setText(bluetooth.getDistance() + " 公尺");
//            itemDegree.setText(degree + " degree");

            // 判斷遠近來更改顯示圖片
            if(d < 50) {
                resID =  this.getResources().getIdentifier("close", "drawable","com.example.emily.beaconside");
            }
            else if(d > 50 && d < 200) {
                resID = this.getResources().getIdentifier("mid", "drawable","com.example.emily.beaconside");
            }
            else {
                resID = this.getResources().getIdentifier("far", "drawable","com.example.emily.beaconside");
            }
            image.setImageResource(resID);

        /* direction */
            if( maxRSSI < Math.abs(bluetooth.getRssi())){
                maxRSSI = (float)Math.abs(bluetooth.getRssi());
                turntoTarget = -degree;//N:0, E:+
            }

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree+turntoTarget,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            image.startAnimation(ra);
            currentDegree = -degree;
        }
        else{
            itemDistance.setText("Searching signal...");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void onBackPressed(View view) {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }

    public void openMap(View view) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), LocationHistory.class);
        Bundle bundle = new Bundle();
        bundle.putString("itemName", name);
        bundle.putString("itemAddress", address);
        bundle.putString("bPic", bPic);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
