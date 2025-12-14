import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import {
  Box,
  Tabs,
  Tab,
  Paper,
} from "@mui/material";
import {
  School as SchoolIcon,
  Bookmark as BookmarkIcon,
  AdminPanelSettings as AdminIcon,
} from "@mui/icons-material";
import CourseManagement from "./CourseManagement";
import RegistrationPeriods from "./RegistrationPeriods";
import AdminSettings from "./AdminSettings";

function AdminManagement() {
  const location = useLocation();
  const [activeTab, setActiveTab] = useState(0);

  useEffect(() => {
    // Xác định tab dựa trên hash hoặc pathname
    if (location.hash === "#periods" || location.pathname.includes("registration-periods")) {
      setActiveTab(1);
    } else if (location.hash === "#users" || location.pathname.includes("admin-settings")) {
      setActiveTab(2);
    } else {
      setActiveTab(0);
    }
  }, [location]);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
    // Update hash để giữ state khi refresh
    const hashMap = ["#courses", "#periods", "#users"];
    window.location.hash = hashMap[newValue];
  };

  return (
    <Box>
      <Paper sx={{ mb: 3 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          variant="scrollable"
          scrollButtons="auto"
        >
          <Tab
            icon={<SchoolIcon />}
            label="Quản lý Môn học"
            iconPosition="start"
          />
          <Tab
            icon={<BookmarkIcon />}
            label="Đợt đăng ký"
            iconPosition="start"
          />
          <Tab
            icon={<AdminIcon />}
            label="Quản lý Tài khoản"
            iconPosition="start"
          />
        </Tabs>
      </Paper>

      {activeTab === 0 && <CourseManagement />}
      {activeTab === 1 && <RegistrationPeriods />}
      {activeTab === 2 && <AdminSettings />}
    </Box>
  );
}

export default AdminManagement;

