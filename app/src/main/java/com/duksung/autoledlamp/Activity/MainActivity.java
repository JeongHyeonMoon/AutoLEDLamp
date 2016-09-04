package com.duksung.autoledlamp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.duksung.autoledlamp.Activity.SplashActivity;
import com.duksung.autoledlamp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageButton button1,button2,button3,button4,button5,button6,button7,button8,button9,button10;
    private ImageView icon1,icon2,icon3,icon4,icon5,icon6,icon7,icon8,icon9,icon0;
    private TextView textView_timer;
    CountDownTimer mCountDown = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,SplashActivity.class));

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
        button7.setOnClickListener(new ledButtonClickListener(7));
        button8.setOnClickListener(new ledButtonClickListener(8));
        button9.setOnClickListener(new ledButtonClickListener(9));
        button10.setOnClickListener(new ledButtonClickListener(0));
    }

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
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.INVISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.INVISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.INVISIBLE);icon6.setVisibility(View.INVISIBLE);
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.INVISIBLE);
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 6:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    icon7.setVisibility(View.INVISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 7:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    icon7.setVisibility(View.VISIBLE);icon8.setVisibility(View.INVISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 8:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    icon7.setVisibility(View.VISIBLE);icon8.setVisibility(View.VISIBLE);
                    icon9.setVisibility(View.INVISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 9:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    icon7.setVisibility(View.VISIBLE);icon8.setVisibility(View.VISIBLE);
                    icon9.setVisibility(View.VISIBLE);icon0.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    icon1.setVisibility(View.VISIBLE);icon2.setVisibility(View.VISIBLE);
                    icon3.setVisibility(View.VISIBLE);icon4.setVisibility(View.VISIBLE);
                    icon5.setVisibility(View.VISIBLE);icon6.setVisibility(View.VISIBLE);
                    icon7.setVisibility(View.VISIBLE);icon8.setVisibility(View.VISIBLE);
                    icon9.setVisibility(View.VISIBLE);icon0.setVisibility(View.VISIBLE);
                    break;

            }
            mCountDown = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    insertToDatabase("11");
                    textView_timer.setText(""+String.format("%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }
                @Override
                public void onFinish() {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
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
        button7 = (ImageButton) findViewById(R.id.button7);
        button8 = (ImageButton) findViewById(R.id.button8);
        button9 = (ImageButton) findViewById(R.id.button9);
        button10 = (ImageButton) findViewById(R.id.button10);

        textView_timer = (TextView) findViewById(R.id.textView_timer);

        icon1 = (ImageView) findViewById(R.id.imageView_main_icon1);
        icon2 = (ImageView) findViewById(R.id.imageView_main_icon2);
        icon3 = (ImageView) findViewById(R.id.imageView_main_icon3);
        icon4 = (ImageView) findViewById(R.id.imageView_main_icon4);
        icon5 = (ImageView) findViewById(R.id.imageView_main_icon5);
        icon6 = (ImageView) findViewById(R.id.imageView_main_icon6);
        icon7 = (ImageView) findViewById(R.id.imageView_main_icon7);
        icon8 = (ImageView) findViewById(R.id.imageView_main_icon8);
        icon9 = (ImageView) findViewById(R.id.imageView_main_icon9);
        icon0 = (ImageView) findViewById(R.id.imageView_main_icon0);


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
