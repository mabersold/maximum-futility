package mabersold

import mabersold.models.City
import mabersold.models.Franchise
import mabersold.models.FranchiseTimeline
import mabersold.models.League
import mabersold.models.Metro

val `a timeline from 1990-1999 with no data` = FranchiseTimeline(
    startSeason = 1990,
    endSeason = 1999,
    name = "Factoria Trash",
    metroArea = Metro.SEATTLE,
    championships = listOf(),
    championshipAppearances = listOf(),
    playoffAppearances = listOf(),
    advancedInPlayoffs = listOf(),
    bestInDivision = listOf(),
    bestInConference = listOf(),
    bestOverall = listOf(),
    worstInDivision = listOf(),
    worstInConference = listOf(),
    worstOverall = listOf()
)

val `a timeline from 1980-1989 with data` = FranchiseTimeline(
    startSeason = 1980,
    endSeason = 1989,
    name = "Bellevue Squares",
    metroArea = Metro.SEATTLE,
    championships = listOf(1985),
    championshipAppearances = listOf(1985, 1986),
    playoffAppearances = listOf(1985, 1986, 1987, 1988),
    advancedInPlayoffs = listOf(1985, 1986),
    bestInDivision = listOf(1985, 1986, 1987, 1988),
    bestInConference = listOf(1985, 1986),
    bestOverall = listOf(1985),
    worstInDivision = listOf(1980, 1981, 1982, 1983),
    worstInConference = listOf(1980, 1982),
    worstOverall = listOf(1980)
)

val `a timeline from 1990-1999 with data` = FranchiseTimeline(
    startSeason = 1990,
    endSeason = 1999,
    name = "Factoria Trash",
    metroArea = Metro.SEATTLE,
    championships = listOf(1995),
    championshipAppearances = listOf(1995, 1996),
    playoffAppearances = listOf(1995, 1996, 1997, 1998),
    advancedInPlayoffs = listOf(1995, 1996),
    bestInDivision = listOf(1995, 1996, 1997, 1998),
    bestInConference = listOf(1995, 1996),
    bestOverall = listOf(1995),
    worstInDivision = listOf(1990, 1991, 1992, 1993),
    worstInConference = listOf(1990, 1992),
    worstOverall = listOf(1990)
)

val `an old timeline that predates divisions` = FranchiseTimeline(
    startSeason = 1940,
    endSeason = 1949,
    name = "Factoria Trash",
    metroArea = Metro.SEATTLE,
    championships = listOf(),
    championshipAppearances = listOf(),
    playoffAppearances = listOf(),
    advancedInPlayoffs = listOf(),
    bestInDivision = listOf(1941),
    bestInConference = listOf(),
    bestOverall = listOf(),
    worstInDivision = listOf(1942),
    worstInConference = listOf(),
    worstOverall = listOf()
)

val `a timeline that straddles the creation of divisions` = FranchiseTimeline(
    startSeason = 1964,
    endSeason = 1973,
    name = "Factoria Trash",
    metroArea = Metro.SEATTLE,
    championships = listOf(),
    championshipAppearances = listOf(),
    playoffAppearances = listOf(),
    advancedInPlayoffs = listOf(),
    bestInDivision = listOf(1964, 1973),
    bestInConference = listOf(),
    bestOverall = listOf(),
    worstInDivision = listOf(1965, 1972),
    worstInConference = listOf(),
    worstOverall = listOf()
)

val `an MLB franchise that has one timeline that straddles the creation of divisions` = Franchise(
    name = "Factoria Trash",
    firstSeason = 1964,
    isDefunct = false,
    league = League.MLB,
    timeline = listOf(`a timeline that straddles the creation of divisions`, `a timeline from 1990-1999 with data`)
)

val `a franchise with two timelines and data` = Franchise(
    name = "Factoria Trash",
    firstSeason = 1980,
    isDefunct = false,
    timeline = listOf(`a timeline from 1980-1989 with data`, `a timeline from 1990-1999 with data`)
)

