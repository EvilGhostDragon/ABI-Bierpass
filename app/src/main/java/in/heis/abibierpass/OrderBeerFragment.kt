package `in`.heis.abibierpass


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_order_beer.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class OrderBeerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_beer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_orderbeer).isChecked = true
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)

        val json = JSONObject()
        json.put("action", "calcamount")
        json.put("payId", token.getString("payId", ""))
        HttpTask {

            if (it == null) {
                println("connection error")
                AlertDialog.Builder(context)
                    .setTitle("Fehler")
                    .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: " + HttpTask.msgError)
                    .setPositiveButton("OK") { dialog, which ->
                        SelectMenu(
                            -1,
                            drawer_layout,
                            activity
                        ).change()
                    }
                    .show()

                return@HttpTask
            }
            val itJson = JsonParser().parse(it).asJsonObject
            if (itJson.get("result").asInt == 0) Toast.makeText(
                context!!,
                "Etwas ist schief gelaufen. War da ein Fuchs am Werk o_O",
                Toast.LENGTH_LONG
            ).show()
            else txt_coins.text = itJson.get("amount").asString
        }.execute(
            "POST",
            "https://abidigital.tk/api/db_use.php",
            json.toString()
        )
    }
}
