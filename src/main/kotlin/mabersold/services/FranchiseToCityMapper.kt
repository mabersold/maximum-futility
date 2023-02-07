package mabersold.services

import mabersold.models.City
import mabersold.models.Franchise
import mabersold.models.Metro

class FranchiseToCityMapper {
    fun mapToCities(franchises: List<Franchise>): List<City> {
        val cityMap = mutableMapOf<Metro, MutableList<Franchise>>()

        franchises.forEach { franchise ->
            val metros = franchise.timeline.map { it.metroArea }.distinct()

            metros.forEach { metro ->
                val cityFranchises = cityMap.getOrPut(metro) { mutableListOf() }
                val filteredTimeline = franchise.timeline.filter { timeline -> timeline.metroArea == metro }

                val filteredFranchise = franchise.copy(
                    name = filteredTimeline.last().name,
                    timeline = filteredTimeline
                )

                cityFranchises.add(filteredFranchise)
            }
        }

        return cityMap.map { City(it.key, it.value) }
    }
}