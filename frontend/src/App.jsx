import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Courses from "./pages/Courses";
import CourseDetail from "./pages/CourseDetail";
import Instructors from "./pages/Instructors";
import Enrollments from "./pages/Enrollments";
import AdminSettings from "./pages/AdminSettings";
import RegistrationPeriods from "./pages/RegistrationPeriods";
import CourseManagement from "./pages/CourseManagement";
import AdminManagement from "./pages/AdminManagement";
import Layout from "./components/Layout";
import { AuthProvider, useAuth } from "./context/AuthContext";

const theme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#10b981",
      light: "#34d399",
      dark: "#059669",
      contrastText: "#ffffff",
    },
    secondary: {
      main: "#0f172a",
      light: "#1e293b",
      dark: "#020617",
      contrastText: "#ffffff",
    },
    background: {
      default: "#f1f5f9",
      paper: "#ffffff",
    },
    text: {
      primary: "#0f172a",
      secondary: "#64748b",
    },
    success: {
      main: "#10b981",
      light: "#34d399",
      dark: "#059669",
    },
    warning: {
      main: "#f59e0b",
      light: "#fbbf24",
      dark: "#d97706",
    },
    error: {
      main: "#ef4444",
      light: "#f87171",
      dark: "#dc2626",
    },
    info: {
      main: "#06b6d4",
      light: "#22d3ee",
      dark: "#0891b2",
    },
  },
  typography: {
    fontFamily: '"Be Vietnam Pro", -apple-system, BlinkMacSystemFont, sans-serif',
    h1: {
      fontWeight: 700,
      fontSize: "2.75rem",
      lineHeight: 1.2,
      letterSpacing: "-0.02em",
    },
    h2: {
      fontWeight: 700,
      fontSize: "2.25rem",
      lineHeight: 1.25,
      letterSpacing: "-0.01em",
    },
    h3: {
      fontWeight: 600,
      fontSize: "1.875rem",
      lineHeight: 1.3,
    },
    h4: {
      fontWeight: 600,
      fontSize: "1.5rem",
      lineHeight: 1.35,
    },
    h5: {
      fontWeight: 600,
      fontSize: "1.25rem",
      lineHeight: 1.4,
    },
    h6: {
      fontWeight: 600,
      fontSize: "1.125rem",
      lineHeight: 1.45,
    },
    body1: {
      fontSize: "1rem",
      lineHeight: 1.65,
      letterSpacing: "0.01em",
    },
    body2: {
      fontSize: "0.875rem",
      lineHeight: 1.6,
    },
    button: {
      textTransform: "none",
      fontWeight: 500,
      letterSpacing: "0.02em",
    },
    caption: {
      fontSize: "0.75rem",
      lineHeight: 1.5,
      letterSpacing: "0.03em",
    },
  },
  shape: {
    borderRadius: 12,
  },
  shadows: [
    "none",
    "0 1px 2px 0 rgba(0, 0, 0, 0.05)",
    "0 1px 3px 0 rgba(0, 0, 0, 0.08), 0 1px 2px 0 rgba(0, 0, 0, 0.04)",
    "0 4px 6px -1px rgba(0, 0, 0, 0.08), 0 2px 4px -1px rgba(0, 0, 0, 0.04)",
    "0 10px 15px -3px rgba(0, 0, 0, 0.08), 0 4px 6px -2px rgba(0, 0, 0, 0.04)",
    "0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 10px 10px -5px rgba(0, 0, 0, 0.03)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
    "0 25px 50px -12px rgba(0, 0, 0, 0.15)",
  ],
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          padding: "10px 24px",
          fontSize: "0.9rem",
          fontWeight: 500,
          boxShadow: "none",
          transition: "all 200ms cubic-bezier(0.4, 0, 0.2, 1)",
          "&:hover": {
            transform: "translateY(-1px)",
            boxShadow: "0 4px 12px rgba(16, 185, 129, 0.25)",
          },
          "&:active": {
            transform: "translateY(0)",
          },
        },
        contained: {
          "&:hover": {
            boxShadow: "0 6px 16px rgba(16, 185, 129, 0.3)",
          },
        },
        containedPrimary: {
          background: "linear-gradient(135deg, #10b981 0%, #059669 100%)",
          "&:hover": {
            background: "linear-gradient(135deg, #059669 0%, #047857 100%)",
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
          border: "1px solid rgba(0,0,0,0.04)",
          transition: "all 250ms cubic-bezier(0.4, 0, 0.2, 1)",
          "&:hover": {
            boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
            transform: "translateY(-2px)",
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
        },
        elevation1: {
          boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
        },
        elevation2: {
          boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
        },
        elevation3: {
          boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root": {
            borderRadius: 10,
            transition: "all 200ms ease",
            "&:hover": {
              boxShadow: "0 2px 8px rgba(16, 185, 129, 0.1)",
            },
            "&.Mui-focused": {
              boxShadow: "0 0 0 3px rgba(16, 185, 129, 0.15)",
            },
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          fontWeight: 500,
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          "& .MuiTableCell-head": {
            fontWeight: 600,
            backgroundColor: "#f8fafc",
            color: "#0f172a",
          },
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          transition: "background-color 150ms ease",
          "&:hover": {
            backgroundColor: "rgba(16, 185, 129, 0.04)",
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 20,
          boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
        },
      },
    },
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          borderRadius: 8,
          backgroundColor: "#0f172a",
          fontSize: "0.8rem",
          padding: "8px 14px",
        },
      },
    },
  },
});

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="courses" element={<Courses />} />
        <Route path="courses/:courseId" element={<CourseDetail />} />
        <Route path="instructors" element={<Instructors />} />
        <Route path="enrollments" element={<Enrollments />} />
        <Route path="admin-management" element={<AdminManagement />} />
        {/* Keep old routes for backward compatibility */}
        <Route path="admin-settings" element={<AdminSettings />} />
        <Route path="registration-periods" element={<RegistrationPeriods />} />
        <Route path="course-management" element={<CourseManagement />} />
      </Route>
    </Routes>
  );
}

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;

