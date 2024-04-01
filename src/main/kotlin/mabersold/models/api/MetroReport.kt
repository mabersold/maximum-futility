package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class MetroReport(
    val startYear: Int,
    val endYear: Int,
    val metricType: MetricType,
    val leaguesIncluded: List<League>,
    val data: List<MetroData>
)
