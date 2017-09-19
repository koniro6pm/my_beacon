package com.example.emily.beaconside;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import static java.lang.Integer.parseInt;


public class Login extends AppCompatActivity implements View.OnClickListener {

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    private Button btn_add;
    private Button btn_friends;
    private Button btn_search;

    public static String uEmail;
    public static String uId ;
    public static String uName ;
    private String JSON_STRING;
    // 放資料庫所有已被註冊的beacon資料
    private ArrayList<String> user_list;
    boolean isRegistered = false;   // User是否已經註冊資料庫
    Button login;
    Button btn_facebook;
    // 本機資料
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_friends.setOnClickListener(this);
        accessToken = AccessToken.getCurrentAccessToken();
        getUserEvent(); // 取得資料庫裡所有User的Email，存在user_list裡面
        login = (Button) findViewById(R.id.login);
/**
 *         FB登入按鈕，要求使用者權限，能要求的有email、friends、profile
 *         未做 : 登入時將資料寫進資料庫
 */

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(Login.this,"I'm clicked",Toast.LENGTH_SHORT).show();
                accessTokenTracker.startTracking();
                accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Toast.makeText(Login.this,"login button clicked",Toast.LENGTH_SHORT).show();
                                //讀出姓名、ID、網頁連結
                                try {
                                    uId=(String) object.get("id");
                                    uName=(String) object.get("name");
                                    uEmail=(String) object.get("email");
                                    // 存到本機
                                    sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
                                    sharedPreferences.edit()
                                            .putString("NAME", uName)
                                            .putString("EMAIL", uEmail)
                                            .putString("ID", uId)
                                            .commit();

                                    for(String email : user_list){
//                                        Toast.makeText(Login.this,email,Toast.LENGTH_SHORT).show();
                                        if(uEmail.equals(email)) {
                                            isRegistered = true;
                                        }
                                    }
                                    if(!isRegistered) {
                                        addUser();
                                        Toast.makeText(Login.this,uEmail+"成功註冊",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        login.setVisibility(View.VISIBLE);
                                        login.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                /**換頁到Main**/
                                                Intent intent = new Intent();
                                                intent.setClass(Login.this, MainActivity.class);
                                                startActivity(intent);
                                                /******/
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }});
                //包入你想要得到的資料，送出 request
                Bundle parameters = new Bundle();
                parameters.putString("fields" , "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();

            }
            @Override
            public void onCancel() {
                Toast.makeText(Login.this,uEmail+"取消",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this,uEmail+"錯誤",Toast.LENGTH_SHORT).show();
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    accessToken = null;
                    Toast.makeText(Login.this,"accesstoken: null",Toast.LENGTH_SHORT).show();
                    final ProgressDialog loading = ProgressDialog.show(Login.this,"Log out...","Wait...",false,false);
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(2000);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                            finally{
                                loading.dismiss();
                            }
                        }
                    }).start();
                    login.setVisibility(View.INVISIBLE);
                }

            }
        };
