package com.example.emily.beaconside;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

public class editBeacon extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener ,OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    EditText editTextbName;
    TextView textViewMac;
    EditText editTextbContent;
    EditText editTextMile;
    Button buttonSubmit;
    ImageButton add_event;
    ImageButton add_group;
    ImageButton add_notification;
    ImageButton buttonChangePic;
    String JSON_STRING;
    String bName;
    String macAddress;
    ImageView device_pic;
    String bPic = "wallet"; //一開始圖片預設wallet
    String title;//dialog的title

    public static final int resultNum = 0;

    private String repeat = "";

    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> select = new ArrayList<>(Arrays.asList("false","false"));

    String[] eventName_array;
    int[] eventId_array;
    String[] groupName_array;
    int[] groupId_array;
    private boolean[]  event_select;//紀錄哪些event被選
    private boolean[]  group_select;//紀錄哪些group被選
    StringBuffer eventIdSelect = new StringBuffer();
    StringBuffer groupIdSelect = new StringBuffer();
    int[] eventId_beacon;//beacon編輯前有的event id
    int[] groupId_beacon;//beacon編輯前有的group id
    private boolean[] event_beaconSelect;//紀錄最初哪些event被選(beacon編輯前的event)
    private boolean[] group_beaconSelect;//紀錄最初哪些event被選(beacon編輯前event)
    StringBuffer eventId_delete = new StringBuffer();
    StringBuffer eventId_add = new StringBuffer();
    StringBuffer groupId_delete = new StringBuffer();
    StringBuffer groupId_add = new StringBuffer();


    private RecyclerView horizontal_recycler_view_event,horizontal_recycler_view_group;
    private ArrayList<String> horizontalList_event,horizontalList_group;
    private HorizontalAdapter horizontalAdapter_event,horizontalAdapter_group;


    boolean switchMode = false;//紀錄switch是開或關
    Switch alarmSwitch;//switch按鈕
    item_plus_content_rowdata adapter;
    View inflatedView;
    ListView dialog_list;
    Button add_check;

    ArrayList<String> text_listName;

    //ListView event_dialog_listview;//增加event的視窗內的listview
    List<Boolean> listShow;    // 這個用來記錄哪幾個 item 是被打勾的

    String uEmail = "sandy@gmail.com";
    String get_uEmail = "\"sandy@gmail.com\"";

    /* time */
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    public int noti_year,noti_month,noti_day;
    public int noti_hourOfDay,noti_minute;
    /* time end */

    Button buttonDateTimeto,buttonDateTimefrom;
    Button add_notification_check;
    Button notification_content_button;
    EditText notification_content;

    boolean edit=false;
    int dateFlag = 0;//0:not setting, 1:start/from, 2:end/to
    int timeFlag = 0;//0:not setting, 1:start/from, 2:end/to
    int dateFromYear, dateFromMonth, dateFromDay;
    int dateToYear, dateToMonth, dateToDay;
    int timeFromHour, timeFromMin;
    int timeToHour, timeToMin;
    String nStartTime;
    String nEndTime;
    String nContent;
    private ListView listView_eventScroll;
    ListView listViewNotification;
    NotificationAdapter notificationAdapter;
    ArrayList<String> nContent_array = new ArrayList<String>();
    ArrayList<String> nStartTime_array = new ArrayList<String>();
    ArrayList<String> nEndTime_array = new ArrayList<String>();

    ScrollView sv3;
    LinearLayout linearLayout4;
    ConstraintLayout constraintLayout4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_beacon);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**接收從SearchDevice傳過來的變數**/
        Intent intent = this.getIntent();
        Bundle extra = intent.getExtras();
        uEmail = intent.getStringExtra("uEmail");
        bName = intent.getStringExtra("bName");
        macAddress = intent.getStringExtra("macAddress");
        eventName_array = extra.getStringArray("eventName_array");
        eventId_array = extra.getIntArray("eventId_array");
        groupName_array = extra.getStringArray("groupName_array");
        groupId_array = extra.getIntArray("groupId_array");
        /***********/
        get_uEmail = "\""+uEmail+"\"";
        event_select = new boolean[eventName_array.length];//讓event_select和event_array一樣長度
        group_select = new boolean[groupName_array.length];//讓group_select和group_array一樣長度
        event_beaconSelect = new boolean[eventName_array.length];
        group_beaconSelect = new boolean[groupName_array.length];
        //boolean array預設為全填滿false


        textViewMac = (TextView) findViewById(R.id.textViewMac);
        editTextbName = (EditText) findViewById(R.id.editTextbName);
        editTextbContent = (EditText) findViewById(R.id.editTextbContent);
        alarmSwitch = (Switch) findViewById(R.id.switchAlarm);
        editTextMile = (EditText) findViewById(R.id.editTextMile);
        device_pic = (ImageView) findViewById(R.id.device_pic);
        horizontal_recycler_view_event= (RecyclerView) findViewById(R.id.horizontal_recycler_view_event);//event左右滑動的內容
        horizontal_recycler_view_group= (RecyclerView) findViewById(R.id.horizontal_recycler_view_group);//group左右滑動的內容
        editTextMile.setText("0", TextView.BufferType.EDITABLE);
        buttonChangePic = (ImageButton) findViewById(R.id.buttonChangePic);
        buttonChangePic.setOnClickListener(this);

        listViewNotification = (ListView) findViewById(R.id.listViewNotification);
        registerForContextMenu(listViewNotification);//

        sv3 = (ScrollView) findViewById(R.id.sv3);
        linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        constraintLayout4 = (ConstraintLayout) findViewById(R.id.constraintLayout4);


        add_event = (ImageButton)findViewById(R.id.add_event);
        add_group = (ImageButton)findViewById(R.id.add_group);
        add_notification = (ImageButton)findViewById(R.id.add_notification);

        editTextbName.setText(bName);
        textViewMac.setText(macAddress);

        String uri = "@drawable/" + bPic; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        device_pic.setImageResource(imageResource);

        //初始化event灰色標籤列表
        horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
        LinearLayoutManager horizontalLayoutManagaer_event
                = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer_event);

        //初始化group灰色標籤列表
        horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
        LinearLayoutManager horizontalLayoutManagaer_group
                = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer_group);


        getBeacon();
        getBeaconEvent();
        getBeaconGroup();
        getBeaconNotice();

        //指定alarmSwitch的click listener  要放在getBeacon後面
        if (alarmSwitch != null) {
            alarmSwitch.setOnCheckedChangeListener(this);
        }



        /* 新增Event */
        add_event.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                repeat = "";
                if(eventName_array.length == 0){
                    title = "您還沒有創建任何事件";
                }else{
                    title = "選擇事件";
                }
                new AlertDialog.Builder(editBeacon.this)
                        .setTitle(title)
                        .setMultiChoiceItems(
                                eventName_array,
                                event_select,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // TODO Auto-generated method stub

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                horizontalList_event=new ArrayList<>();

                                for (int i = 0; i < event_select.length; i++) {
                                    if (event_select[i]) { //如果選擇的是true(被勾選)
                                        eventIdSelect.append(Integer.toString(eventId_array[i])).append(",");
                                        //連接stringbuffer eventIdSelect(這是一段傳給Php的stringbuffer)

                                        horizontalList_event.add(eventName_array[i]);

                                    }
                                }
                                horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
                                horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer);
                                horizontal_recycler_view_event.setAdapter(horizontalAdapter_event);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();

            }
        });
        /*子庭版本 還在修
       add_event.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(addNewBeacon.this);
                // get inflater and inflate layour for dialogue
                inflatedView = addNewBeacon.this.getLayoutInflater().inflate(R.layout.item_plus_dialog, null);
                // now set layout to dialog
                dialog.setContentView(inflatedView);

                adapter=new item_plus_content_rowdata(addNewBeacon.this,event_array);//顯示的方式
                text_listName = adapter.getSelectedString();//text of list
                dialog_list=(ListView) inflatedView.findViewById(R.id.dialog_list);
                add_check = (Button) inflatedView.findViewById(R.id.add_check);
                add_check.setText("Add Event");
                dialog_list.setAdapter(adapter);
                dialog.setTitle("Add new Event");
                add_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {//把text_listName存進資料庫
                        Toast.makeText(addNewBeacon.this, "Add Event "+text_listName+" successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        */


        /* 新增Group */
        add_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                repeat = "";
                if(groupName_array.length == 0){//如果沒有group
                    title = "您還沒有加入任何群組";
                }else{
                    title = "選擇群組";
                }
                new AlertDialog.Builder(editBeacon.this)
                        .setTitle(title)
                        .setMultiChoiceItems(
                                groupName_array,
                                group_select,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // TODO Auto-generated method stub

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                horizontalList_group=new ArrayList<>();//初始化

                                for (int i = 0; i < group_select.length; i++) {
                                    if (group_select[i]) { //如果選擇的是true(被勾選)
                                        groupIdSelect.append(Integer.toString(groupId_array[i])).append(",");
                                        //連接stringbuffer eventIdSelect(這是一段傳給Php的stringbuffer)                                    }
                                        horizontalList_group.add(groupName_array[i]);

                                    }
                                }

                                horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
                                horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer);
                                horizontal_recycler_view_group.setAdapter(horizontalAdapter_group);


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();


            }
        });

        /* 新增Notification */
        /*add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(editBeacon.this);
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),false);
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
                // first dialog
                dialog.setContentView(R.layout.notification_dialog);


                //second dialog
                DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
                if (dpd != null) {
                    dpd.setOnDateSetListener(editBeacon.this);
                }
                TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
                if (tpd != null) {
                    tpd.setOnTimeSetListener(editBeacon.this);
                }
                buttonDate = (Button) dialog.findViewById(R.id.buttonDate);
                buttonTime = (Button) dialog.findViewById(R.id.buttonTime);
                buttonDate.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.setYearRange(1985, 2028);
                        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                    }
                });
                buttonTime.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                    }
                });



                //set first dialog
                datetext = (TextView) dialog.findViewById(R.id.datetext);
                timetext = (TextView) dialog.findViewById(R.id.timetext);
                add_notification_check = (Button) dialog.findViewById(R.id.add_notification_check);
                add_notification_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(editBeacon.this, "Add Notification successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });*/

        add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_click_claim();
            }
        });
    }

    /* time */
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        boolean y = false;
        switch (dateFlag){
            case 0://nothing happen
                break;
            case 1://click start/from button
                dateFromYear = year; dateFromMonth = month+1; dateFromDay = day;
                dateToYear = year; dateToMonth = month+1; dateToDay = day+1;/*還有一個防呆機制沒有做，就是+1的時候可能換月*/
//                buttonDateTimefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay);//I don't know why month will less one so I add it
//                buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay);
                break;
            case 2://click end/to button
                if(year >= dateFromYear)
                    if((month+1) >= dateFromMonth)
                        if(day>=dateFromDay)
                            y=true;
                if(y){//avoid stupid
                    dateToYear = year; dateToMonth = month+1; dateToDay = day;
//                    buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay);
                }
                break;
            case 3://edit
                dateFromYear = year; dateFromMonth = month+1; dateFromDay = day;
//                buttonDateTimefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay);//I don't know why month will less one so I add it
                break;
            case 4:
                dateToYear = year; dateToMonth = month+1; dateToDay = day;/*還有一個防呆機制沒有做，就是+1的時候可能換月*/
//                buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay);
                break;




        }
        /* 透過這些值放入DB */
        //dateFromYear + "-" + dateFromMonth + "-" + dateFromDay
        //dateToYear + "-" + dateToMonth + "-" + dateToDay