val `a really old franchise` = Franchise(
    name = "Brooklyn Bubblebutts",
    firstSeason = 1870,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1870,
            endSeason = 1879,
            name = "Brooklyn Scalawags",
            metroArea = Metro.NEW_YORK,
            championships = listOf(1875),
            championshipAppearances = listOf(1875),
            playoffAppearances = listOf(1875),
            advancedInPlayoffs = listOf(1875),
            bestInDivision = listOf(1875),
            bestInConference = listOf(1875),
            bestOverall = listOf(1875),
            worstInDivision = listOf(1870),
            worstInConference = listOf(1870),
            worstOverall = listOf(1870)
        ),
        FranchiseTimeline(
            startSeason = 1880,
            endSeason = 1920,
            name = "Brooklyn Buttercheeks",
            metroArea = Metro.NEW_YORK,
            championships = listOf(1885, 1895, 1915),
            championshipAppearances = listOf(1885, 1895, 1900, 1915),
            playoffAppearances = listOf(1885, 1895, 1900, 1915),
            advancedInPlayoffs = listOf(1885, 1895, 1915),
            bestInDivision = listOf(1885, 1895, 1900, 1915),
            bestInConference = listOf(1885, 1895, 1900, 1915),
            bestOverall = listOf(1885, 1915),
            worstInDivision = listOf(1881, 1883, 1899, 1903, 1905),
            worstInConference = listOf(1881, 1883, 1899, 1903, 1905),
            worstOverall = listOf(1881, 1899, 1903)
        ),
        FranchiseTimeline(
            startSeason = 1921,
            name = "Brooklyn Bubblebutts",
            metroArea = Metro.NEW_YORK,
            championships = listOf(1930, 1955, 1980, 1988, 2021),
            championshipAppearances = listOf(1930, 1955, 1966, 1980, 1988, 2013, 2021),
            playoffAppearances = listOf(1930, 1955, 1966, 1968, 1980, 1984, 1988, 2013, 2017, 2021),
            advancedInPlayoffs = listOf(1930, 1955, 1966, 1980, 1984, 1988, 2013, 2021),
            bestInDivision = listOf(1930, 1955, 1966, 1968, 1980, 1984, 1988, 2013, 2021),
            bestInConference = listOf(1930, 1955, 1966, 1968, 1980, 1984, 2013, 2021),
            bestOverall = listOf(1930, 1955, 1966, 1968, 1980, 1984, 2013),
            worstInDivision = listOf(1935, 1944, 1951, 1969, 1972, 2001, 2010),
            worstInConference = listOf(1935, 1944, 1969, 2001),
            worstOverall = listOf(1935, 1969)
        ),
    )
)

val `a timeline that is ready to be trimmed` = FranchiseTimeline(
    startSeason = 1970,
    endSeason = 2010,
    name = "Bellevue Squares",
    metroArea = Metro.SEATTLE,
    championships = listOf(1973, 1988, 1989, 2008),
    championshipAppearances = listOf(1972, 1973, 1987, 1988, 1989, 2007, 2008),
    playoffAppearances = listOf(1970, 1971, 1972, 1973, 1985, 1986, 1987, 1988, 1989, 2005, 2006, 2007, 2008),
    advancedInPlayoffs = listOf(1971, 1972, 1973, 1986, 1987, 1988, 1989, 2006, 2007, 2008),
    bestInDivision = listOf(1970, 1971, 1972, 1973, 1985, 1986, 1987, 1988, 1989, 2005, 2006, 2007, 2008),
    bestInConference = listOf(1970, 1971, 1972, 1985, 1987, 1989, 2006, 2007, 2008),
    bestOverall = listOf(1972, 1987, 2007, 2008),
    worstInDivision = listOf(1976, 1977, 1978, 1979, 1990, 1991, 1992, 1993, 2000, 2001, 2002, 2003),
    worstInConference = listOf(1977, 1978, 1979, 1991, 1992, 1993, 2001, 2002, 2003),
    worstOverall = listOf(1978, 1979, 1992, 1993, 2002, 2003)
)

val `a franchise in New York` = Franchise(
    name = "New York Newsies",
    firstSeason = 1900,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1900,
            name = "New York Newsies",
            metroArea = Metro.NEW_YORK,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf(),
        )
    )
)

val `a franchise in Los Angeles` = Franchise(
    name = "Los Angeles Art Deco Enthusiasts",
    firstSeason = 1920,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1920,
            name = "Los Angeles Art Deco Enthusiasts",
            metroArea = Metro.LOS_ANGELES,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf(),
        )
    )
)

val `a franchise with timelines in different cities` = Franchise(
    name = "Los Angeles Divers",
    firstSeason = 1900,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1900,
            endSeason = 1957,
            name = "Brooklyn Divers",
            metroArea = Metro.NEW_YORK,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf(),
        ),
        FranchiseTimeline(
            startSeason = 1958,
            name = "Los Angeles Divers",
            metroArea = Metro.LOS_ANGELES,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf(),
        ),
    )
)

val `a franchise in Chicago` = Franchise(
    name = "Chicago Dogs",
    firstSeason = 1970,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1970,
            endSeason = 1989,
            name = "Chicago Dogs",
            metroArea = Metro.CHICAGO,
            championships = listOf(1983),
            championshipAppearances = listOf(1982, 1983),
            playoffAppearances = listOf(1980, 1981, 1982, 1983),
            advancedInPlayoffs = listOf(1981, 1982, 1983),
            bestInDivision = listOf(1981, 1982, 1985, 1983),
            bestInConference = listOf(1982, 1985),
            bestOverall = listOf(1982),
            worstInDivision = listOf(1970, 1971, 1972),
            worstInConference = listOf(1970, 1971),
            worstOverall = listOf(1970),
        )
    )
)

