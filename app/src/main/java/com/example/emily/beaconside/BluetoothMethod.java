package com.example.emily.beaconside;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.icu.math.BigDecimal;
import android.os.AsyncTask;
import android.widget.Toast;

import com.powenko.ifroglab_bt_lib.*;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.emily.beaconside.MainActivity.bName_list;
import static java.lang.Double.parseDouble;
import static java.lang.Double.toString;
import static java.lang.Integer.parseInt;
import com.example.emily.beaconside.GPSTracker;

public class BluetoothMethod implements ifrog.ifrogCallBack{

    public boolean myStatusBT=true, firstOpenBT=true; boolean isSearching;
    /* 運用library */
    private ifrog mifrog;
    public ArrayList<String> Names = new ArrayList<String>();   // 周圍所有藍牙裝置的名稱
    public ArrayList<String> Address = new ArrayList<String>(); // 周圍所有藍牙裝置的地址
    public ArrayList<Double> Distance = new ArrayList<Double>();    // 周圍所有藍牙裝置的距離
    public ArrayList<Integer> Alert = new ArrayList<Integer>();
    // 警告的視窗跳過了沒，避免一直重複跳出視窗
    boolean isAlert = false;
    /* 調整distance */
    private double count = 0;
    private double distanceTotal = 0;
    double tempdis = 0;

    /* 藍芽 */
    public final int REQUEST_ENABLE_BT = 18;
//    private boolean firstOpen = true;
    /* 呼叫藍牙方法的Activity */
    Context mContext;
    /* public 藍牙資訊 */
    public ArrayList<String> mac = new ArrayList<String>(); // 使用者擁有裝置的地址，從資料庫獲取
    public ArrayList<String> myDeviceDistance = new ArrayList<>(); // 使用者擁有的所有裝置的目前距離
    public double currentRssi = 0;  // 目前指定要搜尋的特定藍牙裝置之訊號強度
    public double currentDistance=100000;    // 目前指定要搜尋的特定藍牙裝置之距離
    public String bluetoothFunction = ""; // 目前要使用的藍牙功能
    public String currentItem = "D0:39:72:DE:DC:3A";    // 目前指定要搜尋的特定藍牙裝置
    public double currentLat, currentLong;
    GPSTracker gps;

    public void getStartSearch(Context context, Long time){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(firstOpenBT || !myStatusBT){
            if (!mBluetoothAdapter.isEnabled()) {//要求開啟藍芽的視窗
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }else{
                mifrog.scanLeDevice(myStatusBT,time);//true
            }
            firstOpenBT = false;
        }else{
            mifrog.scanLeDevice(myStatusBT,time);//true
        }

    }

    public void stopSearch(){
        myStatusBT = false;
        mifrog.scanLeDevice(myStatusBT,3600000);
    }

    public void BTinit(Context context){//藍芽初始化動作
        mifrog=new ifrog();
        mifrog.setTheListener(this);//設定監聽->CallBack(當有什麼反應會有callback的動作)->新增SearchFindDevicestatus, onDestroy
        mContext = context;
        gps = new GPSTracker();
        gps.getCurrentLocation(mContext);

        //取得藍牙service，並把這個service交給此有藍芽的設備(BLE)。有些人有藍芽的設備不見得有藍芽的軟體。// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        if (mifrog.InitCheckBT(bluetoothManager) == null) {
            Toast.makeText(context,"this Device doesn't support Bluetooth BLE", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
            return;
        }
        getStartSearch(mContext,new Long(360000));
    }


