package com.example.emily.beaconside;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NewEvent extends AppCompatActivity {

    private Button buttonChangePic;
    TextView hiword;
    ImageView imageView_cPic;
    private Button button_add;
    EditText edittext_cName;
    String cPic;
    private ListView listview_newEvent;

    String uEmail = "sandy@gmail.com";
    String get_uEmail = "\"sandy@gmail.com\"";
    private String JSON_STRING;

    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> bName_list = new ArrayList<String>();//我的event名稱list

    private BeaconCheckboxAdapter beaconCheckboxAdapter;
    StringBuffer beaconSelect_string = new StringBuffer();//記錄哪幾個beacon被選
    ArrayList<String> beaconSelect_list = new ArrayList<String>();//我的event名稱list

    public static final int resultNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        //接收從main傳過來的資料
        Intent intent = getIntent();
        uEmail = intent.getStringExtra("uEmail");
        get_uEmail = "\""+uEmail+"\"";
        macAddress_list = intent.getStringArrayListExtra("macAddress_list");
        bName_list = intent.getStringArrayListExtra("bName_list");
        bPic_list = intent.getStringArrayListExtra("bPic_list");

        cPic = getIntent().getStringExtra("sayHi");
       // Toast.makeText(MainActivity.this, "測試" + cPic, Toast.LENGTH_SHORT).show();
        imageView_cPic = (ImageView) findViewById(R.id.imageView_cPic);


        String uri = "@drawable/" + cPic; //圖片路徑和名稱

        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

        imageView_cPic.setImageResource(imageResource);


        buttonChangePic = (Button) findViewById(R.id.buttonChangePic);

        //實做OnClickListener界面
        buttonChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextPage();
            }
        });

        button_add = (Button) findViewById(R.id.button_add);

        //實做OnClickListener界面
        /*button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });*/
        edittext_cName = (EditText) findViewById(R.id.editText_cName);
        listview_newEvent = (ListView) findViewById(R.id.listview_newEvent);
//        listView.setOnItemClickListener(this);

        showBeacon();

        button_add.setOnClickListener(new View.OnClickListener() {

            //          Toast.makeText(NewGroup.this,"On click",Toast.LENGTH_SHORT).show();
            @Override
            public void onClick(View v) {

                for(int i=0;i<beaconCheckboxAdapter.mChecked.size();i++){
                    if(beaconCheckboxAdapter.mChecked.get(i)){
                        //beaconSelect_list.add((String) beaconCheckboxAdapter.getItem(i));
                        beaconSelect_string.append((String) beaconCheckboxAdapter.getItem(i)).append(",");

//                                friendId = friendId+","+adapter.getItem(i).toString();
                    }
                }
                addEvent();

            }



        });


    }

    private void showBeacon(){
        ArrayList< HashMap<String, Object>> list = new ArrayList<>();

        for(int x = 0 ; x < macAddress_list.size() ; x++){
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("macAddress" , macAddress_list.get(x));
            hashMap.put("bName" , bName_list.get(x));
            hashMap.put("bPic",bPic_list.get(x));
            list.add(hashMap);
        }

        beaconCheckboxAdapter = new BeaconCheckboxAdapter(NewEvent.this, list);
        listview_newEvent.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview_newEvent.setAdapter(beaconCheckboxAdapter);

    }



    /**
     * 開啟Main2Activity之用
     */
    private void startNextPage() {
        Intent intent = new Intent();
        intent.setClass(NewEvent.this, ChangePic.class);
        startActivityForResult(intent, resultNum);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                cPic = data.getExtras().getString(ChangePic.FLAG);//從changPic得到的值(圖片名稱)

                String uri = "@drawable/" + cPic; //圖片路徑和名稱

                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

                imageView_cPic.setImageResource(imageResource);

            }
        }
    }

    private void addEvent() {
        final String cName = edittext_cName.getText().toString().trim();

        class AddEvent extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NewEvent.this,"Adding...","Wait...",false,false);
                //這裡把MainActivity改為相對應的java檔名就好
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(NewEvent.this,s, Toast.LENGTH_SHORT).show();


            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("cName",cName);
                params.put("uEmail",uEmail);
                params.put("cPic",cPic);
                params.put("beaconSelect_string",beaconSelect_string.toString());
                //params.put(php檔內的接收變數  $_POST["___"] , 要傳給php檔的java變數)

                RequestHandler rh = new RequestHandler();
//                String res = rh.sendPostRequest(Config.URL_CREATE_EVENT, params);
                //String res = rh.sendPostRequest("php檔的網址", params);
                //URL_ADD 是在 Config.java設定好的字串 也就是 http://140.117.71.114/employee/addEmp.php
                //php檔可在ftp上傳下載
                return "0";
            }
        }


        //這兩行不用理
        AddEvent ae = new AddEvent();
        ae.execute();

    }

}


