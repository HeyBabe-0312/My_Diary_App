package com.example.note_notification.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note_notification.R;
import com.example.note_notification.databinding.ActivityMainBinding;
import com.example.note_notification.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding binding;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadingDialog = new LoadingDialog(MainActivity.this);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        this.setTitle("");

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String mail = binding.edtEmail.getText().toString();
                String pass = binding.edtPass.getText().toString();
                if(!mail.isEmpty() || !pass.isEmpty()){
                    if(isValidEmail(mail)){
                        loadingDialog.startLoadingDialog();
                        login(mail,pass);
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Sai địa chỉ email !",Toast.LENGTH_SHORT).show();
                        binding.edtEmail.setText("");
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Vui lòng nhập đầy đủ !",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });
        binding.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_pass);
                Button btn = dialog.findViewById(R.id.btn_forgot);
                EditText edt = dialog.findViewById(R.id.email_forgot);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isValidEmail(edt.getText().toString())){
                            hideKeyboard();
                            loadingDialog.startLoadingDialog();
                            resetEmail(edt.getText().toString());
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Sai địa chỉ email !",Toast.LENGTH_SHORT).show();
                            edt.setText("");
                        }
                    }
                });
                dialog.show();
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
    public void login(String email, String pass){

        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingDialog.dismissDialog();
                    Toast.makeText(MainActivity.this,"Đăng nhập thành công !",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainDiary.class);
                    intent.putExtra("user",binding.edtEmail.getText().toString().replace("@gmail.com",""));
                    startActivity(intent);
                }else{
                    loadingDialog.dismissDialog();
                    Toast.makeText(MainActivity.this,"Email hoặc mật khẩu không chính xác !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void resetEmail(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingDialog.dismissDialog();
                    Toast.makeText(MainActivity.this,"Yêu cầu thành công. Kiểm tra email của bạn !",Toast.LENGTH_SHORT).show();
                }else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(MainActivity.this,"Email chưa được đăng ký !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}