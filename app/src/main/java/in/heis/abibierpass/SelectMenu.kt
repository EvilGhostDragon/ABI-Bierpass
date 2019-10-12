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
    /**
     * Funktion: action
     * Input/Output: -
     * Beschreibung: Ausfüren von kleineren Aktionen, welche kein Fragment benötigen
     */
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
                        auth.signOut()
                        activity!!.recreate()
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

    /**
     * Funktion: change
     * Input/Output: -
     * Beschreibung: Das im Menü ausgewählte Fragment laden
     *                  (+) Überprüfung welcher Menüpunkt ausgewählt wurde
     *                  (+) Laden des entsprechende Fragments im Platzhalter
     */
    fun change(): Boolean {
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
            R.id.nav_orderbeer -> {
                OrderBeerFragment()
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
     * Input: permission
     * Output: -
     * Beschreibung: Nach dem erfolgreichen Anmelden werden Menüpunkte freigegeben bzw versteckt
     *                  (+) Abh#ngig der Berechtigung werden weitere Menüpunkte freigegeben
     *                  (+) Anschließende Weiterleiung auf das Homefragment
     */
    fun makeNewLayout(permission: Long) {
        if (permission < 2) return
        with(nav_view.menu) {
            findItem(R.id.nav_acc_register).isVisible = false
            findItem(R.id.nav_acc_register).isEnabled = false
            findItem(R.id.nav_acc_login).isVisible = false
            findItem(R.id.nav_acc_login).isEnabled = false

            findItem(R.id.nav_acc_profile).isVisible = true
            findItem(R.id.nav_acc_profile).isEnabled = true
            findItem(R.id.action_logout).isVisible = true
            findItem(R.id.action_logout).isEnabled = true
            findItem(R.id.nav_orderbeer).isVisible = true
            findItem(R.id.nav_orderbeer).isEnabled = true

            if (permission >= 10) {
                findItem(R.id.nav_transactions).isVisible = true
                findItem(R.id.nav_transactions).isEnabled = true
            }
            if (permission >= 20) {
                findItem(R.id.nav_acc_admin).isVisible = true
                findItem(R.id.nav_acc_admin).isEnabled = true
            }

        }
        if (permission >= 100) {

        }
        change()

    }
}