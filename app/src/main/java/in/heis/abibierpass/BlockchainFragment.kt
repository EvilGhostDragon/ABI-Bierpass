package `in`.heis.abibierpass


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blockchain.*

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

    class Transaction(
        var id: String,
        var date: String,
        var vulgo: String,
        var amount: String,
        var kind: String,
        var status: Int
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_transactions).isChecked = true
        val transList = mutableListOf<Transaction>()


        db.collection("Transaktionen").orderBy("Datum", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                transList.clear()
                var transId = 0
                for (transaction in result) {
                    val amount = transaction.data["Betrag"].toString()
                    val kind = transaction.data["Auswahl"].toString()
                    val id = transaction.id
                    val status = transaction.data["Status"].toString().toInt()
                    if (status == 10) continue
                    var vulgo: String = ""
                    val vulgoRef = transaction.data["NutzerVon"] as DocumentReference

                    vulgoRef.get()
                        .addOnSuccessListener {
                            vulgo = it.data!!["Vulgo"].toString()

                            println("id" + transId.toString() + " amo " + amount + " st " + status + " vu " + vulgo)
                            transList.add(
                                Transaction(
                                    id,
                                    transId++.toString(),
                                    vulgo,
                                    amount,
                                    kind,
                                    status
                                )
                            )
                            val adapter = TransactionAdapter(context!!, ArrayList(transList))
                            listview_block.adapter = adapter
                            listview_block.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, i, _ ->
                                    val id = transList[i].id
                                    val status = transList[i].status

                                    if (status == 0) {
                                        AlertDialog.Builder(context)
                                            .setTitle("Info")
                                            .setMessage("Auftrag annehem?")
                                            .setNegativeButton("Nein") { _, _ ->
                                            }
                                            .setPositiveButton("Ja") { _, _ ->
                                                db.collection("Transaktionen").document(id).get()
                                                    .addOnSuccessListener {
                                                        db.collection("Transaktionen").document(id)
                                                            .update("Status", 5)
                                                    }

                                            }
                                            .show()
                                    } else {
                                        AlertDialog.Builder(context)
                                            .setTitle("Info")
                                            .setMessage("Auftrag abschließen?")
                                            .setNegativeButton("Nein") { _, _ ->
                                            }
                                            .setPositiveButton("Ja") { _, _ ->
                                                db.collection("Transaktionen").document(id).get()
                                                    .addOnSuccessListener {
                                                        db.collection("Transaktionen").document(id)
                                                            .update("Status", 10)
                                                    }

                                            }
                                            .show()
                                    }
                                }
                        }
                }


            }
    }

}
