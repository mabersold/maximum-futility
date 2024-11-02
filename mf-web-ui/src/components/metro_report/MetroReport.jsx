import React, { useEffect, useState } from "react";
import MetroReportOptions from "./MetroReportOptions";
import MetroReportResults from "./MetroReportResults";
import { Container, Button } from "@mui/material";

const MetroReport = () => {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMetric, setSelectedMetric] = useState("");
  const [selectedLeagues, setSelectedLeagues] = useState([]);
  const [selectedRange, setSelectedRange] = useState([0, 0]);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/v1/reports/metro/options");
        if (!response.ok) {
          throw new Error("Failed to fetch data");
        }
        const data = await response.json();
        setOptions(data);
        setSelectedMetric(data.metrics[0].name);
        setSelectedLeagues(data.leagues.map((league) => league.id));
        setSelectedRange([data.yearRange.startYear, data.yearRange.endYear]);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchMetrics();
  }, []);

  const downloadCSV = () => {
    const params = new URLSearchParams({
      metricType: selectedMetric,
      startYear: selectedRange[0],
      endYear: selectedRange[1],
      minLastActiveYear: selectedRange[1] - 1,
    });

    selectedLeagues.forEach((leagueId) => {
      params.append('leagueId', leagueId);
    });

    const url = `http://localhost:8080/api/v1/reports/metro/csv?${params.toString()}`;
    window.location.href = url;
  };

  return (
    <Container>
      <h1>City Report</h1>
      <MetroReportOptions
        loading={loading}
        options={options}
        selectedMetric={selectedMetric}
        changeMetric={(m) => setSelectedMetric(m)}
        selectedLeagues={selectedLeagues}
        changeLeagues={setSelectedLeagues}
        selectedRange={selectedRange}
        changeRange={(r) => setSelectedRange(r)}
      />
      <Button variant="contained" color="primary" onClick={downloadCSV}>
        Download CSV
      </Button>
      <MetroReportResults
        loading={loading}
        selectedMetric={selectedMetric}
        selectedLeagues={selectedLeagues}
        selectedRange={selectedRange}
      />
    </Container>
  );
};

export default MetroReport;
