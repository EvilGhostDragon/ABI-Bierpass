package `in`.heis.abibierpass

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    }

                }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())
        }

    }

    inner class User(var fName: String?, var lName: String?, var vulgo: String?)
}
