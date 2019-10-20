package `in`.heis.abibierpass

import `in`.heis.abibierpass.data.App
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.nav_view.menu.findItem(R.id.nav_acc_profile).isChecked = true
        activity!!.title = "Deine Daten"
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        switch_notification.isChecked = token.getBoolean("ordernotification", false)
        val user = auth.currentUser
        db.collection("Nutzer").document(user!!.uid).get().addOnSuccessListener {
            val fName = it.data!!["Vorname"].toString()
            val lName = it.data!!["Nachname"].toString()
            val vulgo = it.data!!["Vulgo"].toString()
            val permission =
                CustomConvert().permissionToString(it.data!!["Berechtigung"].toString().toInt())
            txt_profile_fName.setText(fName)
            txt_profile_lName.setText(lName)
            txt_profile_vulgo.setText(vulgo)
            txt_profile_mail.setText(user.email)
            txt_profile_uid.setText(user.uid)
            txt_profile_permission.setText(permission)
        }

        super.onViewCreated(view, savedInstanceState)
        val darkThemeSwitch: SwitchMaterial = view.findViewById(R.id.switch_darktheme)
        val preferenceRepository = (requireActivity().application as App).preferenceRepository

        preferenceRepository.isDarkThemeLive.observe(this, Observer { isDarkTheme ->
            isDarkTheme?.let { darkThemeSwitch.isChecked = it }
        })

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }

        switch_notification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(FirebaseMessaging.getInstance()) {
                    if (token.getInt("permission", 0) >= 10) subscribeToTopic("Bestellungen")
                    subscribeToTopic(auth.currentUser!!.uid)
                    subscribeToTopic("Normal")
                }
                    .addOnCompleteListener {
                        if (it.isSuccessful) token.edit().putBoolean(
                            "ordernotification",
                            true
                        ).apply()
                    }
            } else {
                with(FirebaseMessaging.getInstance()) {
                    unsubscribeFromTopic("Normal")
                    subscribeToTopic(auth.currentUser!!.uid)
                    unsubscribeFromTopic("Bestellungen")
                }
                    .addOnCompleteListener {
                        if (it.isSuccessful) token.edit().putBoolean("notification", false).apply()
                    }
            }
        }

        btn_profile_changepw.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to change pw")
        }
        btn_profile_edit.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
        }

        btn_signout.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setTitle("Info")
                .setMessage("Wirklich abmelden?")
                .setPositiveButton("Ja") { _, _ ->
                    auth.signOut()
                    activity!!.finish()
                    startActivity(activity!!.intent)
                }
                .setNegativeButton("Nein") { _, _ ->
                }
                .show()
        }
    }
}
