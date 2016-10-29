package com.duksung.autoledlamp.Activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.duksung.autoledlamp.R;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageButton button1,button2,button3,button4,button5,button6;
    private ImageView icon1,icon2,icon3,icon4,icon5,icon6;
    private TextView textView_timer;
    CountDownTimer mCountDown = null;

    // usb permission
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";

    // 시간
    private String strPrevNow2 = "first";

    // gsr
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    StringBuffer buffer = new StringBuffer(4);
    String[] result = null;
    String result2 = null;

    // led number
    private String ledid;
    int real_personid;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null; // 아두이노에서 가져오는 data
            try {
                data = new String(arg0, "UTF-8");
                //data.concat("/n");

                // 현재 시간 알아내기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String s = sdfNow.format(date);

                if(!strPrevNow2.equals(s)) {
                    strPrevNow2 = s;
                }

                if(data != null){
                    buffer.append(data); // 아두이노에서 가져온 값을 buffer에 차례로 임시 저장
                    for(int k = 0 ; k < data.length(); k++) {
                        if (data.charAt(k) == '*') { // 아두이노 한줄의 끝에 *을 붙여 가져온다
                            String temp = buffer.toString();
                            //result = temp.split("/"); // /로 끊어서 각 값을 구분 -> GSR만 가져오면 필요 없음
                            result2 = buffer.toString();

                            // GSRinsertToDatabase(Integer.toString(real_person_conditionid), s, result[0]);
                            //insertToDatabase(Integer.toString(real_personid),result[0],ledid);
                            insertToDatabase(Integer.toString(real_personid),result2,ledid);
                            System.out.println("result" + result2);
                            //System.out.println("result1" + result[1]);
                            //System.out.println("result2" + result[2]);

                            strPrevNow2 = s;
                            buffer.setLength(0); // 버퍼 초기화

                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);


                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(button1);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                // onClickStop();
            }
        };
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        actionBar = getSupportActionBar();
        actionBar.setShowHideAnimationEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.show();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);

        init();

        button1.setOnClickListener(new ledButtonClickListener(1));
        button2.setOnClickListener(new ledButtonClickListener(2));
        button3.setOnClickListener(new ledButtonClickListener(3));
        button4.setOnClickListener(new ledButtonClickListener(4));
        button5.setOnClickListener(new ledButtonClickListener(5));
        button6.setOnClickListener(new ledButtonClickListener(6));

        // usb
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);


        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        real_personid = pref.getInt("personid",0);

    }


    public void onClickStart(View view) {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }

    }

    // LED 버튼 리스너
    class ledButtonClickListener implements View.OnClickListener {
        private int num;
        public ledButtonClickListener(int num){
            this.num = num;
        }
        @Override
        public void onClick(View view) {
            switch (num){
                case 1:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.INVISIBLE);
                    icon3.setVisibility(View.INVISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    ledid = "1";
                    break;
                case 2:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.INVISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    ledid = "2";
                    break;
                case 3:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    ledid = "3";
                    break;
                case 4:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    ledid = "4";
                    break;
                case 5:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.INVISIBLE);
                    ledid = "5";
                    break;
                case 6:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    ledid = "6";
                    break;
            }


            // 누른 버튼 값을 arduino로 보내기
            serialPort.write(Integer.toString(num).getBytes());
            onClickStart(button1);

            mCountDown = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    //serialPort.read(mCallback);

                    textView_timer.setText(""+String.format("%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                @Override
                public void onFinish() {
                    serialPort.close();
                    insertToDatabase(Integer.toString(real_personid),ledid);

                    textView_timer.setText("00 : 00");
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);

                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("측정이 완료되었습니다.")
                            .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton(R.string.popup_no, null)
                            .show();
                }
            }.start();
        }
    }

    private void init(){
        button1 = (ImageButton) findViewById(R.id.button1);
        button2 = (ImageButton) findViewById(R.id.button2);
        button3 = (ImageButton) findViewById(R.id.button3);
        button4 = (ImageButton) findViewById(R.id.button4);
        button5 = (ImageButton) findViewById(R.id.button5);
        button6 = (ImageButton) findViewById(R.id.button6);

        textView_timer = (TextView) findViewById(R.id.textView_timer);

        icon1 = (ImageView) findViewById(R.id.imageView_main_icon1);
        icon2 = (ImageView) findViewById(R.id.imageView_main_icon2);
        icon3 = (ImageView) findViewById(R.id.imageView_main_icon3);
        icon4 = (ImageView) findViewById(R.id.imageView_main_icon4);
        icon5 = (ImageView) findViewById(R.id.imageView_main_icon5);
        icon6 = (ImageView) findViewById(R.id.imageView_main_icon6);

    }


    // 서버에 저장하는 함수
    private void insertToDatabase(String memberid, String gsr, String ledid){
        class InsertData extends AsyncTask<String,Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                try{
                    String memberid = (String)params[0];
                    String gsr = (String)params[1];
                    String ledid = (String)params[2];


                    String link="http://14.63.214.221/gsr_insert2.php";
                    String data  = URLEncoder.encode("memberid", "UTF-8") + "=" + URLEncoder.encode(memberid, "UTF-8");
                    data += "&" + URLEncoder.encode("gsr", "UTF-8") + "=" + URLEncoder.encode(gsr, "UTF-8");
                    data += "&" + URLEncoder.encode("ledid", "UTF-8") + "=" + URLEncoder.encode(ledid, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(memberid, gsr, ledid);
    }

    // 서버에 저장하는 함수
    private void insertToDatabase(String memberid, String ledid){
        class InsertData extends AsyncTask<String,Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                try{
                    String memberid = (String)params[0];
                    String ledid = (String)params[1];


                    String link="http://14.63.214.221/best_led_insert.php";
                    String data  = URLEncoder.encode("memberid", "UTF-8") + "=" + URLEncoder.encode(memberid, "UTF-8");
                    data += "&" + URLEncoder.encode("ledid", "UTF-8") + "=" + URLEncoder.encode(ledid, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(memberid, ledid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.my_page:
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
