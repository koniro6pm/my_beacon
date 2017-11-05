package com.example.emily.beaconside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.emily.beaconside.R.id.bName;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class checkitem_rowdata extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> bImage_list;
    private final ArrayList<String>  bName_list;
    private final ArrayList<String>  bStatus_list;
    private LayoutInflater mInflater;
    private final boolean isForget;

    public checkitem_rowdata(Context context, ArrayList<String>  bImage_list, ArrayList<String>  bName_list, ArrayList<String>  bStatus_list, boolean isForget) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.bImage_list = bImage_list;
        this.bName_list = bName_list;
        this.bStatus_list = bStatus_list;
        this.isForget = isForget;
    }


    @Override
    public int getCount() {//算device Name長度
        return bName_list.size();
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
            convertView=mInflater.inflate(R.layout.checkitem_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();
//            Toast.makeText(context, bName_list.get(2), Toast.LENGTH_SHORT).show();

            holder.bImage = (ImageView) convertView
                    .findViewById(R.id.bImage);
            holder.bName = (TextView) convertView
                    .findViewById(bName);
            holder.bStatus = (TextView) convertView
                    .findViewById(R.id.bStatus);
            holder.row = (LinearLayout) convertView
                    .findViewById(R.id.row_container);
            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(bStatus_list.size() < 1) {
            for (int i = 0; i < bName_list.size(); i++) {
                bStatus_list.add("Out of Range");
            }
        }

        double d = tryParse(bStatus_list.get(position));
        if(isForget) {
            if(d < 0.5)
                invisibleRow(holder);
            else
                displayRow(holder,position);
        }
        else{
            if(d < 0.5)
                displayRow(holder,position);
            else
                invisibleRow(holder);
        }



        return convertView;
    }

    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        ImageView bImage;
        TextView bName;
        TextView bStatus;
        LinearLayout row;
    }

    public static double tryParse(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

    private void displayRow(ViewHolder holder, int position) {
        String bPic = bImage_list.get(position);
        int resID = context.getResources().getIdentifier(bPic, "drawable", "com.example.emily.beaconside");
        holder.bImage.setImageResource(resID);
        holder.bName.setText(bName_list.get(position));
        holder.bStatus.setText(bStatus_list.get(position));
        holder.row.setVisibility(View.VISIBLE);
    }

    private void invisibleRow(ViewHolder holder) {
        holder.bImage.setVisibility(View.GONE);
        holder.bName.setVisibility(View.GONE);
        holder.bStatus.setVisibility(View.GONE);
        holder.row.setVisibility(View.GONE);
    }
}