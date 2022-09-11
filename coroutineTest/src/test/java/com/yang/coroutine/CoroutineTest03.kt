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
class CoroutineTest03 {


    @Test
    fun `test release resources`() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("I'm sleeping $it ...")
                    delay(500)
                }
            } finally {
                //在此释放资源
                println("I'm running finally")
            }
        }
        delay(1300)
        println("I'm trying waiting")
        job.cancelAndJoin()
        println("Now I can quit")
    }

    @Test
    fun `test use function`() = runBlocking {
        var br = BufferedReader(FileReader("D:\\test.txt"))
        with(br) {
            var line: String?
            try {
                while (true) {
                    line = readLine() ?: break
                    println(line)
                }
            } finally {
                close()
            }
        }

        BufferedReader(FileReader("D:\\test.txt")).use {
            var line: String?
            while (true) {
                line = readLine() ?: break
                println(line)
            }
        }
    }

    @Test
    fun `test cancel with NonCancelable`() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("I'm sleeping $it ...")
                    delay(500)
                }
            } finally {
                withContext(NonCancellable){//保证finally里的挂起函数可以执行
                    //在此释放资源
                    println("I'm running finally")
                    delay(1000)
                    println("我是finally里的挂起函数")
                }
            }
        }
        delay(1300)
        println("I'm trying waiting")
        job.cancelAndJoin()
        println("Now I can quit")
    }


}