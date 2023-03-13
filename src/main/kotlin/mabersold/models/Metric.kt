package mabersold.models

data class Metric(
    val total: Int,
    val opportunities: Int
) {
    val rate = if (opportunities > 0) total.toDouble() / opportunities else null
}
