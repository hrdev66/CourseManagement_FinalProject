import React, { useState } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Avatar,
  Menu,
  MenuItem,
  Divider,
  Tooltip,
  Badge,
} from "@mui/material";
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  School as SchoolIcon,
  Person as PersonIcon,
  Bookmark as BookmarkIcon,
  Logout as LogoutIcon,
  AdminPanelSettings as AdminIcon,
  Notifications as NotificationsIcon,
  Search as SearchIcon,
  KeyboardArrowRight as ArrowIcon,
} from "@mui/icons-material";
import { useAuth } from "../context/AuthContext";

const drawerWidth = 280;

const getMenuItems = (userRole) => {
  const baseItems = [
    { text: "Tổng quan", icon: <DashboardIcon />, path: "/" },
    { text: "Khóa học", icon: <SchoolIcon />, path: "/courses" },
    { text: "Giảng viên", icon: <PersonIcon />, path: "/instructors" },
    { text: "Đăng ký học", icon: <BookmarkIcon />, path: "/enrollments" },
  ];

  if (userRole === "ADMIN") {
    baseItems.push({
      text: "Quản lý hệ thống",
      icon: <AdminIcon />,
      path: "/admin-management",
    });
  }

  return baseItems;
};

function Layout() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const menuItems = getMenuItems(user?.role);

  const getRoleLabel = (role) => {
    switch (role) {
      case "ADMIN":
        return "Quản trị viên";
      case "INSTRUCTOR":
        return "Giảng viên";
      default:
        return "Sinh viên";
    }
  };

  const getRoleBadgeColor = (role) => {
    switch (role) {
      case "ADMIN":
        return "linear-gradient(135deg, #f59e0b 0%, #d97706 100%)";
      case "INSTRUCTOR":
        return "linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)";
      default:
        return "linear-gradient(135deg, #10b981 0%, #059669 100%)";
    }
  };

  const drawer = (
    <Box
      sx={{
        height: "100%",
        display: "flex",
        flexDirection: "column",
        background: "linear-gradient(180deg, #0f172a 0%, #1e293b 100%)",
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Decorative Elements */}
      <Box
        sx={{
          position: "absolute",
          top: -100,
          right: -100,
          width: 200,
          height: 200,
          background:
            "radial-gradient(circle, rgba(16, 185, 129, 0.15) 0%, transparent 70%)",
          borderRadius: "50%",
          pointerEvents: "none",
        }}
      />
      <Box
        sx={{
          position: "absolute",
          bottom: 100,
          left: -50,
          width: 150,
          height: 150,
          background:
            "radial-gradient(circle, rgba(6, 182, 212, 0.1) 0%, transparent 70%)",
          borderRadius: "50%",
          pointerEvents: "none",
        }}
      />

      {/* Header */}
      <Box
        sx={{
          p: 3,
          pb: 2,
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
          }}
        >
          <Box
            sx={{
              width: 48,
              height: 48,
              borderRadius: 3,
              background: "linear-gradient(135deg, #10b981 0%, #059669 100%)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              boxShadow: "0 4px 14px rgba(16, 185, 129, 0.4)",
            }}
          >
            <SchoolIcon sx={{ color: "white", fontSize: 26 }} />
          </Box>
          <Box>
            <Typography
              sx={{
                fontWeight: 700,
                fontSize: "1.25rem",
                color: "white",
                letterSpacing: "-0.02em",
              }}
            >
              LMS
            </Typography>
            <Typography
              sx={{
                fontSize: "0.75rem",
                color: "rgba(255, 255, 255, 0.5)",
                letterSpacing: "0.05em",
                textTransform: "uppercase",
              }}
            >
              Education Platform
            </Typography>
          </Box>
        </Box>
      </Box>

      {/* Navigation Label */}
      <Box sx={{ px: 3, py: 1.5 }}>
        <Typography
          sx={{
            fontSize: "0.7rem",
            fontWeight: 600,
            color: "rgba(255, 255, 255, 0.35)",
            letterSpacing: "0.1em",
            textTransform: "uppercase",
          }}
        >
          Menu chính
        </Typography>
      </Box>

      {/* Menu Items */}
      <Box sx={{ flex: 1, overflow: "auto", px: 1.5 }}>
        <List sx={{ py: 0 }}>
          {menuItems.map((item, index) => {
            const isSelected =
              location.pathname === item.path ||
              (item.path !== "/" && location.pathname.startsWith(item.path));
            return (
              <ListItem
                key={item.text}
                disablePadding
                sx={{
                  mb: 0.5,
                  animation: "slideInLeft 0.4s ease-out forwards",
                  animationDelay: `${index * 0.05}s`,
                  opacity: 0,
                }}
              >
                <ListItemButton
                  selected={isSelected}
                  onClick={() => {
                    navigate(item.path);
                    setMobileOpen(false);
                  }}
                  sx={{
                    borderRadius: 2.5,
                    py: 1.4,
                    px: 2,
                    transition: "all 200ms cubic-bezier(0.4, 0, 0.2, 1)",
                    position: "relative",
                    overflow: "hidden",
                    "&::before": {
                      content: '""',
                      position: "absolute",
                      left: 0,
                      top: "50%",
                      transform: "translateY(-50%)",
                      width: 3,
                      height: isSelected ? "60%" : 0,
                      background:
                        "linear-gradient(180deg, #10b981 0%, #059669 100%)",
                      borderRadius: "0 4px 4px 0",
                      transition: "height 200ms ease",
                    },
                    "&.Mui-selected": {
                      bgcolor: "rgba(16, 185, 129, 0.15)",
                      "&:hover": {
                        bgcolor: "rgba(16, 185, 129, 0.2)",
                      },
                      "& .MuiListItemIcon-root": {
                        color: "#10b981",
                      },
                      "& .MuiListItemText-primary": {
                        color: "white",
                        fontWeight: 600,
                      },
                    },
                    "&:hover": {
                      bgcolor: "rgba(255, 255, 255, 0.05)",
                      "&::before": {
                        height: "30%",
                      },
                    },
                  }}
                >
                  <ListItemIcon
                    sx={{
                      color: isSelected
                        ? "#10b981"
                        : "rgba(255, 255, 255, 0.5)",
                      minWidth: 42,
                      transition: "color 200ms ease",
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText
                    primary={item.text}
                    primaryTypographyProps={{
                      fontSize: "0.9rem",
                      fontWeight: isSelected ? 600 : 400,
                      color: isSelected ? "white" : "rgba(255, 255, 255, 0.7)",
                    }}
                  />
                  {isSelected && (
                    <ArrowIcon
                      sx={{
                        fontSize: 18,
                        color: "#10b981",
                        animation: "fadeIn 0.2s ease-out",
                      }}
                    />
                  )}
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Box>

      {/* User Profile Section */}
      <Box sx={{ p: 2 }}>
        <Box
          sx={{
            p: 2,
            borderRadius: 3,
            background: "rgba(255, 255, 255, 0.05)",
            backdropFilter: "blur(10px)",
            border: "1px solid rgba(255, 255, 255, 0.08)",
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <Avatar
              sx={{
                width: 44,
                height: 44,
                background: getRoleBadgeColor(user?.role),
                fontWeight: 600,
                fontSize: "1rem",
                boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
              }}
            >
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Box sx={{ flex: 1, minWidth: 0 }}>
              <Typography
                sx={{
                  fontWeight: 600,
                  fontSize: "0.9rem",
                  color: "white",
                }}
                noWrap
              >
                {user?.fullName || user?.username}
              </Typography>
              <Box
                sx={{
                  display: "inline-flex",
                  alignItems: "center",
                  px: 1,
                  py: 0.3,
                  borderRadius: 1,
                  background: "rgba(16, 185, 129, 0.2)",
                  mt: 0.5,
                }}
              >
                <Typography
                  sx={{
                    fontSize: "0.65rem",
                    color: "#10b981",
                    fontWeight: 600,
                    letterSpacing: "0.03em",
                  }}
                >
                  {getRoleLabel(user?.role)}
                </Typography>
              </Box>
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      {/* App Bar */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: "rgba(255, 255, 255, 0.8)",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid",
          borderColor: "rgba(0, 0, 0, 0.06)",
        }}
      >
        <Toolbar
          sx={{
            justifyContent: "space-between",
            px: { xs: 2, sm: 3, md: 4 },
            py: 1,
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{
                display: { sm: "none" },
                color: "text.primary",
              }}
            >
              <MenuIcon />
            </IconButton>
            <Box>
              <Typography
                variant="h5"
                sx={{
                  fontWeight: 700,
                  color: "text.primary",
                  display: { xs: "none", sm: "block" },
                  letterSpacing: "-0.02em",
                }}
              >
                {location.pathname === "/"
                  ? "Tổng quan"
                  : location.pathname.includes("courses")
                  ? "Khóa học"
                  : location.pathname.includes("instructors")
                  ? "Giảng viên"
                  : location.pathname.includes("enrollments")
                  ? "Đăng ký học"
                  : location.pathname.includes("admin-management")
                  ? "Quản lý hệ thống"
                  : "Hệ thống"}
              </Typography>
              <Typography
                variant="body2"
                sx={{
                  color: "text.secondary",
                  display: { xs: "none", md: "block" },
                  mt: 0.3,
                }}
              >
                {new Date().toLocaleDateString("vi-VN", {
                  weekday: "long",
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                })}
              </Typography>
            </Box>
          </Box>

          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            {/* Search Button */}
            <Tooltip title="Tìm kiếm">
              <IconButton
                sx={{
                  color: "text.secondary",
                  bgcolor: "rgba(0, 0, 0, 0.04)",
                  "&:hover": {
                    bgcolor: "rgba(0, 0, 0, 0.08)",
                  },
                }}
              >
                <SearchIcon fontSize="small" />
              </IconButton>
            </Tooltip>

            {/* Notifications */}
            <Tooltip title="Thông báo">
              <IconButton
                sx={{
                  color: "text.secondary",
                  bgcolor: "rgba(0, 0, 0, 0.04)",
                  "&:hover": {
                    bgcolor: "rgba(0, 0, 0, 0.08)",
                  },
                }}
              >
                <Badge
                  badgeContent={3}
                  sx={{
                    "& .MuiBadge-badge": {
                      background:
                        "linear-gradient(135deg, #ef4444 0%, #dc2626 100%)",
                      color: "white",
                      fontSize: "0.65rem",
                      fontWeight: 600,
                    },
                  }}
                >
                  <NotificationsIcon fontSize="small" />
                </Badge>
              </IconButton>
            </Tooltip>

            <Divider orientation="vertical" flexItem sx={{ mx: 1, my: 1 }} />

            {/* User Menu */}
            <IconButton
              onClick={handleMenuOpen}
              sx={{
                p: 0.5,
                "&:hover": {
                  bgcolor: "transparent",
                },
              }}
            >
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  gap: 1.5,
                  px: 1,
                }}
              >
                <Avatar
                  sx={{
                    width: 38,
                    height: 38,
                    background: getRoleBadgeColor(user?.role),
                    fontWeight: 600,
                    fontSize: "0.9rem",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
                  }}
                >
                  {user?.username?.charAt(0).toUpperCase()}
                </Avatar>
                <Box
                  sx={{
                    display: { xs: "none", md: "block" },
                    textAlign: "left",
                  }}
                >
                  <Typography
                    sx={{
                      fontWeight: 600,
                      fontSize: "0.875rem",
                      color: "text.primary",
                      lineHeight: 1.3,
                    }}
                  >
                    {user?.fullName || user?.username}
                  </Typography>
                  <Typography
                    sx={{
                      fontSize: "0.75rem",
                      color: "text.secondary",
                    }}
                  >
                    {getRoleLabel(user?.role)}
                  </Typography>
                </Box>
              </Box>
            </IconButton>

            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "right",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              PaperProps={{
                sx: {
                  mt: 1.5,
                  minWidth: 200,
                  borderRadius: 3,
                  boxShadow: "0 10px 40px rgba(0, 0, 0, 0.15)",
                  border: "1px solid rgba(0,0,0,0.06)",
                  overflow: "visible",
                  "&::before": {
                    content: '""',
                    position: "absolute",
                    top: -6,
                    right: 20,
                    width: 12,
                    height: 12,
                    bgcolor: "background.paper",
                    transform: "rotate(45deg)",
                    borderLeft: "1px solid rgba(0,0,0,0.06)",
                    borderTop: "1px solid rgba(0,0,0,0.06)",
                  },
                },
              }}
            >
              <Box sx={{ px: 2, py: 1.5 }}>
                <Typography sx={{ fontWeight: 600, fontSize: "0.9rem" }}>
                  {user?.fullName || user?.username}
                </Typography>
                <Typography
                  sx={{ color: "text.secondary", fontSize: "0.8rem" }}
                >
                  {user?.email || `${user?.username}@lms.edu`}
                </Typography>
              </Box>
              <Divider />
              <MenuItem
                onClick={handleLogout}
                sx={{
                  py: 1.2,
                  mx: 1,
                  my: 0.5,
                  borderRadius: 2,
                  color: "error.main",
                  "&:hover": {
                    bgcolor: "error.lighter",
                    color: "error.dark",
                  },
                }}
              >
                <ListItemIcon>
                  <LogoutIcon fontSize="small" sx={{ color: "inherit" }} />
                </ListItemIcon>
                <ListItemText
                  primary="Đăng xuất"
                  primaryTypographyProps={{ fontWeight: 500 }}
                />
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Drawer */}
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: "block", sm: "none" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: drawerWidth,
              border: "none",
            },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: "none", sm: "block" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: drawerWidth,
              border: "none",
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          minHeight: "100vh",
          bgcolor: "#f1f5f9",
          position: "relative",
        }}
      >
        {/* Background Pattern */}
        <Box
          sx={{
            position: "fixed",
            top: 0,
            left: drawerWidth,
            right: 0,
            height: "100vh",
            background: `
              radial-gradient(ellipse at 0% 0%, rgba(16, 185, 129, 0.05) 0%, transparent 50%),
              radial-gradient(ellipse at 100% 100%, rgba(6, 182, 212, 0.05) 0%, transparent 50%)
            `,
            pointerEvents: "none",
            zIndex: 0,
          }}
        />

        <Toolbar sx={{ mb: 2 }} />
        <Box
          sx={{
            p: { xs: 2, sm: 3, md: 4 },
            maxWidth: "1440px",
            mx: "auto",
            position: "relative",
            zIndex: 1,
          }}
        >
          <Box
            sx={{
              animation: "fadeInUp 0.5s ease-out",
            }}
          >
            <Outlet />
          </Box>
        </Box>
      </Box>
    </Box>
  );
}

export default Layout;
