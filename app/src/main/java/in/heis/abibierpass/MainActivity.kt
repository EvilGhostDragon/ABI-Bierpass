package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


const val key = "userdata"
val auth = FirebaseAuth.getInstance()
val db: FirebaseFirestore
    get() = FirebaseFirestore.getInstance()
val settings = FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .build()
lateinit var firebaseAnalytics: FirebaseAnalytics


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAbR_kQd0:APA91bHhaPx5Z3vzy_aKW9d8RCqcSAq-jOCsJv8N2SRPWNijrB3VBymhJTjfbXpYWhOkpAN54gVsxOXSxXovx_OgjyRS5UeOdjWub7rbTUwPKORaAlO9OvPxSeAsu3ul0_FwfQvxYFPT"
    private val contentType = "application/json"
    override fun onCreate(savedInstanceState: Bundle?) {
        val token = getSharedPreferences(`in`.heis.abibierpass.key, Context.MODE_PRIVATE)
        db.firestoreSettings = settings
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result?.token
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("firebase", msg)
            })

        val user = auth.currentUser
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    println(deepLink)
                }
            }
            .addOnFailureListener(this) { e -> Log.w("firebase", "getDynamicLink:onFailure", e) }

        if (user != null) {
            if (user.isEmailVerified) {
                val userRef = db.collection("Nutzer").document(user.uid)
                userRef
                    .get()
                    .addOnSuccessListener {
                        (application as App).preferenceRepository
                            .nightModeLive.observe(this, Observer { nightMode ->
                            nightMode?.let { delegate.localNightMode = it }

                            val navView: NavigationView = this.findViewById(R.id.nav_view)
                            navView.setCheckedItem(navView.menu.findItem(R.id.nav_home))
                            SelectMenu(R.id.nav_home, drawer_layout, this).change()
                        })
                        val data = it.data
                        if ((data != null) and (data!!["Berechtigung"].toString().toInt() != 0)) {
                            firebaseAnalytics
                                .setUserProperty("Berechtigung", data["Berechtigung"].toString())
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, null)
                            with(token.edit()) {
                                putInt("permission", data["Berechtigung"].toString().toInt())
                                putString("vulgo", data["Vulgo"].toString())
                            }
                                .apply()

                            SelectMenu(
                                -1,
                                drawer_layout,
                                this
                            ).makeNewLayout(data["Berechtigung"] as Long)
                        } else {
                            if (token.getBoolean("login.blocknotification", false)) Toast.makeText(
                                this,
                                "Du wirst benachrichtigt sobald du freigeschalten wurdest",
                                Toast.LENGTH_LONG
                            ).show()
                            else {
                                AlertDialog.Builder(this)
                                    .setTitle("Info")
                                    .setMessage("Du wurdest noch nicht freigeschalten. Möchtest du benachrichtigt werden?")
                                    .setPositiveButton("Ja") { _, _ ->
                                        FirebaseMessaging.getInstance().subscribeToTopic(it.id)
                                        token.edit().putBoolean("login.blocknotification", true)
                                            .apply()
                                        SelectMenu(-1, drawer_layout, this).change()
                                    }
                                    .setNegativeButton("Nein") { _, _ ->
                                        SelectMenu(-1, drawer_layout, this).change()
                                    }
                                    .show()
                            }
                        }
                    }
            } else Toast.makeText(
                this,
                "Deine E-Mail Adresse wurde noch nicht bestätigt!",
                Toast.LENGTH_LONG
            ).show()

        } else {
            token.edit().putString("permission", null).apply()
            SelectMenu(-1, drawer_layout, this@MainActivity).change()
        }
        println(token.all)

    }


    fun sendNotification(context: Context, topicName: String, title: String, message: String) {
        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context)
        }
        val topic = "/topics/" + topicName
        val notification = JSONObject()
        val notifcationBody = JSONObject()

        try {
            notifcationBody.put("title", title)
            notifcationBody.put("message", message)
            notification.put("to", topic)
            notification.put("data", notifcationBody)
            Log.e("TAG", "try")
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate: " + e.message)
        }

        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("volley", "onResponse: $response")

            },
            Response.ErrorListener {
                Log.i("volley", "onErrorResponse: Didn't work")
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

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.manu_side, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        SelectMenu(item.itemId, null, this@MainActivity).action()
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            SelectMenu(item.itemId, null, this@MainActivity).action()
        }

        SelectMenu(item.itemId, drawer_layout, this@MainActivity).change()

        println("nav " + nav_view.checkedItem)

        nav_view.setCheckedItem(nav_view.checkedItem!!)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
