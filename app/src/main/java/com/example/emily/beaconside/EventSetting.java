package com.example.emily.beaconside;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/11/15.
 */

public class EventSetting extends AppCompatActivity {

    public String cId;
    public String cName;
    public String cPic;
    // 本機資料
    SharedPreferences sharedPreferences;
    public static String uEmail;
    String JSON_STRING;
    ImageView imageView_gPic;
    TextView textView_cName;

    String[] GroupMemberId;
    ArrayList<String> uName_list = new ArrayList<String>();;//群組會員名字
    ArrayList<String> uId_list = new ArrayList<String>();;//群組會員id
    private LinearLayoutManager linearLayoutManager;
    ConstraintLayout constraintLayout_addBeacon;//增加共享物品區塊

    public static final int resultNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_setting);
        // 標題功能列
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable


        //接收從GroupMain傳遞來的
        Bundle extras = getIntent().getExtras();
        cId = extras.getString("cId");
        cName = extras.getString("cName");
        cPic = extras.getString("cPic");
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        uEmail = sharedPreferences.getString("EMAIL", "0");

        //Toast.makeText(GroupSetting.this,gPic,Toast.LENGTH_LONG).show();

        imageView_gPic = (ImageView) findViewById(R.id.imageView_gPic);
        String uri = "@drawable/" + cPic + "_ss"; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        imageView_gPic.setImageResource(imageResource);

        textView_cName = (TextView) findViewById(R.id.textView_cName);
        textView_cName.setText(cName);

        //進入增加活動物品
        constraintLayout_addBeacon = (ConstraintLayout)findViewById(R.id.constraintLayout_addBeacon);
        constraintLayout_addBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EventSetting.this,addBeaconToEvent.class);
                intent.putExtra("cId",cId);
                intent.putExtra("cName",cName);
                intent.putExtra("cPic",cPic);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                cPic = data.getExtras().getString(ChangeGroupPic.FLAG);//從changPic得到的值(圖片名稱)

                String uri = "@drawable/" + cPic + "_ss"; //圖片路徑和名稱

                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

                imageView_gPic.setImageResource(imageResource);

                getRequest(Config.URL_UPDATE_GROUP_PHOTO, cId+"&cPic="+cPic);

            }
        }
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        return true;
    }
    // 群組功能列表
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_name:

                // 跳出視窗來輸入新名字

                final Dialog dialog = new Dialog(EventSetting.this);
                dialog.setContentView(R.layout.dialog_edit_event_name);
                dialog.show();

                final EditText editText_cName;
                Button button_confirm;

                editText_cName = (EditText)dialog.findViewById(R.id.editText_cName);
                button_confirm = (Button)dialog.findViewById(R.id.button_confirm);

                editText_cName.setText(cName);

                button_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cName = editText_cName.getText().toString().trim();//取得edittext上的字
                        getRequest(Config.URL_UPDATE_EVENT_NAME,cId+"&cName="+cName);

                        Intent intent = new Intent();
                        intent.setClass(EventSetting.this,EventSetting.class);
                        intent.putExtra("cId",cId);
                        intent.putExtra("cName",cName);
                        intent.putExtra("cPic",cPic);
                        startActivity(intent);
                        finish();
                    }
                });



                break;
            case R.id.change_photo:
                // 跳出選擇圖片畫面
                Intent intent = new Intent();
                intent.setClass(EventSetting.this, ChangeGroupPic.class);
                startActivityForResult(intent, resultNum);
                break;

            case R.id.delete_event:
                // 插入一個警告視窗來確認退出//

                AlertDialog.Builder alert_exit = new AlertDialog.Builder(this);
                alert_exit.setTitle("");
                alert_exit.setMessage("確定要刪除 "+cName+" 嗎?");

                alert_exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getRequest(Config.URL_DELETE_EVENT, cId);

                        Intent intent = new Intent();
                        intent.setClass(EventSetting.this,MainActivity.class);
                        startActivity(intent);
                        finish();


                    }
                });

                alert_exit.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert_exit.show();

                break;

            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                break;
        }

        return true;
    }

    private void getRequest(final String url,final String getParam){
        class DeleteEmployee extends AsyncTask<Void,Void,String> {
           // ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // loading = ProgressDialog.show(EventSetting.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                Toast.makeText(EventSetting.this, s, Toast.LENGTH_LONG).show();
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
