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
        Toast.makeText(this, gId+" "+gName, Toast.LENGTH_SHORT)
                .show();
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
