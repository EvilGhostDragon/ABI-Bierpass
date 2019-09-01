package `in`.heis.abibierpass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
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

        txt_profile_fName.text = "fname" + token.getString("fName", "")
        txt_profile_lName.text = "lname: " + token.getString("lName", "")
        txt_profile_vulgo.text = "vulgo: " + token.getString("vulgo", "")
        txt_profile_mail.text = "mail: " + token.getString("mail", "")
        txt_profile_payId.text = "payId: " + token.getString("payId", "")
        txt_profile_permission.text = "permission: " + permissionToString(token.getInt("permission", 0))
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
                return "Normal User"
            }
            10 -> {
                return "Fuchs"
            }
            100 -> {
                return "Zer0"
            }
            else -> {
                return "Hacker?"
            }
        }
    }


}