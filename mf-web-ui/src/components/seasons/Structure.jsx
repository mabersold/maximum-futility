import React, { useEffect, useState } from "react";
import { Box } from "@mui/material";
import TeamDisplay from "./TeamDisplay";

const Structure = ({ structure }) => {
  return (
    <>
      <Box sx = {{ border: 1, borderRadius: 2, p: 1, m: 1 }}>
        <Box>
          <h3>{structure.name}</h3>
        </Box>
        <TeamDisplay title={"Finished First"} teams={structure.finishedFirst} />
        <TeamDisplay title={"Finished Last"} teams={structure.finishedLast} />
        {structure.groups.length === 0 && structure.teams.length > 0 && (
          <TeamDisplay title={"All"} teams={structure.teams} />
        )}
        {structure.groups.length > 0 && (
          <Box>
            {structure.groups.map((group, index) => (
              <Structure structure={group} key={index} />
            ))}
          </Box>
        )}
      </Box>
    </>
  );
};

export default Structure;
