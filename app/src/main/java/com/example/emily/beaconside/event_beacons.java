package com.example.emily.beaconside;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class event_beacons extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Bundle bundle =getIntent().getExtras();
//        cId = bundle.getString("message"); // 接傳來的cid

//    }

    Context mContext;
    Button side_new,side_group_bt,side_class_bt;
    View side_class_ls,side_group_ls;
    ImageView chooseGroup,chooseClass,userPicture;
    ListView listView1;
    rowdata adapter;
    ArrayAdapter<String> adapterPress;
    TextView userName;
    TextView eventTitle;
    ImageView pic_view;


    BluetoothMethod bluetooth = new BluetoothMethod();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // 本機資料
    SharedPreferences sharedPreferences;
    //寫死目前的用戶
    public static String uEmail;
    public static String get_uEmail;
    public static String uName;
    public static String uId;
    private String JSON_STRING; //用來接收php檔傳回的json

    public String user_event_json;
    public String user_group_json;

    public static ArrayList<String> bName_list = new ArrayList<String>();//我的beacon名稱list
    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> distance= new ArrayList<String>();
    ArrayList<Integer> bAlert_list = new ArrayList<>();
    private String cId;
    String cPic;
    String cName;

    ImageView imageView_home;

    String[] eventId_array;//儲存event id
    String[] eventName_array;//儲存event name
    String[] eventPic_array;//event 圖片
    int[] groupId_array;//儲存group id
    String[] groupName_array;//儲存group name
    String[] groupPic_array;

    /* class main side */
    ListView group_list;
    private GridView event_list;
    ArrayList<String> groupName_list;
    ArrayList<String> eventName_list;
    ArrayList  eventId_list;
    ArrayList<String> eventPic_list;
    ArrayList<String> eventName_list1 = new ArrayList<String>();
    ArrayList<String> eventName_list2 = new ArrayList<String>();
    ArrayAdapter<String> adapter_sideList_group;
    main_side_event_rowdata  adapter_sideList_event;


    /* long press */
    MergeAdapter mergeAdapter;
    /* end lon */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_side_bar);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");//消除lable

        //當前event資訊
        Bundle bundle =getIntent().getExtras();
        cId = bundle.getString("message"); // 接傳來的cId
        cName = bundle.getString("eventTitle");
        cPic = bundle.getString("eventPic");

        //Toast.makeText(this, cName, Toast.LENGTH_SHORT).show();

        // 從本機資料取使用者資料
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        uName = sharedPreferences.getString("NAME", "YOO");
        uEmail = sharedPreferences.getString("EMAIL", "YOO@gmail.com");
        uId = sharedPreferences.getString("ID", "1234567890");
        get_uEmail = "\""+uEmail+"\"";
        user_event_json = sharedPreferences.getString("user_event_json", "1234567890");
        user_group_json = sharedPreferences.getString("user_group_json", "1234567890");

        // 設置SwipeView重整
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_main);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });
        //listview

        pic_view = (ImageView)findViewById(R.id.pic_view);
        String uri = "@drawable/" + cPic; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        pic_view.setImageResource(imageResource);

        imageView_home = (ImageView)findViewById(R.id.imageView_home);

        /* class main side */
        group_list = (ListView)findViewById(R.id.group_list);
        event_list = (GridView) findViewById(R.id.event_list);

        eventTitle = (TextView)findViewById(R.id.eventTitle);
        eventTitle.setText(cName);

        mContext = this;
        listView1=(ListView) findViewById(R.id.listView1);

        /* list view function */
        adapterPress = new ArrayAdapter<String>(this, R.layout.activity_rowdata, bName_list);
        mergeAdapter = new MergeAdapter();

        mergeAdapter.addAdapter(new ListTitleAdapter(this,adapterPress));
        mergeAdapter.addAdapter(adapterPress);//

//        listView1.setAdapter(mergeAdapter);
        registerForContextMenu(listView1);
