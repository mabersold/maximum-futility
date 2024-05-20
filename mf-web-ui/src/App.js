import './App.css';
import MetroReport from './components/metro_report/MetroReport';
import Seasons from './components/seasons/Seasons';
import { Container, Tab, Box } from "@mui/material";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useState } from 'react';

function App() {
  const [value, setValue] = useState("1");

  const handleChange = (event, newValue) => {
    setValue(newValue);
  }

  return (
    <div className="App">
      <Container>
        <TabContext value={value}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <TabList onChange={handleChange}>
              <Tab label="City Report" value="1" />
              <Tab label="Seasons" value="2" />
            </TabList>
          </Box>
          <TabPanel value="1">
            <MetroReport />
          </TabPanel>
          <TabPanel value="2">
            <Seasons />
          </TabPanel>
        </TabContext>
      </Container>
    </div>
  );
}

export default App;
