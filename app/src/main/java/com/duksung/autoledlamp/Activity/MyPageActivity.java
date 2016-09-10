package com.duksung.autoledlamp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.duksung.autoledlamp.R;

public class MyPageActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Button button_my_page_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        actionBar = getSupportActionBar();
        actionBar.setShowHideAnimationEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.show();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_my_page);

        button_my_page_logout = (Button) findViewById(R.id.button_my_page_logout);
        button_my_page_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin", false);
                    editor.commit();
                    Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                    intent.putExtra("splash","no");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }catch (NullPointerException e){
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin", false);
                    editor.commit();
                    Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                    intent.putExtra("splash","no");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.menu_my_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.back:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
