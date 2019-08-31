package com.dachung.chatdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dachung.chatdemo.util.NetworkUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

/*
    做个简单的聊天室软件
 */

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    TextView content;
    EditText msgOutput;
    Button msgSend;

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;

    private static final boolean CONNECTED_FLAG = false;
    private static final int CONNECT_SUCCESS = 1;
    private static final int SHOW_LOCALADDRESS = 2;
    private static final int UPDATE_MSG = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECT_SUCCESS:
                    content.append("\n连接成功");
                    break;
                case SHOW_LOCALADDRESS:
                    content.setText((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    content.append("\n"+(String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        getConnect();
    }

    //初始化控件
    public void initView(){
        content = (TextView) findViewById(R.id.content);
        msgOutput = (EditText) findViewById(R.id.msg_output);
        msgSend = (Button) findViewById(R.id.msg_send);

        msgSend.setOnClickListener(this);
        showIp();
    }


    //show出本机ip
    private void showIp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    String localname = inetAddress.getHostName();
                    String localip = NetworkUtil.getLocalIpV4Address();
                    Message message = new Message();
                    message.what = SHOW_LOCALADDRESS;
                    message.obj = "本机名：" + localname + "\n" + "本机ip：" + localip;
                    handler.sendMessage(message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //网络连接
    public void getConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("192.168.191.1",7777);
                    writer = new PrintWriter(socket.getOutputStream(),true);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    if(writer!=null && reader != null){
                        Message msg = new Message();
                        msg.what = CONNECT_SUCCESS;
                        handler.sendMessage(msg);
                        System.out.println("OK");
                    }

                    while(true){
                        Message msg = new Message();
                        msg.obj = "服务端："+reader.readLine();
                        msg.what = UPDATE_MSG;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sentMsgToServer(final String txt){
        new Thread(new Runnable() {
            @Override
            public void run() {
                writer.println(txt);
            }
        }).start();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.msg_send:{
                try{
                    String txt = msgOutput.getText().toString();
                    sentMsgToServer(txt);
                    Message msg = new Message();
                    msg.what = UPDATE_MSG;
                    msg.obj = "客户端："+txt;
                    handler.sendMessage(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy(){
        try{
            if(writer != null)
                writer.close();
            if (reader != null)
                reader.close();
            if (socket != null)
                socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
