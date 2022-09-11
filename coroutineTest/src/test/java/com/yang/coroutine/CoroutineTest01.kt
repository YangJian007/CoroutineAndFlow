package com.yang.coroutine

import kotlinx.coroutines.*
import org.junit.Test
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
class CoroutineTest01 {


    @Test
    fun `test coroutine builder`() = runBlocking {
        //job1 job2 并发执行
        val job1 = launch {
            delay(2000)
            println("Job1 finished" + System.currentTimeMillis())
        }

        val job2 = async {
            delay(2000)
            println("Job2 finished" + System.currentTimeMillis())
            "Job2 result"
        }
        println(job2.await())
    }

    @Test
    fun `test coroutine join`() = runBlocking {
        //job1 执行完才会执行job2 job3
        val job1 = launch {
            delay(2000)
            println("One")
        }
        job1.join()
        val job2 = launch {
            delay(2000)
            println("Two")
        }

        val job3 = launch {
            delay(2000)
            println("Three")
        }
    }

    @Test
    fun `test coroutine await`() = runBlocking {
        //job1 执行完才会执行job2 job3
        val job1 = async {
            delay(2000)
            println("One")
        }
        job1.await()
        val job2 = async {
            delay(2000)
            println("Two")
        }

        val job3 = async {
            delay(2000)
            println("Three")
        }
    }

    @Test
    fun `test sync`() = runBlocking {
        //顺序执行
        val time = measureTimeMillis {
            val one = doOne()
            val two = doTwo()
            println("result:${one + two}")
        }
        println(time)
    }

    @Test
    fun `test combine async`() = runBlocking {
        //并发执行
        val time = measureTimeMillis {
            val one =async { doOne() }
            val two =async { doTwo() }
            println("result:${one.await() + two.await()}")
        }
        println(time)
    }


    private suspend fun doOne(): Int {
        delay(1000)
        return 14
    }

    private suspend fun doTwo(): Int {
        delay(1000)
        return 6
    }

    @Test
    fun `test start mode`() = runBlocking {
        //Default  随时都可以取消
        // Atomic   第一个挂起点之前不响应取消
        //Lazy    惰性启动，主动调用start()\await()\join()才会启动  被启动前可以被取消
        //UnDispatched 创建后立即在当前调用栈中执行，直到遇到第一个真正挂起的点
        val job=launch(Dispatchers.IO,start = CoroutineStart.UNDISPATCHED) {
            delay(10000)
            println("job1 finished"+Thread.currentThread().name)
        }
        delay(1000)
        job.cancel()
    }

    @Test
    fun `test coroutineScope builder`()= runBlocking {
        //coroutineScope  原子性，一荣俱荣，一损俱损，子协程异常会相互影响
        coroutineScope {
            val job1 = launch {
                delay(5000)
                println("Job1 finished" + System.currentTimeMillis())
            }

            val job2 = async {
                delay(2000)
                println("Job2 finished" + System.currentTimeMillis())
                throw IllegalArgumentException()
            }
        }
    }

    @Test
    fun `test supervisorScope builder`()= runBlocking {
        //supervisorScope  子协程异常不会相互影响
        supervisorScope {
            val job1 = launch {
                delay(5000)
                println("Job1 finished" + System.currentTimeMillis())
            }

            val job2 = async {
                delay(2000)
                println("Job2 finished" + System.currentTimeMillis())
                throw IllegalArgumentException()
            }
        }
    }
}