package com.example.g015c1140.journey

import android.os.AsyncTask
import android.util.Log

class PostPlanAsyncTask: AsyncTask<String, String, String>() {

    //callBack用
    var callbackPostPlanAsyncTask: CallbackPostPlanAsyncTask? = null


    //insert
    override fun doInBackground(vararg params: String?): String? {
        /*
        //ここでAPIを叩きます。バックグラウンドで処理する内容です。
        var connection: HttpURLConnection? = null
        var result: String? = null

        try {
            val url =  URL(Setting().PLAN_POST_URL)

            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.instanceFollowRedirects = false
            connection.doOutput = true
            connection.connect()  //ここで指定したAPIを叩いてみてます。


            var out: OutputStream? = null
            try {

                for (_cnt in 0 until 6){
                    if (params[_cnt] == null){
                        println("POSTTASK 引数異常WRITE：${params[_cnt]}")
                        return null
                    }
                }

                out = connection.outputStream
                out.write((
                        "user_id=${Setting().USER_ID}" +
                                "&plan_title=${params[0]}" +
                                "&plan_comment=${params[1]}" +
                                "&transportation=${params[2]}" +
                                "&price=${params[3]}"+
                                "&area=${params[4]}"
                        ).toByteArray()
                )

                for (_cnt in 5 until params.size) {
                    var _cntAlpha = when (_cnt) {
                        5 -> "a"
                        6 -> "b"
                        7 -> "c"
                        8 -> "d"
                        9 -> "e"
                        10 -> "f"
                        11 -> "g"
                        12 -> "h"
                        13 -> "i"
                        14 -> "j"
                        15 -> "k"
                        16 -> "l"
                        17 -> "m"
                        18 -> "n"
                        19 -> "o"
                        20 -> "p"
                        21 -> "q"
                        22 -> "r"
                        23 -> "s"
                        24 -> "t"
                        else -> "X"
                    }
                    out.write(("&spot_id_$_cntAlpha=${params[_cnt]}").toByteArray())
                }
                out.flush()
                Log.d("debug", "flush")

            } catch (e: IOException) {
                // POST送信エラー
                e.printStackTrace()
                result = "POST送信エラー:　"
            } finally {
                out?.close()
            }

            val status = connection.responseCode
            when (status) {
                HttpURLConnection.HTTP_OK -> result = "HTTP-OK"
                else -> result += "status=$status"
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }
        return result
        //finallyで接続を切断してあげましょう。
        //失敗した時はnullやエラーコードなどを返しましょう。
        */
        return "HTTP-OK"
    }

    //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Log.d("test PlanSpot","HTTP")
        callbackPostPlanAsyncTask!!.callback(result!!)
        return

    /*
        when(result){
            "HTTP-OK" -> {
                Log.d("test PostSpot","HTTP-OK")
                callbackPostPlanAsyncTask!!.callback("RESULT-OK")
                return
            }

            else ->{
                Log.d("test PostSpot","HTTP-NG")
                callbackPostPlanAsyncTask!!.callback("RESULT-NG")
                return
            }
        }
*/
    }

    fun setOnCallback(cb: CallbackPostPlanAsyncTask) {
        callbackPostPlanAsyncTask = cb
    }

    open class CallbackPostPlanAsyncTask {
        open fun callback(result: String){}
    }
}