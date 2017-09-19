package com.example.emily.beaconside;

/**
 * Created by user on 2017/8/8.
 */

public class Config{

    //各種功能的網址
    public static final String URL_ADD="http://140.117.71.114/employee/addEmp.php";
    public static final String URL_GET_ALL = "http://140.117.71.114/employee/getAllEmp.php";
    public static final String URL_GET_EMP = "http://140.117.71.114/employee/getEmp.php?id=";
    public static final String URL_UPDATE_EMP = "http://140.117.71.114/employee/updateEmp.php";
    public static final String URL_DELETE_EMP = "http://140.117.71.114/employee/deleteEmp.php?id=";
    public static final String URL_GET_BEACON_DATABASE = "http://140.117.71.114/beacon/getBeaconDatabase.php";
    public static final String URL_GET_ALL_BEACON = "http://140.117.71.114/beacon/getAllBeacon.php?uEmail=";
    public static final String URL_GET_BEACON = "http://140.117.71.114/beacon/getBeacon.php?macAddress=";
    public static final String URL_GET_BEACON_EVENT = "http://140.117.71.114/beacon/getBeaconCategory.php?macAddress=";
    public static final String URL_GET_BEACON_GROUP = "http://140.117.71.114/beacon/getBeaconGroup.php?macAddress=";
    public static final String URL_GET_NOTICE = "http://140.117.71.114/beacon/getNotice.php?macAddress=";
    public static final String URL_ADD_BEACON="http://140.117.71.114/beacon/addBeacon.php";
    public static final String URL_GET_USER_DATABASE = "http://140.117.71.114/beacon/getUserDatabase.php";
    public static final String URL_GET_USER_EVENT="http://140.117.71.114/beacon/getUserCategory.php?uEmail=";
    public static final String URL_GET_USER_GROUP="http://140.117.71.114/beacon/getUserGroup.php?uEmail=";
    public static final String URL_ADD_USER = "http://140.117.71.114/beacon/addUser.php";
    public static final String URL_SEARCH = "http://140.117.71.114/beacon/search.php";
    public static final String URL_UPDATE_BEACON="http://140.117.71.114/beacon/updateBeacon.php";
    public static final String URL_DELETE_BEACON="http://140.117.71.114/beacon/deleteBeacon.php?macAddress=";
    public static final String URL_CREATE_GROUP = "http://140.117.71.114/beacon/createGroup.php";


    //Keys that will be used to send the request to php scripts
//    public static final String KEY_EMP_ID = "id";
//    public static final String KEY_EMP_NAME = "name";
//    public static final String KEY_EMP_DESG = "desg";
//    public static final String KEY_EMP_SAL = "salary";
    public static final String KEY_MAC = "macAdress";
    public static final String KEY_ACC = "account";

    //JSON Tags
    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_MAC = "macAddress";
    public static final String TAG_ACC = "account";
    public static final String TAG_bName = "bName";
    //employee id to pass with intent
    //public static final String EMP_ID = "emp_id";
    public static final String BEA_MAC = "bea_mac";
}