//
        // 設置側邊欄使用者名稱
        userName = (TextView) findViewById(R.id.name);
        userName.setText("Hi! "+uName);
        userPicture = (ImageView)findViewById(R.id.userPicture) ;
        String url ="https://graph.facebook.com/"+uId+"/picture?type=large";
        new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                Bitmap bmp = toRoundBitmap(result);
                userPicture.setImageBitmap (bmp);
                super.onPostExecute(result);
            }
        }.execute(url);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //some id
        //左側滑動選單
        side_new = (Button)findViewById(R.id.side_new);
        side_group_bt = (Button)findViewById(R.id.side_group_bt);
        side_class_bt = (Button)findViewById(R.id.side_class_bt);
        side_class_ls = (View)findViewById(R.id.side_class_ls);
        side_group_ls = (View)findViewById(R.id.side_group_ls);
        chooseGroup = (ImageView)findViewById(R.id.chooseGroup);
        chooseClass = (ImageView)findViewById(R.id.chooseClass);

        side_group_bt.setOnClickListener(new View.OnClickListener() {//group
            @Override
            public void onClick(View v) {
                side_class_ls.setVisibility(View.GONE);
                side_group_ls.setVisibility(View.VISIBLE);
                chooseGroup.setVisibility(View.VISIBLE);
                chooseClass.setVisibility(View.GONE);

                side_new.setText("+ 新增群組 ");
                side_new.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this,"new group Clicked ",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(event_beacons.this,NewGroup.class);
                        bluetooth.bluetoothStop();
                        startActivity(intent);
                    }

                });
            }
        });
        side_class_bt.setOnClickListener(new View.OnClickListener() {//class
            @Override
            public void onClick(View v) {
                side_group_ls.setVisibility(View.GONE);
                side_class_ls.setVisibility(View.VISIBLE);
                chooseClass.setVisibility(View.VISIBLE);
                chooseGroup.setVisibility(View.GONE);

                side_new.setText("+ 新增活動");
                side_new.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this,"new event Clicked ",Toast.LENGTH_SHORT).show();
                        Intent MainToNewEvent = new Intent();
                        MainToNewEvent.putExtra("uEmail",uEmail);
                        MainToNewEvent.putExtra("bName_list",bName_list);
                        MainToNewEvent.putExtra("macAddress_list",macAddress_list);
                        MainToNewEvent.putExtra("bPic_list",bPic_list);
                        MainToNewEvent.setClass(event_beacons.this,NewEvent.class);
                        bluetooth.bluetoothStop();
                        startActivity(MainToNewEvent);
                    }

                });
            }
        });


        imageView_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(event_beacons.this,MainActivity.class);
                startActivity(intent);

            }
        });
        getBeacon();
        showUserEvent();
        showUserGroup();

    }

    @Override
    public void onResume(){
        super.onResume();
        // 從本機資料取使用者資料
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        uName = sharedPreferences.getString("NAME", "0");
        uEmail = sharedPreferences.getString("EMAIL", "0");
        uId = sharedPreferences.getString("ID", "0");
        get_uEmail = "\""+uEmail+"\"";
        //Toast.makeText(this, uName+uEmail+uId, Toast.LENGTH_SHORT).show();
        bluetooth.BTinit(this);
//        bluetooth.getStartSearchDevice();
        getBeacon();

        bluetooth.getStartMyItemDistance(macAddress_list);
    }
    //取得用戶擁有的beacon
    private void getBeacon(){
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

                //將取得的json轉換為array list, 顯示在畫面上
                showMyBeacon();


            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_FROM_EVENT,cId);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    //將取得的json轉換為array list, 顯示在畫面上
    private void showMyBeacon(){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array
            bName_list = new ArrayList<>();
            macAddress_list = new ArrayList<>();
            bPic_list = new ArrayList<>();
            bAlert_list = new ArrayList<>();
            bluetooth.myDeviceDistance = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name
                String bAlert = jo.getString("alertMiles");//取得beacon的alertMile
                String isAlert = jo.getString("isAlert");

                //bName,macAddress各自單獨存成一個array
                bName_list.add(bName);
                macAddress_list.add(macAddress);
                bPic_list.add(bPic);
                if(isAlert.equals("1")) {
                    bAlert_list.add(parseInt(bAlert));
//                    Toast.makeText(MainActivity.this, bName + " alert is" + parseInt(bAlert), Toast.LENGTH_SHORT).show(); //顯示訊號
                }
                else
                    bAlert_list.add(100000);

                //distance.add("");//distance先寫死
            }
            bluetooth.mac = macAddress_list;
            //上面的資料讀取完  才設置listview
//            adapter=new rowdata(this,bName_list,distance,macAddress_list,bPic_list,false);//顯示的方式
            adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,false);//顯示的方式
