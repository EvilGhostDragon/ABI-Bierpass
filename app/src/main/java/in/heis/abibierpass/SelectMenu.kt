package `in`.heis.abibierpass

import android.app.Activity
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.extensions.LayoutContainer


class SelectMenu(val itemId: Int?, override val containerView: View?, val activity: Activity?) : LayoutContainer {
    init {
        //   val activity: AppCompatActivity = getActivity(containerView?.context,) as AppCompatActivity
    }

    companion object {
        var view: View? = null
        var act: Activity? = null
    }


    /*fun action() {

        when (itemId) {
            R.id.action_exit -> {
                exitProcess(-1)
            }
            R.id.action_about -> {
                val message =
                    "Diplomarbeit 2018/19\nAutor: Simon Heis\nMitarbeiter: Simon L. Elias K. \nE-Mail: school@heis.in \n\nProjekt im Sinne einer Diplomarbeit an der HTL Anichstraße "
                CreateAlertdialog(containerView?.context!!, message, "About").custom_titel()
            }
            R.id.nav_connection_disc -> {
                BluetoothConnection(containerView?.context!!).disconnect()

                activity!!.nav_view.menu.findItem(R.id.nav_connection_disc).isEnabled = false
                activity.nav_view.menu.findItem(R.id.nav_connection_con).isEnabled = true

                activity.nav_view.menu.findItem(R.id.nav_control).isEnabled = false

                text_nav_selecteddev.text = "Zurzeit keine aktive Verbindung"

                change()

            }
            else -> {
                change()
            }


        }
    }*/

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

            else -> {
                HomeFragment()
                //RegisterFragment()
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
    /*fun makeNewLayout() {


        var selectedDivText: String =
            "Mit ${ConnectionFragment.selectedDevice} verbunden\n${ConnectionFragment.selectedAdress}"
        //println(BluetoothConnection.m_isConnected.toString())

        if (!BluetoothConnection.m_isConnected) CreateAlertdialog(
            view?.context!!,
            "Verbindung konnte nicht aufgebaut werden. Überprüfe deine Einstellungen und ob dein Roboter in Reichweite steht. \n\nWeitere Hilfe findest du im Hilfebereich."
            , null
        ).error()
        else {
            act!!.nav_view.menu.findItem(R.id.nav_control).isEnabled = true
            act!!.nav_view.menu.findItem(R.id.nav_connection_disc).isEnabled = true
            act!!.nav_view.menu.findItem(R.id.nav_connection_con).isEnabled = false

            act!!.text_nav_selecteddev.text = selectedDivText

            CreateAlertdialog(
                view?.context!!,
                "Verbindungsaufbau war erfolgreich\nSteuerung jetzt aktiv",
                null
            ).info()
            SelectMenu(R.id.nav_home, drawer_layout, act!!).change()
        }

        //edit Activ
        //activity!!.nav_view.menu.findItem(R.id.nav_home).isChecked=true
    }*/
}