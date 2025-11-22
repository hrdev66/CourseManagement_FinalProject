import React from 'react'
import { Typography, Box, Paper } from '@mui/material'

function Enrollments() {
  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Quản lý Đăng ký học
      </Typography>
      <Paper elevation={2} sx={{ p: 3, mt: 2 }}>
        <Typography variant="body1" color="text.secondary">
          Tính năng đang được phát triển...
        </Typography>
      </Paper>
    </Box>
  )
}

export default Enrollments

