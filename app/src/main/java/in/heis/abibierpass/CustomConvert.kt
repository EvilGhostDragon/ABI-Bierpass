package `in`.heis.abibierpass

class CustomConvert {
    fun permissionToString(p: Int): String {
        when (p) {
            0 -> {
                return "Gesperrter Nutzer oder mit unbestätigter E-Mail Adresse"
            }
            1 -> {
                return "Nutzer muss manuell freigeschalten werden"
            }
            2 -> {
                return "Normaler Benutzer"
            }
            2 -> {
                return "Normaler Benutzer *hust*"
            }
            10 -> {
                return "Fuchs"
            }
            20 -> {
                return "Bierwart"
            }
            50 -> {
                return "Admin"
            }
            100 -> {
                return "Entwickler"
            }
            else -> {
                return "Hacker?"
            }
        }
    }
}