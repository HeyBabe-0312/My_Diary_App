package com.example.note_notification.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note_notification.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_up extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private LoadingDialog loadingDialog;
    private EditText email;
    private EditText pass;
    private EditText repass;
    private Button btnsignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.edt_newemail);
        pass = findViewById(R.id.edt_newpass);
        repass = findViewById(R.id.edt_repass);
        btnsignup = findViewById(R.id.btn_Signup);
        this.setTitle("Đăng Ký");

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(Sign_up.this);
        if(firebaseAuth.getCurrentUser() != null){
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String mail = email.getText().toString();
                String password = pass.getText().toString();
                String repassword = repass.getText().toString();
                if(mail.isEmpty() || password.isEmpty() || repassword.isEmpty())  Toast.makeText(Sign_up.this,"Vui lòng nhập đầy đủ !",Toast.LENGTH_SHORT).show();
                else{
                    if(isValidEmail(mail)){
                        if(password.length()<6){
                            Toast.makeText(Sign_up.this,"Mật khẩu bạn quá ngắn !",Toast.LENGTH_SHORT).show();
                            repass.setText("");
                            pass.setText("");
                        }
                        if(password.equals(repassword)){
                            loadingDialog.startLoadingDialog();
                            createNewUser(mail,password);
                        }
                        else {
                            Toast.makeText(Sign_up.this,"Nhập mật khẩu hông khớp !",Toast.LENGTH_SHORT).show();
                            repass.setText("");
                        }
                    }
                    else{
                        Toast.makeText(Sign_up.this,"Sai địa chỉ email !",Toast.LENGTH_SHORT).show();
                        email.setText("");
                    }
                }
            }
        });
    }
    public void hideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }catch (NullPointerException ex){
            ex.printStackTrace();

        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void createNewUser(String email,String pass){
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingDialog.dismissDialog();
                    Toast.makeText(Sign_up.this,"Đăng ký thành công !",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Sign_up.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(Sign_up.this,"Địa chỉ email đã có tài khoản !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}