//        Toast.makeText(addNewBeacon.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        boolean y = false;
        switch (timeFlag){
            case 0://nothing happen
                break;
            case 1://click start/from button
                timeFromHour = hourOfDay; timeFromMin = minute;
                timeToHour = hourOfDay+1; timeToMin = minute;/*還有一個防呆機制沒有做，就是+1的時候可能換日*/
                buttonDateTimefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay+"\n"+
                                            timeFromHour + ":" + timeFromMin);
                buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay+"\n"+
                                            timeToHour + ":" + timeToMin);
                break;
            case 2://click end/to button
                if(hourOfDay >= timeFromHour)
                    if(minute>=timeFromMin)
                        y=true;
                if(y){
                    timeToHour = hourOfDay; timeToMin = minute;
                    buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay+"\n"+
                                            timeToHour + ":" + timeToMin);
                }
                break;
            case 3:
                timeFromHour = hourOfDay; timeFromMin = minute;
                buttonDateTimefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay+"\n"+
                                            timeFromHour + ":" + timeFromMin);
                break;
            case 4:
                timeToHour = hourOfDay; timeToMin = minute;/*還有一個防呆機制沒有做，就是+1的時候可能換日*/
                buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay+"\n"+
                                            timeToHour + ":" + timeToMin);
                break;




        }
        /* 透過這些值放入DB */
        //timeFromHour + ":" + timeFromMin
        //timeToHour + ":" + timeToMin

