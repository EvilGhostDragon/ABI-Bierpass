package `in`.heis.abibierpass

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        activity!!.title = "Account erstellen"

        btn_acc_register.setOnClickListener {
            if (isFormOk()) {
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
                btn_acc_register.isEnabled = false
                progressbar.visibility = View.VISIBLE

                val fName = editText_fname.text.toString().trim()
                val lName = editText_lname.text.toString().trim()
                val mail = editText_mail.text.toString().trim()
                val password = editText_pswd.text.toString()
                val vulgo = editText_vulgo.text.toString().trim()
                auth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(activity!!) { task ->
                        btn_acc_register.isEnabled = true
                        progressbar.visibility = View.INVISIBLE
                        if (!task.isSuccessful) {
                            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                            Log.d("firebase", task.exception!!.message)
                            if (task.exception!!.message!!.contains("email address is already in use")) {
                                editText_mail.setTextColor(Color.RED)
                                editText_mail.error = "Diese E-Mail Adresse wird bereits verwendet"
                            }
                            return@addOnCompleteListener
                        } else {
                            Log.d("firebase", "createUserWithEmail:success")
                            val user = auth.currentUser
                            user!!.sendEmailVerification().addOnCompleteListener {
                                if (!it.isSuccessful) {
                                    @Suppress("LABEL_NAME_CLASH")
                                    return@addOnCompleteListener
                                }
                            }
                            val userData = hashMapOf(
                                "Vorname" to fName,
                                "Nachname" to lName,
                                "Vulgo" to vulgo,
                                "Berechtigung" to 0
                            )

                            db.collection("Nutzer")
                                .document(user.uid)
                                .set(userData)
                                .addOnCompleteListener {
                                    @Suppress("LABEL_NAME_CLASH")
                                    if (!it.isSuccessful) return@addOnCompleteListener
                                }
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Info")
                                .setMessage("Deine Daten wurden erfolgreich übermittelt. \nDu erhältst in kürze eine E-Mail mit einem Link zum bestätigen deiner E-Mail Adresse. Überprüfe auch deinen Spam Ordner.")
                                .setPositiveButton("OK") { _, _ ->
                                    SelectMenu(-1, drawer_layout, activity).change()
                                }
                                .show()
                        }
                    }
            }
        }
    }

    private fun isFormOk(): Boolean {
        var state = true
        var item = editText_fname
        while (true) {
            item.setTextColor(Color.BLACK)
            if (item.text.isEmpty()) {
                item.setTextColor(Color.RED)
                state = false
                item.error = "Felder dürfen nicht leer sein"
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
