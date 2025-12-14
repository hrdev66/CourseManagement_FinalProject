import React, { useEffect, useState } from "react";
import { Grid, Paper, Typography, Box, CircularProgress } from "@mui/material";
import {
  School as SchoolIcon,
  People as PeopleIcon,
  Assignment as AssignmentIcon,
  Bookmark as BookmarkIcon,
} from "@mui/icons-material";
import { useAuth } from "../context/AuthContext";
import { dashboardService } from "../services/dashboardService";

const StatCard = ({ title, value, icon, color, loading }) => (
  <Paper
    elevation={1}
    sx={{
      p: 2.5,
      display: "flex",
      alignItems: "center",
      gap: 2,
      bgcolor: 'background.paper',
      border: `1px solid rgba(0, 0, 0, 0.08)`,
      borderRadius: 2,
      transition: 'box-shadow 0.2s',
      '&:hover': {
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
      },
    }}
  >
    <Box
      sx={{
        p: 2,
        borderRadius: 1.5,
        bgcolor: `${color}15`,
        color: color,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      {icon}
    </Box>
    <Box sx={{ flex: 1 }}>
      {loading ? (
        <CircularProgress size={20} sx={{ color: color }} />
      ) : (
        <Typography 
          variant="h5" 
          fontWeight={600}
          sx={{ 
            color: 'text.primary',
            mb: 0.3,
            fontSize: '1.5rem',
          }}
        >
          {value?.toLocaleString('vi-VN') || 0}
        </Typography>
      )}
      <Typography 
        variant="body2" 
        color="text.secondary"
        sx={{ 
          fontSize: '0.85rem',
        }}
      >
        {title}
      </Typography>
    </Box>
  </Paper>
);

function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await dashboardService.getStats();
        setStats(response.data);
      } catch (error) {
        console.error("Error fetching stats:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const getRoleLabel = (role) => {
    switch (role) {
      case 'ADMIN': return 'Quản trị viên';
      case 'INSTRUCTOR': return 'Giảng viên';
      case 'STUDENT': return 'Sinh viên';
      default: return role;
    }
  };

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography 
          variant="h4" 
          gutterBottom
          sx={{ 
            fontWeight: 700,
            mb: 1,
            color: 'text.primary',
          }}
        >
          Chào mừng trở lại, {user?.fullName || user?.username}! 👋
        </Typography>
        <Typography 
          variant="body1" 
          color="text.secondary"
          sx={{ 
            fontSize: '1rem',
            display: 'flex',
            alignItems: 'center',
            gap: 1,
          }}
        >
          <Box
            component="span"
            sx={{
              px: 1.2,
              py: 0.4,
              borderRadius: 1,
              bgcolor: 'primary.main',
              color: 'white',
              fontSize: '0.75rem',
              fontWeight: 500,
            }}
          >
            {getRoleLabel(user?.role)}
          </Box>
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Tổng khóa học"
            value={stats?.totalCourses || 0}
            icon={<SchoolIcon sx={{ fontSize: 40 }} />}
            color="#1976d2"
            loading={loading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Sinh viên"
            value={stats?.totalStudents || 0}
            icon={<PeopleIcon sx={{ fontSize: 40 }} />}
            color="#2e7d32"
            loading={loading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Bài tập"
            value={stats?.totalAssignments || 0}
            icon={<AssignmentIcon sx={{ fontSize: 40 }} />}
            color="#ed6c02"
            loading={loading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Đăng ký học"
            value={stats?.totalEnrollments || 0}
            icon={<BookmarkIcon sx={{ fontSize: 40 }} />}
            color="#9c27b0"
            loading={loading}
          />
        </Grid>
      </Grid>
    </Box>
  );
}

export default Dashboard;
