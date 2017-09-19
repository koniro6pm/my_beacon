package com.example.emily.beaconside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class device_rowdata extends BaseAdapter {
    private final Context context;
    private final String[] value_deviceName;
    private final String[] value_deviceDsc;

    private LayoutInflater mInflater;

    public device_rowdata(Context context, String[] values, String[] values2) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_deviceName = values;
        this.value_deviceDsc = values2;
    }

    @Override
    public int getCount() {//算device Name長度
        return value_deviceName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;//緩存
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.device_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();

            holder.beaconImage = (ImageView) convertView
                    .findViewById(R.id.deviceImage);
            holder.beaconName = (TextView) convertView
                    .findViewById(R.id.deviceName);
            holder.beaconAddress = (TextView) convertView
                    .findViewById(R.id.deviceAddress);


            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.beaconImage.setImageResource(R.drawable.beacon);
        holder.beaconName.setText(value_deviceName[position]);
        holder.beaconAddress.setText(value_deviceDsc[position]);
        return convertView;
    }

    public String getName(){
        ViewHolder holder = new ViewHolder();;//緩存
        return holder.beaconName.getText().toString();
    }

    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        ImageView beaconImage;
        TextView beaconName;
        TextView beaconAddress;
    }

}