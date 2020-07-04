package com.it.eventbuscustomsimple;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.it.eventbus_lib.EventBus;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "发送事件 线程 ：" + Thread.currentThread().getName());
                EventBus.getDefault().post(new EventBean("张三", 30));
                finish();
            }
        });
    }
}
