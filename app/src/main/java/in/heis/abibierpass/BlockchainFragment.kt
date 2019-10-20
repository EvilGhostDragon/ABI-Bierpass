package `in`.heis.abibierpass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blockchain.*

class BlockchainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blockchain, container, false)
    }

    class Transaction(
        var uid: String,
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
        activity!!.title = "Letzte Bestellungen"
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        val transList = mutableListOf<Transaction>()

        refresh_transactions.setOnRefreshListener {
            val navView: NavigationView = activity!!.findViewById(R.id.nav_view)
            navView.setCheckedItem(navView.menu.findItem(R.id.nav_transactions))
            SelectMenu(R.id.nav_transactions, drawer_layout, activity).change()
            refresh_transactions.isRefreshing = false
        }
        db.collection("Transaktionen").whereLessThanOrEqualTo("Status", 5).orderBy("Status")
            .orderBy("Datum", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->
                transList.clear()
                var transId = 0
                for (transaction in result) {
                    val amount = transaction.data["Betrag"].toString()
                    val kind = transaction.data["Auswahl"].toString()
                    val id = transaction.id
                    val status = transaction.data["Status"].toString().toInt()
                    if (status == 10) continue
                    var vulgo: String
                    val vulgoRef = transaction.data["NutzerVon"] as DocumentReference
                    var uid: String

                    vulgoRef.get()
                        .addOnSuccessListener {
                            vulgo = it.data!!["Vulgo"].toString()
                            uid = it.id
                            transList.add(
                                Transaction(
                                    uid,
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
                                    @Suppress("NAME_SHADOWING") val id = transList[i].id
                                    @Suppress("NAME_SHADOWING") val status = transList[i].status
                                    @Suppress("NAME_SHADOWING") val uid = transList[i].uid
                                    @Suppress("NAME_SHADOWING") val amount = transList[i].amount
                                    if (status == 0) {
                                        MaterialAlertDialogBuilder(context)
                                            .setTitle("Info")
                                            .setMessage("Auftrag annehem?")
                                            .setNegativeButton("Nein") { _, _ ->
                                            }
                                            .setPositiveButton("Ja") { _, _ ->
                                                db.collection("Transaktionen").document(id).get()
                                                    .addOnSuccessListener {
                                                        db.collection("Transaktionen").document(id)
                                                            .update("Status", 5)
                                                        MainActivity().sendNotification(
                                                            context!!,
                                                            uid,
                                                            "Update deiner Bestellung",
                                                            token.getString(
                                                                "vulgo",
                                                                ""
                                                            ) + " hat deine Bestellung angenommen"
                                                        )
                                                        val navView: NavigationView =
                                                            activity!!.findViewById(R.id.nav_view)
                                                        navView.setCheckedItem(
                                                            navView.menu.findItem(
                                                                R.id.nav_transactions
                                                            )
                                                        )
                                                        SelectMenu(
                                                            R.id.nav_transactions,
                                                            drawer_layout,
                                                            activity
                                                        ).change()
                                                    }
                                            }
                                            .show()
                                    } else {
                                        MaterialAlertDialogBuilder(context)
                                            .setTitle("Info")
                                            .setMessage("Auftrag abschließen?")
                                            .setNegativeButton("Nein") { _, _ ->
                                            }
                                            .setPositiveButton("Ja") { _, _ ->
                                                db.collection("Transaktionen").document(id).get()
                                                    .addOnSuccessListener {
                                                        db.collection("Transaktionen").document(id)
                                                            .update("Status", 10)
                                                        MainActivity().sendNotification(
                                                            context!!,
                                                            uid,
                                                            "Update deiner Bestellung",
                                                            token.getString(
                                                                "vulgo",
                                                                ""
                                                            ) + " hat deine Bestellung abgeschlossen. \nSolltest du dennoch in ein leeres Glas schauen, könnte ein lautes 'BIERFUCHS' möglicherweise helfen."
                                                        )
                                                        val navView: NavigationView =
                                                            activity!!.findViewById(R.id.nav_view)
                                                        navView.setCheckedItem(
                                                            navView.menu.findItem(
                                                                R.id.nav_transactions
                                                            )
                                                        )
                                                        SelectMenu(
                                                            R.id.nav_transactions,
                                                            drawer_layout,
                                                            activity
                                                        ).change()
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
