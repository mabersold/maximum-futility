package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Metro(val displayName: String) {
    @SerialName("Anderson")
    ANDERSON("Anderson"),
    @SerialName("Atlanta")
    ATLANTA("Atlanta"),
    @SerialName("Baltimore")
    BALTIMORE("Baltimore"),
    @SerialName("Boston")
    BOSTON("Boston"),
    @SerialName("Buffalo")
    BUFFALO("Buffalo"),
    @SerialName("Calgary")
    CALGARY("Calgary"),
    @SerialName("Charlotte")
    CHARLOTTE("Charlotte"),
    @SerialName("Chicago")
    CHICAGO("Chicago"),
    @SerialName("Cincinnati")
    CINCINNATI("Cincinnati"),
    @SerialName("Cleveland")
    CLEVELAND("Cleveland"),
    @SerialName("Columbus")
    COLUMBUS("Columbus"),
    @SerialName("Dallas")
    DALLAS("Dallas"),
    @SerialName("Decatur")
    DECATUR("Decatur"),
    @SerialName("Denver")
    DENVER("Denver"),
    @SerialName("Detroit")
    DETROIT("Detroit"),
    @SerialName("Edmonton")
    EDMONTON("Edmonton"),
    @SerialName("Fort Wayne")
    FORT_WAYNE("Fort Wayne"),
    @SerialName("Grand Rapids")
    GRAND_RAPIDS("Grand Rapids"),
    @SerialName("Green Bay")
    GREEN_BAY("Green Bay"),
    @SerialName("Hartford")
    HARTFORD("Hartford"),
    @SerialName("Houston")
    HOUSTON("Houston"),
    @SerialName("Indianapolis")
    INDIANAPOLIS("Indianapolis"),
    @SerialName("Jacksonville")
    JACKSONVILLE("Jacksonville"),
    @SerialName("Kansas City")
    KANSAS_CITY("Kansas City"),
    @SerialName("Las Vegas")
    LAS_VEGAS("Las Vegas"),
    @SerialName("Los Angeles")
    LOS_ANGELES("Los Angeles"),
    @SerialName("Memphis")
    MEMPHIS("Memphis"),
    @SerialName("Miami")
    MIAMI("Miami"),
    @SerialName("Milwaukee")
    MILWAUKEE("Milwaukee"),
    @SerialName("Minneapolis")
    MINNEAPOLIS("Minneapolis"),
    @SerialName("Montreal")
    MONTREAL("Montreal"),
    @SerialName("Nashville")
    NASHVILLE("Nashville"),
    @SerialName("New Orleans")
    NEW_ORLEANS("New Orleans"),
    @SerialName("New York")
    NEW_YORK("New York"),
    @SerialName("Oklahoma City")
    OKLAHOMA_CITY("Oklahoma City"),
    @SerialName("Orlando")
    ORLANDO("Orlando"),
    @SerialName("Ottawa")
    OTTAWA("Ottawa"),
    @SerialName("Philadelphia")
    PHILADELPHIA("Philadelphia"),
    @SerialName("Phoenix")
    PHOENIX("Phoenix"),
    @SerialName("Pittsburgh")
    PITTSBURGH("Pittsburgh"),
    @SerialName("Portland")
    PORTLAND("Portland"),
    @SerialName("Portsmouth")
    PORTSMOUTH("Portsmouth"),
    @SerialName("Providence")
    PROVIDENCE("Providence"),
    @SerialName("Quebec")
    QUEBEC("Quebec"),
    @SerialName("Raleigh")
    RALEIGH("Raleigh"),
    @SerialName("Rochester")
    ROCHESTER("Rochester"),
    @SerialName("Sacramento")
    SACRAMENTO("Sacramento"),
    @SerialName("Salt Lake City")
    SALT_LAKE_CITY("Salt Lake City"),
    @SerialName("San Antonio")
    SAN_ANTONIO("San Antonio"),
    @SerialName("San Diego")
    SAN_DIEGO("San Diego"),
    @SerialName("San Francisco Bay")
    SAN_FRANCISCO_BAY("San Francisco Bay"),
    @SerialName("Seattle")
    SEATTLE("Seattle"),
    @SerialName("Sheboygan")
    SHEBOYGAN("Sheboygan"),
    @SerialName("St. Louis")
    ST_LOUIS("St. Louis"),
    @SerialName("Syracuse")
    SYRACUSE("Syracuse"),
    @SerialName("Tampa Bay")
    TAMPA_BAY("Tampa Bay"),
    @SerialName("Toronto")
    TORONTO("Toronto"),
    @SerialName("Tri-Cities")
    TRI_CITIES("Tri-Cities"),
    @SerialName("Tulsa")
    TULSA("Tulsa"),
    @SerialName("Vancouver")
    VANCOUVER("Vancouver"),
    @SerialName("Washington")
    WASHINGTON("Washington"),
    @SerialName("Winnipeg")
    WINNIPEG("Winnipeg")
}