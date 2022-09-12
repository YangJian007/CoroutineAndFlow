package com.yang.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-12
 * @describe
 * @changeUser
 * @changTime
 */
class FlowTest01 {

    //返回了多个值，但不是异步的
    fun getList() = listOf<Int>(1, 2, 3)

    //返回了多个值，但不是异步的
    fun simpleSequence(): Sequence<Int> = sequence {
        //调用挂起函数只能是yield()
        for (i in 1..3) {
            sleep(1000) //阻塞
            yield(i)
        }
    }

    //返回了多个值，是异步，但是是一次性 返回的
    suspend fun simpleList(): List<Int> {
        delay(1000)
        return listOf(1, 2, 3)
    }

    //返回多个值，而且是异步的
    fun simpleFlow() = flow<Int> {
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    @Test
    fun `test multiple values`() {
        getList().forEach { println(it) }
        simpleSequence().forEach { println(it) }
    }

    @Test
    fun `test multiple values2`() = runBlocking {
        simpleList().forEach { println(it) }
    }


    @Test
    fun `test multiple values3`() = runBlocking {
        simpleFlow().collect() { println(it) }
    }

    fun simpleFlow2() = flow<Int> {
        println("flow started")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    @Test
    fun `test flow is code`() = runBlocking {
        val flow = simpleFlow2()

        println("calling collect")
        flow.collect() { println(it) }

        println("calling collect again")
        flow.collect() { println(it) }
    }

    @Test
    fun `test flow continuation`() = runBlocking {
        (1..5).asFlow()
            .filter { it % 2 == 0 }
            .map { "string $it" }
            .collect() { println(it) }
    }

    @Test
    fun `test flow builder`() = runBlocking {
        flowOf("one", "two", "three")
            .onEach { delay(1000) }
            .collect() { println(it) }

        (1..5).asFlow().collect() { println(it) }
    }

    fun simpleFlow3() = flow<Int> {
        println("flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    fun simpleFlow4() = flow<Int> {
        withContext(Dispatchers.IO) {
            println("flow started ${Thread.currentThread().name}")
            for (i in 1..3) {
                delay(1000)
                emit(i)
            }
        }
    }

    @Test
    fun `test flow context`() = runBlocking {
//        simpleFlow3().collect(){
        simpleFlow4().collect() {
            println(it)
            println("${Thread.currentThread().name}")
        }
    }


    fun simpleFlow5() = flow<Int> {
        println("flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }.flowOn(Dispatchers.Default)

    @Test
    fun `test flowOn`() = runBlocking {
        simpleFlow5().collect() {
            println(it)
            println("${Thread.currentThread().name}")
        }
    }

    //事件源
    fun events() = (1..5)
        .asFlow()
        .onEach { delay(100) }
        .flowOn(Dispatchers.Default)

    @Test
    fun `test flow launch`() = runBlocking<Unit> {
        //onEach+launchIn 替换collect
        // launchIn返回的是一个Job对象，可以方便的取消流
        events()
//            .collect(){ }
            .onEach { println("$it  ${Thread.currentThread().name}") }
            .launchIn(CoroutineScope(Dispatchers.IO))//改变处理事件的线程，用onEach处理
            .join()
    }


    fun simpleFlow6() = flow<Int> {
        for (i in 1..3) {
            delay(1000)
            emit(i)
            println("emit $i")
        }
    }

    @Test
    fun `test cancel flow`() = runBlocking<Unit> {
        withTimeoutOrNull(2000) {
            simpleFlow6().collect() { println(it) }
            println("Done")
        }
    }

    fun simpleFlow7() = flow<Int> {
        for (i in 1..5) {
            emit(i)
            println("emit $i")
        }
    }

    @Test
    fun `test cancel flow check`() = runBlocking<Unit> {
        /*   simpleFlow7().collect(){
               println(it)
               if (it == 3) cancel()
           }
   */
        //足够繁忙 就取消不了了，如果想取消，必须指定cancellable
        (1..5)
            .asFlow()
            .cancellable()
            .collect() {
                println(it)
                if (it == 3) cancel()
            }

    }

    fun simpleFlow8() = flow<Int> {
        for (i in 1..3) {
            delay(100)
            emit(i)
            println("emit $i  ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `test flow back pressure`() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simpleFlow8()
//                .flowOn(Dispatchers.Default)//也可提高下频率
//                .buffer(50)//加缓存，提高效率
//                .conflate()//加快消费，只处理最新的
                .collectLatest {  //取消并只处理最后一个值
//                .collect() {
                    delay(300)
                    println("collected $it   ${Thread.currentThread().name}")
                }
        }
        println("collected in $time ms")
    }


}