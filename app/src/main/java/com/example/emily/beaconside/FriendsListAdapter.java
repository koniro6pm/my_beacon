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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2017/9/3.
 */

public class FriendsListAdapter extends BaseAdapter{

    private LayoutInflater mLayInf;
    ArrayList<HashMap<String, Object>> mItemList;
    List<Boolean> mChecked;
//    List<Person> listPerson;
    HashMap<Integer,View> map = new HashMap<Integer,View>();





    public FriendsListAdapter(Context context, ArrayList<HashMap<String, Object>> itemList)
    {
        mChecked = new ArrayList<Boolean>();
        for(int i=0;i<itemList.size();i++){
            mChecked.add(false);
        }
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = itemList;
    }

    @Override
    public int getCount()
    {
        //取得 ListView 列表 Item 的數量
        return mItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItemList.get(position).get("friendsId");
        //取得 ListView 列表於 position 位置上的 Item

    }

    @Override
    public long getItemId(int position)
    {
        //取得 ListView 列表於 position 位置上的 Item 的 ID
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        //設定與回傳 convertView 作為顯示在這個 position 位置的 Item 的 View。
        View v = mLayInf.inflate(R.layout.listview, parent, false);
        ViewHolder holder = null;
        holder = new ViewHolder();
        holder.selected = (CheckBox) v.findViewById(R.id.MyAdapter_CheckBox_checkBox);
        holder.name = (TextView)v.findViewById(R.id.name);
        holder.imgView = (ImageView)v.findViewById(R.id.pic);

        final int p = position;
        map.put(position, v);
        holder.selected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                mChecked.set(p, cb.isChecked());
            }
        });
        v.setTag(holder);


        final String url1 = (String) mItemList.get(position).get("pic");
        final ViewHolder finalHolder = holder;

        holder.name.setText(mItemList.get(position).get("name").toString());
        holder.selected.setChecked(mChecked.get(position));

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
                finalHolder.imgView.setImageBitmap (bmp);
                super.onPostExecute(result);
            }
        }.execute(url1);

        return v;
    }
    class ViewHolder{
        CheckBox selected;
        TextView name;
        ImageView imgView;
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
