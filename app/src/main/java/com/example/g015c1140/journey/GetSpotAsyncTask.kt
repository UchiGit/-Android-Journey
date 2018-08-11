package com.example.g015c1140.journey

import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray

class GetSpotAsyncTask(cnt: Int): AsyncTask<String, String, String>() {

    val spotListCnt= cnt
    //callBack用
    var callbackGetSpotAsyncTask: CallbackGetSpotAsyncTask? = null


    override fun doInBackground(vararg params: String?): String? {

/*

        //ここでAPIを叩きます。バックグラウンドで処理する内容です。
        var connection: HttpURLConnection? = null
        //var reader: BufferedReader? = null
        // val buffer: StringBuffer
        var jsonArray: JSONArray

        try {
            val url = URL(Setting().SPOT_GET_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()  //ここで指定したAPIを叩いてみてます。

            //ここから叩いたAPIから帰ってきたデータを使えるよう処理していきます。

            val br = BufferedReader(InputStreamReader(connection.inputStream))
            val sb = StringBuilder()
            for (line in br.readLines()) {
                line.run { sb.append(line) }
            }

            val spotIdList = ArrayList<String>()

            try {
                jsonArray = JSONArray(sb.toString())

                for (i in 0 until jsonArray.length()) {
                    println("array.getJSONObject(i):${jsonArray.getJSONObject(i)}")
                    spotIdList.add(jsonArray.getJSONObject(i).getString("spot_id"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            br.close()

            return spotIdList.toString()

            //ここから下は、接続エラーとかJSONのエラーとかで失敗した時にエラーを処理する為のものです。
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //finallyで接続を切断してあげましょう。
        finally {
            connection?.disconnect()
        }
        //失敗した時はnullやエラーコードなどを返しましょう。
        return null
        */

        val arrayList = arrayListOf("1","2","3","4")
        return  arrayList.toString()
    }

    //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result == null){
            Log.d("test GetSpotTask","return null")
            return
        }

        val result = JSONArray(result)
        Log.d("test GetSpotTask","result：$result")

        val spotIdList = arrayListOf<String>()
        spotIdList.add("HTTP-OK")
        /*
        for ( cnt in (result.length() - spotListCnt) until result.length()) {
            spotIdList.add(result.getString(cnt))
        }
        */
        for ( cnt in 0 until result.length()) {
            spotIdList.add(result.getString(cnt))
        }

        callbackGetSpotAsyncTask!!.callback(spotIdList)
        //return spotIdList
    }

    fun setOnCallback(cb: CallbackGetSpotAsyncTask) {
        callbackGetSpotAsyncTask = cb
    }

    open class CallbackGetSpotAsyncTask {
        open fun callback(result: ArrayList<String>){}
    }

}