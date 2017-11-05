package com.example.emily.beaconside;

import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

public class EditGroupBeacon extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ListView listView_beacon;
    String uEmail;
    String get_uEmail;
    String user_beacon_json;

    String gId;
    String gName;
    String gPic;

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

        Toast.makeText(this,user_beacon_json, Toast.LENGTH_SHORT).show();




        Bundle extras = getIntent().getExtras();
        gId = extras.getString("gId");
        gName = extras.getString("gName");
        gPic = extras.getString("gPic");

        listView_beacon = (ListView)findViewById(R.id.listView_beacon);

    }


    private void addGroupBeacon(){

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

            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> param = new HashMap<>();
                param.put("uEmail",uEmail);
                //param.put("beaconSelect_string",beaconSelect_string.toString());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_CREATE_EVENT, param);
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
            addGroupBeacon();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