/**
 *   HTTP Request取得資料
 */

    }

    @Override
    protected void onResume(){
        super.onResume();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        getUserEvent(); // 取得資料庫裡所有User的Email，存在user_list裡面
        if(accessToken!=null){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
//                            Toast.makeText(Login.this,"Get Token",Toast.LENGTH_SHORT).show();
                            //讀出姓名、ID、網頁連結
                            try {
//                                Toast.makeText(Login.this,"Already Log in",Toast.LENGTH_SHORT).show();
                                uId=(String) object.get("id");
                                uName=(String) object.get("name");
                                uEmail=(String) object.get("email");
                                sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
                                sharedPreferences.edit()
                                        .putString("NAME", uName)
                                        .putString("EMAIL", uEmail)
                                        .putString("ID", uId)
                                        .commit();
                                for(String email : user_list){
//                                    Toast.makeText(Login.this,email,Toast.LENGTH_SHORT).show();
                                    if(uEmail.equals(email)) {
                                        isRegistered = true;
                                    }
                                }
                                if(!isRegistered) {
                                    addUser();
                                    Toast.makeText(Login.this,uEmail+"成功註冊",Toast.LENGTH_SHORT).show();
                                }
                                login.setText("以"+uName+"的身份繼續使用");
                                login.setVisibility(View.VISIBLE);
                                login.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        /**換頁到Main**/
                                        Intent intent = new Intent();
                                        intent.setClass(Login.this, MainActivity.class);
                                        //傳遞變數
//                                        intent.putExtra("uEmail",uEmail);
//                                        intent.putExtra("uName",uName);
                                        startActivity(intent);
//                                        finish();
                                        /******/
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Failed","Failed");
//                                Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                            // Application code
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    /**
     *  分享到user塗鴉牆，需另外要求權限，未使用dialog方塊
     */
    private void publishImage(){
        Bitmap image = BitmapFactory.decodeResource(getResources(),     R.mipmap.ic_launcher);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
//                分享出的內容
                .setCaption("Welcome To Facebook Photo Sharing on steroids!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void search(){
        class Search extends AsyncTask<Void,Void,String> {

            //            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(Login.this,"Adding...","Wait...",false,false);
//                loading = ProgressDialog.show(Login.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
//                Toast.makeText(Login.this,s,Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    JSONArray friend_list=object.getJSONObject("friends").getJSONArray("data");
//                                    for (int i = 0; i < counter ; i++) {
//                                        Object jsonID = friend_list.getJSONObject(i).get("id");
//                                        jID[i] = jsonID.toString();
//                                    }
                                    Object jsonID = friend_list.getJSONObject(3).get("id");
                                    uId=jsonID.toString();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "friends");
                request.setParameters(parameters);
                request.executeAsync();

                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequestParam(Config.URL_SEARCH, uId);
                return res;

            }
        }

        Search ae = new Search();
        ae.execute();
    }
    private void addUser(){

        class AddUser extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(Login.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
//                Toast.makeText(Login.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                //params.put(傳給php檔的post id,傳遞的變數)
                params.put("uEmail",uEmail);
                params.put("uId",uId);
                params.put("uName",uName);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_USER, params);
                return res;
            }
        }

        AddUser ae = new AddUser();
        ae.execute();
    }

    private void getUserEvent(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                //將取得的json轉換為array list, 顯示在畫面上
                showUserEvent();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_USER_DATABASE);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showUserEvent() {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array
            user_list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String email = jo.getString("uEmail");//取得macAddress
                user_list.add(email);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private  void friendsList(){
//        List = (ListView) findViewById(R.id.list);
//        if(accessToken!=null) {
//            GraphRequest request = GraphRequest.newMeRequest(
//                    accessToken,
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//
//                            try {
////                                String friends = object.getString("friends");
//                                //好友資料 個數
//                                JSONArray friend_list=object.getJSONObject("friends").getJSONArray("data");
//                                int counter = friend_list.length();
//                                ArrayList<HashMap<String , String>> list = new ArrayList<>();
//                                String[] jname = new String[counter];
//                                String[] jID = new String[counter];
//
//                                for (int i = 0; i < counter ; i++) {
//                                    Object jsonName = friend_list.getJSONObject(i).get("name");
//                                    jname[i] = jsonName.toString();
//                                    Object jsonID = friend_list.getJSONObject(i).get("id");
//                                    jID[i] = jsonID.toString();
//
//                                    HashMap<String , String> hashMap = new HashMap<>();
//                                    hashMap.put("name" , jname[i]);
//                                    hashMap.put("id" , jID[i]);
//                                    list.add(hashMap);
//                                }
//
//
//                                ListAdapter adapter = new SimpleAdapter(.Login.this , list , android.R.layout.simple_list_item_2
//                                        ,new String[]{"name" , "id"},new int[]{android.R.id.text1 , android.R.id.text2});
//                                List.setAdapter(adapter);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "friends");
//            request.setParameters(parameters);
//            request.executeAsync();
//        }
        Intent intent = new Intent();
        intent.setClass(Login.this, Friends.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        if(v == btn_friends){
            friendsList();
        }
        if(v == btn_search){
            search();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}


//
