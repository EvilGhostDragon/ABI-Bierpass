package `in`.heis.abibierpass

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin.*
import org.json.JSONObject
import java.util.*

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

        val json = JSONObject()
        json.put("action", "getuserinformation")
        json.put("input", "geteverything")

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
            //println(itJson)
            //println(itJson["data"])
            val userData = itJson["data"].asJsonArray
            println(userData[1].asJsonObject.get("fName"))


            val listItems = arrayOfNulls<JsonObject>(5)


            for (i in 0 until 5) {
                val user = userData[i].asJsonObject
                listItems[i] = user
            }
            println(Arrays.toString(listItems))


            val adapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_list_item_multiple_choice,
                listItems
            )
            val ad = UserAdapter(context!!, userData)


            listview_user.adapter = ad

        }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())

    }

    inner class User(var fName: String?, var lName: String?, var vulgo: String?)
}
