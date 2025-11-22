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
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from "@mui/icons-material";
import { courseService } from "../services/courseService";
import { instructorService } from "../services/instructorService";

function Courses() {
  const [courses, setCourses] = useState([]);
  const [instructors, setInstructors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState("");
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
    fetchCourses();
    fetchInstructors();
  }, []);

  const fetchCourses = async () => {
    try {
      const response = await courseService.getAll();
      setCourses(response.data);
    } catch (error) {
      setError("Lỗi khi tải danh sách khóa học");
    } finally {
      setLoading(false);
    }
  };

  const fetchInstructors = async () => {
    try {
      const response = await instructorService.getAll();
      setInstructors(response.data);
    } catch (error) {
      console.error("Error fetching instructors:", error);
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
      } else {
        await courseService.create(data);
      }
      handleClose();
      fetchCourses();
    } catch (error) {
      setError(error.response?.data?.error || "Có lỗi xảy ra");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa khóa học này?")) {
      try {
        await courseService.delete(id);
        fetchCourses();
      } catch (error) {
        setError("Lỗi khi xóa khóa học");
      }
    }
  };

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
        <Typography variant="h4">Quản lý Khóa học</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Thêm khóa học
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Mã khóa học</TableCell>
              <TableCell>Tên khóa học</TableCell>
              <TableCell>Giảng viên</TableCell>
              <TableCell>Thời lượng</TableCell>
              <TableCell>Giá</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Thao tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {courses.map((course) => (
              <TableRow key={course.courseId}>
                <TableCell>{course.courseCode}</TableCell>
                <TableCell>{course.courseName}</TableCell>
                <TableCell>
                  {instructors.find(
                    (i) => i.instructorId === course.instructorId
                  )?.fullName || "N/A"}
                </TableCell>
                <TableCell>{course.durationWeeks} tuần</TableCell>
                <TableCell>
                  {course.price
                    ? new Intl.NumberFormat("vi-VN").format(course.price) +
                      " VNĐ"
                    : "N/A"}
                </TableCell>
                <TableCell>{course.status}</TableCell>
                <TableCell>
                  <IconButton size="small" onClick={() => handleOpen(course)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handleDelete(course.courseId)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {editing ? "Sửa khóa học" : "Thêm khóa học mới"}
        </DialogTitle>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              fullWidth
              label="Tên khóa học"
              value={formData.courseName}
              onChange={(e) =>
                setFormData({ ...formData, courseName: e.target.value })
              }
              required
            />
            <TextField
              fullWidth
              label="Mã khóa học"
              value={formData.courseCode}
              onChange={(e) =>
                setFormData({ ...formData, courseCode: e.target.value })
              }
              required
            />
            <TextField
              fullWidth
              label="Mô tả"
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              multiline
              rows={3}
            />
            <TextField
              fullWidth
              select
              label="Giảng viên"
              value={formData.instructorId}
              onChange={(e) =>
                setFormData({ ...formData, instructorId: e.target.value })
              }
              SelectProps={{ native: true }}
            >
              <option value="">Chọn giảng viên</option>
              {instructors.map((instructor) => (
                <option
                  key={instructor.instructorId}
                  value={instructor.instructorId}
                >
                  {instructor.fullName}
                </option>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Thời lượng (tuần)"
              type="number"
              value={formData.durationWeeks}
              onChange={(e) =>
                setFormData({ ...formData, durationWeeks: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="Giá"
              type="number"
              value={formData.price}
              onChange={(e) =>
                setFormData({ ...formData, price: e.target.value })
              }
            />
            <TextField
              fullWidth
              label="Số lượng tối đa"
              type="number"
              value={formData.maxStudents}
              onChange={(e) =>
                setFormData({ ...formData, maxStudents: e.target.value })
              }
            />
            <TextField
              fullWidth
              select
              label="Trạng thái"
              value={formData.status}
              onChange={(e) =>
                setFormData({ ...formData, status: e.target.value })
              }
              SelectProps={{ native: true }}
            >
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
              <option value="completed">Completed</option>
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editing ? "Cập nhật" : "Tạo mới"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default Courses;
