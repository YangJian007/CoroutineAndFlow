package com.yang.coroutineandflow.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yang.coroutineandflow.R
import com.yang.coroutineandflow.api.userServiceApi
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity06 : AppCompatActivity(), CoroutineScope by MainScope() {
    //      mainScope也可以通过委托来实现
//    private val mainScope= MainScope() //大写开头函数、工厂模式
    private lateinit var tv: TextView

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById<TextView>(R.id.tv_result)
        tv.text = "Jack"

        findViewById<Button>(R.id.btn).setOnClickListener {
/*            mainScope.launch {

                val userInfo = userServiceApi.login1("15225937677", "yang641052")
                tv.text=userInfo.data.toString()

                //协程被取消时候，会抛异常 kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@5f37a66
//                try {
//                    delay(10000)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
            }*/

           launch {
                val userInfo = userServiceApi.login1("15225937677", "yang641052")
                tv.text = userInfo.data.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mainScope.cancel()
        cancel()
    }

}