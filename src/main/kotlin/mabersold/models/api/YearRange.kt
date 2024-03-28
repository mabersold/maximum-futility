package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class YearRange(val startYear: Int, val endYear: Int)
