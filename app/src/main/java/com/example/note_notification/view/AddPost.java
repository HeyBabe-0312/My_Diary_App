package com.example.note_notification.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note_notification.R;
import com.example.note_notification.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPost extends AppCompatActivity {
    private EditText title;
    private EditText desc;
    private int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        title = findViewById(R.id.edt_title);
        desc = findViewById(R.id.edt_desc);

        Intent receive = getIntent();
        if (receive != null){
            ID = receive.getIntExtra("id",0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void sendData(){
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String t = title.getText().toString();
        String d = desc.getText().toString();
        if(TextUtils.isEmpty(t)||TextUtils.isEmpty(d)) {
            Toast.makeText(AddPost.this,"Nhập đầy đủ nội dung",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id",ID+1);
        returnIntent.putExtra("title",t);
        returnIntent.putExtra("desc",d);
        returnIntent.putExtra("date",date.format(new Date()));
        returnIntent.putExtra("time",time.format(new Date()));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.mybutton){
            sendData();
        }
        else if (i == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}