package com.yang.coroutine

import kotlinx.coroutines.*
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader
import java.lang.IllegalArgumentException
import kotlin.system.measureTimeMillis

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-11
 * @describe
 * @changeUser
 * @changTime
 */
class CoroutineTest04 {


    @Test
    fun `test deal with timeout`() = runBlocking {
        withTimeout(1300) {
            repeat(1000) {
                println("I'm sleeping $it ...")
                delay(500)
            }
        }
    }

    @Test
    fun `test deal with timeout return null`() = runBlocking {
        val result = withTimeoutOrNull(1300) {
            repeat(1000) {
                println("I'm sleeping $it ...")
                delay(500)
            }
            "Done"
        }?:"Jack"
        println(result)
    }


}