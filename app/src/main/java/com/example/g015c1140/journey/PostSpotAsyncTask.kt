package com.example.g015c1140.journey

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class PostSpotAsyncTask: AsyncTask<MutableList<SpotData>, String, String>() {

    //callBack用
    var callbackPostSpotAsyncTask: CallbackPostSpotAsyncTask? = null

    //insert
    override fun doInBackground(vararg params: MutableList<SpotData>?): String? {

        //ここでAPIを叩きます。バックグラウンドで処理する内容です。
        var connection: HttpURLConnection? = null
        var result: String? = null
        val url = URL(Setting().SPOT_POST_URL)

        val spotList = params[0]
        if (spotList == null) {
            println("PostSpot 引数異常URL：$params[0]")
            return null
        }

        spotList.forEach {
            try {
                connection = url.openConnection() as HttpURLConnection
                connection!!.requestMethod = "POST"
                connection!!.instanceFollowRedirects = false
                connection!!.doOutput = true
                connection!!.connect()  //ここで指定したAPIを叩いてみてます。

                var out: OutputStream? = null
                try {
                    out = connection!!.outputStream
                    val spotList = params[0]
                    if (spotList == null) {
                        println("PostSpot 引数異常URL：$params[0]")
                        return null
                    }

                    out.write(("user_id=${Setting().USER_ID}" +
                            "&spot_title=${it.title}" +
                            "&spot_address=${it.latitude},${it.longitude}" +
                            "&spot_comment=${it.comment}" +
                            "&spot_image_A=${it.image_A}" +
                            "&spot_image_B=${it.image_B}" +
                            "&spot_image_C=${it.image_C}"
                            ).toByteArray()
                    )
                    out.flush()
                    Log.d("test", "flush")

                } catch (e: IOException) {
                    // POST送信エラー
                    e.printStackTrace()
                    result = "POST送信エラー：　"
                } finally {
                    out?.close()
                }

                val status = connection!!.responseCode
                when (status) {
                    HttpURLConnection.HTTP_OK -> result = "HTTP-OK"
                    else -> {
                        result += "status=$status"
                        return result
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (connection != null) {
                    connection!!.disconnect()
                }
            }
        }
        return result
        //finallyで接続を切断してあげましょう。
    }

    //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Log.d("test PostSpot","onPostEx")
        when(result){
            "HTTP-OK" -> {
                Log.d("test PostSpot","HTTP-OK")
                callbackPostSpotAsyncTask!!.callback("RESULT-OK")
                return
            }

            else ->{
                Log.d("test PostSpot","HTTP-NG")
                callbackPostSpotAsyncTask!!.callback("RESULT-NG")
                return
            }
        }
    }

    fun setOnCallback(cb: CallbackPostSpotAsyncTask) {
        callbackPostSpotAsyncTask = cb
    }

    open class CallbackPostSpotAsyncTask {
        open fun callback(result: String) {}
    }

}