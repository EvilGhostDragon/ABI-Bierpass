package `in`.heis.abibierpass

import com.google.gson.JsonParser
import org.json.JSONObject

class UpdateUser {
    companion object {
        var result = 0
    }

    val json = JSONObject().put("action", "updateuser")

    fun permission(mail: String, permissionNew: Int) {
        json.put("mail", mail)
        json.put("permissionNew", permissionNew)
        HttpTask {
            if (it == null) {
                println("connection error")
                return@HttpTask
            }
            val itJson = JsonParser().parse(it).asJsonObject
            UpdateUser.result = itJson.get("result").asInt
        }.execute("POST", "https://abidigital.tk/api/db_use.php", json.toString())
    }
}