package `in`.heis.abibierpass

import org.json.JSONObject

class UpdateUser {

    val json = JSONObject().put("action", "updateuser")

    fun permission(mail: String, permissionNew: Int) {
        json.put("mail", mail)
        json.put("permissionNew", permissionNew)
        HttpTask {
            if (it == null) {
                println("connection error")
                return@HttpTask
            }
        }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())
    }

}