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

        txt_profile_fName.text = token.getString("fName", "")
        txt_profile_lName.text = token.getString("lName", "")
        txt_profile_vulgo.text = token.getString("vulgo", "")
        txt_profile_mail.text = token.getString("mail", "")
        txt_profile_payId.text = token.getString("payId", "")
        txt_profile_permission.text =
            CustomConvert().permissionToString(token.getString("permission", "")!!.toInt())

        btn_profile_changepw.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to change pw")
        }
        btn_profile_changemail.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()
            //TODO("able to change mail")
        }



        //refresh_profile.setColorSchemeColors(Color.RED, Color.BLUE)

        


    }

    fun permissionToString(p: Int): String {
        when (p) {
            0 -> {
                return "No permission! require email confirmation"
            }
            1 -> {
                return "No permission! require data check"
            }
            2 -> {
                return "Keine. Normaler Benutzer"
            }
            10 -> {
                return "Fuchs. Berechtigt letzten 10 Zahlungen zu sehen"
            }
            20 -> {
                return "Bierwart. Berechtigt Guthaben zu verteilen"
            }
            30 -> {
                return "Admin"
            }
            100 -> {
                return "Zer0"
            }
            else -> {
                return "Hacker?"
            }
        }
    }

    companion object


}
