package `in`.heis.abibierpass

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

val key = "userdata"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    //    val user_data = getSharedPreferences("user.data", Context.MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {

        val token = getSharedPreferences(`in`.heis.abibierpass.key, Context.MODE_PRIVATE)
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

        if (token.getBoolean("loggedin", true)) {
            println("LOGGED IN")
            SelectMenu(-1, drawer_layout, this@MainActivity).makeNewLayout(2)
            //getString(R.id.nav_header_subtitel)


        } else {

            SelectMenu(-1, drawer_layout, this@MainActivity).change()
        }
        println(token.all)


        //var test2 = Hawk.get(KEY,JSONObject())


        handleIntent(intent)


    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { linkId ->
                Uri.parse("content://in.heis.abibierpass")
                    .buildUpon()
                    .appendPath(linkId)
                    .build().also { appData ->
                        handleLink(appData.toString())
                    }

            }
        }
    }

    private fun handleLink(appData: String) {
        if (appData.contains("abibierpass/failed")) {
            AlertDialog.Builder(this)
                .setTitle("Fehler")
                .setMessage("Ups Bier verschüttet. Fehler können passieren. \n\n Fehlercode: 1000")
                .setPositiveButton("OK") { dialog, which ->

                }
                .show()
        } else if (appData.contains("abibierpass/success")) {
            AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Deine E-Mail Adresse wurde erfolgreich bestätigt. \nDu wirst benachrichtigt, sobald deine Daten überprüft wruden")
                .setPositiveButton("OK") { dialog, which ->

                }
                .show()
        } else if (appData.contains("abibierpass/open")) {

        }
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

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
