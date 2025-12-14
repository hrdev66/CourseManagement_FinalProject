import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  Paper,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
  Card,
  CardContent,
  Grid,
  Chip,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from "@mui/material";
import {
  ArrowBack as BackIcon,
  Assignment as AssignmentIcon,
  Announcement as AnnouncementIcon,
  People as PeopleIcon,
  Person as PersonIcon,
  AccessTime as TimeIcon,
  AttachMoney as MoneyIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Add as AddIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  CloudUpload as CloudUploadIcon,
  AttachFile as AttachFileIcon,
  CheckCircle as CheckCircleIcon,
  Visibility as VisibilityIcon,
  Grade as GradeIcon,
  Download as DownloadIcon,
} from "@mui/icons-material";
import { courseService } from "../services/courseService";
import { assignmentService } from "../services/assignmentService";
import { announcementService } from "../services/announcementService";
import { enrollmentService } from "../services/enrollmentService";
import { instructorService } from "../services/instructorService";
import { studentService } from "../services/studentService";
import { submissionService } from "../services/submissionService";
import { useAuth } from "../context/AuthContext";

function CourseDetail() {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [course, setCourse] = useState(null);
  const [assignments, setAssignments] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [students, setStudents] = useState([]);
  const [instructor, setInstructor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [activeTab, setActiveTab] = useState(0);
  const [studentSubmissions, setStudentSubmissions] = useState({}); // Map assignmentId -> submission
  const [assignmentSubmissions, setAssignmentSubmissions] = useState([]); // Submissions for current assignment (for instructor)
  const [loadingSubmissions, setLoadingSubmissions] = useState(false);

  // Dialog states
  const [assignmentDialog, setAssignmentDialog] = useState({
    open: false,
    editing: null,
  });
  const [announcementDialog, setAnnouncementDialog] = useState({
    open: false,
    editing: null,
  });
  const [studentDialog, setStudentDialog] = useState({
    open: false,
    enrollment: null,
  });
  const [assignmentDetailDialog, setAssignmentDetailDialog] = useState({
    open: false,
    assignment: null,
  });
  const [instructorAssignmentDialog, setInstructorAssignmentDialog] = useState({
    open: false,
    assignment: null,
  });
  const [gradingDialog, setGradingDialog] = useState({
    open: false,
    submission: null,
  });
  const [assignmentForm, setAssignmentForm] = useState({
    title: "",
    description: "",
    dueDate: "",
    maxScore: "",
  });
  const [announcementForm, setAnnouncementForm] = useState({
    title: "",
    content: "",
    priority: "normal",
  });
  const [studentForm, setStudentForm] = useState({
    grade: "",
    completionStatus: "enrolled",
    paymentStatus: "pending",
  });
  const [submissionForm, setSubmissionForm] = useState({
    content: "",
    file: null,
    fileName: "",
  });
  const [gradingForm, setGradingForm] = useState({
    score: "",
    status: "submitted",
  });

  const isStudent = user?.role === "STUDENT";
  const isAdmin = user?.role === "ADMIN";
  const isInstructor = user?.role === "INSTRUCTOR";
  const canEdit = isAdmin || isInstructor;

  useEffect(() => {
    fetchCourseData();
  }, [courseId]);

  const fetchCourseData = async () => {
    try {
      setLoading(true);
      const [
        courseRes,
        assignmentsRes,
        announcementsRes,
        enrollmentsRes,
        instructorsRes,
      ] = await Promise.all([
        courseService.getById(courseId),
        assignmentService.getByCourse(courseId),
        announcementService.getByCourse(courseId),
        enrollmentService.getByCourse(courseId),
        instructorService.getAll(),
      ]);

      setCourse(courseRes.data);

      // Lấy thông tin giảng viên
      if (courseRes.data.instructorId) {
        const inst = instructorsRes.data.find(
          (i) => i.instructorId === courseRes.data.instructorId
        );
        setInstructor(inst);
      }

      setAssignments(assignmentsRes.data || []);
      setAnnouncements(announcementsRes.data || []);

      // Nếu là sinh viên, lấy submissions của họ
      if (isStudent && user?.referenceId) {
        try {
          const submissionsRes = await submissionService.getByStudent(
            user.referenceId
          );
          const submissionsMap = {};
          (submissionsRes.data || []).forEach((submission) => {
            submissionsMap[submission.assignmentId] = submission;
          });
          setStudentSubmissions(submissionsMap);
        } catch (err) {
          console.error("Error fetching submissions:", err);
        }
      }

      // Lấy thông tin chi tiết sinh viên
      const studentDetails = await Promise.all(
        enrollmentsRes.data.map(async (enrollment) => {
          try {
            const studentRes = await studentService.getById(
              enrollment.studentId
            );
            return {
              ...enrollment,
              student: studentRes.data,
            };
          } catch {
            return {
              ...enrollment,
              student: null,
            };
          }
        })
      );
      setStudents(studentDetails);
    } catch (err) {
      console.error("Error fetching course data:", err);
      setError("Lỗi khi tải thông tin khóa học");
    } finally {
      setLoading(false);
    }
  };

  // Assignment handlers
  const handleOpenAssignmentDialog = (assignment = null) => {
    if (assignment) {
      setAssignmentDialog({ open: true, editing: assignment });
      setAssignmentForm({
        title: assignment.title || "",
        description: assignment.description || "",
        dueDate: assignment.dueDate ? assignment.dueDate.split("T")[0] : "",
        maxScore: assignment.maxScore || "",
      });
    } else {
      setAssignmentDialog({ open: true, editing: null });
      setAssignmentForm({
        title: "",
        description: "",
        dueDate: "",
        maxScore: "",
      });
    }
  };

  const handleCloseAssignmentDialog = () => {
    setAssignmentDialog({ open: false, editing: null });
    setAssignmentForm({
      title: "",
      description: "",
      dueDate: "",
      maxScore: "",
    });
  };

  const handleSubmitAssignment = async () => {
    try {
      if (!assignmentForm.title) {
        setError("Vui lòng nhập tiêu đề bài tập");
        return;
      }

      const data = {
        courseId: parseInt(courseId),
        title: assignmentForm.title,
        description: assignmentForm.description,
        dueDate: assignmentForm.dueDate || null,
        maxScore: assignmentForm.maxScore
          ? parseFloat(assignmentForm.maxScore)
          : null,
      };

      if (assignmentDialog.editing) {
        await assignmentService.update(
          assignmentDialog.editing.assignmentId,
          data
        );
        setSuccess("Cập nhật bài tập thành công!");
      } else {
        await assignmentService.create(data);
        setSuccess("Thêm bài tập thành công!");
      }

      handleCloseAssignmentDialog();
      fetchCourseData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data || "Có lỗi xảy ra");
    }
  };

  const handleDeleteAssignment = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa bài tập này?")) {
      try {
        await assignmentService.delete(id);
        setSuccess("Xóa bài tập thành công!");
        fetchCourseData();
        setTimeout(() => setSuccess(""), 3000);
      } catch (err) {
        setError("Lỗi khi xóa bài tập");
      }
    }
  };

  // Announcement handlers
  const handleOpenAnnouncementDialog = (announcement = null) => {
    if (announcement) {
      setAnnouncementDialog({ open: true, editing: announcement });
      setAnnouncementForm({
        title: announcement.title || "",
        content: announcement.content || "",
        priority: announcement.priority || "normal",
      });
    } else {
      setAnnouncementDialog({ open: true, editing: null });
      setAnnouncementForm({
        title: "",
        content: "",
        priority: "normal",
      });
    }
  };

  const handleCloseAnnouncementDialog = () => {
    setAnnouncementDialog({ open: false, editing: null });
    setAnnouncementForm({
      title: "",
      content: "",
      priority: "normal",
    });
  };

  const handleSubmitAnnouncement = async () => {
    try {
      if (!announcementForm.title) {
        setError("Vui lòng nhập tiêu đề thông báo");
        return;
      }

      const data = {
        courseId: parseInt(courseId),
        instructorId: course.instructorId,
        title: announcementForm.title,
        content: announcementForm.content,
        priority: announcementForm.priority,
      };

      if (announcementDialog.editing) {
        await announcementService.update(
          announcementDialog.editing.announcementId,
          data
        );
        setSuccess("Cập nhật thông báo thành công!");
      } else {
        await announcementService.create(data);
        setSuccess("Thêm thông báo thành công!");
      }

      handleCloseAnnouncementDialog();
      fetchCourseData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data || "Có lỗi xảy ra");
    }
  };

  const handleDeleteAnnouncement = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa thông báo này?")) {
      try {
        await announcementService.delete(id);
        setSuccess("Xóa thông báo thành công!");
        fetchCourseData();
        setTimeout(() => setSuccess(""), 3000);
      } catch (err) {
        setError("Lỗi khi xóa thông báo");
      }
    }
  };

  // Student management handlers
  const handleOpenStudentDialog = (enrollment) => {
    setStudentDialog({ open: true, enrollment });
    setStudentForm({
      grade: enrollment.grade || "",
      completionStatus: enrollment.completionStatus || "enrolled",
      paymentStatus: enrollment.paymentStatus || "pending",
    });
  };

  const handleCloseStudentDialog = () => {
    setStudentDialog({ open: false, enrollment: null });
    setStudentForm({
      grade: "",
      completionStatus: "enrolled",
      paymentStatus: "pending",
    });
  };

  const handleSubmitStudent = async () => {
    try {
      const data = {
        ...studentDialog.enrollment,
        grade: studentForm.grade ? parseFloat(studentForm.grade) : null,
        completionStatus: studentForm.completionStatus,
        paymentStatus: studentForm.paymentStatus,
      };

      await enrollmentService.update(
        studentDialog.enrollment.enrollmentId,
        data
      );
      setSuccess("Cập nhật thông tin sinh viên thành công!");
      handleCloseStudentDialog();
      fetchCourseData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data || "Có lỗi xảy ra khi cập nhật");
    }
  };

  // Assignment detail and submission handlers for students
  const handleOpenAssignmentDetail = async (assignment) => {
    setAssignmentDetailDialog({ open: true, assignment });

    // Nếu sinh viên đã nộp bài, load submission
    if (isStudent && user?.referenceId) {
      const existingSubmission = studentSubmissions[assignment.assignmentId];
      if (existingSubmission) {
        setSubmissionForm({
          content: existingSubmission.content || "",
          file: null,
          fileName: existingSubmission.attachment || "",
        });
      } else {
        setSubmissionForm({
          content: "",
          file: null,
          fileName: "",
        });
      }
    }
  };

  // Assignment detail handler for instructors
  const handleOpenInstructorAssignmentDetail = async (assignment) => {
    setInstructorAssignmentDialog({ open: true, assignment });
    await fetchAssignmentSubmissions(assignment.assignmentId);
  };

  const handleCloseInstructorAssignmentDetail = () => {
    setInstructorAssignmentDialog({ open: false, assignment: null });
    setAssignmentSubmissions([]);
  };

  const fetchAssignmentSubmissions = async (assignmentId) => {
    try {
      setLoadingSubmissions(true);
      const submissionsRes = await submissionService.getByAssignment(
        assignmentId
      );
      const submissionsWithDetails = await Promise.all(
        (submissionsRes.data || []).map(async (submission) => {
          try {
            const studentRes = await studentService.getById(
              submission.studentId
            );
            return {
              ...submission,
              student: studentRes.data,
            };
          } catch {
            return {
              ...submission,
              student: null,
            };
          }
        })
      );
      setAssignmentSubmissions(submissionsWithDetails);
    } catch (err) {
      console.error("Error fetching submissions:", err);
      setError("Lỗi khi tải danh sách bài nộp");
    } finally {
      setLoadingSubmissions(false);
    }
  };

  // Grading handlers
  const handleOpenGradingDialog = (submission) => {
    setGradingDialog({ open: true, submission });
    setGradingForm({
      score: submission.score !== null ? submission.score.toString() : "",
      status: submission.status || "submitted",
    });
  };

  const handleCloseGradingDialog = () => {
    setGradingDialog({ open: false, submission: null });
    setGradingForm({
      score: "",
      status: "submitted",
    });
  };

  const handleSubmitGrading = async () => {
    try {
      if (!gradingDialog.submission) return;

      const data = {
        ...gradingDialog.submission,
        score: gradingForm.score ? parseInt(gradingForm.score) : null,
        status: gradingForm.status,
      };

      await submissionService.update(
        gradingDialog.submission.submissionId,
        data
      );
      setSuccess("Chấm điểm thành công!");
      handleCloseGradingDialog();

      // Refresh submissions list
      if (instructorAssignmentDialog.assignment) {
        await fetchAssignmentSubmissions(
          instructorAssignmentDialog.assignment.assignmentId
        );
      }

      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data || "Có lỗi xảy ra khi chấm điểm");
    }
  };

  // Download file handler
  const handleDownloadFile = async (submission) => {
    try {
      if (!submission.attachment) {
        setError("Không có file để tải về");
        return;
      }

      // Nếu attachment là URL đầy đủ (http/https), mở trong tab mới để tải
      if (
        submission.attachment.startsWith("http://") ||
        submission.attachment.startsWith("https://")
      ) {
        // Tạo link để tải file
        const link = document.createElement("a");
        link.href = submission.attachment;
        link.download =
          submission.attachment.split("/").pop() ||
          `submission_${submission.submissionId}_${
            submission.student?.fullName || submission.studentId
          }.file`;
        link.target = "_blank";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        return;
      }

      // Nếu attachment là đường dẫn tương đối hoặc tên file
      // Tạo một link tạm để tải file
      const fileName =
        submission.attachment.split("/").pop() ||
        `submission_${submission.submissionId}_${
          submission.student?.fullName || submission.studentId
        }.file`;

      const link = document.createElement("a");
      link.href = submission.attachment;
      link.download = fileName;
      link.target = "_blank";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      console.error("Error downloading file:", err);
      setError("Không thể tải file. Vui lòng thử lại.");
    }
  };

  const handleCloseAssignmentDetail = () => {
    setAssignmentDetailDialog({ open: false, assignment: null });
    setSubmissionForm({
      content: "",
      file: null,
      fileName: "",
    });
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setSubmissionForm({
        ...submissionForm,
        file: file,
        fileName: file.name,
      });
    }
  };

  const handleSubmitStudentAssignment = async () => {
    try {
      if (!assignmentDetailDialog.assignment) return;

      const assignmentId = assignmentDetailDialog.assignment.assignmentId;
      const studentId = user?.referenceId;

      if (!studentId) {
        setError("Không tìm thấy thông tin sinh viên");
        return;
      }

      // Kiểm tra xem đã nộp bài chưa
      const existingSubmission = studentSubmissions[assignmentId];

      // Tạo FormData để upload file
      const formData = new FormData();
      formData.append("assignmentId", assignmentId);
      formData.append("studentId", studentId);
      formData.append("content", submissionForm.content);
      if (submissionForm.file) {
        formData.append("file", submissionForm.file);
      }

      let submissionData;
      if (existingSubmission) {
        // Cập nhật submission
        submissionData = {
          assignmentId: assignmentId,
          studentId: studentId,
          content: submissionForm.content,
          attachment: submissionForm.fileName || existingSubmission.attachment,
          status: "submitted",
        };

        // Nếu có file mới, cần upload
        if (submissionForm.file) {
          // TODO: Implement file upload API
          // For now, we'll just use the filename
          submissionData.attachment = submissionForm.fileName;
        }

        await submissionService.update(
          existingSubmission.submissionId,
          submissionData
        );
        setSuccess("Cập nhật bài nộp thành công!");
      } else {
        // Tạo submission mới
        submissionData = {
          assignmentId: assignmentId,
          studentId: studentId,
          content: submissionForm.content,
          attachment: submissionForm.fileName || null,
          status: "submitted",
          submittedDate: new Date().toISOString().split("T")[0],
        };

        await submissionService.create(submissionData);
        setSuccess("Nộp bài thành công!");
      }

      handleCloseAssignmentDetail();
      fetchCourseData();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data || "Có lỗi xảy ra khi nộp bài");
    }
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleDateString("vi-VN");
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

  if (error && !course) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error || "Không tìm thấy khóa học"}</Alert>
        <Button
          startIcon={<BackIcon />}
          onClick={() => navigate("/courses")}
          sx={{ mt: 2 }}
        >
          Quay lại
        </Button>
      </Box>
    );
  }

  if (!course) return null;

  return (
    <Box>
      {/* Header */}
      <Box sx={{ mb: 3, display: "flex", alignItems: "center", gap: 2 }}>
        <IconButton onClick={() => navigate("/courses")}>
          <BackIcon />
        </IconButton>
        <Box sx={{ flex: 1 }}>
          <Typography variant="h4" gutterBottom>
            {course.courseName}
          </Typography>
          <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap" }}>
            <Chip
              label={course.courseCode}
              color="primary"
              variant="outlined"
            />
            {instructor && (
              <Chip
                icon={<PersonIcon />}
                label={`Giảng viên: ${instructor.fullName}`}
                variant="outlined"
              />
            )}
            <Chip
              icon={<TimeIcon />}
              label={`${course.durationWeeks || 0} tuần`}
              variant="outlined"
            />
            <Chip
              icon={<MoneyIcon />}
              label={formatPrice(course.price)}
              variant="outlined"
            />
          </Box>
        </Box>
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

      {/* Mô tả khóa học */}
      {course.description && (
        <Paper sx={{ p: 2, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Mô tả khóa học
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {course.description}
          </Typography>
        </Paper>
      )}

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          variant="scrollable"
          scrollButtons="auto"
        >
          <Tab icon={<AssignmentIcon />} label="Bài tập" iconPosition="start" />
          <Tab
            icon={<AnnouncementIcon />}
            label="Thông báo"
            iconPosition="start"
          />
          <Tab icon={<PeopleIcon />} label="Sinh viên" iconPosition="start" />
        </Tabs>
      </Paper>

      {/* Tab Content: Bài tập */}
      {activeTab === 0 && (
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
              Danh sách Bài tập ({assignments.length})
            </Typography>
            {canEdit && (
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => handleOpenAssignmentDialog()}
              >
                Thêm bài tập
              </Button>
            )}
          </Box>

          {assignments.length === 0 ? (
            <Paper sx={{ p: 4, textAlign: "center" }}>
              <AssignmentIcon
                sx={{ fontSize: 64, color: "text.secondary", mb: 2 }}
              />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                Chưa có bài tập nào
              </Typography>
              {canEdit && (
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  sx={{ mt: 2 }}
                  onClick={() => handleOpenAssignmentDialog()}
                >
                  Thêm bài tập đầu tiên
                </Button>
              )}
            </Paper>
          ) : (
            <Grid container spacing={2}>
              {assignments.map((assignment) => {
                const submission = studentSubmissions[assignment.assignmentId];
                const isSubmitted = !!submission;

                return (
                  <Grid item xs={12} md={6} key={assignment.assignmentId}>
                    <Card
                      sx={{
                        cursor: isStudent ? "pointer" : "default",
                        transition: "box-shadow 0.2s",
                        "&:hover": isStudent
                          ? {
                              boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
                            }
                          : {},
                      }}
                      onClick={() =>
                        isStudent && handleOpenAssignmentDetail(assignment)
                      }
                    >
                      <CardContent>
                        <Box
                          sx={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "flex-start",
                            mb: 1,
                          }}
                        >
                          <Box sx={{ flex: 1 }}>
                            <Box
                              sx={{
                                display: "flex",
                                alignItems: "center",
                                gap: 1,
                                mb: 1,
                              }}
                            >
                              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                                {assignment.title}
                              </Typography>
                              {isStudent && isSubmitted && (
                                <CheckCircleIcon
                                  color="success"
                                  fontSize="small"
                                />
                              )}
                            </Box>
                            {isStudent && isSubmitted && (
                              <Chip
                                label="Đã nộp"
                                size="small"
                                color="success"
                                sx={{ mb: 1 }}
                              />
                            )}
                          </Box>
                          {canEdit && (
                            <Box>
                              <IconButton
                                size="small"
                                color="primary"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleOpenAssignmentDialog(assignment);
                                }}
                                title="Sửa bài tập"
                              >
                                <EditIcon />
                              </IconButton>
                              <IconButton
                                size="small"
                                color="info"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleOpenInstructorAssignmentDetail(
                                    assignment
                                  );
                                }}
                                title="Xem bài nộp"
                              >
                                <VisibilityIcon />
                              </IconButton>
                              <IconButton
                                size="small"
                                color="error"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleDeleteAssignment(
                                    assignment.assignmentId
                                  );
                                }}
                                title="Xóa bài tập"
                              >
                                <DeleteIcon />
                              </IconButton>
                            </Box>
                          )}
                          {isStudent && !canEdit && (
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={(e) => {
                                e.stopPropagation();
                                handleOpenAssignmentDetail(assignment);
                              }}
                              title="Xem chi tiết và nộp bài"
                            >
                              <VisibilityIcon />
                            </IconButton>
                          )}
                        </Box>
                        <Typography
                          variant="body2"
                          color="text.secondary"
                          sx={{ mb: 2 }}
                        >
                          {assignment.description || "Không có mô tả"}
                        </Typography>
                        <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
                          {assignment.dueDate && (
                            <Chip
                              label={`Hạn nộp: ${formatDate(
                                assignment.dueDate
                              )}`}
                              size="small"
                              color="warning"
                              variant="outlined"
                            />
                          )}
                          {assignment.maxScore && (
                            <Chip
                              label={`Điểm tối đa: ${assignment.maxScore}`}
                              size="small"
                              variant="outlined"
                            />
                          )}
                          {isStudent &&
                            isSubmitted &&
                            submission.score !== null && (
                              <Chip
                                label={`Điểm: ${submission.score}${
                                  assignment.maxScore
                                    ? `/${assignment.maxScore}`
                                    : ""
                                }`}
                                size="small"
                                color="primary"
                              />
                            )}
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          )}
        </Box>
      )}

      {/* Tab Content: Thông báo */}
      {activeTab === 1 && (
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
              Thông báo ({announcements.length})
            </Typography>
            {canEdit && (
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => handleOpenAnnouncementDialog()}
              >
                Thêm thông báo
              </Button>
            )}
          </Box>

          {announcements.length === 0 ? (
            <Paper sx={{ p: 4, textAlign: "center" }}>
              <AnnouncementIcon
                sx={{ fontSize: 64, color: "text.secondary", mb: 2 }}
              />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                Chưa có thông báo nào
              </Typography>
              {canEdit && (
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  sx={{ mt: 2 }}
                  onClick={() => handleOpenAnnouncementDialog()}
                >
                  Thêm thông báo đầu tiên
                </Button>
              )}
            </Paper>
          ) : (
            <Grid container spacing={2}>
              {announcements.map((announcement) => (
                <Grid item xs={12} key={announcement.announcementId}>
                  <Card>
                    <CardContent>
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "flex-start",
                          mb: 1,
                        }}
                      >
                        <Box sx={{ flex: 1 }}>
                          <Box
                            sx={{
                              display: "flex",
                              gap: 1,
                              alignItems: "center",
                              mb: 1,
                            }}
                          >
                            <Typography variant="h6" sx={{ fontWeight: 600 }}>
                              {announcement.title}
                            </Typography>
                            {announcement.priority && (
                              <Chip
                                label={announcement.priority}
                                size="small"
                                color={
                                  announcement.priority === "urgent"
                                    ? "error"
                                    : announcement.priority === "important"
                                    ? "warning"
                                    : "default"
                                }
                              />
                            )}
                          </Box>
                          <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{ mb: 2, whiteSpace: "pre-wrap" }}
                          >
                            {announcement.content || "Không có nội dung"}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {formatDate(announcement.createdAt)}
                          </Typography>
                        </Box>
                        {canEdit && (
                          <Box>
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={() =>
                                handleOpenAnnouncementDialog(announcement)
                              }
                            >
                              <EditIcon />
                            </IconButton>
                            <IconButton
                              size="small"
                              color="error"
                              onClick={() =>
                                handleDeleteAnnouncement(
                                  announcement.announcementId
                                )
                              }
                            >
                              <DeleteIcon />
                            </IconButton>
                          </Box>
                        )}
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          )}
        </Box>
      )}

      {/* Tab Content: Sinh viên */}
      {activeTab === 2 && (
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
              Danh sách Sinh viên ({students.length})
            </Typography>
            {canEdit && (
              <Typography variant="body2" color="text.secondary">
                Click vào hàng để cập nhật điểm và trạng thái
              </Typography>
            )}
          </Box>

          {students.length === 0 ? (
            <Paper sx={{ p: 4, textAlign: "center" }}>
              <PeopleIcon
                sx={{ fontSize: 64, color: "text.secondary", mb: 2 }}
              />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                Chưa có sinh viên nào đăng ký
              </Typography>
            </Paper>
          ) : (
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
                      <strong>Ngày đăng ký</strong>
                    </TableCell>
                    <TableCell>
                      <strong>Trạng thái</strong>
                    </TableCell>
                    <TableCell>
                      <strong>Thanh toán</strong>
                    </TableCell>
                    <TableCell>
                      <strong>Điểm</strong>
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {students.map((enrollment) => (
                    <TableRow
                      key={enrollment.enrollmentId}
                      onClick={() =>
                        canEdit && handleOpenStudentDialog(enrollment)
                      }
                      sx={{
                        cursor: canEdit ? "pointer" : "default",
                        "&:hover": canEdit
                          ? { backgroundColor: "action.hover" }
                          : {},
                      }}
                    >
                      <TableCell>
                        {enrollment.student?.fullName ||
                          `SV #${enrollment.studentId}`}
                      </TableCell>
                      <TableCell>
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 0.5,
                          }}
                        >
                          <EmailIcon fontSize="small" color="action" />
                          {enrollment.student?.email || "-"}
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 0.5,
                          }}
                        >
                          <PhoneIcon fontSize="small" color="action" />
                          {enrollment.student?.phone || "-"}
                        </Box>
                      </TableCell>
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
                              : enrollment.completionStatus === "in_progress"
                              ? "warning"
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
                      <TableCell>
                        {enrollment.grade ? (
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {enrollment.grade}
                          </Typography>
                        ) : (
                          "-"
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Box>
      )}

      {/* Dialog: Thêm/Sửa Bài tập */}
      <Dialog
        open={assignmentDialog.open}
        onClose={handleCloseAssignmentDialog}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          {assignmentDialog.editing ? "Sửa bài tập" : "Thêm bài tập mới"}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              fullWidth
              label="Tiêu đề bài tập *"
              value={assignmentForm.title}
              onChange={(e) =>
                setAssignmentForm({ ...assignmentForm, title: e.target.value })
              }
              required
            />
            <TextField
              fullWidth
              label="Mô tả"
              value={assignmentForm.description}
              onChange={(e) =>
                setAssignmentForm({
                  ...assignmentForm,
                  description: e.target.value,
                })
              }
              multiline
              rows={4}
            />
            <Box sx={{ display: "flex", gap: 2 }}>
              <TextField
                fullWidth
                label="Hạn nộp"
                type="date"
                value={assignmentForm.dueDate}
                onChange={(e) =>
                  setAssignmentForm({
                    ...assignmentForm,
                    dueDate: e.target.value,
                  })
                }
                InputLabelProps={{ shrink: true }}
              />
              <TextField
                fullWidth
                label="Điểm tối đa"
                type="number"
                value={assignmentForm.maxScore}
                onChange={(e) =>
                  setAssignmentForm({
                    ...assignmentForm,
                    maxScore: e.target.value,
                  })
                }
                inputProps={{ min: 0, step: 0.1 }}
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseAssignmentDialog}>Hủy</Button>
          <Button onClick={handleSubmitAssignment} variant="contained">
            {assignmentDialog.editing ? "Cập nhật" : "Thêm bài tập"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog: Thêm/Sửa Thông báo */}
      <Dialog
        open={announcementDialog.open}
        onClose={handleCloseAnnouncementDialog}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          {announcementDialog.editing ? "Sửa thông báo" : "Thêm thông báo mới"}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              fullWidth
              label="Tiêu đề thông báo *"
              value={announcementForm.title}
              onChange={(e) =>
                setAnnouncementForm({
                  ...announcementForm,
                  title: e.target.value,
                })
              }
              required
            />
            <TextField
              fullWidth
              label="Nội dung"
              value={announcementForm.content}
              onChange={(e) =>
                setAnnouncementForm({
                  ...announcementForm,
                  content: e.target.value,
                })
              }
              multiline
              rows={6}
            />
            <FormControl fullWidth>
              <InputLabel>Mức độ ưu tiên</InputLabel>
              <Select
                value={announcementForm.priority}
                onChange={(e) =>
                  setAnnouncementForm({
                    ...announcementForm,
                    priority: e.target.value,
                  })
                }
                label="Mức độ ưu tiên"
              >
                <MenuItem value="normal">Bình thường</MenuItem>
                <MenuItem value="important">Quan trọng</MenuItem>
                <MenuItem value="urgent">Khẩn cấp</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseAnnouncementDialog}>Hủy</Button>
          <Button onClick={handleSubmitAnnouncement} variant="contained">
            {announcementDialog.editing ? "Cập nhật" : "Thêm thông báo"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog: Quản lý Sinh viên */}
      <Dialog
        open={studentDialog.open}
        onClose={handleCloseStudentDialog}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Quản lý Sinh viên -{" "}
          {studentDialog.enrollment?.student?.fullName ||
            `SV #${studentDialog.enrollment?.studentId}`}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              fullWidth
              label="Điểm số"
              type="number"
              value={studentForm.grade}
              onChange={(e) =>
                setStudentForm({ ...studentForm, grade: e.target.value })
              }
              inputProps={{ min: 0, max: 10, step: 0.1 }}
              helperText="Nhập điểm từ 0 đến 10"
            />
            <FormControl fullWidth>
              <InputLabel>Trạng thái học tập</InputLabel>
              <Select
                value={studentForm.completionStatus}
                onChange={(e) =>
                  setStudentForm({
                    ...studentForm,
                    completionStatus: e.target.value,
                  })
                }
                label="Trạng thái học tập"
              >
                <MenuItem value="enrolled">Đã đăng ký</MenuItem>
                <MenuItem value="in_progress">Đang học</MenuItem>
                <MenuItem value="completed">Hoàn thành</MenuItem>
                <MenuItem value="dropped">Đã hủy</MenuItem>
              </Select>
            </FormControl>
            <FormControl fullWidth>
              <InputLabel>Trạng thái thanh toán</InputLabel>
              <Select
                value={studentForm.paymentStatus}
                onChange={(e) =>
                  setStudentForm({
                    ...studentForm,
                    paymentStatus: e.target.value,
                  })
                }
                label="Trạng thái thanh toán"
              >
                <MenuItem value="pending">Chờ thanh toán</MenuItem>
                <MenuItem value="paid">Đã thanh toán</MenuItem>
                <MenuItem value="refunded">Đã hoàn tiền</MenuItem>
              </Select>
            </FormControl>
            {studentDialog.enrollment?.student && (
              <Box
                sx={{
                  mt: 2,
                  p: 2,
                  bgcolor: "background.default",
                  borderRadius: 1,
                }}
              >
                <Typography variant="subtitle2" gutterBottom>
                  Thông tin sinh viên:
                </Typography>
                <Typography variant="body2">
                  Email: {studentDialog.enrollment.student.email}
                </Typography>
                {studentDialog.enrollment.student.phone && (
                  <Typography variant="body2">
                    SĐT: {studentDialog.enrollment.student.phone}
                  </Typography>
                )}
                <Typography variant="body2">
                  Ngày đăng ký:{" "}
                  {formatDate(studentDialog.enrollment.enrollmentDate)}
                </Typography>
              </Box>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseStudentDialog}>Hủy</Button>
          <Button onClick={handleSubmitStudent} variant="contained">
            Cập nhật
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog: Chi tiết Bài tập và Nộp bài (cho Sinh viên) */}
      {isStudent && (
        <Dialog
          open={assignmentDetailDialog.open}
          onClose={handleCloseAssignmentDetail}
          maxWidth="md"
          fullWidth
        >
          <DialogTitle>{assignmentDetailDialog.assignment?.title}</DialogTitle>
          <DialogContent>
            {assignmentDetailDialog.assignment && (
              <Box
                sx={{ display: "flex", flexDirection: "column", gap: 3, mt: 1 }}
              >
                {/* Thông tin bài tập */}
                <Box>
                  <Typography
                    variant="subtitle1"
                    gutterBottom
                    sx={{ fontWeight: 600 }}
                  >
                    Thông tin bài tập
                  </Typography>
                  <Box
                    sx={{ display: "flex", gap: 2, flexWrap: "wrap", mb: 2 }}
                  >
                    {assignmentDetailDialog.assignment.dueDate ? (
                      <Chip
                        label={`Hạn nộp: ${formatDate(
                          assignmentDetailDialog.assignment.dueDate
                        )}`}
                        size="small"
                        color="warning"
                      />
                    ) : (
                      <Chip label="Hạn nộp: Không có hạn" size="small" />
                    )}
                    {assignmentDetailDialog.assignment.maxScore ? (
                      <Chip
                        label={`Điểm: ${assignmentDetailDialog.assignment.maxScore}`}
                        size="small"
                      />
                    ) : (
                      <Chip label="Điểm: 0" size="small" />
                    )}
                    <Chip
                      label="Nộp bài bằng file upload"
                      size="small"
                      color="info"
                    />
                  </Box>
                  {assignmentDetailDialog.assignment.description && (
                    <Paper sx={{ p: 2, bgcolor: "background.default" }}>
                      <Typography
                        variant="body2"
                        sx={{ whiteSpace: "pre-wrap" }}
                      >
                        {assignmentDetailDialog.assignment.description}
                      </Typography>
                    </Paper>
                  )}
                </Box>

                {/* Form nộp bài */}
                <Box>
                  <Typography
                    variant="subtitle1"
                    gutterBottom
                    sx={{ fontWeight: 600 }}
                  >
                    Nộp bài
                  </Typography>

                  <TextField
                    fullWidth
                    label="Nhận xét..."
                    value={submissionForm.content}
                    onChange={(e) =>
                      setSubmissionForm({
                        ...submissionForm,
                        content: e.target.value,
                      })
                    }
                    multiline
                    rows={4}
                    sx={{ mb: 2 }}
                    placeholder="Nhập nội dung bài làm hoặc ghi chú..."
                  />

                  {/* File Upload */}
                  <Box sx={{ mb: 2 }}>
                    <Typography
                      variant="body2"
                      gutterBottom
                      sx={{ fontWeight: 600 }}
                    >
                      Tải lên File
                    </Typography>
                    <Typography
                      variant="caption"
                      color="text.secondary"
                      display="block"
                      sx={{ mb: 1 }}
                    >
                      Tải lên một file, hoặc chọn file bạn đã tải lên trước đó.
                    </Typography>

                    <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
                      <Button
                        variant="outlined"
                        component="label"
                        startIcon={<CloudUploadIcon />}
                      >
                        Tải lên File
                        <input
                          type="file"
                          hidden
                          onChange={handleFileChange}
                          accept=".pdf,.doc,.docx,.txt,.zip,.rar"
                        />
                      </Button>
                    </Box>

                    {submissionForm.fileName && (
                      <Box
                        sx={{
                          display: "flex",
                          alignItems: "center",
                          gap: 1,
                          p: 1,
                          bgcolor: "background.default",
                          borderRadius: 1,
                        }}
                      >
                        <AttachFileIcon fontSize="small" color="action" />
                        <Typography variant="body2" sx={{ flex: 1 }}>
                          {submissionForm.fileName}
                        </Typography>
                        <IconButton
                          size="small"
                          onClick={() =>
                            setSubmissionForm({
                              ...submissionForm,
                              file: null,
                              fileName: "",
                            })
                          }
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    )}

                    <Typography
                      variant="caption"
                      color="text.secondary"
                      display="block"
                      sx={{ mt: 1 }}
                    >
                      <Button
                        size="small"
                        variant="text"
                        sx={{ textTransform: "none", p: 0, minWidth: "auto" }}
                      >
                        + Thêm File khác
                      </Button>
                      {" | "}
                      <Button
                        size="small"
                        variant="text"
                        sx={{ textTransform: "none", p: 0, minWidth: "auto" }}
                      >
                        Click vào đây để tìm file bạn đã tải lên
                      </Button>
                    </Typography>
                  </Box>

                  {/* Thông tin submission nếu đã nộp */}
                  {studentSubmissions[
                    assignmentDetailDialog.assignment.assignmentId
                  ] && (
                    <Box
                      sx={{ p: 2, bgcolor: "success.light", borderRadius: 1 }}
                    >
                      <Typography
                        variant="body2"
                        sx={{ display: "flex", alignItems: "center", gap: 1 }}
                      >
                        <CheckCircleIcon fontSize="small" />
                        <strong>Đã nộp bài</strong>
                      </Typography>
                      {studentSubmissions[
                        assignmentDetailDialog.assignment.assignmentId
                      ].submittedDate && (
                        <Typography
                          variant="caption"
                          display="block"
                          sx={{ mt: 0.5 }}
                        >
                          Ngày nộp:{" "}
                          {formatDate(
                            studentSubmissions[
                              assignmentDetailDialog.assignment.assignmentId
                            ].submittedDate
                          )}
                        </Typography>
                      )}
                      {studentSubmissions[
                        assignmentDetailDialog.assignment.assignmentId
                      ].score !== null && (
                        <Typography
                          variant="caption"
                          display="block"
                          sx={{ mt: 0.5 }}
                        >
                          Điểm:{" "}
                          {
                            studentSubmissions[
                              assignmentDetailDialog.assignment.assignmentId
                            ].score
                          }
                          {assignmentDetailDialog.assignment.maxScore &&
                            ` / ${assignmentDetailDialog.assignment.maxScore}`}
                        </Typography>
                      )}
                    </Box>
                  )}
                </Box>
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseAssignmentDetail}>Hủy</Button>
            <Button
              onClick={handleSubmitStudentAssignment}
              variant="contained"
              startIcon={
                studentSubmissions[
                  assignmentDetailDialog.assignment?.assignmentId
                ] ? (
                  <EditIcon />
                ) : (
                  <CheckCircleIcon />
                )
              }
            >
              {studentSubmissions[
                assignmentDetailDialog.assignment?.assignmentId
              ]
                ? "Cập nhật bài nộp"
                : "Nộp bài tập"}
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Dialog: Chi tiết Bài tập và Xem bài nộp (cho Giảng viên) */}
      {canEdit && (
        <Dialog
          open={instructorAssignmentDialog.open}
          onClose={handleCloseInstructorAssignmentDetail}
          maxWidth="lg"
          fullWidth
        >
          <DialogTitle>
            {instructorAssignmentDialog.assignment?.title}
          </DialogTitle>
          <DialogContent>
            {instructorAssignmentDialog.assignment && (
              <Box
                sx={{ display: "flex", flexDirection: "column", gap: 3, mt: 1 }}
              >
                {/* Thông tin bài tập */}
                <Box>
                  <Typography
                    variant="subtitle1"
                    gutterBottom
                    sx={{ fontWeight: 600 }}
                  >
                    Thông tin bài tập
                  </Typography>
                  <Box
                    sx={{ display: "flex", gap: 2, flexWrap: "wrap", mb: 2 }}
                  >
                    {instructorAssignmentDialog.assignment.dueDate ? (
                      <Chip
                        label={`Hạn nộp: ${formatDate(
                          instructorAssignmentDialog.assignment.dueDate
                        )}`}
                        size="small"
                        color="warning"
                      />
                    ) : (
                      <Chip label="Hạn nộp: Không có hạn" size="small" />
                    )}
                    {instructorAssignmentDialog.assignment.maxScore ? (
                      <Chip
                        label={`Điểm tối đa: ${instructorAssignmentDialog.assignment.maxScore}`}
                        size="small"
                      />
                    ) : (
                      <Chip label="Điểm: 0" size="small" />
                    )}
                  </Box>
                  {instructorAssignmentDialog.assignment.description && (
                    <Paper sx={{ p: 2, bgcolor: "background.default" }}>
                      <Typography
                        variant="body2"
                        sx={{ whiteSpace: "pre-wrap" }}
                      >
                        {instructorAssignmentDialog.assignment.description}
                      </Typography>
                    </Paper>
                  )}
                </Box>

                {/* Danh sách bài nộp */}
                <Box>
                  <Typography
                    variant="subtitle1"
                    gutterBottom
                    sx={{ fontWeight: 600 }}
                  >
                    Danh sách bài nộp ({assignmentSubmissions.length})
                  </Typography>

                  {loadingSubmissions ? (
                    <Box display="flex" justifyContent="center" p={3}>
                      <CircularProgress />
                    </Box>
                  ) : assignmentSubmissions.length === 0 ? (
                    <Paper sx={{ p: 4, textAlign: "center" }}>
                      <Typography variant="body1" color="text.secondary">
                        Chưa có sinh viên nào nộp bài
                      </Typography>
                    </Paper>
                  ) : (
                    <TableContainer component={Paper}>
                      <Table>
                        <TableHead>
                          <TableRow>
                            <TableCell>
                              <strong>Sinh viên</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Email</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Ngày nộp</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Nội dung</strong>
                            </TableCell>
                            <TableCell>
                              <strong>File đính kèm</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Điểm</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Trạng thái</strong>
                            </TableCell>
                            <TableCell>
                              <strong>Thao tác</strong>
                            </TableCell>
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {assignmentSubmissions.map((submission) => (
                            <TableRow key={submission.submissionId}>
                              <TableCell>
                                {submission.student?.fullName ||
                                  `SV #${submission.studentId}`}
                              </TableCell>
                              <TableCell>
                                {submission.student?.email || "-"}
                              </TableCell>
                              <TableCell>
                                {formatDate(submission.submittedDate)}
                              </TableCell>
                              <TableCell>
                                {submission.content ? (
                                  <Typography
                                    variant="body2"
                                    sx={{
                                      maxWidth: 200,
                                      overflow: "hidden",
                                      textOverflow: "ellipsis",
                                      whiteSpace: "nowrap",
                                    }}
                                    title={submission.content}
                                  >
                                    {submission.content}
                                  </Typography>
                                ) : (
                                  "-"
                                )}
                              </TableCell>
                              <TableCell>
                                {submission.attachment ? (
                                  <Button
                                    size="small"
                                    variant="outlined"
                                    startIcon={<DownloadIcon />}
                                    onClick={() =>
                                      handleDownloadFile(submission)
                                    }
                                    sx={{ textTransform: "none" }}
                                  >
                                    Tải về
                                  </Button>
                                ) : (
                                  "-"
                                )}
                              </TableCell>
                              <TableCell>
                                {submission.score !== null ? (
                                  <Typography
                                    variant="body2"
                                    sx={{ fontWeight: 600 }}
                                  >
                                    {submission.score}
                                    {instructorAssignmentDialog.assignment
                                      .maxScore &&
                                      ` / ${instructorAssignmentDialog.assignment.maxScore}`}
                                  </Typography>
                                ) : (
                                  <Typography
                                    variant="body2"
                                    color="text.secondary"
                                  >
                                    Chưa chấm
                                  </Typography>
                                )}
                              </TableCell>
                              <TableCell>
                                <Chip
                                  label={
                                    submission.status === "graded"
                                      ? "Đã chấm"
                                      : submission.status === "late"
                                      ? "Nộp muộn"
                                      : "Đã nộp"
                                  }
                                  size="small"
                                  color={
                                    submission.status === "graded"
                                      ? "success"
                                      : submission.status === "late"
                                      ? "warning"
                                      : "info"
                                  }
                                />
                              </TableCell>
                              <TableCell>
                                <Button
                                  size="small"
                                  variant="outlined"
                                  startIcon={<GradeIcon />}
                                  onClick={() =>
                                    handleOpenGradingDialog(submission)
                                  }
                                >
                                  Chấm điểm
                                </Button>
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  )}
                </Box>
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseInstructorAssignmentDetail}>
              Đóng
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Dialog: Chấm điểm */}
      <Dialog
        open={gradingDialog.open}
        onClose={handleCloseGradingDialog}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Chấm điểm -{" "}
          {gradingDialog.submission?.student?.fullName ||
            `SV #${gradingDialog.submission?.studentId}`}
        </DialogTitle>
        <DialogContent>
          {gradingDialog.submission && (
            <Box
              sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}
            >
              {/* Thông tin sinh viên */}
              {gradingDialog.submission.student && (
                <Box
                  sx={{ p: 2, bgcolor: "background.default", borderRadius: 1 }}
                >
                  <Typography variant="subtitle2" gutterBottom>
                    Thông tin sinh viên:
                  </Typography>
                  <Typography variant="body2">
                    <strong>Họ tên:</strong>{" "}
                    {gradingDialog.submission.student.fullName}
                  </Typography>
                  <Typography variant="body2">
                    <strong>Email:</strong>{" "}
                    {gradingDialog.submission.student.email}
                  </Typography>
                  {gradingDialog.submission.submittedDate && (
                    <Typography variant="body2">
                      <strong>Ngày nộp:</strong>{" "}
                      {formatDate(gradingDialog.submission.submittedDate)}
                    </Typography>
                  )}
                </Box>
              )}

              {/* Nội dung bài nộp */}
              {gradingDialog.submission.content && (
                <Box
                  sx={{ p: 2, bgcolor: "background.default", borderRadius: 1 }}
                >
                  <Typography variant="subtitle2" gutterBottom>
                    Nội dung bài nộp:
                  </Typography>
                  <Typography
                    variant="body2"
                    sx={{
                      whiteSpace: "pre-wrap",
                      maxHeight: 200,
                      overflow: "auto",
                    }}
                  >
                    {gradingDialog.submission.content}
                  </Typography>
                </Box>
              )}

              {/* File đính kèm */}
              {gradingDialog.submission.attachment && (
                <Box>
                  <Typography variant="subtitle2" gutterBottom>
                    File đính kèm:
                  </Typography>
                  <Box sx={{ display: "flex", gap: 1 }}>
                    <Button
                      onClick={() =>
                        handleDownloadFile(gradingDialog.submission)
                      }
                      startIcon={<DownloadIcon />}
                      variant="outlined"
                    >
                      Tải về
                    </Button>
                    <Button
                      component="a"
                      href={gradingDialog.submission.attachment}
                      target="_blank"
                      rel="noopener noreferrer"
                      startIcon={<AttachFileIcon />}
                      variant="outlined"
                    >
                      Xem file
                    </Button>
                  </Box>
                </Box>
              )}

              {/* Form chấm điểm */}
              <TextField
                fullWidth
                label="Điểm số"
                type="number"
                value={gradingForm.score}
                onChange={(e) =>
                  setGradingForm({ ...gradingForm, score: e.target.value })
                }
                inputProps={{
                  min: 0,
                  max: instructorAssignmentDialog.assignment?.maxScore || 100,
                  step: 0.1,
                }}
                helperText={
                  instructorAssignmentDialog.assignment?.maxScore
                    ? `Nhập điểm từ 0 đến ${instructorAssignmentDialog.assignment.maxScore}`
                    : "Nhập điểm số"
                }
              />

              <FormControl fullWidth>
                <InputLabel>Trạng thái</InputLabel>
                <Select
                  value={gradingForm.status}
                  onChange={(e) =>
                    setGradingForm({
                      ...gradingForm,
                      status: e.target.value,
                    })
                  }
                  label="Trạng thái"
                >
                  <MenuItem value="submitted">Đã nộp</MenuItem>
                  <MenuItem value="graded">Đã chấm</MenuItem>
                  <MenuItem value="late">Nộp muộn</MenuItem>
                </Select>
              </FormControl>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseGradingDialog}>Hủy</Button>
          <Button onClick={handleSubmitGrading} variant="contained">
            Lưu điểm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default CourseDetail;
