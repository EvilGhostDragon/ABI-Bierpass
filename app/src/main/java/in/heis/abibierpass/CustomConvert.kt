package `in`.heis.abibierpass

class CustomConvert {
    fun permissionToString(p: Any): String {
        if (p == null) return "ERROR"
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

    fun transStateToString(s: Int): String {
        return when (s) {
            0 -> {
                "Bestellung übermittelt aber wird noch nicht bearbeitet"
            }
            5 -> {
                "Bestellung wird bearbeitet"
            }
            10 -> {
                "Bestellung wurde abgeschlossen"
            }
            else -> {
                "Fehler"
            }
        }
    }
}