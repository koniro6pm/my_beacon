package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.powenko.ifroglab_bt_lib.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class SearchDevice extends AppCompatActivity implements ifrog.ifrogCallBack{
    //	private EditText editText1;
    private ListView listView1;
    private boolean myStatusBT=true, firstOpenBT=true;
    /* 運用library */
    private ifrog mifrog;
    ArrayList<String> Names = new ArrayList<String>();
    ArrayList<String> Address = new ArrayList<String>();
    String[] groupName_array;
    int[] groupId_array;
    String[] eventName_array;
    int[] eventId_array;
    String uEmail;
    // loading spinner
    private ProgressBar spinner;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //用來接收php檔傳回的json
    private String JSON_STRING;
    private ArrayList<String> beacon_list; // 放資料庫所有已被註冊的beacon資料
    /* 若沒有開啟藍芽，預設畫面 */
    String[] testValues= new String[]{	"Beacon1","Beacon2","Beacon3","Beacon4"};
    String[] testValues2= new String[]{	"12","34","56","78"};

    private device_rowdata adapter;
    /* 藍芽 */
    final int REQUEST_ENABLE_BT = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_device);
        //接收從MainActivity傳遞來的cName_array
        Bundle extras = getIntent().getExtras();
        uEmail = extras.getString("uEmail");
        eventName_array = extras.getStringArray("eventName_array");
        eventId_array = extras.getIntArray("eventId_array");
        groupName_array = extras.getStringArray("groupName_array");
        groupId_array = extras.getIntArray("groupId_array");

        // 接收資料庫所有已註冊的beacon清單，如果已被註冊就不顯示該beacon
        getBeaconDatabase();
        /* DeviceList */
        listView1=(ListView) findViewById(R.id.beaconList);   //取得listView1
        /* bluetooth */
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        BTinit();

        //Intent i = getIntent();
        //cName_list = i.getStringArrayListExtra("cName_list");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Names.clear();
                Address.clear();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }//end onCreate


//    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//        mystatus = isChecked;
//        if (isChecked) {// The toggle is enabled
//            checkBTopen();
//        }else{
//            mifrog.scanLeDevice(isChecked,3600000);
//        }
//
//    }



    public void BTinit(){//藍芽初始化動作
        mifrog=new ifrog();
        mifrog.setTheListener(this);//設定監聽->CallBack(當有什麼反應會有callback的動作)->新增SearchFindDevicestatus, onDestroy

        //取得藍牙service，並把這個service交給此有藍芽的設備(BLE)。有些人有藍芽的設備不見得有藍芽的軟體。// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mifrog.InitCheckBT(bluetoothManager) == null) {
            Toast.makeText(this,"this Device doesn't support Bluetooth BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        getStartSearch();
    }
    /*經過了dialog卻還是沒開啟 關掉check*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED){
            myStatusBT = false;
        }
        else{
            myStatusBT = true;
        }
        getStartSearch();
    }

    public void getStartSearch(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(firstOpenBT || !myStatusBT){
            if (!mBluetoothAdapter.isEnabled()) {//要求開啟藍芽的視窗
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }else{
                mifrog.scanLeDevice(myStatusBT,3600000);//true
            }
            firstOpenBT = false;
        }else{
            mifrog.scanLeDevice(myStatusBT,3600000);//true
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void SetupList() {
        adapter = new device_rowdata(this, testValues, testValues2);//顯示的方式
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() { //選項按下反應
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String bName = testValues[position];//取得選擇beacon的名字
                String macAddress = testValues2[position];//取得選擇beacon的macAddress


                /**換頁到addNewBeacon**/
                Intent intent = new Intent();
                intent.setClass(SearchDevice.this,addNewBeacon.class);
                //傳遞變數
                intent.putExtra("uEmail",uEmail);
                intent.putExtra("bName",bName);
                intent.putExtra("macAddress",macAddress);
                intent.putExtra("eventName_array",eventName_array);
                intent.putExtra("eventId_array",eventId_array);
                intent.putExtra("groupName_array",groupName_array);
                intent.putExtra("groupId_array",groupId_array);
                Toast.makeText(SearchDevice.this, bName + " selected\n" + uEmail+"\n"+macAddress+"\n"+eventName_array+"\n"+eventId_array+"\n"+groupName_array+"\n"+groupId_array, Toast.LENGTH_LONG).show(); //顯示訊號
                startActivity(intent);
                finish();
                /******/

            }
        });
    }

    @Override
    public void BTSearchFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
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
        boolean isRegistered = false;
        for (String mac : beacon_list) {
            if(t_address.equals(mac)){
//                Toast.makeText(getBaseContext(),device.getName()+" already registered", Toast.LENGTH_SHORT).show();
                isRegistered = true;
            }
        }

        if(device.getName() != null && !isRegistered){

            if(t_NewDevice==true){//如果是新的device
                listView1.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                Address.add(t_address);
                Names.add(device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//抓名字然後放進列表

                testValues = Names.toArray(new String[Names.size()]);
                testValues2 =Address.toArray(new String[Address.size()]);
            }else{//如果不是新的device
                Names.set(index,device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//更改device名字，RSSI:藍芽4.0裡面可以知道訊號強度
                testValues = Names.toArray(new String[Names.size()]);//放進array
            }
        }

        SetupList();//更新畫面
    }

    @Override
    public void BTSearchFindDevicestatus(boolean arg0) {//arg0:true/false，代表有沒有在找
        if(arg0==false){
            Toast.makeText(getBaseContext(),"Stop Search", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"Start Search",  Toast.LENGTH_SHORT).show();
        }
    }

    //取得用戶擁有的beacon
    private void getBeaconDatabase(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                //將取得的json轉換為array list, 顯示在畫面上
                readMyBeacon();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_BEACON_DATABASE);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    //將取得的json轉換為array list, 顯示在畫面上
    private void readMyBeacon(){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array
            beacon_list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress

                //bName,macAddress各自單獨存成一個array
                beacon_list.add(macAddress);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        super.onDestroy();
        mifrog.BTSearchStop();
    }

    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }
}