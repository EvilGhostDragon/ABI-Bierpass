package `in`.heis.abibierpass


import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_selectbeer.view.*
import kotlinx.android.synthetic.main.fragment_order_beer.*
import java.util.*


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
        activity!!.title = "Bier bestellen"
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        var amount = 0
        val userRef = db.collection("Nutzer").document(auth.currentUser!!.uid)
        progressbar.visibility = View.VISIBLE

        val animator = ValueAnimator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                progressbar.visibility = View.INVISIBLE
                btn_orderbeer.isEnabled = true
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })


        for (readFrom in arrayOf("Nutzer", "NutzerVon")) {
            db.collection("Transaktionen").whereEqualTo(readFrom, userRef).get()
                .addOnSuccessListener { result ->
                    for (transaction in result) {
                        val transAmount = transaction.data["Betrag"].toString().toInt()
                        if (!((readFrom == "NutzerVon") and (transAmount > 0))) amount += transAmount

                    }
                }
                .addOnCompleteListener {
                    animator.setObjectValues(0, amount)
                    animator.addUpdateListener { animation ->
                        txt_coins.text = animation.animatedValue.toString()
                    }
                    animator.duration = 750 // here you set the duration of the anim
                    animator.start()
                }

        }
        btn_lastorder.setOnClickListener {
            Toast.makeText(context, "Diese Aktion ist noch nicht möglich", Toast.LENGTH_LONG).show()

        }


        btn_orderbeer.setOnClickListener {

            val current = Calendar.getInstance(
                Locale.ITALY
            ).time
            val vulgo = token.getString("vulgo", "")
            val transInfo = hashMapOf<String, Any>(
                "Status" to 0,
                "Datum" to current,
                "NutzerVon" to db.collection("Nutzer").document(
                    auth.currentUser!!.uid
                ),
                "Nutzer" to "Bierkasse",
                "Betrag" to -1
            )
            val mDialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_selectbeer, null)
            val mAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                .setView(mDialogView)
                .setTitle("Auswahl")
            val mAlertDialog = mAlertDialogBuilder.show()

            fun manageOrder(beerType: String) {
                mAlertDialog.dismiss()
                if (mDialogView.switch_confirmedbeer.isChecked) transInfo["Status"] = 10
                transInfo["Auswahl"] = beerType
                db.collection("Transaktionen").document()
                    .set(transInfo)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) return@addOnCompleteListener
                        SelectMenu(R.id.nav_orderbeer, drawer_layout, activity).change()
                        Toast.makeText(
                            context,
                            "Bier erfolgrei bestellt.",
                            Toast.LENGTH_LONG
                        ).show()
                        notifyFox(beerType, vulgo!!)
                    }
            }

            mDialogView.btn_beerhell.setOnClickListener {
                val beerTyp = "Augustiner: Hell"
                manageOrder(beerTyp)
            }
            mDialogView.btn_beeredelstoff.setOnClickListener {
                val beerTyp = "Augustiner: Hell"
                manageOrder(beerTyp)
            }
            mDialogView.btn_beertoast.setOnClickListener {
                val beerTyp = "Budentoast"
                manageOrder(beerTyp)
            }
            mDialogView.btn_beerweizen.setOnClickListener {
                val beerTyp = "Franziskaner: Weissbier"
                manageOrder(beerTyp)
            }
            mDialogView.btn_beerblau.setOnClickListener {
                val beerTyp = "Franziskaner: Alkoholfrei"
                manageOrder(beerTyp)
            }
            mDialogView.btn_beerradler.setOnClickListener {
                val beerTyp = "Radler"
                manageOrder(beerTyp)
            }


        }


    }

    private fun notifyFox(beer: String, vulgo: String) {
        MainActivity().sendNotification(
            context!!,
            "Bestellungen",
            "Neue Bestellung",
            "$beer für $vulgo"
        )
    }
}
