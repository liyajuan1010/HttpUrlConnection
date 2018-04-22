package com.example.dell.httpurlconnection;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//get方式要在manifest中 添加使用权限
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnGet;
    private TextView tvShowMsg;
    private static final String TAG="MainActivity";
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();
    }
    private void initView() {
        btnGet=(Button)findViewById(R.id.btn_get);
        tvShowMsg=(TextView)findViewById(R.id.tv_show_message);
    }

    private void init(){
        btnGet.setOnClickListener(this);

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                String strRet=bundle.getString("key");
                tvShowMsg.setText(strRet);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_get:
                requestGet();
                break;
        }
    }

    private void requestGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection=null;
                try {
                    URL url=new URL("http://10.0.2.2/get.php");
//                    URL url=new URL("http://10.0.2.2/get.php?key=my php");
                    //建立与URL连接
                    urlConnection=(HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();//连接服务器
                    if(urlConnection.getResponseCode()==200){//判断返回状态码
                        InputStream is=urlConnection.getInputStream(); //获取返回的InputStream
                        String strRet=streamToString(is); //把获取到的数据流转换成String格式

                        //Handler的最基本用法是子线程和主线程之间的通讯，将耗时的操作放在子线程中进行，
                        // 将操作后的结果或者数据通过Handler传递给UI线程，UI再通过这些数据更新UI和进行相应的用户操作
                        Message msg=mHandler.obtainMessage(); //创建Message对象
                        Bundle bundle=new Bundle();
                        bundle.putString("key",strRet);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                        Log.i(TAG,strRet);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();

    }

    private String streamToString(InputStream is) {
        StringBuilder sBuilder=new StringBuilder();
        byte[] buffer=new byte[512];
        int hasRead=-1;
        try {
            while ((hasRead=is.read(buffer))!=-1){
                sBuilder.append(new String(buffer,0,hasRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sBuilder.toString();
        }
    }

}
