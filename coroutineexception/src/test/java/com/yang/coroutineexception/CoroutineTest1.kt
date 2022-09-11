package com.yang.coroutineexception

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.ArithmeticException
import java.lang.AssertionError
import java.lang.IndexOutOfBoundsException
import java.time.Year
import kotlin.coroutines.CoroutineContext

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-11
 * @describe  CoroutineContext包含：job\Dispatcher\name\exceptionHandler
 * @changeUser
 * @changTime
 */
class CoroutineTest1 {

    @Test
    fun `test CoroutineContext`() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("Test")) {
            println(Thread.currentThread().name)
        }
    }

    @Test
    fun `test CoroutineContext extend`() = runBlocking<Unit> {
        val scope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("Test"))
        val job = scope.launch {
            println("${coroutineContext[Job]}  ${Thread.currentThread().name}")
            val result = async {
                println("${coroutineContext[Job]}  ${Thread.currentThread().name}")
                "OK"
            }.await()
        }
        job.join()
    }


    @Test
    fun `test CoroutineContext extend2`() = runBlocking<Unit> {
        val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job() + Dispatchers.Main + coroutineExceptionHandler)
        val job = scope.launch(Dispatchers.IO) {
            //新携程
            delay(10000)
            1 / 0
        }
        job.join()
    }


    @Test
    fun `test exception propagation`() = runBlocking<Unit> {
        val job1 = GlobalScope.launch {
            try {
                throw IndexOutOfBoundsException()
            } catch (e: Exception) {
                println("Caught IndexOutOfBoundsException")
            }
        }
        job1.join()

        val deferred = GlobalScope.async {
            throw IllegalArgumentException()
        }
        try {
            deferred.await()
        } catch (e: Exception) {
            println("Caught IllegalArgumentException")
        }

    }


    @Test
    fun `test exception propagation2`() = runBlocking<Unit> {
        //非根协程异常会直接抛出，不需要调用await
        val scope = CoroutineScope(Job())
        val job = scope.launch {
            async {
                throw IndexOutOfBoundsException()
            }
        }
        job.join()
    }


    @Test
    fun `test SupervisorJob`() = runBlocking<Unit> {
        val supervisor = CoroutineScope(SupervisorJob())
        val job1 = supervisor.launch {
            delay(100)
            println("child 1")
            throw IllegalArgumentException()
        }

        val job2 = supervisor.launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                println("Job2 finished")
            }
        }

        joinAll(job1, job2)
    }

    @Test
    fun `test supervisorScope`() = runBlocking<Unit> {
        //子协程报错不影响兄弟协程 及父协程
        supervisorScope {
            launch {
                delay(100)
                println("child 1")
                throw IllegalArgumentException()
            }
            try {
                delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                println("Job2 finished")
            }
        }
    }

    @Test
    fun `test supervisorScope2`() = runBlocking<Unit> {
        //父协程报错  子协程全部取消
        supervisorScope {
            val child = launch {
                try {
                    println("child is sleeping")
                    delay(Long.MAX_VALUE)
                } catch (e: Exception) {
                    println("the child is canceled")
                }
            }
            yield()
            println("throwing an exception from scope")
            throw AssertionError()
        }
    }

    @Test
    fun `test CoroutineExceptionHandler`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { context, exception ->
            println("Caught $exception")
        }
        val job1 = GlobalScope.launch(handler) {
            throw AssertionError()
        }

        val deferred = GlobalScope.async(handler) {
            throw ArithmeticException()
        }
        job1.join()
        deferred.await()
    }

    @Test
    fun `test CoroutineExceptionHandler2`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { context, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        val job = scope.launch(handler) {
            launch {
                throw AssertionError()
            }
        }
        job.join()
    }

    @Test
    fun `test CoroutineExceptionHandler3`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { context, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        val job = scope.launch {
            launch(handler) {
                throw AssertionError()
            }
        }
        job.join()
    }

    @Test
    fun `test cancel and exception`() = runBlocking<Unit> {
        val job=launch {
            val child=launch {
                try {
                    delay(Long.MAX_VALUE)
                } catch (e: Exception) {
                    println("child is canceled")
                }
            }
            yield()
            println("canceling child")
            child.cancelAndJoin()
            yield()
            println("parent is not canceled")
        }
    }

    @Test
    fun `test cancel and exception2`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { context, exception ->
            println("Caught $exception")
        }
        val job=GlobalScope.launch (handler){
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } catch (e: Exception) {
                    withContext(NonCancellable) {
                        println("children are canceled,but exception is not handled until children terminate")
                        delay(100)
                        println("the first child finished it's non cancellable block")
                    }
                }
            }
            launch {
                delay(10)
                println("second child throw an exception ")
                throw ArithmeticException()
            }
        }
        job.join()
    }

   @Test
    fun `test exception aggregation`() = runBlocking<Unit> {
       val handler = CoroutineExceptionHandler { context, exception ->
           println("Caught $exception  ${exception.suppressed.contentToString()}")
       }
       val job=GlobalScope.launch(handler) {
           launch {
               try {
                   delay(Long.MAX_VALUE)
               } catch (e: Exception) {
                   throw ArithmeticException() //2
               }
           }
           launch {
               delay(100)
               throw AssertionError() //1
           }
       }
       job.join()
    }


}