//            adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,true);//顯示的方式
            mergeAdapter.addAdapter(new ListTitleAdapter(this,adapter));
            mergeAdapter.addAdapter(adapter);
            listView1.setAdapter(adapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //選項按下反應
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemName = bName_list.get(position);      //哪一個列表
                    String itemAddress = macAddress_list.get(position);
                    Toast.makeText(event_beacons.this, itemName + " selected", Toast.LENGTH_SHORT).show(); //顯示訊號
                    bluetooth.bluetoothFunction="searchItem";

//                /*換頁面 有換Activity*/
                    Intent intent = new Intent();
                    intent.setClass(event_beacons.this, Compass.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("itemName", itemName);
                    bundle.putString("itemAddress", itemAddress);
                    intent.putExtras(bundle);
                    bluetooth.bluetoothStop();
                    startActivity(intent);
                }
            } );
            bluetooth.Alert = bAlert_list;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showUserEvent() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(user_event_json);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            eventName_array = new String[result.length()];
            eventId_array = new String[result.length()];
            eventPic_array = new String[result.length()];

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                String cId = jo.getString("cId");//取得event id , 由string轉為cId
                String cName = jo.getString("cName");//取得event名稱
                String cPic = jo.getString("cPic");

                //Toast.makeText(MainActivity.this, cName, Toast.LENGTH_LONG).show();

                eventId_array[i] = cId;
                eventName_array[i] = cName;
                eventPic_array[i]= cPic;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //        Toast.makeText(MainActivity.this, "start"+eventName_list1+" start2:"+eventName_list2, Toast.LENGTH_LONG).show();

        eventName_list= new ArrayList<>(Arrays.asList(eventName_array));//array to arraylist
        eventId_list= new ArrayList<>(Arrays.asList(eventId_array));//array to arraylist
        eventPic_list= new ArrayList<>(Arrays.asList(eventPic_array));//array to arraylist
        adapter_sideList_event = new main_side_event_rowdata(this,eventName_list,eventId_list,eventPic_list);
        event_list.setAdapter(adapter_sideList_event);

    }

    private void showUserGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(user_group_json);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            groupName_array = new String[result.length()];
            groupId_array = new int[result.length()];
            groupPic_array = new String[result.length()];

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                int gId = parseInt(jo.getString("gId"));//取得event id , 由string轉為cId
                String gName = jo.getString("gName");//取得event名稱
                String gPic = jo.getString("gPic");

                groupId_array[i] = gId;
                groupName_array[i] = gName;
                groupPic_array[i] = gPic;

                //Toast.makeText(MainActivity.this, gName, Toast.LENGTH_LONG).show();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        groupName_list= new ArrayList<>(Arrays.asList(groupName_array));//array to arraylist
