package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jennifer9759 on 2017/10/22.
 * 參考http://blog.jobbole.com/74208/
 */

public class GroupSetting extends AppCompatActivity {
    private List<Person> persons;
    public String gId;
    public String gName;
    public String gPic = "12345";
    // 本機資料
    SharedPreferences sharedPreferences;
    public static String uEmail;
    String JSON_STRING;

    ArrayList<String> uName_list = new ArrayList<String>();;//群組會員名字
    ArrayList<String> uId_list = new ArrayList<String>();;//群組會員id
    private LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerview_member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_setting);
        // 標題功能列
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //接收從MainActivity傳遞來的
        Bundle extras = getIntent().getExtras();
        gId = extras.getString("gId");
        gName = extras.getString("gName");
//        gPic = extras.getString("gPic");
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        uEmail = sharedPreferences.getString("EMAIL", "0");

        //  宣告 recyclerView

        recyclerview_member = (RecyclerView) findViewById(R.id.recyclerview_member);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_member.setLayoutManager(linearLayoutManager);

        getGroupMember();

    }


    private void getGroupMember(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(GroupSetting.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                JSON_STRING = s;
                Toast.makeText(GroupSetting.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showGroupMember();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_GROUP_MEMBER,gId);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showGroupMember() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                String uId = jo.getString("uId");
                String uName = jo.getString("uName");



                uId_list.add(uId);
                uName_list.add(uName);

            }

            final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, uId_list,uName_list);
            //設置分割線使用的divider
            recyclerview_member.setAdapter(adapter);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }
    // 群組功能列表
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_name:
                // 跳出視窗來輸入新名字
                getRequest(Config.URL_UPDATE_GROUP_NAME,gId+"&gName="+gName);
                break;
            case R.id.change_photo:
                // 跳出選擇圖片畫面
                getRequest(Config.URL_UPDATE_GROUP_PHOTO, gId+"&gPic="+gPic);
                break;
            case R.id.delete_group:
                // 插入一個警告視窗來確認刪除//

                getRequest(Config.URL_DELETE_GROUP, gId);
                Intent intent = new Intent();
                intent.setClass(GroupSetting.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.exit_group:
                // 插入一個警告視窗來確認退出//
                getRequest(Config.URL_EXIT_GROUP, gId+"&uEmail="+uEmail);
                intent = new Intent();
                intent.setClass(GroupSetting.this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    private void getRequest(final String url,final String getParam){
        class DeleteEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GroupSetting.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(GroupSetting.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(url, getParam);
                return s;
            }
        }

        DeleteEmployee de = new DeleteEmployee();
        de.execute();
    }

}
