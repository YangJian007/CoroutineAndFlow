package com.yang.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.lang.ArithmeticException
import java.lang.RuntimeException

/**
 * @author 杨剑
 * @fileName
 * @date 2022-09-12
 * @describe  上游异常通过caught操作符捕获，下游异常通过try caught方式捕获
 * @changeUser
 * @changTime
 */
class FlowTest03 {

    fun simpleFlow() = flow<Int> {
        for (i in 1..3) {
            println("emit $i")
            emit(i)
        }
    }


    @Test
    fun `test flow exception`() = runBlocking {
        try {
            simpleFlow().collect {
                println(it)
                check(it <= 1) {
                    "Collected $it"
                }
            }
        } catch (e: Exception) {
            println("Caught $e")
        }
    }

    @Test
    fun `test flow exception2`() = runBlocking {
        flow {
            emit(1)
            throw ArithmeticException("Div 0")
        }
            .catch {
                println("Caught $it")
                emit(10) //捕获到异常后，可以补发事件
            }
            .flowOn(Dispatchers.IO)
            .collect { println(it) }
    }

    fun simpleFlow2() = (1..3).asFlow()

    @Test
    fun `test flow complete in finally`() = runBlocking {
        try {
            simpleFlow2().collect { println(it) }
        } catch (e: Exception) {
            println("Done")
        }
    }

    @Test
    fun `test flow complete in onCompletion`() = runBlocking {

        simpleFlow2()
            .onCompletion { println("Done") }
            .collect { println(it) }

    }

    fun simpleFlow3() = flow<Int> {
        emit(1)
        throw RuntimeException()
    }

    @Test
    fun `test flow complete in onCompletion2`() = runBlocking {
        //onCompletion 可以获取到未捕获异常，但不能捕获
        simpleFlow3()
            .onCompletion { excetion ->
                if (excetion != null) {
                    println("Flow completed exceptionally")
                }
            }
//            .catch { println("Caught $it") }
            .collect { println(it) }

    }

    @Test
    fun `test flow complete in onCompletion3`() = runBlocking {
        //也可以获取到下游的异常
        simpleFlow2()
            .onCompletion { excetion ->
                if (excetion != null) {
                    println("Flow completed exceptionally")
                }
            }.collect{
                println(it)
                check(it<=1) { "Collected $it"}
            }
    }
}