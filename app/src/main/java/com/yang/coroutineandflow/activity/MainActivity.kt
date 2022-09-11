package com.yang.coroutineandflow.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yang.coroutineandflow.R
import com.yang.coroutineandflow.api.userServiceApi

class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.tv_result)
        tv.text = "Jack"

        findViewById<Button>(R.id.btn).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {


                Thread(Runnable {
                    kotlin.run {
                      val res= userServiceApi.login("15225937677", "yang641052").execute().body()
                        runOnUiThread{
                            tv.text = res?.data?.toString()
                        }
                    }
                }).start()

//  AsyncTask这种方式会出问题，不清楚咋回事
//                object : AsyncTask<Void, Void, BaseReq<UserInfo?>>() {
//
//                    override fun doInBackground(vararg p0: Void?): BaseReq<UserInfo?>? {
//                        return userServiceApi.login("15225937677", "yang641052").execute().body()
//                    }
//
//                    fun onPostExecute(result: BaseReq<UserInfo>?) {
//                        tv.text = result?.data?.nickname
//                    }
//                }.execute()


            }

        })

    }


}