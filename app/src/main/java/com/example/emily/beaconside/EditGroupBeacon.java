package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class EditGroupBeacon extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ListView listView_beacon;
    String uEmail;
    String get_uEmail;
    String user_beacon_json;
    String JSON_STRING;

    String gId;
    String gName;
    String gPic;

    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> bName_list = new ArrayList<String>();//我的event名稱list

    private BeaconCheckboxAdapter beaconCheckboxAdapter;
    StringBuffer beaconSelect_string = new StringBuffer();//記錄哪幾個beacon被選

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_beacon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

        //從本機獲取資料
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        uEmail = sharedPreferences.getString("EMAIL", "YOO@gmail.com");
        user_beacon_json = sharedPreferences.getString("user_beacon_json", "YOO@gmail.com");
        get_uEmail = "\""+uEmail+"\"";

        //Toast.makeText(this,user_beacon_json, Toast.LENGTH_SHORT).show();




        Bundle extras = getIntent().getExtras();
        gId = extras.getString("gId");
        gName = extras.getString("gName");
        gPic = extras.getString("gPic");
        Toast.makeText(EditGroupBeacon.this ,gId, Toast.LENGTH_SHORT).show();


        listView_beacon = (ListView)findViewById(R.id.listView_beacon);
        getBeaconNotInGroup();

    }

    //取得那些還沒被加進group的beacon
    private void getBeaconNotInGroup(){
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
                //Toast.makeText(EditGroupBeacon.this ,s, Toast.LENGTH_SHORT).show();

                showBeaconNotInGroup();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_EXCEPT_GROUP,gId+"&uEmail="+get_uEmail);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();

    }


    private void showBeaconNotInGroup(){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            ArrayList< HashMap<String, Object>> list = new ArrayList<>();

            bName_list = new ArrayList<>();
            macAddress_list = new ArrayList<>();
            bPic_list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                HashMap<String ,Object> hashMap = new HashMap<>();
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name

                hashMap.put("macAddress" , macAddress);
                hashMap.put("bName" , bName);
                hashMap.put("bPic",bPic);
                list.add(hashMap);

                //bName,macAddress各自單獨存成一個array
                bName_list.add(bName);
                macAddress_list.add(macAddress);
                bPic_list.add(bPic);
            }

            beaconCheckboxAdapter = new BeaconCheckboxAdapter(EditGroupBeacon.this, list);
            listView_beacon.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView_beacon.setAdapter(beaconCheckboxAdapter);



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void addBeacon(){


        class AddEvent extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(addNewBeacon.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //Toast.makeText(EditGroupBeacon.this,s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> param = new HashMap<>();

                param.put("gId",gId);
                param.put("beaconSelect_string",beaconSelect_string.toString());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_EDIT_GROUP_BEACON, param);
                return res;
            }
        }

        AddEvent ae = new AddEvent();
        ae.execute();
    }

    /* cancel : go back button */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /* check button*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_item_save, menu);
        //Toast.makeText(this,"叫出menu", Toast.LENGTH_SHORT).show();

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_item_check) {
            //執行新增
            //執行新增beacon
            for(int i=0;i<beaconCheckboxAdapter.mChecked.size();i++){
                if(beaconCheckboxAdapter.mChecked.get(i)){
                    //beaconSelect_list.add((String) beaconCheckboxAdapter.getItem(i));
                    beaconSelect_string.append((String) beaconCheckboxAdapter.getItem(i)).append(",");

//                                friendId = friendId+","+adapter.getItem(i).toString();
                }
            }
            addBeacon();
            Intent intent = new Intent();
            intent.setClass(EditGroupBeacon.this,GroupMain.class);

            intent.putExtra("click_gId",Integer.parseInt(gId));
            intent.putExtra("click_gName",gName);
            intent.putExtra("click_gPic",gPic);

            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
