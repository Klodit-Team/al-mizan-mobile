package com.klodit.almizan.data.tender

// ─────────────────────────────────────────────
//  DATA MODELS
//  These match exactly what your API will return.
//  When you add Retrofit, just map the JSON
//  response to these same classes — screens
//  won't need to change at all.
// ─────────────────────────────────────────────

data class Tender(
    val id          : String,
    val type        : String,   // "NATIONAL" or "INTERNATIONAL"
    val organization: String,
    val title       : String,
    val deadline    : String,   // e.g. "Dans 14 jours"
    val daysLeft    : Int,      // used to color deadline red if <= 7
    val date        : String    // e.g. "12/10/2023"
)

data class PlatformStats(
    val activeTenders: String,
    val awarded      : String,
    val operators    : String
)


object TenderRepository {

    fun getStats(): PlatformStats {
        // TODO later: return apiService.getStats()
        return PlatformStats(
            activeTenders = "1,240",
            awarded       = "450",
            operators     = "8,000"
        )
    }

    fun getLatestTenders(): List<Tender> {
        // TODO later: return apiService.getLatestTenders()
        return listOf(
            Tender(
                id           = "1",
                type         = "NATIONAL",
                organization = "SONATRACH - DIVISION PRODUCTION",
                title        = "Fourniture d'équipements de forage pour les champs de Hassi Messaoud",
                deadline     = "Dans 14 jours",
                daysLeft     = 14,
                date         = "12/10/2023"
            ),
            Tender(
                id           = "2",
                type         = "INTERNATIONAL",
                organization = "MINISTÈRE DE LA SANTÉ",
                title        = "Installation de systèmes d'imagerie médicale avancée - CHU Constantine",
                deadline     = "Dans 28 jours",
                daysLeft     = 28,
                date         = "10/10/2023"
            ),
            Tender(
                id           = "3",
                type         = "NATIONAL",
                organization = "ALGÉRIE TÉLÉCOM",
                title        = "Extension du réseau de fibre optique FTTH - Wilaya d'Oran",
                deadline     = "Dans 5 jours",
                daysLeft     = 5,
                date         = "08/10/2023"
            )
        )
    }
}