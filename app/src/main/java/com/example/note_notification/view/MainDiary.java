package com.example.note_notification.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_notification.R;
import com.example.note_notification.model.Post;
import com.example.note_notification.model.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainDiary extends AppCompatActivity {
    private LoadingDialog loadingDialog;
    private String user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private TextView dateNow;
    private RecyclerView rcvPost;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FloatingActionButton btnAdd;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_diary);

        this.setTitle("My Diary App");

        Intent receive = getIntent();
        if (receive != null){
            user = receive.getStringExtra("user");
        }
        loadingDialog = new LoadingDialog(MainDiary.this);
        rcvPost = findViewById(R.id.RcvPost);
        rcvPost.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<Post>();
        postAdapter = new PostAdapter(postList, this);
        rcvPost.setAdapter(postAdapter);

        dateNow = findViewById(R.id.textView2);
        btnAdd = findViewById(R.id.floatingActionButton);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateNow.setText(sdf.format(new Date()));
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        showPost();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainDiary.this, AddPost.class);
                i.putExtra("id",postAdapter.getItemCount());
                startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
    }
    public void showPost(){
        loadingDialog.startLoadingDialog();
        getAllPostMessage();
    }
    public void PostNewMessage(Post data){
        mDatabase = FirebaseDatabase.getInstance("https://diaryhominhhieu-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users/"+user);
        mDatabase.child(String.valueOf(data.getId())).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainDiary.this,"Thêm thành công",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainDiary.this,"Thêm thất bại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getAllPostMessage(){
        mDatabase = FirebaseDatabase.getInstance("https://diaryhominhhieu-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users/"+user);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot item:snapshot.getChildren()){
                    Post data = item.getValue(Post.class);
                    postList.add(new Post(data.getId(),data.getTitle(),data.getContent(),data.getDate(),data.getTime()));
                }
                loadingDialog.dismissDialog();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(MainDiary.this,"Dữ liệu trống",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra("id",1);
                String title = data.getStringExtra("title");
                String desc= data.getStringExtra("desc");
                String date = data.getStringExtra("date");
                String time = data.getStringExtra("time");
                PostNewMessage(new Post(id,title,desc,date,time));
                showPost();
            }
        }
    }
    protected void closeApp(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Đăng xuất khỏi tài khoản ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainDiary.this,"Bạn đã đăng xuất !",Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(MainDiary.this,MainActivity.class);
                       startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    @Override
    public void onBackPressed() {
        closeApp();
    }
}