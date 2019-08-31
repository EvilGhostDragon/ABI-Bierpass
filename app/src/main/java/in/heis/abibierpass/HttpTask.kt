package `in`.heis.abibierpass

import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


val TIMEOUT = 10 * 1000

class HttpTask(callback: (String?) -> Unit) : AsyncTask<String, Unit, String>() {

    companion object {
        var msgError = "0001"
    }

    var callback = callback

    override fun doInBackground(vararg params: String): String? {
        val url = URL(params[1])
        val httpClient = url.openConnection() as HttpURLConnection
        httpClient.readTimeout = TIMEOUT
        httpClient.connectTimeout = TIMEOUT
        httpClient.requestMethod = params[0]

        if (params[0] == "POST") {
            httpClient.instanceFollowRedirects = false
            httpClient.doOutput = true
            httpClient.doInput = true
            httpClient.useCaches = false
            httpClient.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            if (params[0] == "POST") {
                httpClient.connect()
                val os = httpClient.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))

                writer.write(params[2])
                writer.flush()
                writer.close()
                os.close()
            }
            if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                val stream = BufferedInputStream(httpClient.inputStream)
                val data: String = readStream(inputStream = stream)
                return data
            } else {
                println("ERROR ${httpClient.responseCode}")
                msgError = "${httpClient.responseCode}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            httpClient.disconnect()
        }

        return null
    }

    fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        callback(result)
    }
}