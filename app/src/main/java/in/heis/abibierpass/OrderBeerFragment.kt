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

//TODO("CODE CLEANUP!!")
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
        json.put("payId_from", token.getString("payId", ""))
        btn_orderbeer.setOnClickListener {
            if (txt_coins.text.toString() != "0") {
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
                    else {
                        var newCoins = itJson.get("amount").asInt - 1
                        txt_coins.text = newCoins.toString()
                        json.put("action", "newblock")
                        json.put("amount", -1)
                        HttpTask { it2 ->

                            if (it2 == null) {
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
                            println(it2)
                            val itJson2 = JsonParser().parse(it2).asJsonObject
                            if (itJson2.get("result").asInt == 0) Toast.makeText(
                                context!!,
                                "Etwas ist schief gelaufen. War da ein Fuchs am Werk o_O",
                                Toast.LENGTH_LONG
                            ).show()
                            else {
                                Toast.makeText(
                                    context!!,
                                    "Bier erfolgreich bestellt",
                                    Toast.LENGTH_LONG
                                ).show()
                                SelectMenu(R.id.nav_orderbeer, view, activity).change()
                            }
                        }.execute(
                            "POST",
                            "https://abidigital.tk/api/db_use.php",
                            json.toString()
                        )
                    }
                }.execute(
                    "POST",
                    "https://abidigital.tk/api/db_use.php",
                    json.toString()
                )
            }
        }


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
