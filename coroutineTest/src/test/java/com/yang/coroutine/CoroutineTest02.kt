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
class CoroutineTest02 {


    @Test
    fun `test scope cancel`() = runBlocking<Unit> {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            delay(1000)
            println("job1")
        }
        scope.launch {
            delay(1000)
            println("job2")
        }
        delay(100)
        scope.cancel()
        delay(2000)
    }

    @Test
    fun `test brother cancel`() = runBlocking<Unit> {
        val scope = CoroutineScope(Dispatchers.Default)
        val job1 = scope.launch {
            delay(1000)
            println("job1")
        }
        val job2 = scope.launch {
            delay(1000)
            println("job2")
        }
        delay(100)
        job1.cancel()
        delay(2000)
    }

    @Test
    fun `test cancellationException`() = runBlocking<Unit> {
        val job1 = GlobalScope.launch {
            try {
                delay(1000)
                println("Job 1")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        delay(100)
//        job1.cancel(CancellationException("自定义取消异常"))
//        job1.cancel()
//        job1.join()
        job1.cancelAndJoin()
    }

   @Test
    fun `test cancel cpu task by isActive`() = runBlocking {
       val startTime=System.currentTimeMillis()
       val job = launch(Dispatchers.Default) {
            var nextPrintTime=startTime
           var i = 1
           while (i < 10 && isActive) {
               if (System.currentTimeMillis() >= nextPrintTime) {
                   println("I am sleeping ${i++}...")
                   nextPrintTime+=500
               }
           }
       }
       delay(1300)
       println("I'm trying waiting")
       job.cancelAndJoin()
       println("Now I can quit")
    }

  @Test
    fun `test cancel cpu task by ensureActive`() = runBlocking {
       val startTime=System.currentTimeMillis()
       val job = launch(Dispatchers.Default) {
            var nextPrintTime=startTime
           var i = 1
           while (i < 10 ) {
               ensureActive() //会抛异常 cancellationException
               if (System.currentTimeMillis() >= nextPrintTime) {
                   println("I am sleeping ${i++}...")
                   nextPrintTime+=500
               }
           }
       }
       delay(1300)
       println("I'm trying waiting")
       job.cancelAndJoin()
       println("Now I can quit")
    }

 @Test
    fun `test cancel cpu task by yield`() = runBlocking {
       val startTime=System.currentTimeMillis()
       val job = launch(Dispatchers.Default) {
            var nextPrintTime=startTime
           var i = 1
           while (i < 10 ) {
               yield()
               if (System.currentTimeMillis() >= nextPrintTime) {
                   println("I am sleeping ${i++}...")
                   nextPrintTime+=500
               }
           }
       }
       delay(1300)
       println("I'm trying waiting")
       job.cancelAndJoin()
       println("Now I can quit")
    }


}