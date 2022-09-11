package com.yang.coroutineexception

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-11
 * @describe
 * @changeUser
 * @changTime
 */
class GlobalCoroutineExceptionHandler:CoroutineExceptionHandler {
    override val key=CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Log.i("TAG", "UnHandleException:  $exception")
    }


}