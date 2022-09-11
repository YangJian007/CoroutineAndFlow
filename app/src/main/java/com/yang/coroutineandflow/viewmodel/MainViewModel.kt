package com.yang.coroutineandflow.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yang.coroutineandflow.api.UserInfo
import com.yang.coroutineandflow.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-11
 * @describe
 * @changeUser
 * @changTime
 */
class MainViewModel : ViewModel() {

    val userLiveData=MutableLiveData<UserInfo?>()

    private val userRepository=UserRepository()

    fun getUser(name:String,password:String){
        viewModelScope.launch {
            val userInfo = userRepository.getUser(name, password)
            userLiveData.value=userInfo
        }
    }

}