import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  CircularProgress,
  Chip,
  Card,
  CardContent,
  Grid,
  Tooltip,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  School as SchoolIcon,
  Info as InfoIcon,
} from "@mui/icons-material";
import { instructorService } from "../services/instructorService";
import { courseService } from "../services/courseService";
import { useAuth } from "../context/AuthContext";

function Instructors() {
  const { user } = useAuth();
  const [instructors, setInstructors] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    specialization: "",
    bio: "",
  });

  const isAdmin = user?.role === "ADMIN";

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [instructorsRes, coursesRes] = await Promise.all([
        instructorService.getAll(),
        courseService.getAll(),
      ]);
      setInstructors(instructorsRes.data);
      setCourses(coursesRes.data);
    } catch (err) {
      setError("Lỗi khi tải dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  const handleOpen = (instructor = null) => {
    if (instructor) {
      setEditing(instructor);
      setFormData({
        fullName: instructor.fullName || "",
        email: instructor.email || "",
        phone: instructor.phone || "",
        specialization: instructor.specialization || "",
        bio: instructor.bio || "",
      });
    } else {
      setEditing(null);
      setFormData({
        fullName: "",
        email: "",
        phone: "",
        specialization: "",
        bio: "",
      });
    }
    setOpen(true);
    setError("");
  };

  const handleClose = () => {
    setOpen(false);
    setEditing(null);
    setError("");
  };

  const handleSubmit = async () => {
    try {
      if (!formData.fullName || !formData.email) {
        setError("Vui lòng điền đầy đủ thông tin bắt buộc (Họ tên và Email)");
        return;
      }

      // Validate email format
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.email)) {
        setError("Email không hợp lệ");
        return;
      }

      if (editing) {
        await instructorService.update(editing.instructorId, formData);
        setSuccess("Cập nhật giảng viên thành công!");
      } else {
        await instructorService.create(formData);
        setSuccess("Thêm giảng viên thành công!");
      }

      handleClose();
      fetchData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(
        err.response?.data?.error || err.response?.data || "Có lỗi xảy ra"
      );
    }
  };

  const handleDelete = async (id) => {
    // Kiểm tra xem giảng viên có đang dạy khóa học nào không
    const instructorCourses = courses.filter((c) => c.instructorId === id);
    if (instructorCourses.length > 0) {
      setError(
        `Không thể xóa giảng viên này. Đang dạy ${instructorCourses.length} khóa học. Vui lòng gỡ giảng viên khỏi các khóa học trước.`
      );
      return;
    }

    if (window.confirm("Bạn có chắc chắn muốn xóa giảng viên này?")) {
      try {
        await instructorService.delete(id);
        setSuccess("Xóa giảng viên thành công!");
        fetchData();
        setTimeout(() => setSuccess(""), 3000);
      } catch (err) {
        setError("Lỗi khi xóa giảng viên");
      }
    }
  };

  const getInstructorCourseCount = (instructorId) => {
    return courses.filter((c) => c.instructorId === instructorId).length;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleDateString("vi-VN");
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={3}>
        <CircularProgress />
      </Box>
    );
  }

  // Nếu không phải admin, chỉ hiển thị danh sách xem
  if (!isAdmin) {
    return (
      <Box>
        <Typography
          variant="h4"
          gutterBottom
          sx={{ display: "flex", alignItems: "center", gap: 1 }}
        >
          <PersonIcon color="primary" />
          Danh sách Giảng viên
        </Typography>
        <Grid container spacing={2} sx={{ mt: 2 }}>
          {instructors.map((instructor) => (
            <Grid item xs={12} md={6} lg={4} key={instructor.instructorId}>
              <Card>
                <CardContent>
                  <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
                    {instructor.fullName}
                  </Typography>
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 0.5,
                      mb: 0.5,
                    }}
                  >
                    <EmailIcon fontSize="small" color="action" />
                    <Typography variant="body2">{instructor.email}</Typography>
                  </Box>
                  {instructor.phone && (
                    <Box
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 0.5,
                        mb: 0.5,
                      }}
                    >
                      <PhoneIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {instructor.phone}
                      </Typography>
                    </Box>
                  )}
                  {instructor.specialization && (
                    <Chip
                      label={instructor.specialization}
                      size="small"
                      color="primary"
                      variant="outlined"
                      sx={{ mt: 1 }}
                    />
                  )}
                  {instructor.bio && (
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ mt: 1 }}
                    >
                      {instructor.bio.length > 100
                        ? instructor.bio.substring(0, 100) + "..."
                        : instructor.bio}
                    </Typography>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    );
  }

  // Giao diện Admin - Quản lý đầy đủ
  return (
    <Box>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Typography
          variant="h4"
          sx={{ display: "flex", alignItems: "center", gap: 1 }}
        >
          <PersonIcon color="primary" />
          Quản lý Giảng viên
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Thêm giảng viên
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* Thống kê */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                Tổng số giảng viên
              </Typography>
              <Typography variant="h4">{instructors.length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                Giảng viên đang dạy
              </Typography>
              <Typography variant="h4" color="success.main">
                {
                  instructors.filter(
                    (i) => getInstructorCourseCount(i.instructorId) > 0
                  ).length
                }
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                Tổng số khóa học
              </Typography>
              <Typography variant="h4" color="primary.main">
                {courses.filter((c) => c.instructorId).length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>
                <strong>Họ tên</strong>
              </TableCell>
              <TableCell>
                <strong>Email</strong>
              </TableCell>
              <TableCell>
                <strong>Số điện thoại</strong>
              </TableCell>
              <TableCell>
                <strong>Chuyên môn</strong>
              </TableCell>
              <TableCell>
                <strong>Số khóa học</strong>
              </TableCell>
              <TableCell>
                <strong>Ngày tạo</strong>
              </TableCell>
              <TableCell align="center">
                <strong>Thao tác</strong>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {instructors.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                  <PersonIcon
                    sx={{ fontSize: 64, color: "text.secondary", mb: 2 }}
                  />
                  <Typography variant="h6" color="text.secondary" gutterBottom>
                    Chưa có giảng viên nào
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb: 2 }}
                  >
                    Hãy thêm giảng viên mới để bắt đầu
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => handleOpen()}
                  >
                    Thêm giảng viên đầu tiên
                  </Button>
                </TableCell>
              </TableRow>
            ) : (
              instructors.map((instructor) => (
                <TableRow key={instructor.instructorId} hover>
                  <TableCell>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                      <PersonIcon color="primary" fontSize="small" />
                      <Typography variant="body1" sx={{ fontWeight: 500 }}>
                        {instructor.fullName}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Box
                      sx={{ display: "flex", alignItems: "center", gap: 0.5 }}
                    >
                      <EmailIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {instructor.email}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    {instructor.phone ? (
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 0.5 }}
                      >
                        <PhoneIcon fontSize="small" color="action" />
                        <Typography variant="body2">
                          {instructor.phone}
                        </Typography>
                      </Box>
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        -
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    {instructor.specialization ? (
                      <Chip
                        label={instructor.specialization}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        -
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    <Chip
                      icon={<SchoolIcon />}
                      label={getInstructorCourseCount(instructor.instructorId)}
                      size="small"
                      color={
                        getInstructorCourseCount(instructor.instructorId) > 0
                          ? "success"
                          : "default"
                      }
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {formatDate(instructor.createdAt)}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip title="Sửa giảng viên">
                      <IconButton
                        size="small"
                        color="primary"
                        onClick={() => handleOpen(instructor)}
                      >
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Xóa giảng viên">
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => handleDelete(instructor.instructorId)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Dialog thêm/sửa giảng viên */}
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {editing ? "Sửa giảng viên" : "Thêm giảng viên mới"}
        </DialogTitle>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Alert severity="info" icon={<InfoIcon />} sx={{ mb: 2 }}>
            Sau khi thêm giảng viên, bạn có thể gán giảng viên này cho các môn
            học trong trang "Quản lý Môn học".
          </Alert>

          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              fullWidth
              label="Họ tên *"
              value={formData.fullName}
              onChange={(e) =>
                setFormData({ ...formData, fullName: e.target.value })
              }
              required
              placeholder="VD: Nguyễn Văn An"
            />
            <TextField
              fullWidth
              label="Email *"
              type="email"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
              required
              placeholder="VD: nva@email.com"
              helperText="Email phải là duy nhất"
            />
            <TextField
              fullWidth
              label="Số điện thoại"
              value={formData.phone}
              onChange={(e) =>
                setFormData({ ...formData, phone: e.target.value })
              }
              placeholder="VD: 0901234567"
            />
            <TextField
              fullWidth
              label="Chuyên môn"
              value={formData.specialization}
              onChange={(e) =>
                setFormData({ ...formData, specialization: e.target.value })
              }
              placeholder="VD: Lập trình Java, Web Development, Data Science..."
            />
            <TextField
              fullWidth
              label="Tiểu sử / Giới thiệu"
              value={formData.bio}
              onChange={(e) =>
                setFormData({ ...formData, bio: e.target.value })
              }
              multiline
              rows={4}
              placeholder="Giới thiệu về giảng viên, kinh nghiệm, thành tích..."
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editing ? "Cập nhật" : "Thêm giảng viên"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default Instructors;
