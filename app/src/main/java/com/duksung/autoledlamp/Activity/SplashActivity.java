package com.duksung.autoledlamp.Activity;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duksung.autoledlamp.R;

public class SplashActivity extends AppCompatActivity{

    ImageView imageView_splash_icon1,imageView_splash_icon2,imageView_splash_icon3,imageView_splash_icon4,imageView_splash_icon5, imageView_splash_icon_lamp;
    LinearLayout linearLayout;
    TextView textView_splash_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView_splash_icon1 = (ImageView)findViewById(R.id.imageView_splash_icon1);
        imageView_splash_icon2 = (ImageView)findViewById(R.id.imageView_splash_icon2);
        imageView_splash_icon3 = (ImageView)findViewById(R.id.imageView_splash_icon3);
        imageView_splash_icon4 = (ImageView)findViewById(R.id.imageView_splash_icon4);
        imageView_splash_icon5 = (ImageView)findViewById(R.id.imageView_splash_icon5);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        textView_splash_label = (TextView)findViewById(R.id.textView_splash_label);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {
                switch (i){
                    case 0:
                        imageView_splash_icon1.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor1));
                        break;
                    case 1:
                        imageView_splash_icon2.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor2));
                        break;
                    case 2:
                        imageView_splash_icon3.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor3));
                        break;
                    case 3:
                        imageView_splash_icon4.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor4));
                        break;
                    case 4:
                        imageView_splash_icon5.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(Color.WHITE);
                        textView_splash_label.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        imageView_splash_icon1.setVisibility(View.INVISIBLE);
                        imageView_splash_icon2.setVisibility(View.INVISIBLE);
                        imageView_splash_icon3.setVisibility(View.INVISIBLE);
                        imageView_splash_icon4.setVisibility(View.INVISIBLE);
                        imageView_splash_icon5.setVisibility(View.INVISIBLE);
                        textView_splash_label.setVisibility(View.INVISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor0));
                        break;
                    case 6:
                        imageView_splash_icon1.setVisibility(View.VISIBLE);
                        imageView_splash_icon2.setVisibility(View.VISIBLE);
                        imageView_splash_icon3.setVisibility(View.VISIBLE);
                        imageView_splash_icon4.setVisibility(View.VISIBLE);
                        imageView_splash_icon5.setVisibility(View.VISIBLE);
                        textView_splash_label.setVisibility(View.VISIBLE);
                        finish();
                        linearLayout.setBackgroundColor(Color.WHITE);
                        break;
                    case 7:
                        imageView_splash_icon1.setVisibility(View.INVISIBLE);
                        imageView_splash_icon2.setVisibility(View.INVISIBLE);
                        imageView_splash_icon3.setVisibility(View.INVISIBLE);
                        imageView_splash_icon4.setVisibility(View.INVISIBLE);
                        imageView_splash_icon5.setVisibility(View.INVISIBLE);
                        textView_splash_label.setVisibility(View.INVISIBLE);
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.darkColor0));
                        break;
                    case 8:
                        imageView_splash_icon1.setVisibility(View.VISIBLE);
                        imageView_splash_icon2.setVisibility(View.VISIBLE);
                        imageView_splash_icon3.setVisibility(View.VISIBLE);
                        imageView_splash_icon4.setVisibility(View.VISIBLE);
                        imageView_splash_icon5.setVisibility(View.VISIBLE);
                        textView_splash_label.setVisibility(View.VISIBLE);
                        linearLayout.setBackgroundColor(Color.WHITE);
                        break;
                }
                i++;
                handler.postDelayed(this, 300);
            }
        };
        handler.postDelayed(runnable, 200);
    }

}
