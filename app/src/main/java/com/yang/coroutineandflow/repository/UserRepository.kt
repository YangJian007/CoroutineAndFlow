package com.yang.coroutineandflow.repository

import com.yang.coroutineandflow.api.UserInfo
import com.yang.coroutineandflow.api.userServiceApi

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-11
 * @describe
 * @changeUser
 * @changTime
 */
class UserRepository {

    suspend fun getUser(name:String,password:String):UserInfo?{
        return userServiceApi.login1(name,password).data
    }

}