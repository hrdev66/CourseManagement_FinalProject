import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Button,
  Alert,
  CircularProgress,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Checkbox,
  Divider,
  Stepper,
  Step,
  StepLabel,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from "@mui/material";
import {
  School as SchoolIcon,
  Person as PersonIcon,
  AccessTime as TimeIcon,
  HowToReg as RegisterIcon,
  CheckCircle as CheckIcon,
  Delete as DeleteIcon,
  CalendarMonth as CalendarIcon,
  ShoppingCart as CartIcon,
  ArrowForward as ArrowIcon,
} from "@mui/icons-material";
import { courseService } from "../services/courseService";
import { enrollmentService } from "../services/enrollmentService";
import { instructorService } from "../services/instructorService";
import { registrationPeriodService } from "../services/registrationPeriodService";
import { useAuth } from "../context/AuthContext";

function Enrollments() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);
  const [instructors, setInstructors] = useState([]);
  const [activePeriods, setActivePeriods] = useState([]);
  const [periodCourses, setPeriodCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Đăng ký states
  const [selectedPeriod, setSelectedPeriod] = useState(null);
  const [selectedCourseIds, setSelectedCourseIds] = useState([]);
  const [confirmDialog, setConfirmDialog] = useState(false);
  const [registering, setRegistering] = useState(false);
  const [activeStep, setActiveStep] = useState(0);

  const isStudent = user?.role === "STUDENT";
  const isAdmin = user?.role === "ADMIN";

  const steps = ["Chọn đợt đăng ký", "Chọn khóa học", "Xác nhận"];

  useEffect(() => {
    fetchData();
  }, [user]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [coursesRes, instructorsRes] = await Promise.all([
        courseService.getAll(),
        instructorService.getAll(),
      ]);

      setCourses(coursesRes.data);
      setInstructors(instructorsRes.data);

      if (isStudent && user.referenceId) {
        const [enrollmentsRes, periodsRes] = await Promise.all([
          enrollmentService.getByStudent(user.referenceId),
          registrationPeriodService.getActive(),
        ]);
        setEnrollments(enrollmentsRes.data);
        setActivePeriods(periodsRes.data);
      } else if (isAdmin) {
        const enrollmentsRes = await enrollmentService.getAll();
        setEnrollments(enrollmentsRes.data);
      }
    } catch (err) {
      console.error("Error fetching data:", err);
      setError("Lỗi khi tải dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  // Chọn đợt đăng ký
  const handleSelectPeriod = async (period) => {
    setSelectedPeriod(period);
    setSelectedCourseIds([]);
    try {
      const res = await registrationPeriodService.getCourses(period.periodId);
      // Lọc các khóa học chưa đăng ký
      const enrolledCourseIds = enrollments.map((e) => e.courseId);
      const availableCourses = res.data.filter(
        (c) => !enrolledCourseIds.includes(c.courseId)
      );
      setPeriodCourses(availableCourses);
    } catch (err) {
      setPeriodCourses([]);
    }
    setActiveStep(1);
  };

  // Toggle chọn khóa học
  const handleToggleCourse = (courseId) => {
    setSelectedCourseIds((prev) =>
      prev.includes(courseId)
        ? prev.filter((id) => id !== courseId)
        : [...prev, courseId]
    );
  };

  // Quay lại bước trước
  const handleBack = () => {
    if (activeStep === 1) {
      setSelectedPeriod(null);
      setPeriodCourses([]);
      setSelectedCourseIds([]);
    } else if (activeStep === 2) {
      // Giữ nguyên selection
    }
    setActiveStep((prev) => prev - 1);
  };

  // Chuyển sang bước xác nhận
  const handleNext = () => {
    if (activeStep === 1 && selectedCourseIds.length === 0) {
      setError("Vui lòng chọn ít nhất một khóa học");
      return;
    }
    setError("");
    setActiveStep((prev) => prev + 1);
  };

  // Xác nhận đăng ký
  const handleConfirmRegister = async () => {
    try {
      setRegistering(true);
      setError("");

      // Đăng ký từng khóa học
      for (const courseId of selectedCourseIds) {
        await enrollmentService.create({
          studentId: user.referenceId,
          courseId: courseId,
          periodId: selectedPeriod.periodId,
          enrollmentDate: new Date().toISOString().split("T")[0],
          completionStatus: "enrolled",
          paymentStatus: "pending",
        });
      }

      setSuccess(`Đăng ký thành công ${selectedCourseIds.length} khóa học!`);

      // Reset
      setActiveStep(0);
      setSelectedPeriod(null);
      setSelectedCourseIds([]);
      setPeriodCourses([]);
      fetchData();

      setTimeout(() => setSuccess(""), 5000);
    } catch (err) {
      console.error("Error registering:", err);
      setError(err.response?.data || "Lỗi khi đăng ký khóa học");
    } finally {
      setRegistering(false);
    }
  };

  const handleDeleteEnrollment = async (enrollmentId) => {
    if (window.confirm("Bạn có chắc chắn muốn hủy đăng ký này?")) {
      try {
        await enrollmentService.delete(enrollmentId);
        setSuccess("Hủy đăng ký thành công!");
        fetchData();
        setTimeout(() => setSuccess(""), 3000);
      } catch (err) {
        setError("Lỗi khi hủy đăng ký");
      }
    }
  };

  const getInstructorName = (instructorId) => {
    const instructor = instructors.find((i) => i.instructorId === instructorId);
    return instructor?.fullName || "N/A";
  };

  const formatPrice = (price) => {
    if (!price) return "Miễn phí";
    return new Intl.NumberFormat("vi-VN").format(price) + " VNĐ";
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleDateString("vi-VN");
  };

  const getSelectedCourses = () => {
    return periodCourses.filter((c) => selectedCourseIds.includes(c.courseId));
  };

  const getTotalPrice = () => {
    return getSelectedCourses().reduce((sum, c) => sum + (c.price || 0), 0);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={3}>
        <CircularProgress />
      </Box>
    );
  }

  // Giao diện cho Admin
  if (isAdmin) {
    return (
      <Box>
        <Typography variant="h4" gutterBottom>
          Quản lý Đăng ký học
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError("")}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert
            severity="success"
            sx={{ mb: 2 }}
            onClose={() => setSuccess("")}
          >
            {success}
          </Alert>
        )}

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Sinh viên</TableCell>
                <TableCell>Khóa học</TableCell>
                <TableCell>Đợt ĐK</TableCell>
                <TableCell>Ngày đăng ký</TableCell>
                <TableCell>Trạng thái</TableCell>
                <TableCell>Thanh toán</TableCell>
                <TableCell>Điểm</TableCell>
                <TableCell>Thao tác</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {enrollments.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={9} align="center">
                    Chưa có đăng ký nào
                  </TableCell>
                </TableRow>
              ) : (
                enrollments.map((enrollment) => {
                  const course = courses.find(
                    (c) => c.courseId === enrollment.courseId
                  );
                  return (
                    <TableRow key={enrollment.enrollmentId}>
                      <TableCell>{enrollment.enrollmentId}</TableCell>
                      <TableCell>ID: {enrollment.studentId}</TableCell>
                      <TableCell>{course?.courseName || "N/A"}</TableCell>
                      <TableCell>{enrollment.periodId || "-"}</TableCell>
                      <TableCell>
                        {formatDate(enrollment.enrollmentDate)}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={enrollment.completionStatus}
                          size="small"
                          color={
                            enrollment.completionStatus === "completed"
                              ? "success"
                              : "default"
                          }
                        />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={enrollment.paymentStatus}
                          size="small"
                          color={
                            enrollment.paymentStatus === "paid"
                              ? "success"
                              : "warning"
                          }
                        />
                      </TableCell>
                      <TableCell>{enrollment.grade || "-"}</TableCell>
                      <TableCell>
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() =>
                            handleDeleteEnrollment(enrollment.enrollmentId)
                          }
                        >
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    );
  }

  // Giao diện cho sinh viên
  return (
    <Box>
      <Typography
        variant="h4"
        gutterBottom
        sx={{ display: "flex", alignItems: "center", gap: 1 }}
      >
        <RegisterIcon color="primary" />
        Đăng ký khóa học
      </Typography>

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

      {!isStudent ? (
        <Alert severity="info">Chỉ sinh viên mới có thể đăng ký khóa học</Alert>
      ) : activePeriods.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: "center" }}>
          <CalendarIcon sx={{ fontSize: 64, color: "text.secondary", mb: 2 }} />
          <Typography variant="h6" color="text.secondary" gutterBottom>
            Hiện tại không có đợt đăng ký nào đang mở
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Vui lòng quay lại sau khi có đợt đăng ký mới.
          </Typography>
        </Paper>
      ) : (
        <Paper sx={{ p: 3 }}>
          {/* Stepper */}
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {/* Step 0: Chọn đợt đăng ký */}
          {activeStep === 0 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Chọn đợt đăng ký
              </Typography>
              <Grid container spacing={2}>
                {activePeriods.map((period) => (
                  <Grid item xs={12} md={6} key={period.periodId}>
                    <Card
                      sx={{
                        cursor: "pointer",
                        transition: "box-shadow 0.2s",
                        "&:hover": {
                          boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
                        },
                      }}
                      onClick={() => handleSelectPeriod(period)}
                    >
                      <CardContent>
                        <Box
                          sx={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            mb: 1,
                          }}
                        >
                          <Typography variant="h6">
                            {period.periodName}
                          </Typography>
                          <Chip label="Đang mở" color="success" size="small" />
                        </Box>
                        <Typography
                          variant="body2"
                          color="text.secondary"
                          sx={{ mb: 2 }}
                        >
                          {period.description || "Không có mô tả"}
                        </Typography>
                        <Box
                          sx={{ display: "flex", alignItems: "center", gap: 1 }}
                        >
                          <CalendarIcon fontSize="small" color="action" />
                          <Typography variant="body2">
                            {formatDate(period.startDate)} -{" "}
                            {formatDate(period.endDate)}
                          </Typography>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* Step 1: Chọn khóa học */}
          {activeStep === 1 && (
            <Box>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  mb: 2,
                }}
              >
                <Typography variant="h6">
                  Chọn khóa học - {selectedPeriod?.periodName}
                </Typography>
                <Chip
                  icon={<CartIcon />}
                  label={`Đã chọn: ${selectedCourseIds.length}`}
                  color="primary"
                />
              </Box>

              {periodCourses.length === 0 ? (
                <Alert severity="info">
                  Không có khóa học nào để đăng ký trong đợt này (có thể bạn đã
                  đăng ký hết).
                </Alert>
              ) : (
                <Grid container spacing={2}>
                  {periodCourses.map((course) => {
                    const isSelected = selectedCourseIds.includes(
                      course.courseId
                    );
                    return (
                      <Grid item xs={12} md={6} lg={4} key={course.courseId}>
                        <Card
                          sx={{
                            cursor: "pointer",
                            border: isSelected ? "2px solid" : "1px solid",
                            borderColor: isSelected
                              ? "primary.main"
                              : "divider",
                            backgroundColor: isSelected
                              ? "action.selected"
                              : "background.paper",
                            transition: "all 0.2s",
                          }}
                          onClick={() => handleToggleCourse(course.courseId)}
                        >
                          <CardContent>
                            <Box
                              sx={{
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "flex-start",
                              }}
                            >
                              <Box sx={{ flex: 1 }}>
                                <Typography
                                  variant="h6"
                                  sx={{ fontWeight: 600 }}
                                >
                                  {course.courseName}
                                </Typography>
                                <Typography
                                  variant="body2"
                                  color="text.secondary"
                                >
                                  {course.courseCode}
                                </Typography>
                              </Box>
                              <Checkbox checked={isSelected} />
                            </Box>

                            <Divider sx={{ my: 1.5 }} />

                            <Box
                              sx={{
                                display: "flex",
                                alignItems: "center",
                                gap: 1,
                                mb: 0.5,
                              }}
                            >
                              <PersonIcon fontSize="small" color="action" />
                              <Typography variant="body2">
                                {getInstructorName(course.instructorId)}
                              </Typography>
                            </Box>
                            <Box
                              sx={{
                                display: "flex",
                                alignItems: "center",
                                gap: 1,
                                mb: 0.5,
                              }}
                            >
                              <TimeIcon fontSize="small" color="action" />
                              <Typography variant="body2">
                                {course.durationWeeks} tuần
                              </Typography>
                            </Box>
                            <Typography
                              variant="h6"
                              color="primary.main"
                              sx={{ mt: 1 }}
                            >
                              {formatPrice(course.price)}
                            </Typography>
                          </CardContent>
                        </Card>
                      </Grid>
                    );
                  })}
                </Grid>
              )}

              <Box
                sx={{ display: "flex", justifyContent: "space-between", mt: 3 }}
              >
                <Button onClick={handleBack}>Quay lại</Button>
                <Button
                  variant="contained"
                  onClick={handleNext}
                  disabled={selectedCourseIds.length === 0}
                  endIcon={<ArrowIcon />}
                >
                  Tiếp tục
                </Button>
              </Box>
            </Box>
          )}

          {/* Step 2: Xác nhận */}
          {activeStep === 2 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Xác nhận đăng ký
              </Typography>

              <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="subtitle1" gutterBottom>
                  <strong>Đợt đăng ký:</strong> {selectedPeriod?.periodName}
                </Typography>
                <Typography variant="subtitle1" gutterBottom>
                  <strong>Số khóa học:</strong> {selectedCourseIds.length}
                </Typography>

                <Divider sx={{ my: 2 }} />

                <Typography variant="subtitle2" gutterBottom>
                  Danh sách khóa học:
                </Typography>
                <List dense>
                  {getSelectedCourses().map((course) => (
                    <ListItem key={course.courseId}>
                      <ListItemIcon>
                        <SchoolIcon color="primary" />
                      </ListItemIcon>
                      <ListItemText
                        primary={course.courseName}
                        secondary={`${course.courseCode} - ${formatPrice(
                          course.price
                        )}`}
                      />
                    </ListItem>
                  ))}
                </List>

                <Divider sx={{ my: 2 }} />

                <Typography variant="h6" color="primary.main">
                  Tổng học phí: {formatPrice(getTotalPrice())}
                </Typography>
              </Paper>

              <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                <Button onClick={handleBack}>Quay lại</Button>
                <Button
                  variant="contained"
                  color="success"
                  onClick={handleConfirmRegister}
                  disabled={registering}
                  startIcon={
                    registering ? <CircularProgress size={20} /> : <CheckIcon />
                  }
                >
                  {registering ? "Đang đăng ký..." : "Xác nhận đăng ký"}
                </Button>
              </Box>
            </Box>
          )}
        </Paper>
      )}
    </Box>
  );
}

export default Enrollments;
