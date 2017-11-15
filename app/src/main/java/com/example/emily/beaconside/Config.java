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
    public static final String URL_GET_NOTICE = "http://140.117.71.114/beacon/getBeaconNotice.php?macAddress=";
    public static final String URL_ADD_BEACON="http://140.117.71.114/beacon/addBeacon.php";
    public static final String URL_GET_USER_DATABASE = "http://140.117.71.114/beacon/getUserDatabase.php";
    public static final String URL_GET_USER_EVENT="http://140.117.71.114/beacon/getUserCategory.php?uEmail=";
    public static final String URL_GET_USER_GROUP="http://140.117.71.114/beacon/getUserGroup.php?uEmail=";
    public static final String URL_GET_USER_INFO="http://140.117.71.114/beacon/getUserInfo.php?uEmail=";
    public static final String URL_ADD_USER = "http://140.117.71.114/beacon/addUser.php";
    public static final String URL_SEARCH = "http://140.117.71.114/beacon/search.php";
    public static final String URL_UPDATE_BEACON="http://140.117.71.114/beacon/updateBeacon.php";
    public static final String URL_UPDATE_BEACON_LOCATION="http://140.117.71.114/beacon/updateBeaconLocation.php";
    public static final String URL_UPDATE_BEACON_DISTANCE="http://140.117.71.114/beacon/updateBeaconDistance.php";
    public static final String URL_DELETE_BEACON="http://140.117.71.114/beacon/deleteBeacon.php?macAddress=";
    public static final String URL_CREATE_GROUP = "http://140.117.71.114/beacon/createGroup.php";
    public static final String URL_CREATE_EVENT = "http://140.117.71.114/beacon/createEvent.php";
    public static final String URL_ADD_BEACON_TO_GROUP = "http://140.117.71.114/beacon/addBeaconToGroup.php";
    public static final String URL_GET_GROUPBeacon = "http://140.117.71.114/beacon/getGroupBeacon_1.php?gId=";
    public static final String URL_DELETE_GROUP= "http://140.117.71.114/beacon/deleteGroup.php?gId=";
    public static final String URL_EXIT_GROUP= "http://140.117.71.114/beacon/exitGroup.php?gId=";
    public static final String URL_UPDATE_GROUP_NAME= "http://140.117.71.114/beacon/updateGroupName.php?gId=";
    public static final String URL_UPDATE_GROUP_PHOTO= "http://140.117.71.114/beacon/exitGroup.php?gId=";
    public static final String URL_GET_BEACON_FROM_EVENT = "http://140.117.71.114/beacon/getBeaconFromEvent.php?cId=";
    public static final String URL_DELETE_BEACON_EVENT="http://140.117.71.114/beacon/deleteBeaconEvent.php?temp=";
    public static final String URL_GET_GROUP_MEMBER="http://140.117.71.114/beacon/getGroupMember.php?gId=";
    public static final String URL_EDIT_GROUP_MEMBER="http://140.117.71.114/beacon/editGroupMember.php?gId=";
    public static final String URL_GET_BEACON_EXCEPT_GROUP="http://140.117.71.114/beacon/showBeaconNotInGroup.php?gId=";
    public static final String URL_EDIT_GROUP_BEACON="http://140.117.71.114/beacon/editGroupBeacon.php";

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
