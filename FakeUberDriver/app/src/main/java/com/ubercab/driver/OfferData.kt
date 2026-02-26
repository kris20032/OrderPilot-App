package com.ubercab.driver

import kotlin.random.Random

data class OfferData(
    val amount: String,
    val time: String,
    val distance: String,
    val pickup: String,
    val dropoff: String
)

object OfferRandomizer {

    private val pickupAddresses = listOf(
        "Carrefour (Przywidzka 6)",
        "McDonald's (Grunwaldzka 141)",
        "KFC (Rajska 10)",
        "Panie Janie Pizza Morena",
        "Burger King (Galeria Bałtycka)",
        "Żabka (Partyzantów 68)",
        "Lidl (Jabłoniowa 35)",
        "Pizza Hut (Oliwska 55)",
        "Subway (Długa 22)",
        "Netto (Kartuska 245)"
    )

    private val dropoffAddresses = listOf(
        "Aleksandry Gabrysiak, Gdańsk",
        "Dąbrówki & Królowej Jadwigi, Gdańsk",
        "Słowackiego 12, Gdańsk",
        "Wita Stwosza 45, Gdańsk",
        "Hallera 22, Gdańsk",
        "Bałtycka 8, Gdańsk",
        "Grunwaldzka 200, Gdańsk",
        "Obrońców Wybrzeża 5, Gdańsk",
        "Łostowice 18, Gdańsk",
        "Traugutta 7, Gdańsk"
    )

    fun random(): OfferData {
        val amountInt = Random.nextInt(800, 3500)
        val amountStr = "%d,%02d zł".format(amountInt / 100, amountInt % 100)

        val time = Random.nextInt(10, 46)
        val distanceRaw = Random.nextInt(15, 121)
        val distanceStr = "%.1f km".format(distanceRaw / 10.0)

        return OfferData(
            amount = amountStr,
            time = "$time min",
            distance = distanceStr,
            pickup = pickupAddresses.random(),
            dropoff = dropoffAddresses.random()
        )
    }
}
