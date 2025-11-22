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
import { assignmentService } from "../services/assignmentService";
import { courseService } from "../services/courseService";

function Assignments() {
  const [assignments, setAssignments] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState("");
  const [formData, setFormData] = useState({
    courseId: "",
    title: "",
    description: "",
    dueDate: "",
    maxScore: "",
    assignmentType: "homework",
    status: "published",
  });

  useEffect(() => {
    fetchAssignments();
    fetchCourses();
  }, []);

  const fetchAssignments = async () => {
    try {
      const response = await assignmentService.getAll();
      setAssignments(response.data);
    } catch (error) {
      setError("Lỗi khi tải danh sách bài tập");
    } finally {
      setLoading(false);
    }
  };

  const fetchCourses = async () => {
    try {
      const response = await courseService.getAll();
      setCourses(response.data);
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  const handleOpen = (assignment = null) => {
    if (assignment) {
      setEditing(assignment);
      setFormData({
        courseId: assignment.courseId || "",
        title: assignment.title || "",
        description: assignment.description || "",
        dueDate: assignment.dueDate || "",
        maxScore: assignment.maxScore || "",
        assignmentType: assignment.assignmentType || "homework",
        status: assignment.status || "published",
      });
    } else {
      setEditing(null);
      setFormData({
        courseId: "",
        title: "",
        description: "",
        dueDate: "",
        maxScore: "",
        assignmentType: "homework",
        status: "published",
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
        courseId: parseInt(formData.courseId),
        maxScore: formData.maxScore ? parseInt(formData.maxScore) : 100,
      };

      if (editing) {
        await assignmentService.update(editing.assignmentId, data);
      } else {
        await assignmentService.create(data);
      }
      handleClose();
      fetchAssignments();
    } catch (error) {
      setError(error.response?.data?.error || "Có lỗi xảy ra");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa bài tập này?")) {
      try {
        await assignmentService.delete(id);
        fetchAssignments();
      } catch (error) {
        setError("Lỗi khi xóa bài tập");
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
        <Typography variant="h4">Quản lý Bài tập</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Thêm bài tập
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
              <TableCell>Tiêu đề</TableCell>
              <TableCell>Khóa học</TableCell>
              <TableCell>Hạn nộp</TableCell>
              <TableCell>Điểm tối đa</TableCell>
              <TableCell>Loại</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Thao tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {assignments.map((assignment) => (
              <TableRow key={assignment.assignmentId}>
                <TableCell>{assignment.title}</TableCell>
                <TableCell>
                  {courses.find((c) => c.courseId === assignment.courseId)
                    ?.courseName || "N/A"}
                </TableCell>
                <TableCell>{assignment.dueDate || "N/A"}</TableCell>
                <TableCell>{assignment.maxScore}</TableCell>
                <TableCell>{assignment.assignmentType}</TableCell>
                <TableCell>{assignment.status}</TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    onClick={() => handleOpen(assignment)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handleDelete(assignment.assignmentId)}
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
          {editing ? "Sửa bài tập" : "Thêm bài tập mới"}
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
              select
              label="Khóa học"
              value={formData.courseId}
              onChange={(e) =>
                setFormData({ ...formData, courseId: e.target.value })
              }
              SelectProps={{ native: true }}
              required
            >
              <option value="">Chọn khóa học</option>
              {courses.map((course) => (
                <option key={course.courseId} value={course.courseId}>
                  {course.courseName}
                </option>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Tiêu đề"
              value={formData.title}
              onChange={(e) =>
                setFormData({ ...formData, title: e.target.value })
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
              label="Hạn nộp"
              type="date"
              value={formData.dueDate}
              onChange={(e) =>
                setFormData({ ...formData, dueDate: e.target.value })
              }
              InputLabelProps={{ shrink: true }}
            />
            <TextField
              fullWidth
              label="Điểm tối đa"
              type="number"
              value={formData.maxScore}
              onChange={(e) =>
                setFormData({ ...formData, maxScore: e.target.value })
              }
            />
            <TextField
              fullWidth
              select
              label="Loại bài tập"
              value={formData.assignmentType}
              onChange={(e) =>
                setFormData({ ...formData, assignmentType: e.target.value })
              }
              SelectProps={{ native: true }}
            >
              <option value="homework">Homework</option>
              <option value="quiz">Quiz</option>
              <option value="project">Project</option>
            </TextField>
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
              <option value="published">Published</option>
              <option value="draft">Draft</option>
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

export default Assignments;
