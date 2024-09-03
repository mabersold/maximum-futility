import React, { useState, useEffect } from "react";
import DataTable from "react-data-table-component";

const MetroReportResults = ({loading, selectedMetric, selectedLeagues, selectedRange}) => {
  const [reportResults, setReportResults] = useState([]);

  const columns = [
    {
      name: "City",
      selector: (row) => row.name,
      sortable: true,
    },
    {
      name: "Total",
      selector: (row) => row.total,
      sortable: true,
    },
    {
      name: "Opportunities",
      selector: (row) => row.opportunities,
      sortable: true,
    },
    {
      name: "Rate",
      selector: (row) => row.rate,
      sortable: true,
      format: (row) => `${(row.rate * 100).toFixed(2)}%`,
    },
  ];

  const buildUrl = (baseUrl, selectedMetric, selectedLeagues, selectedRange) => {
    const url = new URL(baseUrl);
    const params = new URLSearchParams();

    if (selectedMetric) {
      params.append('metricType', selectedMetric);
    }

    if (selectedLeagues && selectedLeagues.length > 0) {
      selectedLeagues.forEach((leagueId) => {
        params.append('leagueId', leagueId);
      });
    }

    if (selectedRange && selectedRange.length === 2) {
      params.append('startYear', selectedRange[0]);
      params.append('endYear', selectedRange[1]);
      params.append('minLastActiveYear', selectedRange[1] - 1);
    }

    url.search = params.toString();
    return url.toString();
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const url = buildUrl("http://localhost:8080/api/v1/metro_report", selectedMetric, selectedLeagues, selectedRange);
        
        const response = await fetch(url);
        if (!response.ok) {
          throw new Error("Failed to fetch data");
        }
        const data = await response.json();
        setReportResults(data);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    if (selectedMetric) {
      fetchData();
    }
  }, [selectedMetric, selectedLeagues, selectedRange]);

  return (
    <div>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <DataTable columns={columns} data={reportResults.data} striped={true}/>
      )}
    </div>
  );
};

export default MetroReportResults;
