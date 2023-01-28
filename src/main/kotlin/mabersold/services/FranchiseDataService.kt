package mabersold.services

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mabersold.models.Franchise

class FranchiseDataService {
    fun getFranchiseData(): List<Franchise> {
        val franchiseJsonString = this::class.java.classLoader.getResource("data/baseball/franchises.json")?.readText()
            ?: throw RuntimeException("Could not load data")

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        return json.decodeFromString(franchiseJsonString)
    }
}