//        Toast.makeText(addNewBeacon.this, "new time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
    }
    /* time end */


    /*notification click*/
    public void notification_click_claim() {
        final Dialog dialog = new Dialog(editBeacon.this);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        // first dialog
        dialog.setContentView(R.layout.notification_dialog);
        buttonDateTimeto = (Button) dialog.findViewById(R.id.buttonDateTimeto);
        buttonDateTimefrom = (Button) dialog.findViewById(R.id.buttonDateTimefrom);
        add_notification_check = (Button) dialog.findViewById(R.id.add_notification_check);
        notification_content = (EditText) dialog.findViewById(R.id.notification_content);

        //second dialog
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
        if (dpd != null) {
            dpd.setOnDateSetListener(editBeacon.this);
        }
        TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
        if (tpd != null) {
            tpd.setOnTimeSetListener(editBeacon.this);
        }


//                buttonDateto.setClickable(false);
//                buttonDateto.setClickable(false);


        if (edit) {
            add_notification_check.setText("更新");//原本是"Add notifiaction"
            notification_content.setText(nContent_array.get(0));
            buttonDateTimefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay+"\n"+
                    timeFromHour + ":" + timeFromMin);//I don't know why month will less one so I add it
            buttonDateTimeto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay+"\n"+
                    timeToHour + ":" + timeToMin);
        }


        buttonDateTimefrom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開完date以後再開time
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
//                        buttonTimeto.setClickable(true);
                if(edit){
                    timeFlag = 3;
                }else{
                    timeFlag = 1;
                }

                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                if(edit){
                    dateFlag = 3;
                }else{
                    dateFlag = 1;
                }

