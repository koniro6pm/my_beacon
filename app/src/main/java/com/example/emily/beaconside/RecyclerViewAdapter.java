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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2017/11/1.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> uName_list;//群組會員名字
    private ArrayList<String> uId_list;//群組會員id
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> uId_list,ArrayList<String> uName_list) {
        this.context = context;
        this.uName_list = uName_list;
        this.uId_list = uId_list;
    }

    //建立View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_member_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    //item點擊的監聽介面
    public interface OnItemClikListener{
        void onItemClik(View view,int position);
        void onItemLongClik(View view,int position);
    }
    private OnItemClikListener mOnItemClikListener;
    //對外設置item點擊暴露的方法
    public void setItemClikListener(OnItemClikListener mOnItemClikListener ){
        this.mOnItemClikListener=mOnItemClikListener;
    }

    //數據的綁定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.textView_uName.setText(uName_list.get(position));
        String avatar_url = "https://graph.facebook.com/"+uId_list.get(position)+"/picture?type=small";

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
                holder.imageView_uPic.setImageBitmap (bmp);
                super.onPostExecute(result);
            }
        }.execute(avatar_url);

//對外調用了方法
        if(mOnItemClikListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    mOnItemClikListener.onItemClik(holder.itemView,pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=holder.getLayoutPosition();
                    mOnItemClikListener.onItemLongClik(holder.itemView,pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return uId_list.size();
    }
    //自定義ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView_uName;
        public final ImageView imageView_uPic;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_uName = (TextView) itemView.findViewById(R.id.textView_uName);
            imageView_uPic = (ImageView) itemView.findViewById(R.id.imageView_uPic);
        }
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
