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
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin.*
import org.json.JSONObject

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_acc_admin).isChecked = true
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)

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



        btn_show.setOnClickListener {
            val json = JSONObject()
            json.put("action", "getuserinformation")
            if (switch_showfox.isChecked) json.put("permission", 10)
            if (switch_shownew.isChecked) json.put("permission", 1)
            if (switch_shownormal.isChecked) json.put("permission", 2)


            if (editText_search.text.isEmpty()) {
                json.put("input", "geteverything")
            } else {
                json.put("input", editText_search.text.toString())
            }
            HttpTask {
                if (it == null) {
                    println("connection error")
                    AlertDialog.Builder(context)
                        .setTitle("Fehler")
                        .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + HttpTask.msgError)
                        .setPositiveButton("OK") { dialog, which ->
                            SelectMenu(-1, drawer_layout, activity).change()
                        }
                        .show()

                    return@HttpTask
                }
                //println(it)
                val itJson = JsonParser().parse(it).asJsonObject
                if (itJson.get("result").asInt == 0) {
                    Toast.makeText(
                        context!!,
                        "Keine Benutzer mit diesen Kriterien gefunden",
                        Toast.LENGTH_LONG
                    ).show()
                    val adapter = UserAdapter(context!!, JsonArray())
                    listview_user.adapter = adapter
                    return@HttpTask
                } else if (itJson.get("result").asInt == 1) {
                    val userData = itJson["data"].asJsonArray
                    val adapter = UserAdapter(context!!, userData)
                    listview_user.adapter = adapter
                    listview_user.onItemClickListener =
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val fName = userData[i].asJsonObject.get("fName").asString
                            val lName = userData[i].asJsonObject.get("lName").asString
                            val vulgo = userData[i].asJsonObject.get("vulgo").asString
                            val mail = userData[i].asJsonObject.get("mail").asString
                            val permission = userData[i].asJsonObject.get("permission").asInt
                            var message =
                                "Vorname: " + fName + "\nNachname: " + lName + "\nVulgo: " + vulgo + "\n\n\n"

                            if (permission <= 1) {
                                if (permission == 0) message =
                                    message + "Wichtig:\nDieser Benutzer hat seine E-Mail Adresse noch nicht bestätigt oder wurde gesperrt! Trotzdem freischalten?"
                                else message =
                                    message + "Den ausgewählten Benutzer jetzt freischalten?"

                                AlertDialog.Builder(context)
                                    .setTitle("Ausgewählter Benutzer")
                                    .setMessage(message)

                                    .setPositiveButton("Ja") { dialog, which ->
                                        UpdateUser().permission(mail, 2)
                                    }
                                    .setNegativeButton("Nein") { dialog, which ->
                                        //SelectMenu(-1, drawer_layout, activity).change()
                                    }
                                    .show()
                            } else if (token.getString("permission", "")!!.toInt() >= 20) {
                                AlertDialog.Builder(context)
                                    .setTitle("Ausgewählter Benutzer")
                                    .setMessage(message)

                                    .setPositiveButton("Berechtigung Ändern") { dialog, which ->
                                        if (permission < token.getString(
                                                "permission",
                                                ""
                                            )!!.toInt()
                                        ) {
                                            AlertDialog.Builder(context)
                                                .setTitle("Ausgewählter Benutzer")
                                                .setMessage(
                                                    message + "Berechtigungshierarchie: \n\t- " +
                                                            CustomConvert().permissionToString(0) + " \n\t- " +
                                                            CustomConvert().permissionToString(1) + " \n\t- " +
                                                            CustomConvert().permissionToString(2) + " \n\t- " +
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
                                                .setNegativeButton("Berechtigung Erhöhen") { dialog, which ->
                                                    if ((token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() >= 20) and (permission < 10)
                                                    ) {
                                                        //zum Fuchs
                                                        UpdateUser().permission(mail, 10)
                                                    } else if ((token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() >= 50) and (permission < 20)
                                                    ) {
                                                        //zum Bierwart
                                                        UpdateUser().permission(mail, 20)
                                                    } else if ((token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() > 50) and (permission < 50)
                                                    ) {
                                                        //zum Admin
                                                        UpdateUser().permission(mail, 50)
                                                    }
                                                }

                                                .setPositiveButton("Berechtigung Vermindern") { dialog, which ->
                                                    if ((permission >= 50) and (token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() > 50)
                                                    ) {
                                                        UpdateUser().permission(mail, 20)
                                                    } else if ((permission >= 20) and (token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() > 20)
                                                    ) {
                                                        UpdateUser().permission(mail, 10)
                                                    } else if ((permission >= 10) and (token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() >= 20)
                                                    ) {
                                                        UpdateUser().permission(mail, 2)
                                                    } else if ((permission >= 2) and (token.getString(
                                                            "permission",
                                                            ""
                                                        )!!.toInt() >= 50)
                                                    ) {
                                                        UpdateUser().permission(mail, 0)
                                                    }
                                                }
                                                .show()


                                        } else Toast.makeText(
                                            context,
                                            "Du bist nicht berechtigt Änderungen an diesem Nutzer vorzunehmen",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    .setNegativeButton("Guthaben Aufladen") { dialog, which ->

                                    }
                                    .show()
                            }
                        }
                }

            }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())
        }

    }

}
