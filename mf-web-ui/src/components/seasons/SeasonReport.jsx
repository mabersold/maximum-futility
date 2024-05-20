import React, { useEffect, useState } from "react";
import { Box, Alert } from "@mui/material";
import Structure from "./Structure";
import TeamDisplay from "./TeamDisplay";

const SeasonReport = ({ seasonReport }) => {
  return (
    <div>
      {seasonReport ? (
        <>
          <Box sx={{ my: 2 }}>
            {seasonReport.warnings.map((warning) => (
              <Alert severity="warning">{warning.message}</Alert>
            ))}
          </Box>
          <Box>
            <h2>{seasonReport.name}</h2>
            <p>Start Year: {seasonReport.startYear}</p>
            <p>End Year: {seasonReport.endYear}</p>
          </Box>
          <Box sx={{ my: 2 }}>
            <h2>Postseason</h2>
            <Box>
                <h3>Champion</h3>
                {seasonReport.champion}
            </Box>
            <TeamDisplay title={"Finalists"} teams={seasonReport.teamsInChampionship} />
            <TeamDisplay title={"Advanced in Playoffs"} teams={seasonReport.teamsAdvancedInPostseason} />
            <TeamDisplay title={"Made Playoffs"} teams={seasonReport.teamsInPostseason} />
          </Box>
          <Box sx={{ my: 2 }}>
            <h2>Structure</h2>
            <Structure structure={seasonReport.structure} />
          </Box>
        </>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};

export default SeasonReport;
