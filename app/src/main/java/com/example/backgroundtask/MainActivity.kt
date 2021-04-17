package com.example.backgroundtask

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.concurrent.*

class MainActivity : AppCompatActivity() {

    lateinit var rprogress: ProgressBar
    lateinit var hprogress: ProgressBar
    lateinit var switch: Switch
    lateinit var textView: TextView
    lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rprogress = findViewById(R.id.rprogressBar)
        hprogress = findViewById(R.id.hprogressBar)
        switch = findViewById(R.id.switch1)
        textView = findViewById(R.id.textView)
        btn = findViewById(R.id.button)

        rprogress.visibility = View.INVISIBLE
        hprogress.visibility = View.INVISIBLE
        textView.text = ""

        btn.setOnClickListener {
           // AsyncDownloadTask().execute("this is passing String")
           //task()
           // lists()
            sync("this is passing String")
        }

    }

    private fun sync(vararg params: String?){
        //preExecute
        rprogress.visibility = View.VISIBLE
        textView.text = "Downloading..."
        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        //do in backGround
        executorService.execute {
            Log.d("AsyncTask", "Do in BG : parameter : " + params[0])
            Log.d("AsyncTask", "Do in BG : Thread : ${Thread.currentThread().name}")

            for (i in 1..10) {
                try {
                    Thread.sleep(500)
                } catch (ex: Exception) {
                    print(ex.message)
                }
                //onExecuteUpdate
                handler.postAtFrontOfQueue {
                    Log.d("AsyncTask", "onProgressUpdate : ${Thread.currentThread().name} - "+i)
                    hprogress.visibility = View.VISIBLE
                    hprogress.progress = i
                }
            }
            //onpostExecute
            handler.post {
                Log.d("AsyncTask", "onPostExecute : ${Thread.currentThread().name}")
                hprogress.visibility = View.INVISIBLE
                rprogress.visibility = View.INVISIBLE
                textView.text = "Downloaded."
            }
            executorService.shutdown();
        }
    }
    private fun lists() {
        val callableTask = Callable {
            TimeUnit.MILLISECONDS.sleep(1000)
            "Current time :: " + Thread.currentThread().name
        }
        val executor: ExecutorService = Executors.newFixedThreadPool(1)

        val tasksList: List<Callable<String>> = Arrays.asList(callableTask, callableTask, callableTask)
        try {
            val results: List<Future<String>> = executor.invokeAll(tasksList)
            for (result in results) {
                System.out.println(result.get())
            }
        } catch (e1: InterruptedException) {
            e1.printStackTrace()
        }
        val result: Future<String> = executor.submit(callableTask)
        while (result.isDone() === false) {
            try {
                System.out.println("The method return value : " + result.get())
                break
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            //Sleep for 1 second
            try {
                Thread.sleep(1000L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        executor.shutdownNow();
    }
    private fun task() {

        val exec = Executors.newFixedThreadPool(5)
        for (i in 1..10) {
            exec.execute {println("Running in: " + Thread.currentThread().name) }
        }
        exec.shutdown()
        try {
            val b = exec.awaitTermination(50, TimeUnit.SECONDS)
            println("All done: $b")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
    class AsyncDownloadTask : AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("AsyncTask", "onPreExecute : ${Thread.currentThread().name}")
            //textView.text = "Downloading..."
        }

        override fun doInBackground(vararg params: String?): String {
            Log.d("AsyncTask", "Do in BG : parameter : " + params[0])
            Log.d("AsyncTask", "Do in BG : Thread : ${Thread.currentThread().name}")

            for (i in 1..10) {
                try {
                    Thread.sleep(500)
                } catch (ex: Exception) {
                    print(ex.message)
                }
                publishProgress(i)
            }
            return "Downloaded."
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            Log.d("AsyncTask", "onProgressUpdate : ${Thread.currentThread().name}")
            Log.d("AsyncTask", "onProgressUpdate : " + values[0])

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("AsyncTask", "onPostExecute : ${Thread.currentThread().name}")

        }
    }
}
