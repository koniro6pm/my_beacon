package com.example.emily.beaconside;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    ImageButton buttonChangePic;
    TextView hiword;
    ImageView pic_view;
    EditText editText_cName;
    String cPic = "group_pic1";//預設圖片一開始是group_pic1
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

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

        //接收從main傳過來的資料
        Intent intent = getIntent();
        uEmail = intent.getStringExtra("uEmail");
        get_uEmail = "\""+uEmail+"\"";
        macAddress_list = intent.getStringArrayListExtra("macAddress_list");
        bName_list = intent.getStringArrayListExtra("bName_list");
        bPic_list = intent.getStringArrayListExtra("bPic_list");


        pic_view = (ImageView) findViewById(R.id.pic_view);

       buttonChangePic = (ImageButton) findViewById(R.id.buttonChangePic);

        //實做OnClickListener界面
        buttonChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextPage();
            }
        });


        //實做OnClickListener界面
        /*button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });*/
        editText_cName = (EditText) findViewById(R.id.editText_cName);
        listview_newEvent = (ListView) findViewById(R.id.listview_newEvent);
//        listView.setOnItemClickListener(this);

        showBeacon();



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
        intent.setClass(NewEvent.this, ChangeGroupPic.class);
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

                pic_view.setImageResource(imageResource);

            }
        }
    }


    private void addEvent(){

        final String cName = editText_cName.getText().toString().trim();


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
                Toast.makeText(NewEvent.this,s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> param = new HashMap<>();
                param.put("cName",cName);
                param.put("uEmail",uEmail);
                param.put("cPic",cPic);
                param.put("beaconSelect_string",beaconSelect_string.toString());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_CREATE_EVENT, param);
                return res;
            }
        }

        AddEvent ae = new AddEvent();
        ae.execute();
    }


    /* check button*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_item_save, menu);
        //Toast.makeText(this,"叫出menu", Toast.LENGTH_SHORT).show();

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_item_check) {
            //執行新增beacon
            for(int i=0;i<beaconCheckboxAdapter.mChecked.size();i++){
                if(beaconCheckboxAdapter.mChecked.get(i)){
                    //beaconSelect_list.add((String) beaconCheckboxAdapter.getItem(i));
                    beaconSelect_string.append((String) beaconCheckboxAdapter.getItem(i)).append(",");

//                                friendId = friendId+","+adapter.getItem(i).toString();
                }
            }
            addEvent();
            Intent intent = new Intent();
            intent.setClass(NewEvent.this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */

}
