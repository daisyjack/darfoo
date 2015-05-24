package com.darfoo.darfoolauncher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.darfoo.darfoolauncher.R;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;


/**
 * Created by Administrator on 2015/1/27 0027.
 */
public class UserLoginFragment extends BaseFragmentActivity {

    private ImageButton return_btn;
    private ImageButton register_btn;
    private ImageButton advice_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_login);
        return_btn = (ImageButton)findViewById(R.id.retrun_button);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginFragment.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        advice_btn = (ImageButton)findViewById(R.id.advice_btn);
        advice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = sendMailByApache();
                if (i == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "邮件发送成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
    });
    }

    public int sendMailByApache() {

        try {
            HtmlEmail email = new HtmlEmail();
            // 这里是发送服务器的名字
            email.setHostName("smtp.126.com");
            // 编码集的设置
            email.setTLS(true);
            email.setSSL(true);

            email.setCharset("gbk");
            // 收件人的邮箱
            email.addTo("714327494@qq.com");
            // 发送人的邮箱
            email.setFrom("shitou_0820@126.com");
            // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
            email.setAuthentication("shitou_0820", "wang88461950lei");
            email.setSubject("测试Email Apache");
            // 要发送的信息
            email.setMsg("测试Email Apache");
            // 发送
            email.send();
        } catch (EmailException e) {
            // TODO Auto-generated catch block
            Log.i("IcetestActivity", e.getMessage());
        }

        return 1;
    }

}

