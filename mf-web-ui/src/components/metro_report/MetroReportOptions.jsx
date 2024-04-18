import React, { useState } from "react";
import { Slider, Select, MenuItem, Box } from "@mui/material";

const MetroReportOptions = ({
  loading,
  options,
  selectedMetric,
  changeMetric,
  selectedLeagues,
  changeLeagues,
  selectedRange,
  changeRange,
}) => {
  const [sliderValue, setSliderValue] = useState(selectedRange);
  const [metricValue, setMetricValue] = useState(selectedMetric);

  const handleMetricChange = (event) => {
    setMetricValue(event.target.value);
    changeMetric(event.target.value);
  };
  
  const handleCheckboxChange = (event) => {
    const leagueId = parseInt(event.target.value);

    changeLeagues((prevSelectedLeagues) => {
      if (event.target.checked) {
        return [...prevSelectedLeagues, leagueId];
      } else {
        return prevSelectedLeagues.filter((id) => id !== leagueId);
      }
    });
  };

  const handleRangeChange = (event, newValue) => {
    setSliderValue(newValue);
  };

  const handleRangeChangeCommitted = (event, newValue) => {
    const minValue = Math.min(newValue[0], newValue[1]);
    const maxValue = Math.max(newValue[0], newValue[1]);
    changeRange([minValue, maxValue]);
  };

  const defaultRange = sliderValue[0] === 0 && sliderValue[1] === 0;

  return (
    <div>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div>
          <Box sx={{ my: 2 }}>
            <Select
              value={metricValue ? metricValue : options.metrics[0].name}
              onChange={handleMetricChange}
            >
              {options.metrics.map((metric, index) => (
                <MenuItem key={index} value={metric.name}>
                  {metric.display_name}
                </MenuItem>
              ))}

            </Select>
          </Box>
          <Box sx={{ my: 2 }}>
            {options.leagues.map((option, index) => (
              <span key={index}>
                <input
                  type="checkbox"
                  key={index}
                  id={`league-` + option.id}
                  value={option.id}
                  checked={selectedLeagues.includes(option.id)}
                  onChange={handleCheckboxChange}
                />
                <label htmlFor={`league-` + option.id}>{option.name}</label>
              </span>
            ))}
          </Box>
          <Box sx={{ my: 4 }}>
            <Slider
              getAriaLabel={() => "Year range"}
              min={options.yearRange.startYear}
              max={options.yearRange.endYear}
              value={defaultRange ? [options.yearRange.startYear, options.yearRange.endYear] : sliderValue}
              onChange={handleRangeChange}
              onChangeCommitted={handleRangeChangeCommitted}
              disableSwap={true}
              valueLabelDisplay="on"
            />
          </Box>
        </div>
      )}
    </div>
  );
};

export default MetroReportOptions;
