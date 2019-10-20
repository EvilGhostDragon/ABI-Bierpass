package `in`.heis.abibierpass

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*


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
        activity!!.title = "Login"

        btn_profile_resendmail.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to resend mail")
        }
        btn_profile_resetpw.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to reset pw")

        }
        btn_acc_login.setOnClickListener {
            if (isFormOk()) {
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
                btn_acc_login.isEnabled = false
                progressbar.visibility = View.VISIBLE
                val mail = editText_mail.text.toString().trim()
                val password = editText_pswd.text.toString()
                auth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(activity!!) { task ->
                        btn_acc_login.isEnabled = true
                        progressbar.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val mailconfirmed = user!!.isEmailVerified
                            if (mailconfirmed) {
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "mail")
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                                activity!!.finish()
                                startActivity(activity!!.intent)
                            } else {
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Info")
                                    .setMessage("Deine E-Mail Adresse wurde noch nicht bestätigt.\nÜberprüfe deinen Posteingang und auch Spam-Ordner")
                                    .setPositiveButton("OK") { _, _ ->
                                        SelectMenu(-1, drawer_layout, activity).change()
                                    }
                                    .show()
                                auth.signOut()
                            }
                        } else {
                            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                            Log.i("firebase", task.exception!!.message)
                            if (task.exception!!.message!!.contains("password is invalid") or task.exception!!.message!!.contains(
                                    "There is no user record"
                                )
                            ) {
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Fehler")
                                    .setMessage("Zu viel Bier oder doch nur vertippt.\nÜberprüfe deine Eingabe")
                                    .setPositiveButton("OK") { _, _ ->
                                        editText_pswd.text.clear()
                                    }
                                    .show()
                            } else {
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Fehler")
                                    .setMessage("Ups Bier verschüttet. Fehler können passieren.")
                                    .setPositiveButton("OK") { _, _ ->
                                        SelectMenu(-1, drawer_layout, activity).change()
                                    }
                                    .show()
                            }
                        }
                    }
            }
        }
    }


    private fun isFormOk(): Boolean {
        var state = true
        var item = editText_mail
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
