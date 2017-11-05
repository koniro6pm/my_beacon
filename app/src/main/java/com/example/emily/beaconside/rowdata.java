package com.example.emily.beaconside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class rowdata extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> value_deviceName;
    private final ArrayList<String> value_deviceDsc;
    private final ArrayList<String> value_address;
    private boolean isLoading;

    private final ArrayList<String> value_bPic;

    private LayoutInflater mInflater;

    public rowdata(Context context, ArrayList<String> name, ArrayList<String> distance, ArrayList<String> address, ArrayList<String> value_bPic, boolean isLoading) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_deviceName = name;
        this.value_deviceDsc = distance;
        this.value_address = address;
        this.isLoading = isLoading;
        this.value_bPic = value_bPic;
    }


    @Override
    public int getCount() {//算device Name長度
        return value_deviceName.size();
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
            convertView=mInflater.inflate(R.layout.activity_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();
            holder.beaconImage = (ImageView) convertView
                    .findViewById(R.id.beaconImage);
            holder.beaconName = (TextView) convertView
                    .findViewById(R.id.beaconName);
            holder.beaconDistance = (TextView) convertView
                    .findViewById(R.id.beaconDistance);
            holder.beaconAddress = (TextView) convertView.findViewById(R.id.beaconAddress);
            holder.beaconAddress = (TextView) convertView.findViewById(R.id.beaconAddress);
            holder.spinner = (ProgressBar) convertView.findViewById(R.id.progressBar);

            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(value_deviceDsc.size() < 1) {
            for (int i = 0; i < value_deviceName.size(); i++) {
                value_deviceDsc.add("Out of Range");
            }
        }
        //根據value_bPic array來顯示圖片
//        Toast.makeText(context,"distance"+value_deviceDsc,Toast.LENGTH_SHORT).show();
        String bPic = value_bPic.get(position);
        int resID = context.getResources().getIdentifier(bPic, "drawable","com.example.emily.beaconside");
        holder.beaconImage.setImageResource(resID);
        holder.beaconName.setText(value_deviceName.get(position));
        if(value_deviceDsc.get(position).equals("Out of Range")){
            holder.beaconDistance.setText(value_deviceDsc.get(position));
        }else{
            holder.beaconDistance.setText(value_deviceDsc.get(position)+" M");

        }
        holder.beaconAddress.setText(value_address.get(position));


        if(isLoading) {
            holder.spinner.setVisibility(View.VISIBLE);
            holder.beaconDistance.setVisibility(View.GONE);
        }
        else
            holder.spinner.setVisibility(View.GONE);
        return convertView;
    }



    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        ImageView beaconImage;
        TextView beaconName;
        TextView beaconDistance;
        TextView beaconAddress;
        TextView beaconNearby;
        ImageButton item_setting;
        ProgressBar spinner;
    }
}
