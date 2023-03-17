package mabersold.services

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mabersold.models.Franchise
import mabersold.models.League

class FranchiseDataService {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    fun getFranchiseData(source: String = "data/football/pre-super-bowl-nfl.json"): List<Franchise> {
        return getFranchiseListFromFile(source)
    }

    fun getFranchiseData(sources: Map<League, List<String>>, startYear: Int = 1903, endYear: Int = 2022): List<Franchise> {
        val newMap = sources.mapValues { entry ->
            entry.value.flatMap { sourceFile ->
                getFranchiseListFromFile(sourceFile).map { it.withLeague(entry.key) }
            }.mergeFranchises()
        }

        return newMap.values.flatten().within(startYear, endYear).sortedBy { it.name }
    }

    private fun getFranchiseListFromFile(sourceFile: String): List<Franchise> {
        val franchiseJsonString = this::class.java.classLoader.getResource(sourceFile)?.readText()
            ?: return listOf()

        return json.decodeFromString(franchiseJsonString)
    }

    private fun List<Franchise>.mergeFranchises(): List<Franchise> {
        val mergedFranchises = mutableMapOf<String, Franchise>()

        for (franchise in this) {
            if (mergedFranchises.containsKey(franchise.name)) {
                mergedFranchises[franchise.name] = franchise.merge(mergedFranchises[franchise.name]!!)
            } else {
                mergedFranchises[franchise.name] = franchise
            }
        }

        return mergedFranchises.values.toList()
    }

    private fun Franchise.merge(other: Franchise): Franchise {
        return Franchise(
            name = this.name,
            firstSeason = minOf(this.firstSeason, other.firstSeason),
            isDefunct = this.isDefunct && other.isDefunct,
            timeline = (this.timeline + other.timeline).sortedBy { it.startSeason },
            league = this.league
        )
    }

    private fun List<Franchise>.within(startYear: Int, endYear: Int) =
        this.filter { it.isWithin(startYear, endYear) }
            .map { it.within(startYear, endYear) }
}