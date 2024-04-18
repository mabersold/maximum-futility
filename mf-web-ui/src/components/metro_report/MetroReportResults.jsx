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

  useEffect(() => {
    const fetchData = async () => {
      try {
        let url = "http://localhost:8080/metro_report";
        if (selectedMetric) {
          url += `?metricType=${selectedMetric}`;
        }

        if (selectedLeagues && selectedLeagues.length > 0) {
          selectedLeagues.forEach((leagueId) => {
            url += `&leagueId=${leagueId}`;
          });
        }

        if (selectedRange && selectedRange.length === 2) {
          url += `&startYear=${selectedRange[0]}&endYear=${selectedRange[1]}`;
          url += `&minLastActiveYear=${selectedRange[1] - 1}`
        }
        
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
