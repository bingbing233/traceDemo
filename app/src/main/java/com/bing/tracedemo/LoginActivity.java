package com.bing.tracedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.leancloud.AVUser;
import cn.leancloud.callback.LogInCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
Button signInBtn;
TextView signUpText;
EditText accountEdit,passwordEdit;
Activity activity ;
ImageView imageView;
ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_login);
        signInBtn = findViewById(R.id.login_btn);
        signUpText = findViewById(R.id.sign_up_text);
        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        back = findViewById(R.id.back);
       /* imageView = findViewById(R.id.head_img);
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)imageView.getDrawable();
        drawable.start();*/
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                signIn(account,password);
            }
        });
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                signUp(account,password);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signIn(final String account, String password){
        AVUser.logIn(account, password).subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
                Intent intent = new Intent(activity,Main2Activity.class);
                intent.putExtra("account",account);
                startActivity(intent);
                finish();
            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
                Toast.makeText(activity,"登录失败，请检查密码是否正确",Toast.LENGTH_SHORT).show();
            }
            public void onComplete() {}
        });
    }
    public void signUp(String account,String password){
        // 创建实例
        AVUser user = new AVUser();

// 等同于 user.put("username", "Tom")
        user.setUsername(account);
        user.setPassword(password);

// 可选
//        user.setEmail("tom@leancloud.rocks");
//        user.setMobilePhoneNumber("+8618200008888");

// 设置其他属性的方法跟 AVObject 一样
//        user.put("gender", "secret");

        user.signUpInBackground().subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 注册成功
                Toast.makeText(activity,"注册成功",Toast.LENGTH_SHORT).show();
            }
            public void onError(Throwable throwable) {
                // 注册失败（通常是因为用户名已被使用）
            }
            public void onComplete() {}
        });
    }
}
