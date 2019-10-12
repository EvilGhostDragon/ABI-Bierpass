package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin.*
import java.util.*
import kotlin.collections.ArrayList


class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    class User(
        var fName: String,
        var lName: String,
        var vulgo: String,
        var uid: String,
        var permission: Int
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_acc_admin).isChecked = true
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)

        /**
         * Beschreibung: Sicher stellen, dass nie zwei Switches gleichzeitig aktiviert sind
         *                  (+) Diese werden zum Filtern der mySQL Anfrage genutzt
         *                  (+) Keine Funktion beim klicken; ledeglich der Status wird beim Suchen übernommen
         */
        switch_showall.setOnClickListener {
            if (switch_showall.isChecked) {
                switch_showfox.isChecked = false
                switch_shownew.isChecked = false
                switch_shownormal.isChecked = false
            } else switch_showall.isChecked = true
        }
        switch_showfox.setOnClickListener {
            if (switch_showfox.isChecked) {
                switch_showall.isChecked = false
                switch_shownew.isChecked = false
                switch_shownormal.isChecked = false
            } else switch_showall.isChecked = true
        }
        switch_shownew.setOnClickListener {
            if (switch_shownew.isChecked) {
                switch_showfox.isChecked = false
                switch_showall.isChecked = false
                switch_shownormal.isChecked = false
            } else switch_showall.isChecked = true
        }
        switch_shownormal.setOnClickListener {
            if (switch_shownormal.isChecked) {
                switch_showfox.isChecked = false
                switch_shownew.isChecked = false
                switch_showall.isChecked = false
            } else switch_showall.isChecked = true
        }
        /**
         * Beschreibung: Anzeige aller Nutzer mit Filterfunktion
         *                  (+) Benutzer können via Switches grob gefiltert werden
         *                  (+) Benutzer können durch Suchbegriffe gesucht werden
         *                  (+) Abhängig der eigenen Berechtigung:
         *                      (-) Nutzer einer höheren/niederen Berechtigungsstufe zuordnen
         *                      (-) Nutzer sperren/freischalten
         *                      (-) Bier-Coins gutschreiben
         */
        btn_show.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            btn_show.isEnabled = false
            var userRef = db.collection("Nutzer").get()

            //var userList = hashMapOf<String,Any>()
            val userList = mutableListOf<User>()
            if (switch_showfox.isChecked) userRef =
                db.collection("Nutzer").whereEqualTo("Berechtigung", 10).get()
            if (switch_shownew.isChecked) userRef =
                db.collection("Nutzer").whereEqualTo("Berechtigung", 0).get()
            if (switch_shownormal.isChecked) userRef =
                db.collection("Nutzer").whereEqualTo("Berechtigung", 2).get()
            if (editText_search.text.isNotEmpty()) {
                userRef =
                    db.collection("Nutzer").whereEqualTo("Vulgo", editText_search.text.toString())
                        .get()
            }
            userRef
                .addOnCompleteListener {
                    progressbar.visibility = View.INVISIBLE
                    btn_show.isEnabled = true
                }
                .addOnSuccessListener { result ->
                    userList.clear()
                    for (user in result) {
                        val fName = user.data["Vorname"].toString()
                        val lName = user.data["Nachname"].toString()
                        val vulgo = user.data["Vulgo"].toString()
                        val permission = user.data["Berechtigung"].toString().toInt()
                        userList.add(
                            User(
                                fName,
                                lName,
                                vulgo,
                                user.id,
                                permission
                            )
                        )
                    }

                    val adapter = UserAdapter(context!!, ArrayList(userList))
                    listview_user.adapter = adapter
                    listview_user.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, i, _ ->
                            val fName = userList[i].fName
                            val lName = userList[i].lName
                            val vulgo = userList[i].vulgo
                            val uid = userList[i].uid
                            val permission = userList[i].permission
                            var permissionOwn: Int = 0
                            db.collection("Nutzer").document(auth.currentUser!!.uid).get()
                                .addOnSuccessListener {
                                    permissionOwn = it.data!!["Berechtigung"].toString().toInt()
                                    println(permissionOwn)



                                    println(permission + permissionOwn)
                                    var message =
                                        "Vorname: $fName\nNachname: $lName\nVulgo: $vulgo\n$uid\n\n"

                                    if (permission <= 1) {
                                        message += if (permission == 0) "Wichtig:\nDieser Benutzer hat seine E-Mail Adresse noch nicht bestätigt oder wurde gesperrt! Trotzdem freischalten?"
                                        else "Den ausgewählten Benutzer jetzt freischalten?"
                                        /**
                                         * AlerDialog:
                                         * Beschreibung: Nutzer freischalten
                                         *                  (+) Gesperrte Nutzer oder welche deren E-Mail Adresse noch nicht bestätigt wurden freischalten (mit Hinweis auf dies)
                                         *                  (+) Nutzer einfach freischalten
                                         */
                                        AlertDialog.Builder(context)
                                            .setTitle("Ausgewählter Benutzer")
                                            .setMessage(message)

                                            .setPositiveButton("Ja") { _, _ ->
                                                //UpdateUser().permission(mail, 2)
                                                db.collection("Nutzer").document(uid)
                                                    .update("Berechtigung", 2)
                                            }
                                            .setNegativeButton("Nein") { _, _ ->
                                                //SelectMenu(-1, drawer_layout, activity).change()
                                            }
                                            .show()
                                    } else {
                                        /**
                                         * AlerDialog:
                                         * Beschreibung: Auswahl zwischen Guthaben aufladen oder Berechtigung ändern
                                         *                  (+) Berechtigung ändern: Anzeige eines weiteren AlertDialog
                                         *                  (+) Guthaben aufladen: Anzeige eines weiteren AlertDialog
                                         */
                                        AlertDialog.Builder(context)
                                            .setTitle("Ausgewählter Benutzer")
                                            .setMessage(message)

                                            .setPositiveButton("Berechtigung Ändern") { _, _ ->
                                                if (permission < permissionOwn) {
                                                    /**
                                                     * AlerDialog:
                                                     * Beschreibung: Berechtigung erhöhen/vermindern abhängig der eigenen Berechtiungsstufe
                                                     *                  (+) Nutzer (20+) sind berechtigt andere Nutzer weiter zu berechtigen - maximal eine Stufe unter der eigenen
                                                     *                  (+) Nutzer (50+) sind berechtigt Nutzer zu Sperren
                                                     *                  (+) Nutzer ist es nicht möglich andere Nutzer, welche höher gestuft sind, Berechtiungen zu entziehne
                                                     */
                                                    val userRef =
                                                        db.collection("Nutzer").document(uid)

                                                    AlertDialog.Builder(context)
                                                        .setTitle("Ausgewählter Benutzer")
                                                        .setMessage(
                                                            message + "Berechtigungshierarchie: \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        0
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        1
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        2
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        10
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        20
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        50
                                                                    ) + " \n\t- " +
                                                                    CustomConvert().permissionToString(
                                                                        100
                                                                    )
                                                        )
                                                        .setNegativeButton("Berechtigung Erhöhen") { _, _ ->
                                                            when {
                                                                (permissionOwn >= 20) and (permission < 10)
                                                                -> //zum Fuchs
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        10
                                                                    )
                                                                (permissionOwn >= 50) and (permission < 20)
                                                                -> //zum Bierwart
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        20
                                                                    )
                                                                (permissionOwn > 50) and (permission < 50)
                                                                -> //zum Admin
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        50
                                                                    )
                                                            }
                                                        }

                                                        .setPositiveButton("Berechtigung Vermindern") { _, _ ->
                                                            when {
                                                                (permission >= 50) and (permissionOwn > 50)
                                                                -> //zum Bierwart
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        20
                                                                    )
                                                                (permission >= 20) and (permissionOwn > 20)
                                                                -> //zum Fuchs
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        10
                                                                    )
                                                                (permission >= 10) and (permissionOwn >= 20)
                                                                -> //zum normalen Benutzer
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        2
                                                                    )
                                                                (permission >= 2) and (permissionOwn >= 50)
                                                                -> //Nutzer sperren
                                                                    userRef.update(
                                                                        "Berechtigung",
                                                                        0
                                                                    )
                                                            }
                                                        }
                                                        .show()


                                                } else Toast.makeText(
                                                    context,
                                                    "Du bist nicht berechtigt Änderungen an diesem Nutzer vorzunehmen",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            .setNegativeButton("Guthaben Aufladen") { _, _ ->
                                                /**
                                                 * AlerDialog:
                                                 * Beschreibung: Bier-Coins gutschreiben
                                                 *                  (+) Nutzer (20+) sind berechtigt andere Nutzer Bier-Coins gutzuschreiben
                                                 *                  (+) Auswahl zwischen 1,5,10
                                                 */
                                                val current =
                                                    Calendar.getInstance(Locale.ITALY).time

                                                val transInfo = hashMapOf<String, Any>(
                                                    "Status" to 10,
                                                    "Datum" to current,
                                                    "NutzerVon" to db.collection("Nutzer").document(
                                                        auth.currentUser!!.uid
                                                    ),
                                                    "Nutzer" to db.collection("Nutzer").document(uid)
                                                )
                                                val newTrans =
                                                    db.collection("Transaktionen").document()
                                                AlertDialog.Builder(context)
                                                    .setTitle("Bier-Coins aufladen")
                                                    .setMessage("Jede deiner durchgeführeten positiven Transaktion, wird mit deiner Benutzer-ID markiert. Dies wird benötigt um Fehler leichter finden zu können und um Missbrauch zu mindern.\n\n\n Nun da das geklärt ist, wie viele Coins möchtest du gutschreiben?")

                                                    .setPositiveButton("Schwacher Abend: 1 Coin") { _, _ ->
                                                        transInfo["Betrag"] = 1
                                                        newTrans
                                                            .set(transInfo)
                                                            .addOnCompleteListener { task ->
                                                                if (!task.isSuccessful) return@addOnCompleteListener
                                                            }

                                                    }
                                                    .setNegativeButton("Angemessener Abend: 5 Coins") { _, _ ->
                                                        transInfo["Betrag"] = 5
                                                        newTrans
                                                            .set(transInfo)
                                                            .addOnCompleteListener { task ->
                                                                if (!task.isSuccessful) return@addOnCompleteListener
                                                            }
                                                    }
                                                    .setNeutralButton("Guter Abend: 10 Coins") { _, _ ->
                                                        transInfo["Betrag"] = 10
                                                        newTrans
                                                            .set(transInfo)
                                                            .addOnCompleteListener { task ->
                                                                if (!task.isSuccessful) return@addOnCompleteListener
                                                            }
                                                    }
                                                    .show()
                                            }
                                            .show()
                                    }
                                }
                            //SelectMenu(R.id.nav_acc_admin,drawer_layout,activity).change()
                        }
                }

        }
    }

}