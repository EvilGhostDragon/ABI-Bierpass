package `in`.heis.abibierpass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_home).isChecked = true
        activity!!.title = "Home"
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        val permission = token.getInt("permission", 0)
        if (permission == 100) {
            btn_devtest1.isVisible = true
            btn_devtest2.isVisible = true
        }
        if (permission >= 2) btn_quicklink1.isVisible = true

        btn_quicklink1.setOnClickListener {
            val navView: NavigationView = activity!!.findViewById(R.id.nav_view)
            navView.setCheckedItem(navView.menu.findItem(R.id.nav_orderbeer))
            SelectMenu(R.id.nav_orderbeer, drawer_layout, activity).change()
        }

        btn_devtest1.setOnClickListener {

        }
        btn_devtest2.setOnClickListener {

        }
    }
}
