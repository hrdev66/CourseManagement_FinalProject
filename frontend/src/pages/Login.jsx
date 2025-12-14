import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Link,
  InputAdornment,
  IconButton,
} from "@mui/material";
import {
  School as SchoolIcon,
  Person as PersonIcon,
  Lock as LockIcon,
  Visibility,
  VisibilityOff,
} from "@mui/icons-material";
import { useAuth } from "../context/AuthContext";
import StudentRegistrationDialog from "../components/StudentRegistrationDialog";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [openRegister, setOpenRegister] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    const result = await login(username, password);
    if (result.success) {
      navigate("/");
    } else {
      setError(result.error || "Đăng nhập thất bại");
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Left Side - Decorative */}
      <Box
        sx={{
          display: { xs: "none", md: "flex" },
          width: "55%",
          background:
            "linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%)",
          position: "relative",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          overflow: "hidden",
        }}
      >
        {/* Decorative circles */}
        <Box
          sx={{
            position: "absolute",
            top: -150,
            right: -150,
            width: 400,
            height: 400,
            background:
              "radial-gradient(circle, rgba(16, 185, 129, 0.2) 0%, transparent 70%)",
            borderRadius: "50%",
          }}
        />
        <Box
          sx={{
            position: "absolute",
            bottom: -100,
            left: -100,
            width: 300,
            height: 300,
            background:
              "radial-gradient(circle, rgba(6, 182, 212, 0.15) 0%, transparent 70%)",
            borderRadius: "50%",
          }}
        />
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "30%",
            width: 200,
            height: 200,
            background:
              "radial-gradient(circle, rgba(16, 185, 129, 0.1) 0%, transparent 70%)",
            borderRadius: "50%",
            transform: "translate(-50%, -50%)",
          }}
        />

        {/* Content */}
        <Box
          sx={{
            position: "relative",
            zIndex: 1,
            textAlign: "center",
            px: 6,
            animation: "fadeInUp 0.8s ease-out",
          }}
        >
          <Box
            sx={{
              width: 100,
              height: 100,
              borderRadius: 4,
              background: "linear-gradient(135deg, #10b981 0%, #059669 100%)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              mx: "auto",
              mb: 4,
              boxShadow: "0 20px 40px rgba(16, 185, 129, 0.3)",
            }}
          >
            <SchoolIcon sx={{ color: "white", fontSize: 50 }} />
          </Box>

          <Typography
            variant="h2"
            sx={{
              color: "white",
              fontWeight: 700,
              mb: 2,
              letterSpacing: "-0.02em",
            }}
          >
            LMS
          </Typography>

          <Typography
            sx={{
              color: "rgba(255, 255, 255, 0.6)",
              fontSize: "1.1rem",
              maxWidth: 400,
              mx: "auto",
              lineHeight: 1.7,
            }}
          >
            Hệ thống quản lý khóa học trực tuyến hiện đại, giúp việc học tập trở
            nên dễ dàng và hiệu quả hơn.
          </Typography>

          {/* Feature badges */}
          <Box
            sx={{
              display: "flex",
              gap: 2,
              justifyContent: "center",
              mt: 5,
              flexWrap: "wrap",
            }}
          >
            {["Quản lý khóa học", "Theo dõi tiến độ", "Báo cáo thống kê"].map(
              (feature, index) => (
                <Box
                  key={feature}
                  sx={{
                    px: 2.5,
                    py: 1,
                    borderRadius: 3,
                    background: "rgba(255, 255, 255, 0.08)",
                    backdropFilter: "blur(10px)",
                    border: "1px solid rgba(255, 255, 255, 0.1)",
                    animation: "fadeInUp 0.6s ease-out",
                    animationDelay: `${0.2 + index * 0.1}s`,
                    animationFillMode: "backwards",
                  }}
                >
                  <Typography
                    sx={{
                      color: "rgba(255, 255, 255, 0.8)",
                      fontSize: "0.85rem",
                      fontWeight: 500,
                    }}
                  >
                    {feature}
                  </Typography>
                </Box>
              )
            )}
          </Box>
        </Box>
      </Box>

      {/* Right Side - Login Form */}
      <Box
        sx={{
          flex: 1,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          background: "#f8fafc",
          position: "relative",
          px: { xs: 2, sm: 4 },
        }}
      >
        {/* Subtle pattern */}
        <Box
          sx={{
            position: "absolute",
            inset: 0,
            background: `
              radial-gradient(ellipse at 0% 0%, rgba(16, 185, 129, 0.03) 0%, transparent 50%),
              radial-gradient(ellipse at 100% 100%, rgba(6, 182, 212, 0.03) 0%, transparent 50%)
            `,
            pointerEvents: "none",
          }}
        />

        <Container maxWidth="sm" sx={{ position: "relative", zIndex: 1 }}>
          <Box
            sx={{
              animation: "fadeInUp 0.6s ease-out",
              animationDelay: "0.2s",
              animationFillMode: "backwards",
            }}
          >
            {/* Mobile Logo */}
            <Box
              sx={{
                display: { xs: "flex", md: "none" },
                flexDirection: "column",
                alignItems: "center",
                mb: 4,
              }}
            >
              <Box
                sx={{
                  width: 70,
                  height: 70,
                  borderRadius: 3,
                  background:
                    "linear-gradient(135deg, #10b981 0%, #059669 100%)",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  mb: 2,
                  boxShadow: "0 10px 25px rgba(16, 185, 129, 0.3)",
                }}
              >
                <SchoolIcon sx={{ color: "white", fontSize: 35 }} />
              </Box>
              <Typography
                variant="h4"
                sx={{
                  fontWeight: 700,
                  color: "#0f172a",
                  letterSpacing: "-0.02em",
                }}
              >
                LMS
              </Typography>
            </Box>

            <Paper
              elevation={0}
              sx={{
                p: { xs: 3, sm: 5 },
                borderRadius: 4,
                background: "white",
                boxShadow: "0 4px 24px rgba(0,0,0,0.06)",
                border: "1px solid rgba(0,0,0,0.04)",
              }}
            >
              <Box sx={{ mb: 4 }}>
                <Typography
                  variant="h4"
                  sx={{
                    fontWeight: 700,
                    color: "#0f172a",
                    mb: 1,
                    letterSpacing: "-0.01em",
                  }}
                >
                  Chào mừng trở lại! 👋
                </Typography>
                <Typography
                  sx={{
                    color: "text.secondary",
                    fontSize: "0.95rem",
                  }}
                >
                  Đăng nhập để tiếp tục sử dụng hệ thống
                </Typography>
              </Box>

              {error && (
                <Alert
                  severity="error"
                  sx={{
                    mb: 3,
                    borderRadius: 3,
                    "& .MuiAlert-icon": {
                      alignItems: "center",
                    },
                  }}
                >
                  {error}
                </Alert>
              )}

              <form onSubmit={handleSubmit}>
                <TextField
                  fullWidth
                  label="Tên đăng nhập"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  margin="normal"
                  required
                  autoFocus
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonIcon
                          sx={{ color: "text.secondary", fontSize: 20 }}
                        />
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    "& .MuiOutlinedInput-root": {
                      borderRadius: 3,
                      bgcolor: "#f8fafc",
                      transition: "all 200ms ease",
                      "&:hover": {
                        bgcolor: "#f1f5f9",
                      },
                      "&.Mui-focused": {
                        bgcolor: "white",
                        boxShadow: "0 0 0 3px rgba(16, 185, 129, 0.1)",
                      },
                    },
                  }}
                />
                <TextField
                  fullWidth
                  label="Mật khẩu"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  margin="normal"
                  required
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockIcon
                          sx={{ color: "text.secondary", fontSize: 20 }}
                        />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPassword(!showPassword)}
                          edge="end"
                          size="small"
                        >
                          {showPassword ? (
                            <VisibilityOff sx={{ fontSize: 20 }} />
                          ) : (
                            <Visibility sx={{ fontSize: 20 }} />
                          )}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    "& .MuiOutlinedInput-root": {
                      borderRadius: 3,
                      bgcolor: "#f8fafc",
                      transition: "all 200ms ease",
                      "&:hover": {
                        bgcolor: "#f1f5f9",
                      },
                      "&.Mui-focused": {
                        bgcolor: "white",
                        boxShadow: "0 0 0 3px rgba(16, 185, 129, 0.1)",
                      },
                    },
                  }}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  sx={{
                    mt: 4,
                    mb: 2,
                    py: 1.6,
                    borderRadius: 3,
                    fontSize: "1rem",
                    fontWeight: 600,
                    textTransform: "none",
                    background:
                      "linear-gradient(135deg, #10b981 0%, #059669 100%)",
                    boxShadow: "0 4px 14px rgba(16, 185, 129, 0.35)",
                    transition: "all 250ms cubic-bezier(0.4, 0, 0.2, 1)",
                    "&:hover": {
                      background:
                        "linear-gradient(135deg, #059669 0%, #047857 100%)",
                      boxShadow: "0 6px 20px rgba(16, 185, 129, 0.45)",
                      transform: "translateY(-2px)",
                    },
                    "&:active": {
                      transform: "translateY(0)",
                    },
                  }}
                >
                  Đăng nhập
                </Button>
                <Box textAlign="center">
                  <Typography
                    variant="body2"
                    sx={{ color: "text.secondary", display: "inline" }}
                  >
                    Chưa có tài khoản?{" "}
                  </Typography>
                  <Link
                    component="button"
                    type="button"
                    onClick={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      setOpenRegister(true);
                    }}
                    sx={{
                      cursor: "pointer",
                      color: "#10b981",
                      fontWeight: 600,
                      textDecoration: "none",
                      transition: "color 150ms ease",
                      "&:hover": {
                        color: "#059669",
                        textDecoration: "underline",
                      },
                    }}
                  >
                    Đăng ký ngay
                  </Link>
                </Box>
              </form>
            </Paper>

            {/* Footer */}
            <Typography
              sx={{
                textAlign: "center",
                mt: 4,
                color: "text.secondary",
                fontSize: "0.8rem",
              }}
            >
              © 2024 LMS. Phát triển bởi Nhóm 5.
            </Typography>
          </Box>
        </Container>
      </Box>

      <StudentRegistrationDialog
        open={openRegister}
        onClose={() => setOpenRegister(false)}
      />
    </Box>
  );
}

export default Login;