//        Toast.makeText(MainActivity.this, "start"+groupName_list+"end", Toast.LENGTH_LONG).show();
        adapter_sideList_group = new ArrayAdapter<String>(event_beacons.this, android.R.layout.simple_list_item_1,groupName_list);
        group_list.setAdapter(adapter_sideList_group);

        //每個group點進去進入group頁面
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { //選項按下反應
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int click_gId = groupId_array[position];//取得選擇beacon的名字
                String click_gName =  groupName_array[position];//取得選擇beacon的macAddress
                String click_gPic = groupPic_array[position];
                //Toast.makeText(MainActivity.this, click_gName, Toast.LENGTH_LONG).show();

                /**換頁到addNewBeacon**/
                Intent intent = new Intent();
                intent.setClass(event_beacons.this,GroupMain.class);
                intent.putExtra("click_gId",click_gId);
                intent.putExtra("click_gName",click_gName);
                intent.putExtra("click_gPic",click_gPic);
                startActivity(intent);
                finish();
                /******/

            }
        });

    }


    private void deleteBeacon(final String macAddress){
        class DeleteEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(event_beacons.this, "Updating...", "Wait...", false, false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(event_beacons.this, s, Toast.LENGTH_LONG).show();
                getBeacon();
                bluetooth.mac = macAddress_list;
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String temp = cId+","+macAddress;
                String s = rh.sendGetRequestParam(Config.URL_DELETE_BEACON_EVENT, temp);
                return s;
            }
        }

        DeleteEmployee de = new DeleteEmployee();
        de.execute();
    }

    /* Item setting */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView1) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(bName_list.get(info.position));
            /*長按著的選項*/
            String[] menuItems = new String[]{"從活動中刪除"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems   = new String[]{"Delete"};
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = bName_list.get(info.position);
        final String listItemMac = macAddress_list.get(info.position);
//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch (menuItemName){
//            case "Edit":
//                //進入編輯頁面
//                Intent intent = new Intent();
//                intent.setClass(event_beacons.this,editBeacon.class);
//                intent.putExtra("macAddress",listItemMac);
//                intent.putExtra("bName",listItemName);
//                intent.putExtra("uEmail",uEmail);
//                intent.putExtra("eventId_array",eventId_array);
//                intent.putExtra("eventName_array",eventName_array);
//                intent.putExtra("groupName_array",groupName_array);
//                intent.putExtra("groupId_array",groupId_array);
//                startActivity(intent);
//                Toast.makeText(event_beacons.this,groupName_array+","+ listItemName+","+groupId_array, Toast.LENGTH_LONG).show();

//                break;
            case "Delete":
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("");
                alert.setMessage("確定要刪除 "+cName+ " 中的 "+listItemName+" 嗎?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteBeacon(listItemMac);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();

                break;
        }
//        Toast.makeText(MainActivity.this, String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_LONG).show();
        return true;
    }
    /* Item setting end */


    /* refresh */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.group_setting_button, menu);
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
            case R.id.action_name:
                refresh();
                break;

            case R.id.setting:
                Intent intent = new Intent();
                intent.setClass(event_beacons.this,EventSetting.class);
                intent.putExtra("cId",cId);
                intent.putExtra("cName",cName);
                intent.putExtra("cPic",cPic);
                startActivity(intent);
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    /* refresh end */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*經過了dialog卻還是沒開啟 關掉check*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==bluetooth.REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED){
            bluetooth.myStatusBT = false;
        }
        else{
            bluetooth.myStatusBT = true;
        }
    }


    public void refresh() {
        bluetooth.getStartMyItemDistance(macAddress_list);  // 傳送使用者目前擁有的裝置列表，檢查是否在周圍，如果有的話就會顯示距離
//        getBeacon();
        bluetooth.mac = macAddress_list;

        adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,true);//顯示的方式
        listView1.setAdapter(adapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,false);//顯示的方式
                listView1.setAdapter(adapter);
            }
        }, 3000);
        for(int x = 0 ; x < bluetooth.myDeviceDistance.size() ; x++){
            String distance = bluetooth.myDeviceDistance.get(x);
            if(!(distance.equals("Out of Range"))){
                //如果這個beacon的距離不是out of range(表示有搜尋到)
                //就傳給getNotice這顆beacon的mac
                getNotice(bName_list.get(x),macAddress_list.get(x));
                //Toast.makeText(getBaseContext(), "不是out of range", Toast.LENGTH_SHORT).show();

            }else{
                // do nothing
            }

            //Toast.makeText(getBaseContext(),bName_list.get(x)+"距離"+bluetooth.myDeviceDistance.get(x), Toast.LENGTH_SHORT).show();


        }
    }

    //從資料庫取得指定beacon的notice
    private void getNotice(final String bName,final String macAddress){

        class GetNotice extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(MainActivity.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();


                //將php返回的json->object->array
                try {
                    //jsonObject,jsonArray都是全域　方便給checkIfThere使用
                    JSONObject jsonObjectNotice = new JSONObject(s);
                    JSONArray jsonArrayNotice = jsonObjectNotice.getJSONArray(Config.TAG_JSON_ARRAY);

                    //如果返回的notice json不為空的 就推播
                    if (jsonArrayNotice.length() != 0) {
                        //Toast.makeText(getBaseContext(),"這個s是"+jsonArrayNotice, Toast.LENGTH_SHORT).show();

                        for (int j = 0; j < jsonArrayNotice.length(); j++) { //跑迴圈從result json第一個開始到最後一個
                            JSONObject joo = jsonArrayNotice.getJSONObject(j);
                            int nId = joo.getInt("nId");
                            String notice_macAddress = joo.getString("macAddress"); //取得json的"macAddress"
                            String nStartTime = joo.getString("nStartTime");
                            String nEndTime = joo.getString("nEndTime");
                            String nContent = joo.getString("nContent");

                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間

                            try {
                                Date startDate = formatter.parse(nStartTime); //將string轉為date
                                Date endDate = formatter.parse(nEndTime); //將string轉為date

                                if(currentDate.after(startDate) && currentDate.before(endDate)){
                                    //如果 startDate < currentDate < endDate 就推播
                                    broadcastNotice(nId,bName, nContent);

                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                    } else {
                        //Toast.makeText(getBaseContext(), "這個beacon沒有notice喔", Toast.LENGTH_SHORT).show();

                    }
                    //TAG_JSON_ARRAY是"result" php回傳json的最外圍array名稱

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_NOTICE,macAddress);
                //Toast.makeText(getBaseContext(),s, Toast.LENGTH_SHORT).show();
                return s;
            }
        }
        GetNotice gj = new GetNotice();
        gj.execute();
    }

    //推播
    public void broadcastNotice(int nId,String title, String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.compass)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setDefaults(Notification.DEFAULT_VIBRATE);
        // Creates an explicit intent for an Activity in your app
        builder.setDefaults(0);

        // 取得NotificationManager物件
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // 建立通知物件
        Notification notification = builder.build();

        // 使用設定的通知編號為編號發出通知
        manager.notify(nId, notification);

    }

    public void checkItem(View view) {
        Intent intent = new Intent();
        intent.setClass(event_beacons.this,CheckItem.class);
        intent.putExtra("macAddress",macAddress_list);
        intent.putExtra("bPic_list",bPic_list);
        intent.putExtra("bName_list",bName_list);
        intent.putExtra("bStatus_list", bluetooth.myDeviceDistance);
        bluetooth.bluetoothStop();
        startActivity(intent);
//        finish();
    }

    // 從URL下載圖片
    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

}
