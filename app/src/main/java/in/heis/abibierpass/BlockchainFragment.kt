package `in`.heis.abibierpass


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class BlockchainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blockchain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_transactions).isChecked = true

        val json = JSONObject()
        json.put("action", "getblockchain")
/*
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
                    "Keine neuen Transaktionen gefunden",
                    Toast.LENGTH_LONG
                ).show()

                return@HttpTask
            } else if (itJson.get("result").asInt == 1) {
                val userData = itJson["data"].asJsonArray
                val adapter = TransactionAdapter(context!!, userData)
                listview_block.adapter = adapter
            }
        }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())

 */
    }

}
