package `in`.heis.abibierpass

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_home).isChecked = true
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val db_settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        // db.firestoreSettings(db_settings)

        btn_devtest1.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_selectbeer, null)

            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("Auswahl")
            //show dialog
            val mAlertDialog = mBuilder.show()
        }
        btn_devtest2.setOnClickListener {

        }
    }


}
