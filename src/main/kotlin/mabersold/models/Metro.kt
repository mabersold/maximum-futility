package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Metro(val displayName: String) {
    @SerialName("Atlanta")
    ATLANTA("Atlanta"),
    @SerialName("Baltimore")
    BALTIMORE("Baltimore"),
    @SerialName("Boston")
    BOSTON("Boston"),
    @SerialName("Chicago")
    CHICAGO("Chicago"),
    @SerialName("Cincinnati")
    CINCINNATI("Cincinnati"),
    @SerialName("Cleveland")
    CLEVELAND("Cleveland"),
    @SerialName("Dallas")
    DALLAS("Dallas"),
    @SerialName("Denver")
    DENVER("Denver"),
    @SerialName("Detroit")
    DETROIT("Detroit"),
    @SerialName("Grand Rapids")
    GRAND_RAPIDS("Grand Rapids"),
    @SerialName("Houston")
    HOUSTON("Houston"),
    @SerialName("Kansas City")
    KANSAS_CITY("Kansas City"),
    @SerialName("Los Angeles")
    LOS_ANGELES("Los Angeles"),
    @SerialName("Miami")
    MIAMI("Miami"),
    @SerialName("Milwaukee")
    MILWAUKEE("Milwaukee"),
    @SerialName("Minneapolis")
    MINNEAPOLIS("Minneapolis"),
    @SerialName("Montreal")
    MONTREAL("Montreal"),
    @SerialName("New York")
    NEW_YORK("New York"),
    @SerialName("Philadelphia")
    PHILADELPHIA("Philadelphia"),
    @SerialName("Phoenix")
    PHOENIX("Phoenix"),
    @SerialName("Pittsburgh")
    PITTSBURGH("Pittsburgh"),
    @SerialName("San Diego")
    SAN_DIEGO("San Diego"),
    @SerialName("San Francisco Bay")
    SAN_FRANCISCO_BAY("San Francisco Bay"),
    @SerialName("Seattle")
    SEATTLE("Seattle"),
    @SerialName("St. Louis")
    ST_LOUIS("St. Louis"),
    @SerialName("Tampa Bay")
    TAMPA_BAY("Tampa Bay"),
    @SerialName("Toronto")
    TORONTO("Toronto"),
    @SerialName("Washington")
    WASHINGTON("Washington")
}