package `in`.heis.abibierpass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject


class HomeFragment : Fragment() {
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAbR_kQd0:APA91bHhaPx5Z3vzy_aKW9d8RCqcSAq-jOCsJv8N2SRPWNijrB3VBymhJTjfbXpYWhOkpAN54gVsxOXSxXovx_OgjyRS5UeOdjWub7rbTUwPKORaAlO9OvPxSeAsu3ul0_FwfQvxYFPT"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.nav_view.menu.findItem(R.id.nav_home).isChecked = true
        btn_devtest1.isVisible = true
        btn_devtest2.isVisible = true

        btn_devtest1.setOnClickListener {
            val topic = "/topics/Bestellungen" //topic has to match what the receiver subscribed to

            val notification = JSONObject()
            val notifcationBody = JSONObject()

            try {
                notifcationBody.put("title", "Neue Bestellung")
                notifcationBody.put("message", "teest")
                notification.put("to", topic)
                notification.put("data", notifcationBody)
                Log.e("TAG", "try")
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }

            sendNotification(notification)
            //MainActivity().sendNotification(notification)

        }
        btn_devtest2.setOnClickListener {
            /*FirebaseMessaging.getInstance().subscribeToTopic("/topics/Enter_your_topic_name")
                .addOnCompleteListener { task ->
                    var msg = "suc"
                    if (!task.isSuccessful) {
                        msg = "fail"
                    }
                    Log.d("sub", msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }*/
        }
    }

    fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")

            },
            Response.ErrorListener {
                Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

}
