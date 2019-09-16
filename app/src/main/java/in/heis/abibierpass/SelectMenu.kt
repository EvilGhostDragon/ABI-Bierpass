package `in`.heis.abibierpass

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess


class SelectMenu(val itemId: Int?, override val containerView: View?, val activity: Activity?) : LayoutContainer {

    val token = activity?.getSharedPreferences(`in`.heis.abibierpass.key, Context.MODE_PRIVATE)

    init {
        //   val activity: AppCompatActivity = getActivity(containerView?.context,) as AppCompatActivity
    }


    fun action() {

        when (itemId) {
            R.id.action_exit -> {
                exitProcess(-1)
            }
            R.id.action_about -> {
                println("about")
            }
            R.id.action_logout -> {

                AlertDialog.Builder(activity)
                    .setTitle("Info")
                    .setMessage("Wirklich abmelden?")
                    .setPositiveButton("Ja") { dialog, which ->
                        activity!!.recreate()
                        token!!.edit().putBoolean("loggedin", false).apply()
                    }
                    .setNegativeButton("Nein") { dialog, which ->

                    }
                    .show()


            }

            else -> {
                println("Action ERROR")
            }


        }
    }

    fun change(): Boolean {
        //activity!!.nav_view.menu.findItem(R.id.nav_home).isChecked = true
        val fragment = when (itemId) {
            R.id.nav_home -> {
                HomeFragment()
            }
            R.id.nav_acc_login -> {
                LoginFragment()
            }
            R.id.nav_acc_register -> {
                RegisterFragment()
            }
            R.id.nav_acc_profile -> {
                ProfileFragment()
            }
            R.id.nav_acc_admin -> {
                AdminFragment()
            }
            R.id.nav_transactions -> {
                BlockchainFragment()
            }

            else -> {
                HomeFragment()
            }
        }

        (activity as FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.ContentPlaceholder, fragment)
            .commit()

        return true
    }

    /**
     * Funktion: makeNewLayout
     * Input/Output: -
     * Beschreibung: Nach erfolgreichen Verbinden mit RasPi/Arduino wird der Steuerbereich der App und der Verbindung trennen Button aktiviert. Deaktiverit wird der Verbindung herstellen Button
     *                  (1) Dialog mit Hinweis ob Verbindung erfolgreich war
     *                  (2) Aktivierung und Deaktivierung der genannten Menus
     *                  (3) Weiterleiung auf das Homefragment
     */
    fun makeNewLayout(permission: Int) {
        with(nav_view.menu) {
            findItem(R.id.nav_acc_register).isVisible = false
            findItem(R.id.nav_acc_register).isEnabled = false
            findItem(R.id.nav_acc_login).isVisible = false
            findItem(R.id.nav_acc_login).isEnabled = false


            //}
            //val see = with(nav_view.menu){
            findItem(R.id.nav_acc_profile).isVisible = true
            findItem(R.id.nav_acc_profile).isEnabled = true
            findItem(R.id.action_logout).isVisible = true
            findItem(R.id.action_logout).isEnabled = true

            if (permission >= 10) {
                findItem(R.id.nav_transactions).isVisible = true
                findItem(R.id.nav_transactions).isEnabled = true
            }
            if (permission >= 20) {
                findItem(R.id.nav_acc_admin).isVisible = true
                findItem(R.id.nav_acc_admin).isEnabled = true
            }
        }
        change()

    }
}