package com.example.emily.beaconside;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewGroup extends AppCompatActivity implements View.OnClickListener {


    CallbackManager callbackManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    private ListView List;
    private ImageView Img;
    private FriendsListAdapter adapter;
    private EditText groupName;
    ImageButton buttonChangePic;
    public static String uEmail,founderId;
    private String gId;
    ArrayList<String> listItemID = new ArrayList<String>();
    public static final int resultNum = 0;
    public String pic = "groupPic_1";
    ImageView pic_view;

    //取不到好友信箱 先寫死
    String friendId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

        List = (ListView) findViewById(R.id.listView);
        Img  = (ImageView) findViewById(R.id.imageView3);
        groupName = (EditText) findViewById(R.id.newGroupName);
        buttonChangePic = (ImageButton) findViewById(R.id.buttonChangePic);
        buttonChangePic.setOnClickListener(this);
        pic_view = (ImageView)findViewById(R.id.pic_view);

        if(accessToken!=null) {

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                final GraphResponse response) {
                            try {
                                //好友資料 個數
                                JSONArray friend_list=object.getJSONObject("friends").getJSONArray("data");
                                uEmail = object.get("email").toString();
                                founderId = object.get("id").toString();

//                                Toast.makeText( NewGroup.this,founderId,Toast.LENGTH_SHORT).show();

                                int counter = friend_list.length();
                                ArrayList< HashMap<String, Object>> list = new ArrayList<>();
                                String[] jName = new String[counter];
                                String[] jID = new String[counter];
                                String[] jPic = new String[counter];
                                //
                                for (int i = 0; i < counter ; i++) {
                                    Object jsonName = friend_list.getJSONObject(i).get("name");
                                    Object jsonID = friend_list.getJSONObject(i).get("id");

                                    String picID=jsonID.toString();

                                    jName[i] = jsonName.toString();
                                    jID[i] = jsonID.toString();
                                    jPic[i]="https://graph.facebook.com/"+picID+"/picture?type=large";

                                    HashMap<String ,Object> hashMap = new HashMap<>();
                                    hashMap.put("name" , jName[i]);
                                    hashMap.put("pic" , jPic[i]);
                                    hashMap.put("friendsId",jID[i]);
                                    list.add(hashMap);
                                }

                                adapter = new FriendsListAdapter(NewGroup.this, list);
                                List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                List.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "friends,email");
            request.setParameters(parameters);
            request.executeAsync();

        }

    }


    //    @Override
//    public void onBackPressed() {
//        Intent backPressedIntent = new Intent();
//        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
//        startActivity(backPressedIntent );
//        finish();
//    }
    private void addGroup() {
        final String gName = groupName.getText().toString().trim();

        class AddGroup extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NewGroup.this,"Adding...","Wait...",false,false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                gId = s;
//                Toast.makeText( NewGroup.this,gId,Toast.LENGTH_SHORT).show();

                Intent goAddBeacon = new Intent();
                goAddBeacon.setClass(NewGroup.this,addBeaconToGroup.class);
                goAddBeacon.putExtra("gId",gId);
                goAddBeacon.putExtra("gName",gName);
                startActivity(goAddBeacon);
                finish();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("gName",gName);
                params.put("uEmail",uEmail);
                params.put("uId",founderId);
                params.put("friendId",friendId);

                //params.put(php檔內的接收變數  $_POST["___"] , 要傳給php檔的java變數)

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_CREATE_GROUP, params);
                //String res = rh.sendPostRequest("php檔的網址", params);
                //URL_ADD 是在 Config.java設定好的字串 也就是 http://140.117.71.114/employee/addEmp.php
                //php檔可在ftp上傳下載
                return res;
            }
        }
        //這兩行不用理
        AddGroup ae = new AddGroup();
        ae.execute();
    }


    public void onClick(View v) {
        if(v == buttonChangePic){

            Intent intent = new Intent();
            intent.setClass(NewGroup.this, ChangeGroupPic.class);
            startActivityForResult(intent, resultNum);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                pic = data.getExtras().getString(ChangePic.FLAG);//從changPic得到的值(圖片名稱)

                String uri = "@drawable/" + pic; //圖片路徑和名稱

                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

                pic_view.setImageResource(imageResource);

            }
        }
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
            /* 切回到原本的畫面 */
            listItemID.clear();
            friendId=null;
            for(int i=0;i<adapter.mChecked.size();i++){
                if(adapter.mChecked.get(i)){
                    listItemID.add((String) adapter.getItem(i));
//                                friendId = friendId+","+adapter.getItem(i).toString();
                }
            }
            if(listItemID.size()==0){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewGroup.this);
                builder1.setMessage("None");
                builder1.show();
            }else{
                StringBuilder sb = new StringBuilder();

                for(int i=0;i<listItemID.size();i++){
//                                sb.append(listItemID.get(i)+",");
                    if (i==0)
                        friendId = listItemID.get(i).toString()+",";
                    else {
                        friendId = friendId + listItemID.get(i).toString()+",";
                    }
                }
                friendId = friendId.substring(0,friendId.length() - 1);
                Toast.makeText( NewGroup.this,friendId,Toast.LENGTH_SHORT).show();
//                            sb.append(friendId);
//                            AlertDialog.Builder builder2 = new AlertDialog.Builder(NewGroup.this);
//                            builder2.setMessage(sb.toString());
//                            builder2.show();
            }
            addGroup();

            Intent backPressedIntent = new Intent();
            backPressedIntent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(backPressedIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */

}
