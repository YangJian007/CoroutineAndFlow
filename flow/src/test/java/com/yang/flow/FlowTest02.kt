package com.yang.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-12
 * @describe
 * @changeUser
 * @changTime
 */
class FlowTest02 {

    suspend fun performRequest(request: Int): String {
        delay(1000)
        return "response $request"
    }

    @Test
    fun `test transform flow operator`() = runBlocking {
        //map  一生一
        /*     (1..3).asFlow()
                 .map { performRequest(it) }
                 .collect { println(it) }*/

        //transform 一生三
        (1..3).asFlow()
            .transform {
                emit("Making Request $it")
                emit(performRequest(it))
            }
            .collect { println(it) }
    }

    fun numbers() = flow<Int> {
        try {
            emit(1)
            emit(2)
            println("this line will not execute")
            emit(3)
        } finally {
            println("finally in numbers")
        }
    }

    @Test
    fun `test limit length operator`() = runBlocking {
        numbers().take(2)
            .collect { println(it) }
    }

    @Test
    fun `test terminal operator`() = runBlocking {
        val sum = (1..5).asFlow()
            .map { it * it }
            .reduce { a, b -> a + b }
        println(sum)
    }

    @Test
    fun `test zip`() = runBlocking {
        val nums = (1..3).asFlow()
        val strs = flowOf("One", "Two", "Three")
        nums.zip(strs) { a, b -> "$a -> $b" }
            .collect { println(it) }
    }

    @Test
    fun `test zip2`() = runBlocking {
        val nums = (1..3).asFlow().onEach { delay(100) }
        val strs = flowOf("One", "Two", "Three").onEach { delay(500) }
        val startTime = System.currentTimeMillis()
        nums.zip(strs) { a, b -> "$a -> $b" }
            .collect { println("$it  at ${System.currentTimeMillis() - startTime}ms") }
    }

    fun requestFlow(i: Int) = flow<String> {
        emit("$i First")
        delay(500)
        emit("$i Second")
    }

    @Test
    fun `test flatmapConcat`() = runBlocking {
        val startTime=System.currentTimeMillis()
        //Flow<Flow<String>> 用map返回，这种情况可以通过flatMapConcat展平为Flow<String>
        (1..3).asFlow()
            .onEach { delay(100) }
//            .map { requestFlow(it) }
            .flatMapConcat {  requestFlow(it) }
            .collect{
                println("$it at ${System.currentTimeMillis()-startTime}ms")
            }
    }

    @Test
    fun `test flatMapMerge`() = runBlocking {
        val startTime=System.currentTimeMillis()
        //flatMapMerge 并行发射，时间缩短
        (1..3).asFlow()
            .onEach { delay(100) }
            .flatMapMerge {  requestFlow(it) }
            .collect{
                println("$it at ${System.currentTimeMillis()-startTime}ms")
            }
    }

    @Test
    fun `test flatMapLatest`() = runBlocking {
        val startTime=System.currentTimeMillis()
        //flatMapLatest 只保留最新
        (1..3).asFlow()
            .onEach { delay(100) }
            .flatMapLatest {  requestFlow(it) }
            .collect{
                println("$it at ${System.currentTimeMillis()-startTime}ms")
            }
    }


}