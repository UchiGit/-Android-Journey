package com.example.g015c1140.journey

import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray

class GetSpotAsyncTask(cnt: Int): AsyncTask<String, String, String>() {
    val SPOT_LIST_CNT = cnt

    //callBack用
    var callbackGetSpotAsyncTask: CallbackGetSpotAsyncTask? = null
    private lateinit var result:String

    override fun doInBackground(vararg params: String?): String? {
/*
        //ここでAPIを叩きます。バックグラウンドで処理する内容です。
        var connection: HttpURLConnection? = null

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
            br.close()

            var spotIdList = ArrayList<String>()

            try {
                val jsonObject = JSONObject(sb.toString())
                val jsonArray = jsonObject.getJSONArray("record")

                Log.d("test GSAT", "${jsonArray.length()}             ${sb.length}")
                for (i in 0 until jsonArray.length()) {
                    Log.d("test","array.getJSONObject(i): ${jsonArray.getJSONObject(i)}")
                    spotIdList.add(jsonArray.getJSONObject(i).getString("spot_id"))
                }
                result =  spotIdList.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

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
        return result
*/
        return arrayListOf("1","2","3","4","5","7","8","9","10","11").toString()
    }

    //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result == null){
            Log.d("test GetSpotTask","return null")
            callbackGetSpotAsyncTask!!.callback(arrayListOf("RESULT-NG"))
            return
        }

        val result = JSONArray(result)
        Log.d("test GetSpotTask","result：$result")

        val spotIdList = arrayListOf<String>()
        spotIdList.add("RESULT-OK")

        for ( cnt in (result.length() - 5/*POT_LIST_CNT*/) until result.length()) {
            spotIdList.add(result.getString(cnt))
        }

        callbackGetSpotAsyncTask!!.callback(spotIdList)
    }

    fun setOnCallback(cb: CallbackGetSpotAsyncTask) {
        callbackGetSpotAsyncTask = cb
    }

    open class CallbackGetSpotAsyncTask {
        open fun callback(result: ArrayList<String>){}
    }

}