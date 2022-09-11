package com.yang.coroutineandflow.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yang.coroutineandflow.databinding.ActivityMain07Binding
import com.yang.coroutineandflow.viewmodel.MainViewModel

class MainActivity07 : AppCompatActivity() {

    private lateinit var myBinding:ActivityMain07Binding
    private val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding=ActivityMain07Binding.inflate(layoutInflater)
        setContentView(myBinding.root)
        myBinding.viewModel=mainViewModel
        myBinding.lifecycleOwner=this
        myBinding.btn.setOnClickListener {
            mainViewModel.getUser("15225937677","yang641052")
        }
    }
}