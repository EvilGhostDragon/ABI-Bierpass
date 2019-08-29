package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

val TIMEOUT = 10 * 1000
var msgError = "0001"

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_acc_register).isChecked = true


        btn_acc_register.setOnClickListener {
            // Toast.makeText(context, editText_fname.text, Toast.LENGTH_SHORT).show()

            if (isFormOk()) {
                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
                btn_acc_register.isEnabled = false
                progressbar.visibility = View.VISIBLE

                val json = JSONObject()
                json.put("fName", editText_fname.text)
                json.put("lName", editText_lname.text)
                json.put("vulgo", editText_vulgo.text)
                json.put("mail", editText_mail.text)
                json.put("password", editText_pswd.text)

                HttpTask {
                    if (it == null) {
                        println("connection error")
                        AlertDialog.Builder(context)
                            .setTitle("Fehler")
                            .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + msgError)
                            .setPositiveButton("OK") { dialog, which ->
                                SelectMenu(-1, drawer_layout, activity).change()
                            }
                            .show()

                        return@HttpTask
                    }
                    println(it)

                    btn_acc_register.isEnabled = true
                    progressbar.visibility = View.INVISIBLE

                    AlertDialog.Builder(context)
                        .setTitle("Info")
                        .setMessage("Deine Daten wurden erfolgreich übermittelt und werden in kürze überprüft. \nDu erhältst eine E-Mail sobald du dich anmelden kannst.")
                        .setPositiveButton("OK") { dialog, which ->
                            SelectMenu(-1, drawer_layout, activity).change()
                        }
                        .show()

                }.execute("POST", "https://heis.in/api/abibierpass/db_adduser.php", json.toString())
            }
        }

    }


    private fun isFormOk(): Boolean {
        var state = true
        var item = editText_fname
        while (true) {
            item.setTextColor(Color.BLACK)
            if (item.text.contains(" ") or item.text.isEmpty()) {
                if (item.text.contains(" ")) item.setTextColor(Color.RED)
                state = false
                item.error = "Felder dürfen nicht leer sein oder Leerzeichen beinhalten"
            }
            if ((item == editText_mail) and item.text.isNotEmpty()) {
                if (!item.text.contains("@") or !item.text.contains(".")) {
                    item.setTextColor(Color.RED)
                    state = false
                    item.error = "Gib eine gültige E-Mail Adresse an"
                }
            }
            if ((item == editText_pswd) and (item.text.isNotEmpty())) {
                if (item.text.count() < 6) {
                    state = false
                    item.error = "Dein Passwort muss aus mindestens 6 Zeichen bestehen"
                }
            }
            if ((item == editText_pswd2) and (item.text.isNotEmpty())) {
                if (editText_pswd.text.toString() != item.text.toString()) {
                    item.setTextColor(Color.RED)
                    state = false
                    item.error = "Überprüfe dein Passwort"
                }
            }
            when (item) {
                editText_fname -> {
                    item = editText_lname

                }
                editText_lname -> {
                    item = editText_vulgo
                }
                editText_vulgo -> {
                    item = editText_mail
                }
                editText_mail -> {
                    item = editText_pswd
                }
                editText_pswd -> {
                    item = editText_pswd2
                }
                editText_pswd2 -> {
                    return state
                }
            }

            //if(editText_fname.text == null)Toast.makeText(context, editText_fname.text, Toast.LENGTH_SHORT).show()
        }
        //return state
    }


    class HttpTask(callback: (String?) -> Unit) : AsyncTask<String, Unit, String>() {

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


}
