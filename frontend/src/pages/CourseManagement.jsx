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
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Grid,
  Card,
  CardContent,
  Tooltip,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  School as SchoolIcon,
  Person as PersonIcon,
  AccessTime as TimeIcon,
  AttachMoney as MoneyIcon,
  Info as InfoIcon,
} from "@mui/icons-material";
import { courseService } from "../services/courseService";
import { instructorService } from "../services/instructorService";
import { useAuth } from "../context/AuthContext";

function CourseManagement() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [instructors, setInstructors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [formData, setFormData] = useState({
    courseName: "",
    courseCode: "",
    description: "",
    instructorId: "",
    durationWeeks: "",
    price: "",
    maxStudents: "",
    status: "active",
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [coursesRes, instructorsRes] = await Promise.all([
        courseService.getAll(),
        instructorService.getAll(),
      ]);
      setCourses(coursesRes.data);
      setInstructors(instructorsRes.data);
    } catch (error) {
      setError("Lỗi khi tải dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  const handleOpen = (course = null) => {
    if (course) {
      setEditing(course);
      setFormData({
        courseName: course.courseName || "",
        courseCode: course.courseCode || "",
        description: course.description || "",
        instructorId: course.instructorId || "",
        durationWeeks: course.durationWeeks || "",
        price: course.price || "",
        maxStudents: course.maxStudents || "",
        status: course.status || "active",
      });
    } else {
      setEditing(null);
      setFormData({
        courseName: "",
        courseCode: "",
        description: "",
        instructorId: "",
        durationWeeks: "",
        price: "",
        maxStudents: "",
        status: "active",
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
      if (!formData.courseName || !formData.courseCode) {
        setError(
          "Vui lòng điền đầy đủ thông tin bắt buộc (Tên môn học và Mã môn học)"
        );
        return;
      }

      const data = {
        ...formData,
        instructorId: formData.instructorId
          ? parseInt(formData.instructorId)
          : null,
        durationWeeks: formData.durationWeeks
          ? parseInt(formData.durationWeeks)
          : null,
        price: formData.price ? parseFloat(formData.price) : null,
        maxStudents: formData.maxStudents
          ? parseInt(formData.maxStudents)
          : null,
      };

      if (editing) {
        await courseService.update(editing.courseId, data);
        setSuccess("Cập nhật môn học thành công!");
      } else {
        await courseService.create(data);
        setSuccess("Thêm môn học thành công!");
      }

      handleClose();
      fetchData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (error) {
      setError(
        error.response?.data?.error || error.response?.data || "Có lỗi xảy ra"
      );
    }
  };

  const handleDelete = async (id) => {
    if (
      window.confirm(
        "Bạn có chắc chắn muốn xóa môn học này? Hành động này sẽ xóa tất cả dữ liệu liên quan."
      )
    ) {
      try {
        await courseService.delete(id);
        setSuccess("Xóa môn học thành công!");
        fetchData();
        setTimeout(() => setSuccess(""), 3000);
      } catch (error) {
        setError(
          "Lỗi khi xóa môn học. Có thể môn học này đang được sử dụng trong đợt đăng ký hoặc đã có sinh viên đăng ký."
        );
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "active":
        return "success";
      case "inactive":
        return "default";
      case "completed":
        return "info";
      default:
        return "default";
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "active":
        return "Hoạt động";
      case "inactive":
        return "Tạm dừng";
      case "completed":
        return "Hoàn thành";
      default:
        return status;
    }
  };

  const formatPrice = (price) => {
    if (!price) return "Miễn phí";
    return new Intl.NumberFormat("vi-VN").format(price) + " VNĐ";
  };

  const getInstructorName = (instructorId) => {
    const instructor = instructors.find((i) => i.instructorId === instructorId);
    return instructor?.fullName || "Chưa gán";
  };

  if (user?.role !== "ADMIN") {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Bạn không có quyền truy cập trang này. Chỉ Admin mới có thể quản lý
          môn học.
        </Alert>
      </Box>
    );
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={3}>
        <CircularProgress />
      </Box>
    );
  }

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
          <SchoolIcon color="primary" />
          Quản lý Môn học
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Thêm môn học mới
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
                Tổng số môn học
              </Typography>
              <Typography variant="h4">{courses.length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                Môn học đang hoạt động
              </Typography>
              <Typography variant="h4" color="success.main">
                {courses.filter((c) => c.status === "active").length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="text.secondary" gutterBottom>
                Môn học đã gán giảng viên
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
                <strong>Mã môn</strong>
              </TableCell>
              <TableCell>
                <strong>Tên môn học</strong>
              </TableCell>
              <TableCell>
                <strong>Giảng viên</strong>
              </TableCell>
              <TableCell>
                <strong>Thời lượng</strong>
              </TableCell>
              <TableCell>
                <strong>Học phí</strong>
              </TableCell>
              <TableCell>
                <strong>Số SV tối đa</strong>
              </TableCell>
              <TableCell>
                <strong>Trạng thái</strong>
              </TableCell>
              <TableCell align="center">
                <strong>Thao tác</strong>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {courses.length === 0 ? (
              <TableRow>
                <TableCell colSpan={8} align="center" sx={{ py: 4 }}>
                  <SchoolIcon
                    sx={{ fontSize: 48, color: "text.secondary", mb: 2 }}
                  />
                  <Typography variant="h6" color="text.secondary" gutterBottom>
                    Chưa có môn học nào
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb: 2 }}
                  >
                    Hãy thêm môn học mới để bắt đầu
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => handleOpen()}
                  >
                    Thêm môn học đầu tiên
                  </Button>
                </TableCell>
              </TableRow>
            ) : (
              courses.map((course) => (
                <TableRow key={course.courseId} hover>
                  <TableCell>
                    <Chip
                      label={course.courseCode}
                      color="primary"
                      size="small"
                      variant="outlined"
                      icon={<SchoolIcon />}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body1" sx={{ fontWeight: 500 }}>
                      {course.courseName}
                    </Typography>
                    {course.description && (
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        display="block"
                      >
                        {course.description.length > 60
                          ? course.description.substring(0, 60) + "..."
                          : course.description}
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    {course.instructorId ? (
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 0.5 }}
                      >
                        <PersonIcon fontSize="small" color="primary" />
                        <Typography variant="body2">
                          {getInstructorName(course.instructorId)}
                        </Typography>
                      </Box>
                    ) : (
                      <Chip
                        label="Chưa gán"
                        size="small"
                        color="warning"
                        variant="outlined"
                      />
                    )}
                  </TableCell>
                  <TableCell>
                    <Box
                      sx={{ display: "flex", alignItems: "center", gap: 0.5 }}
                    >
                      <TimeIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {course.durationWeeks || 0} tuần
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Typography
                      variant="body2"
                      color="primary.main"
                      sx={{ fontWeight: 500 }}
                    >
                      {formatPrice(course.price)}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    {course.maxStudents ? (
                      <Typography variant="body2">
                        {course.maxStudents} SV
                      </Typography>
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        Không giới hạn
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={getStatusLabel(course.status)}
                      color={getStatusColor(course.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip title="Sửa môn học">
                      <IconButton
                        size="small"
                        color="primary"
                        onClick={() => handleOpen(course)}
                      >
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Xóa môn học">
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => handleDelete(course.courseId)}
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

      {/* Dialog thêm/sửa môn học */}
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {editing ? "Sửa môn học" : "Thêm môn học mới"}
        </DialogTitle>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Alert severity="info" icon={<InfoIcon />} sx={{ mb: 2 }}>
            Sau khi thêm môn học, bạn có thể thêm môn học này vào các đợt đăng
            ký trong trang "Đợt đăng ký".
          </Alert>

          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Tên môn học *"
                  value={formData.courseName}
                  onChange={(e) =>
                    setFormData({ ...formData, courseName: e.target.value })
                  }
                  required
                  placeholder="VD: Lập trình Java cơ bản"
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Mã môn học *"
                  value={formData.courseCode}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      courseCode: e.target.value.toUpperCase(),
                    })
                  }
                  required
                  placeholder="VD: JAVA101"
                  helperText="Mã môn học phải là duy nhất"
                />
              </Grid>
            </Grid>

            <TextField
              fullWidth
              label="Mô tả môn học"
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              multiline
              rows={3}
              placeholder="Mô tả chi tiết về môn học, nội dung, mục tiêu học tập..."
            />

            <FormControl fullWidth>
              <InputLabel>Giảng viên dạy môn này *</InputLabel>
              <Select
                value={formData.instructorId}
                onChange={(e) =>
                  setFormData({ ...formData, instructorId: e.target.value })
                }
                label="Giảng viên dạy môn này *"
                required
              >
                <MenuItem value="">-- Chọn giảng viên --</MenuItem>
                {instructors.map((instructor) => (
                  <MenuItem
                    key={instructor.instructorId}
                    value={instructor.instructorId}
                  >
                    {instructor.fullName}{" "}
                    {instructor.email && `(${instructor.email})`}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Grid container spacing={2}>
              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Thời lượng (tuần)"
                  type="number"
                  value={formData.durationWeeks}
                  onChange={(e) =>
                    setFormData({ ...formData, durationWeeks: e.target.value })
                  }
                  inputProps={{ min: 1 }}
                  helperText="Số tuần học"
                />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Học phí (VNĐ)"
                  type="number"
                  value={formData.price}
                  onChange={(e) =>
                    setFormData({ ...formData, price: e.target.value })
                  }
                  inputProps={{ min: 0 }}
                  helperText="Để trống nếu miễn phí"
                />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Số sinh viên tối đa"
                  type="number"
                  value={formData.maxStudents}
                  onChange={(e) =>
                    setFormData({ ...formData, maxStudents: e.target.value })
                  }
                  inputProps={{ min: 1 }}
                  helperText="Để trống nếu không giới hạn"
                />
              </Grid>
            </Grid>

            <FormControl fullWidth>
              <InputLabel>Trạng thái</InputLabel>
              <Select
                value={formData.status}
                onChange={(e) =>
                  setFormData({ ...formData, status: e.target.value })
                }
                label="Trạng thái"
              >
                <MenuItem value="active">Hoạt động</MenuItem>
                <MenuItem value="inactive">Tạm dừng</MenuItem>
                <MenuItem value="completed">Hoàn thành</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editing ? "Cập nhật" : "Thêm môn học"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default CourseManagement;