val `a second franchise in Chicago` = Franchise(
    name = "Chicago Buellers",
    firstSeason = 1970,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1970,
            endSeason = 1979,
            name = "Chicago Camerons",
            metroArea = Metro.CHICAGO,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(1970, 1971, 1972),
            worstInConference = listOf(1970, 1971),
            worstOverall = listOf(1970),
        ),
        FranchiseTimeline(
            startSeason = 1980,
            endSeason = 1989,
            name = "Chicago Buellers",
            metroArea = Metro.CHICAGO,
            championships = listOf(1989),
            championshipAppearances = listOf(1989),
            playoffAppearances = listOf(1988, 1989),
            advancedInPlayoffs = listOf(1989),
            bestInDivision = listOf(1988),
            bestInConference = listOf(1988),
            bestOverall = listOf(1988),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf(),
        ),
    )
)

val `a city with one franchise` = City(
    metroArea = Metro.CHICAGO,
    franchises = listOf(
        `a franchise in Chicago`
    )
)

val `a city with two franchises` = City(
    metroArea = Metro.CHICAGO,
    franchises = listOf(
        `a franchise in Chicago`,
        `a second franchise in Chicago`
    )
)

val `an MLB franchise that has some seasons before divisional play` = Franchise(
    name = "Cleveland Rocks",
    firstSeason = 1960,
    isDefunct = false,
    league = League.MLB,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1960,
            endSeason = 1979,
            name = "Cleveland Rocks",
            metroArea = Metro.CLEVELAND,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(1965, 1966, 1975, 1976),
            bestInConference = listOf(1965, 1966, 1975, 1976),
            bestOverall = listOf(1965, 1966, 1975, 1976),
            worstInDivision = listOf(1961, 1962, 1971, 1972),
            worstInConference = listOf(1961, 1962, 1971, 1972),
            worstOverall = listOf(1961, 1962, 1971, 1972),
        )
    )
)

val `a city with an MLB franchise that has some seasons before divisional play` = City(
    metroArea = Metro.CLEVELAND,
    franchises = listOf(`an MLB franchise that has some seasons before divisional play`)
)

val `an MLB franchise that includes a double season` = Franchise(
    name = "Detroit Temptations",
    firstSeason = 1980,
    isDefunct = false,
    league = League.MLB,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1980,
            endSeason = 1989,
            name = "Detroit Temptations",
            metroArea = Metro.DETROIT,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(1981, 1985),
            bestInConference = listOf(1981, 1985),
            bestOverall = listOf(1981, 1985),
            worstInDivision = listOf(1981, 1989),
            worstInConference = listOf(1981, 1989),
            worstOverall = listOf(1981, 1989),
        )
    )
)

val `a franchise that does not have a double season` = Franchise(
    name = "Detroit Robocops",
    firstSeason = 1980,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1980,
            endSeason = 1989,
            name = "Detroit Robocops",
            metroArea = Metro.DETROIT,
            championships = listOf(),
            championshipAppearances = listOf(),
            playoffAppearances = listOf(),
            advancedInPlayoffs = listOf(),
            bestInDivision = listOf(1981, 1985),
            bestInConference = listOf(1981, 1985),
            bestOverall = listOf(1981, 1985),
            worstInDivision = listOf(1981, 1989),
            worstInConference = listOf(1981, 1989),
            worstOverall = listOf(1981, 1989),
        )
    )
)

val `a city with an MLB franchise that played in a double season, and one other franchise` = City(
    metroArea = Metro.DETROIT,
    franchises = listOf(`an MLB franchise that includes a double season`, `a franchise that does not have a double season`)
)

val `an mlb franchise with a season without a postseason` = Franchise(
    name = "Cincinnati Flying Pigs",
    firstSeason = 1990,
    isDefunct = false,
    league = League.MLB,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1990,
            endSeason = 1999,
            name = "Cincinnati Flying Pigs",
            metroArea = Metro.CINCINNATI,
            championships = listOf(1991, 1995),
            championshipAppearances = listOf(1991, 1995),
            playoffAppearances = listOf(1991, 1995),
            advancedInPlayoffs = listOf(1991, 1995),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf()
        )
    )
)

val `another franchise with all seasons having postseasons` = Franchise(
    name = "Cincinnati Skylines",
    firstSeason = 1990,
    isDefunct = false,
    timeline = listOf(
        FranchiseTimeline(
            startSeason = 1990,
            endSeason = 1999,
            name = "Cincinnati Skylines",
            metroArea = Metro.CINCINNATI,
            championships = listOf(1991, 1995),
            championshipAppearances = listOf(1991, 1995),
            playoffAppearances = listOf(1991, 1995),
            advancedInPlayoffs = listOf(1991, 1995),
            bestInDivision = listOf(),
            bestInConference = listOf(),
            bestOverall = listOf(),
            worstInDivision = listOf(),
            worstInConference = listOf(),
            worstOverall = listOf()
        )
    )
)

val `a city with an MLB franchise that played in a season with no postseason, and one other franchise` = City(
    metroArea = Metro.CINCINNATI,
    franchises = listOf(`an mlb franchise with a season without a postseason`, `another franchise with all seasons having postseasons`)
)