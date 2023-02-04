package mabersold.services

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mabersold.models.Franchise

class FranchiseDataService {
    fun getFranchiseData(source: String = "data/baseball/franchises.json"): List<Franchise> {
        val franchiseJsonString = this::class.java.classLoader.getResource(source)?.readText()
            ?: throw RuntimeException("Could not load data")

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        return json.decodeFromString(franchiseJsonString)
    }
}