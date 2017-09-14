package com.example.ccdez.news314;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<Bean.Second.Third> list;
    private OkHttpClient client = new OkHttpClient();
    private ListView news_list;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            list = (List<Bean.Second.Third>) msg.obj;
            news_list.setAdapter(new NewsListAdapter(list, MainActivity.this));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        news_list = (ListView) findViewById(R.id.list_item);
        //判断网络状态
        if (!NetworkDetect.isNetworkConnected(this)) {
            Toast.makeText(this, "网络连接不可用", Toast.LENGTH_SHORT).show();
        }

        openNews();

        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                String url = NewsListAdapter.list.get(position).url;
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });



    }

    private void openNews(){

        final Gson gson = new Gson();
        final Request request = new Request.Builder().get()
                .url("http://v.juhe.cn/toutiao/index?type=keji&key=65d4c89f2460e131bd8b288f3f70bff6")
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String content = response.body().string();
                        Bean bean = gson.fromJson(content, Bean.class);
                        Bean.Second second = bean.result;

                        list = second.data;

                        Message message = handler.obtainMessage();
                        message.obj = list;
                        handler.sendMessage(message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}