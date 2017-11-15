package com.example.emily.beaconside;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import java.util.ArrayList;
import android.widget.LinearLayout;
public class main_side_event_rowdata extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> event_name;
    private final ArrayList<String> event_cId;
    private final ArrayList<String> event_cPic;
    private LayoutInflater mInflater;

    private int[] image = {
            R.drawable.group_pic1_s, R.drawable.group_pic2_s, R.drawable.group_pic3_s, R.drawable.group_pic3_s, R.drawable.group_pic4_s, R.drawable.group_pic5_s, R.drawable.group_pic6_s, R.drawable.group_pic7_s, R.drawable.group_pic8_s
    };
    private String[] imgText = new String[]{"group_pic1", "group_pic2", "group_pic3", "group_pic4", "group_pic5","group_pic6","group_pic7","group_pic8"};


    public main_side_event_rowdata(Context context, ArrayList<String> name,ArrayList<String> cId,ArrayList<String> pic) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.event_name = name;
        this.event_cId = cId;
        this.event_cPic = pic;
    }


    @Override
    public int getCount() {//算device Name長度
        return event_name.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;//緩存
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.activity_main_side_event_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();

            holder.event_side_bt = (Button) convertView.findViewById(R.id.event_side_bt);
            holder.event_side_bt.setText(event_name.get(position));
            String p =event_cPic.get(position);
            for(int i = 0; i < imgText.length; i++){
                if (p.equals(imgText[i])){
                    holder.event_side_bt.setBackgroundResource(image[i]);
                }
            }

            holder.event_side_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();

                    bundle.putString("message", event_cId.get(position));
                    bundle.putString("eventTitle",event_name.get(position));
                    bundle.putString("eventPic",event_cPic.get(position));
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(context, event_beacons.class);
                    context.startActivity(intent);

                }


            });


            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        // Set height and width and gravity
        holder.event_side_bt.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        holder.event_side_bt.setGravity(Gravity.CENTER);
        return convertView;
    }



    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        Button event_side_bt;
    }

}
