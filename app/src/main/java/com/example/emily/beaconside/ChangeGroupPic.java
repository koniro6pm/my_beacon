package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeGroupPic extends AppCompatActivity {

    public static final String FLAG = "BACK_STRING";

    private GridView gridView;
    private int[] image = {
            R.drawable.group_pic1_s, R.drawable.group_pic2_s, R.drawable.group_pic3_s, R.drawable.group_pic4_s, R.drawable.group_pic5_s, R.drawable.group_pic6_s, R.drawable.group_pic7_s, R.drawable.group_pic8_s
    };
    private String[] imgText = {
            "group_pic1", "group_pic2", "group_pic3", "group_pic4", "group_pic5","group_pic6","group_pic7","group_pic8"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_pic);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("image", image[i]);
            //item.put("text", imgText[i]);
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,
                items, R.layout.group_pic_item, new String[]{"image"},
                new int[]{R.id.image});
        gridView = (GridView) findViewById(R.id.pic_grid);
        gridView.setNumColumns(2);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ChangePic.this, "你選擇了" + imgText[position], Toast.LENGTH_SHORT).show();

                /*Intent intent = new Intent();
                intent.setClass(ChangePic.this,MainActivity.class);
                intent.putExtra("sayHi",imgText[position]);//試著傳值
                finish();
                startActivity(intent);*/

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString(FLAG, imgText[position]);
                intent.putExtras(b);
                ChangeGroupPic.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
