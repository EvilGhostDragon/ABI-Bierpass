package `in`.heis.abibierpass


import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
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
        val token = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        var amount = 0
        val userRef = db.collection("Nutzer").document(auth.currentUser!!.uid)
        progressbar.visibility = View.VISIBLE

        val animator = ValueAnimator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                progressbar.visibility = View.INVISIBLE
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
                    if (readFrom == "NutzerVon") {
                        //progressbar.visibility = View.INVISIBLE
                    }
                    animator.setObjectValues(0, amount)
                    animator.addUpdateListener { animation ->
                        txt_coins.text = animation.animatedValue.toString()
                    }
                    animator.duration = 750 // here you set the duration of the anim
                    animator.start()

                    // txt_coins.text = amount.toString()
                }

        }







        btn_orderbeer.setOnClickListener {
            btn_orderbeer.isEnabled = false
            if (amount > 0) {
                val current = Calendar.getInstance(
                    Locale.ITALY
                ).time
                val transInfo = hashMapOf<String, Any>(
                    "Status" to 0,
                    "Datum" to current,
                    "NutzerVon" to db.collection("Nutzer").document(
                        auth.currentUser!!.uid
                    ),
                    "Nutzer" to "Bierkasse",
                    "Betrag" to -1
                )
                db.collection("Transaktionen").document()
                    .set(transInfo)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) return@addOnCompleteListener
                        SelectMenu(R.id.nav_orderbeer, drawer_layout, activity).change()
                    }
            }
        }
    }
}
