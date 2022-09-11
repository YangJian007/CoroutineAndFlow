package com.yang.coroutineexception

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hanler=CoroutineExceptionHandler{_,exception->
            Log.i("TAG", "Caught $exception")
        }

        findViewById<Button>(R.id.btn).setOnClickListener {
            GlobalScope.launch {
                "abc".substring(19)
            }
        }

    }
}