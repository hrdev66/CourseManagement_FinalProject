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
    elevation={2}
    sx={{
      p: 3,
      display: "flex",
      alignItems: "center",
      gap: 2,
      background: `linear-gradient(135deg, ${color}15 0%, ${color}05 100%)`,
    }}
  >
    <Box
      sx={{
        p: 2,
        borderRadius: 2,
        bgcolor: `${color}20`,
        color: color,
      }}
    >
      {icon}
    </Box>
    <Box>
      {loading ? (
        <CircularProgress size={24} />
      ) : (
        <Typography variant="h4" fontWeight="bold">
          {value}
        </Typography>
      )}
      <Typography variant="body2" color="text.secondary">
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

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Chào mừng, {user?.fullName || user?.username}! 👋
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Vai trò: <strong>{user?.role}</strong>
      </Typography>

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
