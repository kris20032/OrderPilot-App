package com.ubercab.driver

import kotlin.random.Random

enum class UberLanguage { PL, UK, EN }

data class OfferData(
    val amount: String,
    val time: String,
    val distance: String,
    val pickup: String,
    val dropoff: String
)

object OfferRandomizer {

    private val pickupAddressesPL = listOf(
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

    private val dropoffAddressesPL = listOf(
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

    private val pickupAddressesUK = listOf(
        "McDonald's (вул. Хрещатик 10)",
        "KFC (просп. Перемоги 42)",
        "Сільпо (вул. Льва Толстого 15)",
        "Burger King (вул. Велика Васильківська 66)",
        "Pizza Celentano (вул. Саксаганського 32)",
        "Novus (просп. Науки 5)",
        "Domino's Pizza (вул. Антоновича 21)",
        "Puzata Hata (вул. Борисоглібська 2)",
        "Fozzy (вул. Гоголівська 55)",
        "Subway (вул. Басейна 18)"
    )

    private val dropoffAddressesUK = listOf(
        "вул. Хрещатик 22, Київ",
        "просп. Перемоги 14, Київ",
        "вул. Велика Васильківська 72, Київ",
        "вул. Саксаганського 44, Київ",
        "вул. Льва Толстого 9, Київ",
        "бул. Дружби Народів 3, Київ",
        "вул. Антоновича 55, Київ",
        "просп. Науки 18, Київ",
        "вул. Борисоглібська 7, Київ",
        "вул. Гоголівська 30, Київ"
    )

    private val pickupAddressesEN = listOf(
        "McDonald's (Market Square 1)",
        "KFC (High Street 45)",
        "Tesco Express (Station Road 8)",
        "Burger King (Central Park Ave 12)",
        "Pizza Hut (Oak Lane 33)",
        "Sainsbury's (Queen Street 20)",
        "Domino's Pizza (Park Road 5)",
        "Subway (Bridge Street 17)",
        "Costa Coffee (Mill Lane 9)",
        "Greggs (Church Road 41)"
    )

    private val dropoffAddressesEN = listOf(
        "12 Victoria Road, Warsaw",
        "45 Albert Street, Warsaw",
        "8 Queen's Avenue, Warsaw",
        "33 King's Road, Warsaw",
        "21 Elm Street, Warsaw",
        "67 Maple Drive, Warsaw",
        "3 Oak Lane, Warsaw",
        "14 Park View, Warsaw",
        "55 Church Street, Warsaw",
        "29 High Street, Warsaw"
    )

    fun random(language: UberLanguage = UberLanguage.PL): OfferData {
        val amountInt = Random.nextInt(800, 3500)
        val time = Random.nextInt(10, 46)
        val distanceRaw = Random.nextInt(15, 121)

        return when (language) {
            UberLanguage.PL -> OfferData(
                amount = "%d,%02d zł".format(amountInt / 100, amountInt % 100),
                time = "$time min",
                distance = "%.1f km".format(distanceRaw / 10.0),
                pickup = pickupAddressesPL.random(),
                dropoff = dropoffAddressesPL.random()
            )
            UberLanguage.UK -> OfferData(
                amount = "%d,%02d грн".format(amountInt / 100, amountInt % 100),
                time = "$time хв",
                distance = "%.1f км".format(distanceRaw / 10.0),
                pickup = pickupAddressesUK.random(),
                dropoff = dropoffAddressesUK.random()
            )
            UberLanguage.EN -> OfferData(
                amount = "%.2f PLN".format(amountInt / 100.0),
                time = "$time min",
                distance = "%.1f km".format(distanceRaw / 10.0),
                pickup = pickupAddressesEN.random(),
                dropoff = dropoffAddressesEN.random()
            )
        }
    }
}