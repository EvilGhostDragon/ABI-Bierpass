package `in`.heis.abibierpass

class CustomConvert {
    fun permissionToString(p: Int): String {
        when (p) {
            0 -> {
                return "No permission! require email confirmation"
            }
            1 -> {
                return "No permission! require data check"
            }
            2 -> {
                return "Keine. Normaler Benutzer"
            }
            10 -> {
                return "Fuchs. Berechtigt letzten 10 Zahlungen zu sehen"
            }
            20 -> {
                return "Bierwart. Berechtigt Guthaben zu verteilen"
            }
            30 -> {
                return "Admin"
            }
            100 -> {
                return "Zer0"
            }
            else -> {
                return "Hacker?"
            }
        }
    }
}