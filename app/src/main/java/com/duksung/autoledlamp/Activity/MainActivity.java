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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;

    // usb permission
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";

    // 시간
    private String strPrevNow2 = "first";
    String strNow;

    //getdata
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_PERSON_CONDITION_ID = "person_conditionid";
    private static final String TAG_PERSON_ID = "personid";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_CONDITION_ID = "conditionid";
    JSONArray std_place = null;

    int real_personid;
    int real_conditionid;
    int real_person_conditionid;
    String real_start_time;

    // gsr
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    StringBuffer buffer = new StringBuffer(4);
    String[] result ;


    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null; // 아두이노에서 가져오는 data
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");

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
                            result = temp.split("/"); // /로 끊어서 각 값을 구분 -> GSR만 가져오면 필요 없음

                            GSRinsertToDatabase(Integer.toString(real_person_conditionid), s, result[0]);
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

        //getdata
        getData("http://14.63.214.221/condition_get.php");
        //getData("http://14.63.214.221/person_condition_get.php");

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
                    break;
                case 2:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.INVISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.INVISIBLE);
                    break;
                case 6:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    break;
                /*
                case 7:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    break;
                    */
            }

            // 시간
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            strNow = sdfNow.format(date);

            // 누른 버튼 값을 arduino로 보내기
            serialPort.write(Integer.toString(num).getBytes());


            mCountDown = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    insertToDatabase("11");
                    insertToDatabase(Integer.toString(real_personid),Integer.toString(real_conditionid),strNow);

                    textView_timer.setText(""+String.format("%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                @Override
                public void onFinish() {
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

    private void insertToDatabase(String value){
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
                    String value = (String)params[0];

                    String link="http://14.63.214.221/test_insert.php";
                    String data  = URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");

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
        task.execute(value);
    }


    // 서버에 저장하는 함수
    private void insertToDatabase(String personid, String conditionid, String time){
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
                    String personid = (String)params[0];
                    String conditionid = (String)params[1];
                    String time = (String)params[2];


                    String link="http://14.63.214.221/person_condition_insert.php";
                    String data  = URLEncoder.encode("personid", "UTF-8") + "=" + URLEncoder.encode(personid, "UTF-8");
                    data += "&" + URLEncoder.encode("conditionid", "UTF-8") + "=" + URLEncoder.encode(conditionid, "UTF-8");
                    data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

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
        task.execute(personid, conditionid, time);
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

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity loginActivity = (LoginActivity) LoginActivity.LoginActivity;
                            loginActivity.finish();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();
        }
    }


    // 서버에서 사용자 정보 가져오는 함수
    private void checkPersonCondition(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            std_place = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < std_place.length(); i++) {
                JSONObject c = std_place.getJSONObject(i);
                int json_person_condition_id = c.getInt(TAG_PERSON_CONDITION_ID);
                int json_personid = c.getInt(TAG_PERSON_ID);
                int json_conditionid = c.getInt(TAG_CONDITION_ID);
                String json_strNow = c.getString(TAG_START_TIME);

                Log.d("hyunhye",json_personid +","+ real_personid +","+ json_conditionid +","+ real_conditionid +","+ real_start_time +","+ json_strNow);
                if(json_personid == real_personid && json_conditionid == real_conditionid && real_start_time.equals(json_strNow)){
                    real_person_conditionid = json_person_condition_id;
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("person_condition_id", real_person_conditionid);
                    editor.commit();
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                checkPersonCondition();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    // 서버에 저장하는 함수_gsr
    private void GSRinsertToDatabase(String person_conditionid, String time, String gsrdata){
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
                    String person_conditionid = (String)params[0];
                    String time = (String)params[1];
                    String gsrdata = (String)params[2];


                    String link="http://14.63.214.221/gsr_insert.php";
                    String data  = URLEncoder.encode("person_conditionid", "UTF-8") + "=" + URLEncoder.encode(person_conditionid, "UTF-8");
                    data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                    data += "&" + URLEncoder.encode("gsrdata", "UTF-8") + "=" + URLEncoder.encode(gsrdata, "UTF-8");


                    //Log.d("attention",attention);
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
        task.execute(person_conditionid, time, gsrdata);
    }

}
