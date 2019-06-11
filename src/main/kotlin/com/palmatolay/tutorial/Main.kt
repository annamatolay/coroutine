package com.palmatolay.tutorial

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() = runTasks()

private fun runTasks() = runBlocking { // this: CoroutineScope
    launch {
        //        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope {
        // Creates a coroutine scope
        launch {
            //            delay(500L)
            println("Task from nested launch")
        }

//        delay(100L)
        println("Task from coroutine scope") // This line will be printed before the nested launch
    }

    println("Coroutine scope is over") // This line is not printed until the nested launch completes
}


fun expWithCoroutines() {
    println("- Execution started -")
    GlobalScope.async {
        println("Hello ~ Coroutine")
        coroutineScope {

            launch {
                println("task started ~ Coroutine")
                delay(1000)
                println("task done ~ Coroutine")
            }

            //TODO: why executed before launch block execution?
            println("task launched ~ Coroutine")

            async {
                println("async task started ~ Coroutine")
                delay(1000)
                println("async task done ~ Coroutine")
            }
        }
        println("Bye ~ Coroutine")
    }
    println("(main thread sleep started...)")
    Thread.sleep(1500)
    println("- Execution finished -")
}

// https://kotlinlang.org/docs/tutorials/coroutines/coroutines-basic-jvm.html#async-returning-a-value-from-a-coroutine
fun createTonsOfCoroutines2() {
    val deferred = (1..1_000_000).map { n ->
        GlobalScope.async {
            // Make sure that our coroutines actually run in parallel
            // it won't run for 1'000'000 seconds (over 11,5 days)
//            delay(1000)
//            n
            workload(n)
        }
    }
    runBlocking {
        val sum = deferred.sumBy { it.await() }
        println("Sum: $sum")
    }
}

suspend fun workload(n: Int): Int {
    delay(1000)
    return n
}


// https://kotlinlang.org/docs/tutorials/coroutines/coroutines-basic-jvm.html#lets-run-a-lot-of-them
fun createTonsOfThreads() {
    val c = AtomicLong()

    val time = measureTimeMillis {
        for (i in 1..1_000_000L)
            thread(start = true) {
                c.addAndGet(i)
            }
    }
    println(c.get()) // 500000500000
    println(time) // 73155 ms
}

fun createTonsOfCoroutines() {
    val c = AtomicLong()

    val time = measureTimeMillis {
        for (i in 1..1_000_000L)
            GlobalScope.launch {
                c.addAndGet(i)
            }
    }
    println(c.get()) // 500000500000
    println(time) // 813 ms
}

// https://kotlinlang.org/docs/tutorials/coroutines/coroutines-basic-jvm.html#my-first-coroutine
fun startCoroutine() {
    println("Start")

    // Start a coroutine
    val job = GlobalScope.launch {
        delay(2000)
        println("Hello")
    }
    runBlocking {
        delay(1000)
    }
//    Thread.sleep(2000)
    println("Stop")

    runBlocking {
        // https://kotlinlang.org/docs/reference/coroutines/basics.html#waiting-for-a-job
        // with join the execution won't stop till the jo is running
        job.join()
    }
}
