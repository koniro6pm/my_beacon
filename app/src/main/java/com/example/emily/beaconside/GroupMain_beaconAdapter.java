package com.example.emily.beaconside;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GroupMain_beaconAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> value_deviceName;
    private final ArrayList<String> value_deviceDsc;
    private final ArrayList<String> value_address;
    private final ArrayList<String> value_avatar;
    private boolean isLoading;

    private final ArrayList<String> value_bPic;

    private LayoutInflater mInflater;

    public GroupMain_beaconAdapter(Context context, ArrayList<String> name, ArrayList<String> distance, ArrayList<String> address, ArrayList<String> value_bPic, ArrayList<String> avatar,boolean isLoading) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_deviceName = name;
        this.value_deviceDsc = distance;
        this.value_address = address;
        this.isLoading = isLoading;
        this.value_bPic = value_bPic;
        this.value_avatar = avatar;
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
            convertView=mInflater.inflate(R.layout.group_main_beacon_list,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();
            holder.beaconImage = (ImageView) convertView
                    .findViewById(R.id.beaconImage);
            holder.beaconName = (TextView) convertView
                    .findViewById(R.id.beaconName);
            holder.beaconDistance = (TextView) convertView
                    .findViewById(R.id.beaconDistance);
            holder.beaconAddress = (TextView) convertView.findViewById(R.id.beaconAddress);
            holder.spinner = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.imageView_avatar = (ImageView)convertView.findViewById(R.id.imageView_avatar);

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
        holder.beaconDistance.setText(value_deviceDsc.get(position));
        holder.beaconAddress.setText(value_address.get(position));
        String avatar_url = value_avatar.get(position);

        final ViewHolder finalHolder = holder;

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
                finalHolder.imageView_avatar.setImageBitmap (bmp);
                super.onPostExecute(result);
            }
        }.execute(avatar_url);

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
        ImageView imageView_avatar;
        TextView beaconNearby;
        ImageButton item_setting;
        ProgressBar spinner;
    }

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
        int width =100;
        int height = 100;
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
