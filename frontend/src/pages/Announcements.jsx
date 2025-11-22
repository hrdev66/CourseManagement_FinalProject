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
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from "@mui/icons-material";
import { announcementService } from "../services/announcementService";
import { courseService } from "../services/courseService";
import { useAuth } from "../context/AuthContext";

function Announcements() {
  const { user } = useAuth();
  const [announcements, setAnnouncements] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState("");
  const [formData, setFormData] = useState({
    courseId: "",
    title: "",
    content: "",
    priority: "normal",
  });

  useEffect(() => {
    fetchAnnouncements();
    fetchCourses();
  }, []);

  const fetchAnnouncements = async () => {
    try {
      const response = await announcementService.getAll();
      setAnnouncements(response.data);
    } catch (error) {
      setError("Lỗi khi tải danh sách thông báo");
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

  const handleOpen = (announcement = null) => {
    if (announcement) {
      setEditing(announcement);
      setFormData({
        courseId: announcement.courseId || "",
        title: announcement.title || "",
        content: announcement.content || "",
        priority: announcement.priority || "normal",
      });
    } else {
      setEditing(null);
      setFormData({
        courseId: "",
        title: "",
        content: "",
        priority: "normal",
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
        instructorId: user?.referenceId || 1, // Get from user context
      };

      if (editing) {
        await announcementService.update(editing.announcementId, data);
      } else {
        await announcementService.create(data);
      }
      handleClose();
      fetchAnnouncements();
    } catch (error) {
      setError(error.response?.data?.error || "Có lỗi xảy ra");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa thông báo này?")) {
      try {
        await announcementService.delete(id);
        fetchAnnouncements();
      } catch (error) {
        setError("Lỗi khi xóa thông báo");
      }
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case "urgent":
        return "error";
      case "important":
        return "warning";
      default:
        return "default";
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
        <Typography variant="h4">Thông báo</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Thêm thông báo
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
              <TableCell>Nội dung</TableCell>
              <TableCell>Độ ưu tiên</TableCell>
              <TableCell>Ngày tạo</TableCell>
              <TableCell>Thao tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {announcements.map((announcement) => (
              <TableRow key={announcement.announcementId}>
                <TableCell>{announcement.title}</TableCell>
                <TableCell>
                  {courses.find((c) => c.courseId === announcement.courseId)
                    ?.courseName || "N/A"}
                </TableCell>
                <TableCell>
                  {announcement.content?.substring(0, 50)}...
                </TableCell>
                <TableCell>
                  <Chip
                    label={announcement.priority}
                    color={getPriorityColor(announcement.priority)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {announcement.createdAt
                    ? new Date(announcement.createdAt).toLocaleDateString(
                        "vi-VN"
                      )
                    : "N/A"}
                </TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    onClick={() => handleOpen(announcement)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handleDelete(announcement.announcementId)}
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
          {editing ? "Sửa thông báo" : "Thêm thông báo mới"}
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
              label="Nội dung"
              value={formData.content}
              onChange={(e) =>
                setFormData({ ...formData, content: e.target.value })
              }
              multiline
              rows={5}
              required
            />
            <TextField
              fullWidth
              select
              label="Độ ưu tiên"
              value={formData.priority}
              onChange={(e) =>
                setFormData({ ...formData, priority: e.target.value })
              }
              SelectProps={{ native: true }}
            >
              <option value="normal">Normal</option>
              <option value="important">Important</option>
              <option value="urgent">Urgent</option>
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

export default Announcements;
