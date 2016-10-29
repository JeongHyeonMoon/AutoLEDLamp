package com.duksung.autoledlamp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.duksung.autoledlamp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ButtonActivity extends AppCompatActivity {

    Button button_measure, button_start;
    int real_personid;

    // getdata
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_LED_ID = "ledid";
    JSONArray myprofile = null;

    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_button);


        button_measure = (Button) findViewById(R.id.button_measure);
        button_start = (Button) findViewById(R.id.button_start);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        real_personid = pref.getInt("personid",0);

        // 측정하기
        button_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // led 켜기
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertToDatabase(Integer.toString(real_personid));
                getData("http://14.63.214.221/best_led_get.php");
            }
        });
    }

    // 서버에 저장하는 함수
    private void insertToDatabase(String memberid){
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


                    String link="http://14.63.214.221/best_led_find_insert.php";
                    String data  = URLEncoder.encode("memberid", "UTF-8") + "=" + URLEncoder.encode(memberid, "UTF-8");

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
        task.execute(memberid);
    }


    private void checkBestLED(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            myprofile = jsonObj.getJSONArray(TAG_RESULTS);

            int i;
            for (i = 0; i < myprofile.length(); i++) {
                JSONObject c = myprofile.getJSONObject(i);
                int ledid = c.getInt(TAG_LED_ID);
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
                checkBestLED();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
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
}
