package com.yang.coroutineandflow.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yang.coroutineandflow.R
import kotlin.coroutines.*

class MainActivity03 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main03)

        //协程基础层
       val continuation= suspend {
            5
        }.createCoroutine(object :Continuation<Int>{
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(result: Result<Int>) {
                println("CoroutineEnd: $result")
            }
        })

        continuation.resume(Unit)



    }
}