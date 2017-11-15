package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EditGroupMember extends AppCompatActivity {

    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    private ListView listView_memberlist;
    private ImageView Img;
    private FriendsListAdapter adapter;
    private EditText groupName;
    ImageButton buttonChangePic;
    public static String uEmail,founderId;
    private String gId;
    String gName;
    String gPic;
    ArrayList<String> listItemID = new ArrayList<String>();
    public static final int resultNum = 0;
    public String pic = "groupPic_1";
    ImageView pic_view;
    String[] GroupMemberId;

    //取不到好友信箱 先寫死
    String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_member_to_group);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

        Bundle extras = getIntent().getExtras();
        gId = extras.getString("gId");
        gName = extras.getString("gName");
        gPic = extras.getString("gPic");
        GroupMemberId = extras.getStringArray("GroupMemberId");


        for(int i = 0 ; i < GroupMemberId.length ; i++){
            //Toast.makeText( EditGroupMember.this,founderId,Toast.LENGTH_SHORT).show();
        }

        listView_memberlist = (ListView)findViewById(R.id.listView_memberlist);

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

                                adapter = new FriendsListAdapter(EditGroupMember.this, list);
                                listView_memberlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                listView_memberlist.setAdapter(adapter);


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

    private void addGroupMember() {

        class AddGroup extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(EditGroupMember.this,"Adding...","Wait...",false,false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
//                Toast.makeText( NewGroup.this,gId,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClass(EditGroupMember.this,GroupSetting.class);
                intent.putExtra("gId",gId);
                intent.putExtra("gName",gName);
                intent.putExtra("gPic",gPic);
//            intent.putExtra("gPic",gPic);
                startActivity(intent);
                finish();

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("gId",gId);
                params.put("friendId",friendId);

                //params.put(php檔內的接收變數  $_POST["___"] , 要傳給php檔的java變數)

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_EDIT_GROUP_MEMBER, params);
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
        switch(id){
            case R.id.action_new_item_check:

                //執行新增
                listItemID.clear();
                friendId=null;
                for(int i=0;i<adapter.mChecked.size();i++){
                    if(adapter.mChecked.get(i)){
                        listItemID.add((String) adapter.getItem(i));
//                                friendId = friendId+","+adapter.getItem(i).toString();
                    }
                }
                if(listItemID.size()==0){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditGroupMember.this);
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
//                            sb.append(friendId);
//                            AlertDialog.Builder builder2 = new AlertDialog.Builder(NewGroup.this);
//                            builder2.setMessage(sb.toString());
//                            builder2.show();
                }
                addGroupMember();

                //return true;
                break;

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */
}
