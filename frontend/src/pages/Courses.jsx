import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  School as SchoolIcon,
  Person as PersonIcon,
  AccessTime as TimeIcon,
  AttachMoney as MoneyIcon,
  Visibility as ViewIcon,
} from "@mui/icons-material";
import { courseService } from "../services/courseService";
import { instructorService } from "../services/instructorService";
import { enrollmentService } from "../services/enrollmentService";
import { registrationPeriodService } from "../services/registrationPeriodService";
import { useAuth } from "../context/AuthContext";

function Courses() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [enrolledCourses, setEnrolledCourses] = useState([]);
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

  const isStudent = user?.role === "STUDENT";
  const isAdmin = user?.role === "ADMIN";
  const isInstructor = user?.role === "INSTRUCTOR";

  useEffect(() => {
    if (isStudent) {
      fetchEnrolledCourses();
    } else if (isInstructor) {
      fetchInstructorCourses();
    } else {
    fetchCourses();
    }
    fetchInstructors();
  }, [user]);

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const response = await courseService.getAll();
      setCourses(response.data);
    } catch (error) {
      setError("Lỗi khi tải danh sách khóa học");
    } finally {
      setLoading(false);
    }
  };

  const fetchInstructorCourses = async () => {
    try {
      setLoading(true);
      // Lấy các đợt đăng ký đang active
      const periodsRes = await registrationPeriodService.getActive();
      const activePeriods = periodsRes.data;
      
      if (activePeriods.length === 0) {
        setCourses([]);
        setLoading(false);
        return;
      }

      // Lấy tất cả khóa học trong các đợt đăng ký đang active
      const allPeriodCourses = [];
      for (const period of activePeriods) {
        try {
          const coursesRes = await registrationPeriodService.getCourses(period.periodId);
          allPeriodCourses.push(...coursesRes.data);
        } catch (err) {
          console.error(`Error fetching courses for period ${period.periodId}:`, err);
        }
      }

      // Lọc các khóa học mà giảng viên này đang dạy
      const instructorCourses = allPeriodCourses.filter(
        course => course.instructorId === user.referenceId && course.status === 'active'
      );

      // Loại bỏ trùng lặp
      const uniqueCourses = instructorCourses.filter(
        (course, index, self) => 
          index === self.findIndex(c => c.courseId === course.courseId)
      );

      setCourses(uniqueCourses);
    } catch (error) {
      console.error("Error fetching instructor courses:", error);
      setError("Lỗi khi tải danh sách khóa học");
    } finally {
      setLoading(false);
    }
  };

  const fetchEnrolledCourses = async () => {
    try {
      setLoading(true);
      const enrollmentResponse = await enrollmentService.getByStudent(user.referenceId);
      const enrollments = enrollmentResponse.data;
      
      const courseResponse = await courseService.getAll();
      const allCourses = courseResponse.data;
      
      const enrolled = enrollments.map(enrollment => {
        const course = allCourses.find(c => c.courseId === enrollment.courseId);
        return {
          ...enrollment,
          course: course || {}
        };
      });
      
      setEnrolledCourses(enrolled);
    } catch (error) {
      console.error("Error fetching enrolled courses:", error);
      setError("Lỗi khi tải danh sách khóa học đã đăng ký");
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
      if (!formData.courseName || !formData.courseCode) {
        setError("Vui lòng điền đầy đủ thông tin bắt buộc");
        return;
      }

      const data = {
        ...formData,
        instructorId: formData.instructorId ? parseInt(formData.instructorId) : null,
        durationWeeks: formData.durationWeeks ? parseInt(formData.durationWeeks) : null,
        price: formData.price ? parseFloat(formData.price) : null,
        maxStudents: formData.maxStudents ? parseInt(formData.maxStudents) : null,
      };

      if (editing) {
        await courseService.update(editing.courseId, data);
        setSuccess("Cập nhật khóa học thành công!");
      } else {
        await courseService.create(data);
        setSuccess("Tạo khóa học thành công!");
      }
      
      handleClose();
      fetchCourses();
      setTimeout(() => setSuccess(""), 3000);
    } catch (error) {
      setError(error.response?.data?.error || "Có lỗi xảy ra");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa khóa học này?")) {
      try {
        await courseService.delete(id);
        setSuccess("Xóa khóa học thành công!");
        fetchCourses();
        setTimeout(() => setSuccess(""), 3000);
      } catch (error) {
        setError("Lỗi khi xóa khóa học");
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "enrolled": return "info";
      case "in_progress": return "warning";
      case "completed": return "success";
      case "dropped": return "error";
      case "active": return "success";
      case "inactive": return "default";
      default: return "default";
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "enrolled": return "Đã đăng ký";
      case "in_progress": return "Đang học";
      case "completed": return "Hoàn thành";
      case "dropped": return "Đã hủy";
      case "active": return "Hoạt động";
      case "inactive": return "Tạm dừng";
      default: return status;
    }
  };

  const getPaymentStatusColor = (status) => {
    switch (status) {
      case "paid": return "success";
      case "pending": return "warning";
      case "refunded": return "error";
      default: return "default";
    }
  };

  const getPaymentStatusLabel = (status) => {
    switch (status) {
      case "paid": return "Đã thanh toán";
      case "pending": return "Chờ thanh toán";
      case "refunded": return "Đã hoàn tiền";
      default: return status;
    }
  };

  const formatPrice = (price) => {
    if (!price) return "Miễn phí";
    return new Intl.NumberFormat("vi-VN").format(price) + " VNĐ";
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={3}>
        <CircularProgress />
      </Box>
    );
  }

  // Giao diện cho sinh viên - hiển thị khóa học đã đăng ký
  if (isStudent) {
    return (
      <Box>
        <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <SchoolIcon color="primary" />
          Khóa học của tôi
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError("")}>
            {error}
          </Alert>
        )}

        {enrolledCourses.length === 0 ? (
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <SchoolIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              Bạn chưa đăng ký khóa học nào
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Hãy vào mục "Đăng ký học" để xem và đăng ký các khóa học
            </Typography>
          </Paper>
        ) : (
          <Grid container spacing={3}>
            {enrolledCourses.map((enrollment) => (
              <Grid item xs={12} md={6} lg={4} key={enrollment.enrollmentId}>
                <Card 
                  sx={{ 
                    height: '100%', 
                    display: 'flex', 
                    flexDirection: 'column',
                    cursor: 'pointer',
                    transition: 'box-shadow 0.2s',
                    '&:hover': {
                      boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
                    }
                  }}
                  onClick={() => navigate(`/courses/${enrollment.courseId}`)}
                >
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Typography variant="h6" component="div" sx={{ fontWeight: 600 }}>
                        {enrollment.course?.courseName || 'N/A'}
                      </Typography>
                      <Chip 
                        label={getStatusLabel(enrollment.completionStatus)} 
                        color={getStatusColor(enrollment.completionStatus)}
                        size="small"
                      />
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                      Mã: {enrollment.course?.courseCode || 'N/A'}
                    </Typography>
                    
                    <Typography variant="body2" sx={{ mb: 2 }}>
                      {enrollment.course?.description || 'Không có mô tả'}
                    </Typography>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <PersonIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        GV: {instructors.find(i => i.instructorId === enrollment.course?.instructorId)?.fullName || 'N/A'}
                      </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <TimeIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {enrollment.course?.durationWeeks || 0} tuần
                      </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                      <MoneyIcon fontSize="small" color="action" />
                      <Chip 
                        label={getPaymentStatusLabel(enrollment.paymentStatus)} 
                        color={getPaymentStatusColor(enrollment.paymentStatus)}
                        size="small"
                        variant="outlined"
                      />
                    </Box>

                    {enrollment.grade && (
                      <Box sx={{ mt: 2 }}>
                        <Typography variant="body2" gutterBottom>
                          Điểm: <strong>{enrollment.grade}</strong>
                        </Typography>
                      </Box>
                    )}

                    <Typography variant="caption" color="text.secondary">
                      Ngày đăng ký: {enrollment.enrollmentDate ? new Date(enrollment.enrollmentDate).toLocaleDateString('vi-VN') : 'N/A'}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    );
  }

  // Giao diện cho Admin - chỉ xem danh sách (quản lý ở trang riêng)
  if (isAdmin) {
  return (
    <Box>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
          <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <SchoolIcon color="primary" />
            Danh sách Môn học
          </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
            onClick={() => navigate('/course-management')}
        >
            Quản lý Môn học
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

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
                <TableCell><strong>Mã môn</strong></TableCell>
                <TableCell><strong>Tên môn học</strong></TableCell>
                <TableCell><strong>Giảng viên</strong></TableCell>
                <TableCell><strong>Thời lượng</strong></TableCell>
                <TableCell><strong>Học phí</strong></TableCell>
                <TableCell><strong>Số SV tối đa</strong></TableCell>
                <TableCell><strong>Trạng thái</strong></TableCell>
                <TableCell align="center"><strong>Thao tác</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
              {courses.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={8} align="center">
                    Chưa có môn học nào. Hãy thêm môn học mới!
                  </TableCell>
                </TableRow>
              ) : (
                courses.map((course) => (
                  <TableRow key={course.courseId} hover>
                    <TableCell>
                      <Chip label={course.courseCode} color="primary" size="small" variant="outlined" />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body1" sx={{ fontWeight: 500 }}>
                        {course.courseName}
                      </Typography>
                      {course.description && (
                        <Typography variant="caption" color="text.secondary" display="block">
                          {course.description.length > 50 
                            ? course.description.substring(0, 50) + '...' 
                            : course.description}
                        </Typography>
                      )}
                    </TableCell>
                <TableCell>
                      {instructors.find(i => i.instructorId === course.instructorId)?.fullName || "Chưa gán"}
                </TableCell>
                    <TableCell>{course.durationWeeks || 0} tuần</TableCell>
                <TableCell>
                      <Typography variant="body2" color="primary.main" sx={{ fontWeight: 500 }}>
                        {formatPrice(course.price)}
                      </Typography>
                </TableCell>
                    <TableCell>{course.maxStudents || 'Không giới hạn'}</TableCell>
                <TableCell>
                      <Chip 
                        label={getStatusLabel(course.status)} 
                        color={getStatusColor(course.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Button
                    size="small"
                        variant="outlined"
                        onClick={() => navigate('/course-management')}
                  >
                        Quản lý
                      </Button>
                </TableCell>
              </TableRow>
                ))
              )}
          </TableBody>
        </Table>
      </TableContainer>
      </Box>
    );
  }

  // Giao diện cho Instructor - hiển thị các khóa học đang dạy trong đợt đăng ký
  if (isInstructor) {
    return (
      <Box>
        <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <SchoolIcon color="primary" />
          Khóa học của tôi
        </Typography>
        
          {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError("")}>
              {error}
            </Alert>
          )}

        {courses.length === 0 ? (
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <SchoolIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              Bạn chưa có khóa học nào trong các đợt đăng ký đang diễn ra
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Các khóa học của bạn sẽ hiển thị ở đây khi có đợt đăng ký đang mở.
            </Typography>
          </Paper>
        ) : (
          <Grid container spacing={3}>
            {courses.map((course) => (
              <Grid item xs={12} md={6} lg={4} key={course.courseId}>
                <Card 
                  sx={{ 
                    height: '100%', 
                    display: 'flex', 
                    flexDirection: 'column',
                    cursor: 'pointer',
                    transition: 'box-shadow 0.2s',
                    '&:hover': {
                      boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
                    }
                  }}
                  onClick={() => navigate(`/courses/${course.courseId}`)}
                >
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Typography variant="h6" component="div" sx={{ fontWeight: 600 }}>
                        {course.courseName}
                      </Typography>
                      <Chip 
                        label={course.courseCode} 
                        size="small"
                        color="primary"
                        variant="outlined"
            />
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
                      {course.description || 'Không có mô tả'}
                    </Typography>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                      <TimeIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {course.durationWeeks || 0} tuần
                      </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                      <MoneyIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {formatPrice(course.price)}
                      </Typography>
          </Box>

                    <Chip 
                      label={getStatusLabel(course.status)} 
                      color={getStatusColor(course.status)}
                      size="small"
                    />
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
    </Box>
  );
  }

  return null;
}

export default Courses;
