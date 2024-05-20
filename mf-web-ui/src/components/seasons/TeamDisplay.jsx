import { Box } from "@mui/material";

const TeamDisplay = ({ title, teams }) => {
  return (
    <Box>
      {title && <h3>{title}</h3>}

      {teams.map((team, index) => (
        <Box>{team}</Box>
      ))}
    </Box>
  );
};

export default TeamDisplay;
