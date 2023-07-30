package mabersold.models

data class MetroData(
    val name: String,
    val metricType: MetricType,
    val total: Int,
    val opportunities: Int
) {
    val rate = if (opportunities > 0) total.toDouble() / opportunities else null
}

enum class MetricType(val displayName: String) {
    TOTAL_CHAMPIONSHIPS("Total Championships"),
    CHAMPIONSHIP_APPEARANCES("Championship Appearances"),
    ADVANCED_IN_PLAYOFFS("Advanced in Playoffs"),
    QUALIFIED_FOR_PLAYOFFS("Qualified for Playoffs"),
    BEST_OVERALL("Best Overall"),
    WORST_OVERALL("Worst Overall"),
    BEST_CONFERENCE("Best in Conference"),
    WORST_CONFERENCE("Worst in Conference"),
    BEST_DIVISION("Best in Division"),
    WORST_DIVISION("Worst in Division")
}