    public void bluetoothStop() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        mifrog.BTSearchStop();
    }

    public double calculateDistance(int rssi){
        /*   d = 10^((abs(RSSI) - A) / (10 * n))  */
        double result = 0;

        if(count>15){
            tempdis = distanceTotal/count;
            count = 0;
            distanceTotal = 0;
        }
        else{
            float txPower = -59;//hard coded power value. Usually ranges between -59 to -65
            if(rssi == 0){
                result = -1.0;
            }
            double ratio = rssi*1.0/txPower;
            if (ratio < 1.0) {
                result =  Math.pow(ratio,10);
            }
            else{
                double distance = (0.89976)*Math.pow(ratio,7.7095) + 0.111;
                result =  distance;
            }
            count ++;
            distanceTotal += result;
        }
        result = result/10; // 換算成公尺
        // 四捨五入到小數點第一位
        DecimalFormat df = new DecimalFormat("##.0");
        result = Double.parseDouble(df.format(result));

        return result;
    }

    @Override
    public void BTSearchFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        Toast.makeText(mContext,"我還在搜尋", Toast.LENGTH_SHORT).show();
        switch (bluetoothFunction) {
            case "searchItem":  // 搜尋特定MAC地址的藍牙裝置的rssi及距離資訊，會儲存到public變數中
                searchItem(device,currentItem,rssi);

                break;
            case "searchDevice":    // 掃描周圍所有藍牙裝置，將裝置名稱、地址、距離儲存到public的ArrayList中
                searchDevice(device,rssi);
                myItemDistance(mac);
                myItemAlert();
                break;
            case "myItemDistance":  // 掃描周圍所有藍牙裝置，檢查使用者的beacon是否有在周圍，如果有的話則顯示距離資訊
                searchDevice(device,rssi);
                myItemDistance(mac);
                myItemAlert();
                break;
            default:
                searchDevice(device,rssi);
                myItemDistance(mac);
                myItemAlert();
                break;
        }

    }

    @Override
    public void BTSearchFindDevicestatus(boolean arg0) {//arg0:true/false，代表有沒有在找
        if(arg0==false){
            //Toast.makeText(mContext,"Stop Search", Toast.LENGTH_SHORT).show();
            isSearching = false;
        }else{
            //Toast.makeText(mContext,"Start Search",  Toast.LENGTH_SHORT).show();
            isSearching = true;
        }
    }

    public void myItemDistance(ArrayList<String> mac) {
        int i,j;

        String d="Out of Range";
        for (i=0; i<mac.size(); i++) {
            for(j=0;j<Address.size();j++){
                // 有搜尋到裝置訊號的話
                if(mac.get(i).equals(Address.get(j))){
                    // 該beacon的距離
                    d = String.valueOf(Distance.get(j));
                    if(gps.isLocationChanged){
                        final String macAddress = mac.get(i);
                        final Double latitude = gps.currentLatitude;
                        final Double longitude = gps.currentLongitude;
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                        currentLat = gps.currentLatitude;
                        currentLong = gps.currentLongitude;
                        // 更改裝置的歷史經緯度
                        class UpdateBeacon extends AsyncTask<Void,Void,String> {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);

//                                Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("macAddress", macAddress);
                                hashMap.put("latitude",latitude.toString());
                                hashMap.put("longitude",longitude.toString());
                                hashMap.put("time",sdf.format(new Date()));
                                RequestHandler rh = new RequestHandler();
                                String s = rh.sendPostRequest(Config.URL_UPDATE_BEACON_LOCATION,hashMap);

                                return s;
                            }
                        }
                        UpdateBeacon ue = new UpdateBeacon();
                        ue.execute();

                        //Toast.makeText(mContext,macAddress+"經緯度已更改至"+gps.currentLatitude+" "+gps.currentLongitude+"時間"+sdf.format(new Date()), Toast.LENGTH_SHORT).show();
                        gps.isLocationChanged = false;

                    }
                    break;
                }
                else
                    d = "Out of Range";
            }
            if(myDeviceDistance.size() < mac.size()){
                myDeviceDistance.add(d);
            }
            else{
                myDeviceDistance.set(i,d);
            }
        }
    }

    public void myItemAlert() {
        for(int i=0; i<myDeviceDistance.size(); i++) {
//            Toast.makeText(mContext,"my Distance: "+myDeviceDistance.get(i), Toast.LENGTH_SHORT).show();
//            Toast.makeText(mContext,"my alert Distance: "+Alert.get(i), Toast.LENGTH_SHORT).show();
            double myDistance;
            if(tryParse(myDeviceDistance.get(i))!= -1) {  // 避免字串轉double發生錯誤，如果myDeviceDistance為字串則會回傳-1
                myDistance = parseDouble(myDeviceDistance.get(i));
                //myDistance = myDistance / 100;// 換算成公尺
            }
            else
                myDistance = -1;
            //myDeviceDistance 和 Alert 長度不一樣
            //所以Alert.get(i)會出錯
            if(myDistance > Alert.get(i) && !isAlert) {
//                Toast.makeText(mContext,mac.get(i)+" is out of "+Alert.get(i)+"m\nnow is "+myDistance+"m", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(mContext,AlertDistance.class);
                intent.putExtra("bName",bName_list.get(i));
                intent.putExtra("bAlertDistance",Alert.get(i).toString());
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
                bluetoothStop();
                isAlert = true;
            }
        }
        //Toast.makeText(mContext,"distance長度是"+myDeviceDistance.size(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(mContext,"alert長度是"+Alert.size(), Toast.LENGTH_SHORT).show();

    }

    public void searchItem(BluetoothDevice device,String item,int rssi) {
//        Toast.makeText(mContext,item +" || "+device.getAddress(), Toast.LENGTH_SHORT).show();
        if(item.equals(device.getAddress())) {
            currentRssi = rssi;
            currentDistance = calculateDistance(rssi);
//            Toast.makeText(mContext,item +"is "+currentDistance+"cm away", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchDevice(BluetoothDevice device,int rssi){
        String t_address= device.getAddress();//有找到裝置的話先抓Address
        int index=0;
        boolean t_NewDevice=true;
        for(int i=0;i<Address.size();i++){
            String t_Address2=Address.get(i);
            if(t_Address2.compareTo(t_address)==0){//如果address和列表中的address一模一樣
                t_NewDevice=false;//登記說他不是新的device
                index=i;//把index記起來
                break;
            }
        }
        if(device.getName() != null){

            if(t_NewDevice==true){//如果是新的device
                Address.add(t_address);
                //null can appear
                Names.add(device.getName());
                Distance.add(calculateDistance(rssi));
                //Toast.makeText(mContext,"Find new device"+ device.getName(), Toast.LENGTH_SHORT).show();

            }else{//如果不是新的device
                Names.set(index,device.getName());
                Distance.set(index,calculateDistance(rssi));
            }
        }


    }

    public void getStartSearchItem(String item) {
        bluetoothFunction="searchItem";
        currentItem = item;
        if(!isSearching) // 如果現在還沒開始搜尋
            getStartSearch(mContext, new Long(3600000));
    }

    public void getStartMyItemDistance(ArrayList<String> address) {
        bluetoothFunction="myItemDistance";
        mac = address;
        if(!isSearching) // 如果現在還沒開始搜尋
            getStartSearch(mContext, new Long(3600000));
    }

    public void getStartSearchDevice() {

        bluetoothFunction="searchDevice";
        if(!isSearching) // 如果現在還沒開始搜尋
            getStartSearch(mContext, new Long(3600000));
    }

    public double getDistance() {
        return currentDistance;
    }

    public double getRssi() {
        return currentRssi;
    }

    public static double tryParse(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}