//                        buttonDateto.setClickable(true);

            }
        });
        buttonDateTimeto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開完date以後再開time
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                if(edit){
                    timeFlag = 4;
                }else{
                    timeFlag = 2;
                }

                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                if(edit){
                    dateFlag = 4;
                }else{
                    dateFlag = 2;
                }


            }
        });
//        buttonTimefrom.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        buttonTimeto.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        //按下dialog的新增 或 更新 後
        //set first dialog
        add_notification_check.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //String content = notification_content.getText().toString();
                /*if(dateFlag!=0 && timeFlag!=0 && content!=null){
                    notification_content_button.setText(content+"\n\n"+
                            dateFromYear+"/"+dateFromMonth+"/"+dateFromDay+" ~ "+dateToYear+"/"+dateToMonth+"/"+dateToDay+"\n"+
                            timeFromHour+":"+timeFromMin+" ~ "+timeToHour+":"+timeToMin);

                }*/
                if(dateFlag!=0 && timeFlag!=0) {
                    nContent = notification_content.getText().toString();

                    String dateFromYear_s = Integer.toString(dateFromYear);
                    String dateFromMonth_s = Integer.toString(dateFromMonth);
                    String dateFromDay_s = Integer.toString(dateFromDay);
                    String timeFromHour_s = Integer.toString(timeFromHour);
                    String timeFromMin_s = Integer.toString(timeFromMin);
                    String dateToYear_s = Integer.toString(dateToYear);
                    String dateToMonth_s = Integer.toString(dateToMonth);
                    String dateToDay_s = Integer.toString(dateToDay);
                    String timeToHour_s = Integer.toString(timeToHour);
                    String timeToMin_s = Integer.toString(timeToMin);

                    //檢查如果只有個位數的話 加上"0" 變為二位數 符合datetime格式
                    if(dateFromMonth < 10){
                        dateFromMonth_s = "0" + dateFromMonth;
                    }
                    if(dateFromDay < 10){
                        dateFromDay_s = "0" + dateFromDay;
                    }
                    if(timeFromHour < 10){
                        timeFromHour_s = "0" + timeFromHour;
                    }
                    if(timeFromMin < 10){
                        timeFromMin_s = "0" + timeFromMin;
                    }
                    if(dateToMonth < 10){
                        dateToMonth_s = "0" + dateToMonth;
                    }
                    if(dateToDay < 10){
                        dateToDay_s = "0" + dateToDay;
                    }
                    if(timeToHour < 10){
                        timeToHour_s = "0" + timeToHour;
                    }
                    if(timeToMin < 10){
                        timeToMin_s = "0" + timeToMin;
                    }


                    nStartTime = dateFromYear_s+"-"+dateFromMonth_s+"-"+dateFromDay_s+" "+timeFromHour_s+":"+timeFromMin_s;
                    nEndTime = dateToYear_s+"-"+dateToMonth_s+"-"+dateToDay_s+" "+timeToHour_s+":"+timeToMin_s;

                    //先寫成只能加一個notice 加完第一個後便會變編輯按鈕 而不是增加按鈕
                    //但不確定之後會不會改成多個notice 所以還是保留這些arraylist
                    if (edit) {
                        nContent_array.set(0,nContent);
                        nStartTime_array.set(0,nStartTime);
                        nEndTime_array.set(0,nEndTime);
                    } else {
                        nContent_array.add(0, nContent);
                        nStartTime_array.add(0, nStartTime);
                        nEndTime_array.add(0, nEndTime);
                    }
                    //計算constraintLayout應有的高度
                    int x = nContent_array.size();
                    int height = 150 + 240*x;

                    //重新設定constraintLayout的高度 listview才不會擠成一團
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    constraintLayout4.setLayoutParams(params);

                    //當增加完notification後  把圖示改為edit
                    String uri = "@drawable/" + "edit"; //圖片路徑和名稱
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
                    add_notification.setImageResource(imageResource);

                    //標示目前狀態為編輯
                    edit = true;
                }



                notificationAdapter=new NotificationAdapter(getBaseContext(),nContent_array,nStartTime_array,nEndTime_array);//顯示的方式
                listViewNotification.setAdapter(notificationAdapter);


                dialog.dismiss();

            }
        });



        dialog.show();

    }

    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeacon();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeacon() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                String bContent = jo.getString("bContent");
                String alertMiles = jo.getString("alertMiles");
                String isAlert = jo.getString("isAlert");
                String bPic = jo.getString("bPic");

                editTextbContent.setText(bContent);
                editTextMile.setText(alertMiles);
                editTextMile.setTag(editTextMile.getKeyListener());
                //設定switch
                if(isAlert.equals("1")){
                    alarmSwitch.setChecked(true);

                }else{
                    alarmSwitch.setChecked(false);
                }

                //設定圖片
                String uri = "@drawable/" + bPic; //圖片路徑和名稱
                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
                device_pic.setImageResource(imageResource);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBeaconEvent(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeaconEvent();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_EVENT,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeaconEvent() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            eventId_beacon = new int[result.length()];
            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                int cId = jo.getInt("cId");
                eventId_beacon[i] = cId;
            }

            horizontalList_event=new ArrayList<>();

            //如果user擁有的(全部)event ID 和 beacon 的 ID 相同  這個event就是被select(true)
            for(int x = 0 ; x < eventId_array.length ; x++){
                for(int y = 0 ; y < eventId_beacon.length ; y++){
                    if(eventId_array[x] == eventId_beacon[y]){
                        event_select[x] = true;//紀錄當前beacon event誰被選了
                        horizontalList_event.add(eventName_array[x]);
                        event_beaconSelect[x] = true;//紀錄beacon編輯前  event誰被選了
                    }
                }
            }

            horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer);

            horizontal_recycler_view_event.setAdapter(horizontalAdapter_event);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBeaconGroup(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeaconGroup();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_GROUP,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeaconGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            groupId_beacon = new int[result.length()];
            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                int gId = jo.getInt("gId");
                groupId_beacon[i] = gId;
            }

            horizontalList_group=new ArrayList<>();

            //如果user擁有的(全部)event ID 和 beacon 的 ID 相同  這個event就是被select(true)
            for(int x = 0 ; x < groupId_array.length ; x++){
                for(int y = 0 ; y < groupId_beacon.length ; y++){
                    if(groupId_array[x] == groupId_beacon[y]){
                        group_select[x] = true;
                        horizontalList_group.add(groupName_array[x]);
                        group_beaconSelect[x] = true;
                    }
                }
            }

            horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer);
            horizontal_recycler_view_group.setAdapter(horizontalAdapter_group);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getBeaconNotice(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                if(JSON_STRING.isEmpty()){
                    //沒有notice 編輯模式false
                    edit = false;

                }else{
                    //有notcie 編輯模式true
                    edit = true;
                    showBeaconNotice();

                }


            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_NOTICE,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeaconNotice() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                int nId = jo.getInt("nId");
                nStartTime = jo.getString("nStartTime");
                nEndTime = jo.getString("nEndTime");
                nContent = jo.getString("nContent");

                DateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm");//設定datetime格式 去掉後面的:00(:ss)
                DateFormat year = new SimpleDateFormat("yyyy");
                DateFormat month = new SimpleDateFormat("MM");
                DateFormat date = new SimpleDateFormat("dd");
                DateFormat hour = new SimpleDateFormat("HH");
                DateFormat min = new SimpleDateFormat("mm");

                //先將資料庫裡的string轉為datetime格式的date物件
                Date start = datetime.parse(nStartTime);
                Date end = datetime.parse(nEndTime);
                nStartTime = datetime.format(start);
                nEndTime = datetime.format(end);

                /****convert nStartTime*****/
                dateFromYear = Integer.parseInt(year.format(start));
                //將date物件的start 以year格式取得yyyy 再從string轉為int
                dateFromMonth = Integer.parseInt(month.format(start));
                dateFromDay = Integer.parseInt(date.format(start));
                timeFromHour = Integer.parseInt(hour.format(start));
                timeFromMin = Integer.parseInt(min.format(start));

                /****convert nEndTime*****/
                dateToYear = Integer.parseInt(year.format(end));
                //將date物件的start 以year格式取得yyyy 再從string轉為int
                dateToMonth = Integer.parseInt(month.format(end));
                dateToDay = Integer.parseInt(date.format(end));
                timeToHour = Integer.parseInt(hour.format(end));
                timeToMin = Integer.parseInt(min.format(end));



                //nStartTime = dateFromYear+"-"+dateFromMonth+"-"+dateFromDay+" "+timeFromHour+":"+timeFromMin;
                //nEndTime = dateToYear+"-"+dateToMonth+"-"+dateToDay+" "+timeToHour+":"+timeToMin;

                //設定卡片
                nContent_array.add(0, nContent);
                nStartTime_array.add(0,nStartTime);
                nEndTime_array.add(0, nEndTime);

                //計算constraintLayout應有的高度
                int x = nContent_array.size();
                int height = 150 + 240*x;

                //重新設定constraintLayout的高度 listview才不會擠成一團
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                constraintLayout4.setLayoutParams(params);

                //當增加完notification後  把圖示改為edit
                String uri = "@drawable/" + "edit"; //圖片路徑和名稱
                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
                add_notification.setImageResource(imageResource);

                //標示目前狀態為編輯
                edit = true;

                notificationAdapter=new NotificationAdapter(getBaseContext(),nContent_array,nStartTime_array,nEndTime_array);//顯示的方式
                listViewNotification.setAdapter(notificationAdapter);


            }





        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void updateBeacon(){

        final String bName = editTextbName.getText().toString().trim();
        final String bContent= editTextbContent.getText().toString().trim();
        final String alertMiles = editTextMile.getText().toString().trim();

        for(int i = 0 ; i < eventId_array.length ; i++){
            if(event_beaconSelect[i] && !event_select[i]){//原本為true(勾選) 更新後為false(不勾選)
                //要被刪除的紀錄
                eventId_delete.append(Integer.toString(eventId_array[i])).append(",");
            }else if(!event_beaconSelect[i] && event_select[i]){//原本為false(不勾選) 更新後為true(勾選)
                //要被增加的紀錄
                eventId_add.append(Integer.toString(eventId_array[i])).append(",");
            }else{
                //編輯前後狀態都一樣 就不用動作
            }
        }

        for(int i = 0 ; i < groupId_array.length ; i++){
            if(group_beaconSelect[i] && !group_select[i]){//原本為true(勾選) 更新後為false(不勾選)
                //要被刪除的紀錄
                groupId_delete.append(Integer.toString(groupId_array[i])).append(",");
            }else if(!group_beaconSelect[i] && group_select[i]){//原本為false(不勾選) 更新後為true(勾選)
                //要被增加的紀錄
                groupId_add.append(Integer.toString(groupId_array[i])).append(",");
            }else{
                //編輯前後狀態都一樣 就不用動作
            }
        }


        class UpdateBeacon extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(editBeacon.this,s, Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("macAddress",macAddress);
                hashMap.put("bName",bName);
                hashMap.put("bContent",bContent);
                hashMap.put("alertMiles",alertMiles);

                if (switchMode) {
                    //如果switch開
                    hashMap.put("isAlert","1");
                }else{
                    //如果switch關
                    hashMap.put("isAlert","0");
                }
                hashMap.put("bPic",bPic);
                hashMap.put("eventId_delete",eventId_delete.toString());
                hashMap.put("eventId_add",eventId_add.toString());
                hashMap.put("groupId_delete",groupId_delete.toString());
                hashMap.put("groupId_add",groupId_add.toString());

                hashMap.put("nContent",nContent_array.get(0));
                hashMap.put("nStartTime",nStartTime_array.get(0));
                hashMap.put("nEndTime",nEndTime_array.get(0));


                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_BEACON,hashMap);

                return s;
            }
        }

        UpdateBeacon ue = new UpdateBeacon();
        ue.execute();
    }



    @Override
    public void onClick(View v) {
        if(v == buttonChangePic){

            Intent intent = new Intent();
            intent.setClass(editBeacon.this, ChangePic.class);
            startActivityForResult(intent, resultNum);
        }
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

            updateBeacon();
            //執行更新beacon
            /* 切回到原本的畫面 */
            Intent intent = new Intent();
            intent.setClass(editBeacon.this, MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                bPic = data.getExtras().getString(ChangePic.FLAG);//從changPic得到的值(圖片名稱)

                String uri = "@drawable/" + bPic; //圖片路徑和名稱

                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

                device_pic.setImageResource(imageResource);

            }
        }
    }

    //switchAlarm 開或關的動作
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
        if(isChecked) {
            //do stuff when Switch is ON
            //設定讓editTextMile可以編輯
            editTextMile.setKeyListener((KeyListener) editTextMile.getTag());
            editTextMile.setTextColor(0xff000000);//設定editTextMile為黑色
            switchMode = true;
        } else {
            //do stuff when Switch if OFF
            //設定讓editTextMile不能編輯
            editTextMile.setTag(editTextMile.getKeyListener());
            editTextMile.setKeyListener(null);
            editTextMile.setTextColor(0xff808080);//設定editTextMile為灰色
            switchMode = false;
            Toast.makeText(this,"若要再次編輯警報距離，請開啟警報模式",
                    Toast.LENGTH_SHORT).show();

        }
    }




}
