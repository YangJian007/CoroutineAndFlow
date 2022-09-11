package com.yang.coroutineandflow.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yang.coroutineandflow.R
import com.yang.coroutineandflow.api.userServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity02 : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.tv_result)
        tv.text = "Jack"

        findViewById<Button>(R.id.btn).setOnClickListener {

            GlobalScope.launch(Dispatchers.Main) {
                //retrofit会自动切换线程，不需要写withContext了
                val loginRes =userServiceApi.login1("15225937677", "yang641052")
                tv.text = loginRes?.data?.toString()
            }

        }

    }


}