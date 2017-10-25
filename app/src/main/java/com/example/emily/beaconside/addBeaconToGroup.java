package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class addBeaconToGroup extends AppCompatActivity {

    CallbackManager callbackManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    String JSON_STRING;
    public static String uEmail;
    public static String get_uEmail;
    private ListView List;
    private BeaconCheckboxAdapter beaconCheckboxAdapter;
    ArrayList<String> listItemID = new ArrayList<String>();
    String macAddress,gId;
    public TextView groupName;
    String gName;


    ArrayList<String> bName_list = new ArrayList<String>();//我的beacon名稱list
    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> distance= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon_to_group);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

       List = (ListView) findViewById(R.id.listView_beacon);
        groupName = (TextView) findViewById(R.id.groupName);


        Bundle extras = getIntent().getExtras();
        gId = extras.getString("gId");
        gName = extras.getString("gName");
        groupName.setText(gName);


        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            uEmail=object.get("email").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();

        get_uEmail = "\""+uEmail+"\"";
//        Toast.makeText( addBeaconToGroup.this,uEmail,Toast.LENGTH_SHORT).show();

        getBeacon();
    }//end create

    @Override
    public void onResume(){
        super.onResume();
        uEmail = Login.uEmail;
        get_uEmail = "\""+uEmail+"\"";
//        refresh();
        getBeacon();

    }

    //取得用戶擁有的beacon
    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;

                showMyBeacon();
                //將取得的json轉換為array list, 顯示在畫面上

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_ALL_BEACON,get_uEmail);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }
    private void showMyBeacon(){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            macAddress_list = new ArrayList<>();
            bPic_list = new ArrayList<>();
            ArrayList< HashMap<String, Object>> list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name

                //bName,macAddress各自單獨存成一個array
//                bName_list.add(bName);
//                macAddress_list.add(macAddress);
//                bPic_list.add(bPic);
                HashMap<String ,Object> hashMap = new HashMap<>();
//                Toast.makeText(addBeaconToGroup.this,bPic+bName+macAddress,Toast.LENGTH_LONG).show();

                hashMap.put("macAddress" ,macAddress);
                hashMap.put("bName" , bName);
                hashMap.put("bPic",bPic);
                list.add(hashMap);

            }

            beaconCheckboxAdapter = new BeaconCheckboxAdapter(addBeaconToGroup.this, list);
            List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            List.setAdapter(beaconCheckboxAdapter);



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void add() {

        class Add extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(addBeaconToGroup.this,"Adding...","Wait...",false,false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText( addBeaconToGroup.this,s,Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("gId",gId);
                params.put("macAddress",macAddress);

                //params.put(php檔內的接收變數  $_POST["___"] , 要傳給php檔的java變數)

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_BEACON_TO_GROUP, params);
                //String res = rh.sendPostRequest("php檔的網址", params);
                //URL_ADD 是在 Config.java設定好的字串 也就是 http://140.117.71.114/employee/addEmp.php
                //php檔可在ftp上傳下載
                return res;
            }
        }
        //這兩行不用理
        Add ae = new Add();
        ae.execute();
    }

    /* check button*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_item_save, menu);
        //Toast.makeText(this,"叫出menu", Toast.LENGTH_SHORT).show();
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_item_check) {
            //執行新增group
            listItemID.clear();
            macAddress=null;
            for(int i=0;i<beaconCheckboxAdapter.mChecked.size();i++){
                if(beaconCheckboxAdapter.mChecked.get(i)){
                    listItemID.add((String) beaconCheckboxAdapter.getItem(i));
//                                friendId = friendId+","+adapter.getItem(i).toString();
                }
            }
            if(listItemID.size()==0){
                Toast.makeText( addBeaconToGroup.this,"select no data",Toast.LENGTH_SHORT).show();

            }else{
                StringBuilder sb = new StringBuilder();

                for(int i=0;i<listItemID.size();i++){
//                                sb.append(listItemID.get(i)+",");
                    if (i==0)
                        macAddress = listItemID.get(i).toString()+",";
                    else {
                        macAddress = macAddress + listItemID.get(i).toString()+",";
                    }
                }
                macAddress = macAddress.substring(0,macAddress.length() - 1);
                Toast.makeText( addBeaconToGroup.this,macAddress,Toast.LENGTH_SHORT).show();

            }
            add();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */
}