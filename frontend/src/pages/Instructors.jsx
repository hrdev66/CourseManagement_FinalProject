import React from 'react'
import { Typography, Box, Paper } from '@mui/material'

function Instructors() {
  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Quản lý Giảng viên
      </Typography>
      <Paper elevation={2} sx={{ p: 3, mt: 2 }}>
        <Typography variant="body1" color="text.secondary">
          Tính năng đang được phát triển...
        </Typography>
      </Paper>
    </Box>
  )
}

export default Instructors

