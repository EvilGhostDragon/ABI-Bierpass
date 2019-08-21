package `in`.heis.abibierpass

import android.graphics.Color
import android.os.Bundle
import android.os.Debug
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*


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
            btn_acc_register.isEnabled = false
           // if (isFormOk()) {
                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
                register(editText_fname.text.toString(),editText_lname.text.toString(),editText_vulgo.text.toString(),editText_mail.text.toString(),editText_pswd.text.hashCode().toString())
            println("hey")
            //}
            btn_acc_register.isEnabled = true
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


    fun register(fNamea: String, lName: String, vulgo: String, mail: String, password: String){
        //Toast.makeText(context, password, Toast.LENGTH_SHORT).show()

        //val connectionProps = Properties()
        val username = "firetoast"
        val pw = "firetoast"
        val url = "jdbc:" + "mysql" + "://" + "db4free.net" + ":" + "3306" + "/androiddev" + ""




    }
}
