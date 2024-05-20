import React, { useEffect, useState } from "react";
import {
  Container,
  Select,
  MenuItem,
  Box,
  InputLabel,
  FormControl,
} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import SeasonReport from "./SeasonReport";

const Seasons = () => {
  const [leagues, setLeagues] = useState([]);
  const [seasons, setSeasons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedLeague, setSelectedLeague] = useState(null);
  const [seasonsLoaded, setSeasonsLoaded] = useState(false);
  const [selectedSeason, setSelectedSeason] = useState(null);
  const [seasonReport, setSeasonReport] = useState(null);
  const [seasonReportLoaded, setSeasonReportLoaded] = useState(false);

  useEffect(() => {
    const fetchLeagues = async () => {
      const response = await fetch("http://localhost:8080/leagues");

      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      const data = await response.json();
      setLeagues(data);
      setLoading(false);
    };

    const fetchSeasons = async () => {
      const response = await fetch(
        `http://localhost:8080/leagues/${selectedLeague}/seasons`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      const data = await response.json();
      setSeasons(data);
      setSeasonsLoaded(true);
    };

    const fetchSeasonReport = async () => {
      const response = await fetch(
        `http://localhost:8080/season_report/${selectedSeason}`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      const data = await response.json();
      setSeasonReport(data);
      setSeasonReportLoaded(true);
    };

    fetchLeagues();
    if (selectedLeague) {
      fetchSeasons();
    }

    if (selectedSeason) {
      fetchSeasonReport();
    }
  }, [selectedLeague, selectedSeason]);

  const handleSelectLeague = (event) => {
    setSelectedLeague(event.target.value);
    setSeasonsLoaded(false);
    setSeasonReportLoaded(false);
  };

  const handleSelectSeason = (event) => {
    setSelectedSeason(event.target.value);
    setSeasonReportLoaded(false);
  };

  return (
    <Container>
      <h1>Seasons</h1>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <Box sx={{ my: 1 }}>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel id="select-league-label">League</InputLabel>
            <Select
              labelId="select-league-label"
              label="League"
              onChange={handleSelectLeague}
            >
              {leagues.map((league, index) => (
                <MenuItem key={index} value={league.id}>
                  {league.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      )}
      {seasonsLoaded && (
        <Box sx={{ my: 1 }}>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel id="select-season-label">Season</InputLabel>
            <Select
              labelId="select-season-label"
              label="Season"
              onChange={handleSelectSeason}
            >
              {seasons.map((season, index) => (
                <MenuItem key={index} value={season.id}>
                  {season.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      )}
      {seasonReportLoaded && (
        <>
            <DeleteIcon />
            <SeasonReport seasonReport={seasonReport} />
        </>
      )}
    </Container>
  );
};

export default Seasons;
