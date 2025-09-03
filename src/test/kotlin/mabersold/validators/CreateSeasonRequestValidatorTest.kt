package mabersold.validators

import kotlinx.serialization.json.Json
import mabersold.models.api.requests.CreateSeasonRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateSeasonRequestValidatorTest {
    @Test
    fun `is valid `() {
        val testReq = getRequest("valid_single_table_no_playoffs")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(0, errors.size)
    }

    @Test
    fun `is invalid if endDate is before startDate`() {
        val testReq = getRequest("end_date_too_early")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(END_YEAR_TOO_LOW, errors[0])
    }

    @Test
    fun `is invalid if endDate is greater than startDate by more than one`() {
        val testReq = getRequest("end_date_too_far_ahead")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(END_YEAR_TOO_HIGH, errors[0])
    }

    @Test
    fun `is invalid if standings go beyond a depth of 2`() {
        val testReq = getRequest("subgroups_too_deep")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(NO_SUBGROUPS_AT_THIS_LEVEL, errors[0])
    }

    @Test
    fun `is invalid if franchise appears more than once`() {
        for (requestName in listOf(
            "duplicate_franchise_single_table",
            "duplicate_franchise_conferences",
            "duplicate_franchise_divisions"
        )) {
            val testReq = getRequest(requestName)

            val errors = validateCreateSeasonRequest(testReq)

            assertEquals(1, errors.size)
            assertEquals(DUPLICATE_TEAM_IN_STANDINGS, errors[0])
        }
    }

    @Test
    fun `is invalid if standings are overloaded`() {
        val testReq = getRequest("overloaded_standings")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(NO_RESULTS_AT_THIS_LEVEL, errors[0])
    }

    @Test
    fun `is invalid if standings are underloaded`() {
        val testReq = getRequest("underloaded_standings")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(NO_SUBGROUPS_AT_THIS_LEVEL, errors[0])
    }

    @Test
    fun `is invalid if playoff team not in the standings`() {
        val testReq = getRequest("playoff_interloper")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(INVALID_POSTSEASON_TEAM, errors[0])
    }

    @Test
    fun `is invalid if winning team do not appear in later postseason round`() {
        val testReq = getRequest("winner_does_not_advance")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(2, errors.size)
        assertEquals(LOSING_TEAM_ADVANCED, errors[0])
        assertEquals(WINNING_TEAM_DID_NOT_ADVANCE, errors[1])
    }

    @Test
    fun `is invalid if a team plays itself`() {
        val testReq = getRequest("congratulation_you_played_yourself")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(TEAM_PLAYED_ITSELF, errors[0])
    }

    @Test
    fun `is invalid if multiple championship rounds`() {
        val testReq = getRequest("too_many_championships")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(CHAMPIONSHIP_INVALID, errors[0])
    }

    @Test
    fun `is invalid if last round has more than one matchup`() {
        val testReq = getRequest("you_know_those_championships_that_are_like_double_championships")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(CHAMPIONSHIP_INVALID, errors[0])
    }

    @Test
    fun `is invalid if last round is not a championship`() {
        val testReq = getRequest("no_championship")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(CHAMPIONSHIP_INVALID, errors[0])
    }

    @Test
    fun `is invalid if major divisions is greater than 0 but no major divisions found`() {
        val testReq = getRequest("no_major_divisions")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(2, errors.size)
        assertEquals(NO_RESULTS_AT_THIS_LEVEL, errors[0])
        assertEquals(MISMATCHED_MAJOR_DIVISIONS, errors[1])
    }

    @Test
    fun `is invalid if major divisions doesn't match provided number`() {
        val testReq = getRequest("mismatched_major_divisions")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(MISMATCHED_MAJOR_DIVISIONS, errors[0])
    }

    @Test
    fun `is invalid if minor divisions doesn't match provided number`() {
        val testReq = getRequest("mismatched_minor_divisions")

        val errors = validateCreateSeasonRequest(testReq)

        assertEquals(1, errors.size)
        assertEquals(MISMATCHED_MINOR_DIVISIONS, errors[0])
    }

    // TODO: how to handle temporary franchise merges

    private fun getRequest(name: String): CreateSeasonRequest {
        val json = readResource()
        val requests = Json.decodeFromString<Map<String, CreateSeasonRequest>>(json)

        return requests[name] ?: throw Error("Could not find example $name")
    }

    private fun readResource(): String {
        return this::class.java.classLoader.getResource("CreateSeasonValidationRequests.json")?.readText() ?: error ("Resource not found")
    }
}