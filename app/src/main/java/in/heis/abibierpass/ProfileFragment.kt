package `in`.heis.abibierpass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.nav_view.menu.findItem(R.id.nav_acc_profile).isChecked = true
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        val user = auth.currentUser
        db.collection("Nutzer").document(user!!.uid).get().addOnSuccessListener {
            val fName = it.data!!["Vorname"].toString()
            val lName = it.data!!["Nachname"].toString()
            val vulgo = it.data!!["Vulgo"].toString()
            val permission =
                CustomConvert().permissionToString(it.data!!["Berechtigung"].toString().toInt())
            txt_profile_fName.text = fName
            txt_profile_lName.text = lName
            txt_profile_vulgo.text = vulgo
            txt_profile_mail.text = user.email
            txt_profile_uid.text = user.uid
            txt_profile_permission.text = permission
        }



        btn_profile_changepw.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to change pw")
            //user.updateEmail()
        }
        btn_profile_changemail.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to change mail")
            //user.updateP
        }



        //refresh_profile.setColorSchemeColors(Color.RED, Color.BLUE)

        


    }
}
