package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject


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
                        println("connection error - checkuser")
                        AlertDialog.Builder(context)
                            .setTitle("Fehler")
                            .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + HttpTask.msgError)
                            .setPositiveButton("OK") { dialog, which ->
                                SelectMenu(R.id.nav_acc_register, drawer_layout, activity).change()
                            }
                            .show()

                        return@HttpTask
                    }
                    val itJson = JsonParser().parse(it).asJsonObject
                    println(itJson)

                    btn_acc_register.isEnabled = true
                    progressbar.visibility = View.INVISIBLE

                    if (itJson.get("result").asInt == 1) {
                        editText_mail.setTextColor(Color.RED)
                        editText_mail.error = "Diese E-Mail Adresse wird bereits verwendet"

                        return@HttpTask
                    }


                    HttpTask { it2 ->
                        if (it2 == null) {
                            println("connection error - adduser")
                            AlertDialog.Builder(context)
                                .setTitle("Fehler")
                                .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + HttpTask.msgError)
                                .setPositiveButton("OK") { dialog, which ->
                                    SelectMenu(R.id.nav_acc_register, drawer_layout, activity).change()
                                }
                                .show()

                            return@HttpTask
                        }
                        val itJson2 = JsonParser().parse(it2).asJsonObject
                        println(itJson2)
                        btn_acc_register.isEnabled = true
                        progressbar.visibility = View.INVISIBLE
                        if (itJson2.get("result").asInt == 1) {
                            AlertDialog.Builder(context)
                                .setTitle("Info")
                                .setMessage("Deine Daten wurden erfolgreich übermittelt und werden in kürze überprüft. \nDu erhältst eine E-Mail sobald du dich anmelden kannst.")
                                .setPositiveButton("OK") { dialog, which ->
                                    SelectMenu(-1, drawer_layout, activity).change()
                                }
                                .show()
                        }
                    }.execute("POST", "https://abidigital.tk/api/db_adduser.php", json.toString())


                }.execute("POST", "https://abidigital.tk/api/db_checkuser.php", json.toString())


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
        }
    }
}
