import React, { useState } from 'react';
import { Slider } from '@mui/material';

const TestSlider = () => {
  const [value, setValue] = useState([0, 100]);
  return (
    <Slider
      value={value}
      onChange={(e, v) => setValue(v)}
      onChangeCommitted={(e, v) => setValue(v)}
      valueLabelDisplay="auto"
      min={0}
      max={100}
    />
  );
};

export default TestSlider;
