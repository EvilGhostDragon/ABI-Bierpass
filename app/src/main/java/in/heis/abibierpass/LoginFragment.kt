package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject


class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_acc_login).isChecked = true

        btn_profile_resendmail.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to resend mail")
        }
        btn_profile_resetpw.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to reset pw")

        }

        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        btn_acc_login.setOnClickListener {
            if (isFormOk()) {
                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
                btn_acc_login.isEnabled = false
                progressbar.visibility = View.VISIBLE

                val json = JSONObject()
                json.put("mail", editText_mail.text)
                json.put("password", editText_pswd.text)
                json.put("action", "login")

                HttpTask {
                    if (it == null) {
                        println("connection error")
                        AlertDialog.Builder(context)
                            .setTitle("Fehler")
                            .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + HttpTask.msgError)
                            .setPositiveButton("OK") { dialog, which ->
                                SelectMenu(-1, drawer_layout, activity).change()
                            }
                            .show()

                        return@HttpTask
                    }
                    println(it)
                    val itJson = JsonParser().parse(it).asJsonObject
                    if (itJson.get("result").asInt == 0) {
                        AlertDialog.Builder(context)
                            .setTitle("Fehler")
                            .setMessage("Zu viel Bier oder doch nur vertippt.\nÜberprüfe deine Eingabe")
                            .setPositiveButton("OK") { dialog, which ->
                                editText_pswd.text.clear()
                            }
                            .show()

                        //return@HttpTask
                    } else if ((itJson.get("result").asInt == 1) and (itJson.get("permission").asInt >= 2)) {
                        val editor = token.edit()
                        with(editor) {
                            putBoolean("loggedin", true)
                            putString("fName", itJson.get("fName").asString)
                            putString("lName", itJson.get("lName").asString)
                            putString("mail", itJson.get("mail").asString)
                            putString("vulgo", itJson.get("vulgo").asString)
                            putString("payId", itJson.get("payId").asString)
                            putString("permission", itJson.get("permission").asString)
                        }.apply()



                        activity!!.finish()
                        startActivity(activity!!.intent)


                    } else if ((itJson.get("result").asInt == 1) and (itJson.get("permission").asInt == 0)) {
                        AlertDialog.Builder(context)
                            .setTitle("Info")
                            .setMessage("Deine E-Mail Adresse wurde noch nicht bestätigt.\nÜberprüfe deinen Posteingang und auch Spam-Ordner")
                            .setPositiveButton("OK") { dialog, which ->
                                SelectMenu(-1, drawer_layout, activity).change()
                            }
                            .show()
                    } else {
                        AlertDialog.Builder(context)
                            .setTitle("Info")
                            .setMessage("Du wurdest noch nicht freigeschalten. Du erhältst eine E-Mail sobald es soweit ist.")
                            .setPositiveButton("OK") { dialog, which ->

                                SelectMenu(-1, drawer_layout, activity).change()
                            }
                            .show()
                    }


                    btn_acc_login.isEnabled = true
                    progressbar.visibility = View.INVISIBLE


                }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())
            }
        }
    }


    private fun isFormOk(): Boolean {
        var state = true
        var item = editText_mail
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

            when (item) {
                editText_mail -> {
                    item = editText_pswd
                }
                editText_pswd -> {
                    return state
                }
            }
        }
    }
}
