import React, { useEffect, useState } from 'react'
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
  Checkbox,
  FormControlLabel,
  FormGroup,
  Divider,
  Grid,
  Card,
  CardContent,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material'
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  CalendarMonth as CalendarIcon,
  School as SchoolIcon,
  ArrowForward as ArrowIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material'
import { registrationPeriodService } from '../services/registrationPeriodService'
import { courseService } from '../services/courseService'
import { instructorService } from '../services/instructorService'
import { useAuth } from '../context/AuthContext'

function RegistrationPeriods() {
  const { user } = useAuth()
  const [periods, setPeriods] = useState([])
  const [courses, setCourses] = useState([])
  const [instructors, setInstructors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  
  // Dialog states
  const [openDialog, setOpenDialog] = useState(false)
  const [editing, setEditing] = useState(null)
  const [activeStep, setActiveStep] = useState(0)
  const [selectedCourseIds, setSelectedCourseIds] = useState([])
  
  const [formData, setFormData] = useState({
    periodName: '',
    description: '',
    startDate: '',
    endDate: '',
  })

  const steps = ['Thông tin đợt đăng ký', 'Chọn khóa học']

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      setLoading(true)
      const [periodsRes, coursesRes, instructorsRes] = await Promise.all([
        registrationPeriodService.getAll(),
        courseService.getAll(),
        instructorService.getAll()
      ])
      setPeriods(periodsRes.data)
      setCourses(coursesRes.data.filter(c => c.status === 'active'))
      setInstructors(instructorsRes.data)
    } catch (err) {
      setError('Lỗi khi tải dữ liệu')
    } finally {
      setLoading(false)
    }
  }

  const handleOpenDialog = async (period = null) => {
    setActiveStep(0)
    if (period) {
      setEditing(period)
      setFormData({
        periodName: period.periodName || '',
        description: period.description || '',
        startDate: period.startDate || '',
        endDate: period.endDate || '',
      })
      // Lấy danh sách khóa học đã chọn
      try {
        const res = await registrationPeriodService.getCourses(period.periodId)
        setSelectedCourseIds(res.data.map(c => c.courseId))
      } catch (err) {
        setSelectedCourseIds([])
      }
    } else {
      setEditing(null)
      setFormData({
        periodName: '',
        description: '',
        startDate: '',
        endDate: '',
      })
      setSelectedCourseIds([])
    }
    setOpenDialog(true)
    setError('')
  }

  const handleCloseDialog = () => {
    setOpenDialog(false)
    setEditing(null)
    setActiveStep(0)
    setSelectedCourseIds([])
    setError('')
  }

  const handleNext = () => {
    if (activeStep === 0) {
      // Validate form data
      if (!formData.periodName || !formData.startDate || !formData.endDate) {
        setError('Vui lòng điền đầy đủ thông tin bắt buộc')
        return
      }
      if (new Date(formData.startDate) > new Date(formData.endDate)) {
        setError('Ngày kết thúc phải sau ngày bắt đầu')
        return
      }
      setError('')
    }
    setActiveStep(prev => prev + 1)
  }

  const handleBack = () => {
    setActiveStep(prev => prev - 1)
  }

  const handleCourseToggle = (courseId) => {
    setSelectedCourseIds(prev => 
      prev.includes(courseId) 
        ? prev.filter(id => id !== courseId)
        : [...prev, courseId]
    )
  }

  const handleSelectAll = () => {
    if (selectedCourseIds.length === courses.length) {
      setSelectedCourseIds([])
    } else {
      setSelectedCourseIds(courses.map(c => c.courseId))
    }
  }

  const handleSubmit = async () => {
    try {
      if (selectedCourseIds.length === 0) {
        setError('Vui lòng chọn ít nhất một khóa học')
        return
      }

      let periodId;
      if (editing) {
        const updated = await registrationPeriodService.update(editing.periodId, formData)
        periodId = editing.periodId
        setSuccess('Cập nhật đợt đăng ký thành công!')
      } else {
        const created = await registrationPeriodService.create(formData)
        periodId = created.data.periodId
        setSuccess('Tạo đợt đăng ký thành công!')
      }

      // Cập nhật danh sách khóa học
      await registrationPeriodService.updateCourses(periodId, selectedCourseIds)
      
      handleCloseDialog()
      fetchData()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err) {
      setError(err.response?.data || 'Có lỗi xảy ra')
    }
  }

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa đợt đăng ký này?')) {
      try {
        await registrationPeriodService.delete(id)
        setSuccess('Xóa đợt đăng ký thành công!')
        fetchData()
        setTimeout(() => setSuccess(''), 3000)
      } catch (err) {
        setError('Lỗi khi xóa đợt đăng ký')
      }
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'active': return 'success'
      case 'upcoming': return 'info'
      case 'closed': return 'default'
      default: return 'default'
    }
  }

  const getStatusLabel = (status) => {
    switch (status) {
      case 'active': return 'Đang mở'
      case 'upcoming': return 'Sắp mở'
      case 'closed': return 'Đã đóng'
      default: return status
    }
  }

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A'
    return new Date(dateStr).toLocaleDateString('vi-VN')
  }

  const formatPrice = (price) => {
    if (!price) return 'Miễn phí'
    return new Intl.NumberFormat('vi-VN').format(price) + ' VNĐ'
  }

  const getInstructorName = (instructorId) => {
    const instructor = instructors.find(i => i.instructorId === instructorId)
    return instructor?.fullName || 'N/A'
  }

  // Lấy số lượng khóa học trong đợt đăng ký
  const [periodCourseCounts, setPeriodCourseCounts] = useState({})
  
  useEffect(() => {
    const fetchCourseCounts = async () => {
      const counts = {}
      for (const period of periods) {
        try {
          const res = await registrationPeriodService.getCourses(period.periodId)
          counts[period.periodId] = res.data.length
        } catch {
          counts[period.periodId] = 0
        }
      }
      setPeriodCourseCounts(counts)
    }
    if (periods.length > 0) {
      fetchCourseCounts()
    }
  }, [periods])

  if (user?.role !== 'ADMIN') {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Bạn không có quyền truy cập trang này. Chỉ Admin mới có thể quản lý đợt đăng ký.
        </Alert>
      </Box>
    )
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={3}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <CalendarIcon color="primary" />
          Quản lý Đợt đăng ký
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          Thêm đợt đăng ký
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
          {success}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Tên đợt đăng ký</TableCell>
              <TableCell>Mô tả</TableCell>
              <TableCell>Ngày bắt đầu</TableCell>
              <TableCell>Ngày kết thúc</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Số khóa học</TableCell>
              <TableCell>Thao tác</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {periods.length === 0 ? (
              <TableRow>
                <TableCell colSpan={8} align="center">
                  Chưa có đợt đăng ký nào
                </TableCell>
              </TableRow>
            ) : (
              periods.map((period) => (
                <TableRow key={period.periodId}>
                  <TableCell>{period.periodId}</TableCell>
                  <TableCell><strong>{period.periodName}</strong></TableCell>
                  <TableCell>{period.description || '-'}</TableCell>
                  <TableCell>{formatDate(period.startDate)}</TableCell>
                  <TableCell>{formatDate(period.endDate)}</TableCell>
                  <TableCell>
                    <Chip 
                      label={getStatusLabel(period.status)} 
                      color={getStatusColor(period.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip 
                      icon={<SchoolIcon />}
                      label={periodCourseCounts[period.periodId] || 0}
                      size="small"
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    <IconButton size="small" onClick={() => handleOpenDialog(period)} title="Sửa">
                      <EditIcon />
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => handleDelete(period.periodId)} title="Xóa">
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Dialog thêm/sửa đợt đăng ký với Stepper */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          {editing ? 'Sửa đợt đăng ký' : 'Thêm đợt đăng ký mới'}
        </DialogTitle>
        <DialogContent>
          {/* Stepper */}
          <Stepper activeStep={activeStep} sx={{ mb: 3, mt: 1 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {/* Step 1: Thông tin đợt đăng ký */}
          {activeStep === 0 && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                fullWidth
                label="Tên đợt đăng ký *"
                value={formData.periodName}
                onChange={(e) => setFormData({ ...formData, periodName: e.target.value })}
                placeholder="VD: Đợt đăng ký học kỳ 1 - 2024"
              />
              <TextField
                fullWidth
                label="Mô tả"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                multiline
                rows={2}
              />
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Ngày bắt đầu *"
                    type="date"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Ngày kết thúc *"
                    type="date"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
              </Grid>
            </Box>
          )}

          {/* Step 2: Chọn khóa học */}
          {activeStep === 1 && (
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="body1">
                  Chọn các khóa học có trong đợt đăng ký này:
                </Typography>
                <Box>
                  <Button 
                    size="small" 
                    onClick={handleSelectAll}
                    variant="outlined"
                  >
                    {selectedCourseIds.length === courses.length ? 'Bỏ chọn tất cả' : 'Chọn tất cả'}
                  </Button>
                  <Chip 
                    label={`Đã chọn: ${selectedCourseIds.length}/${courses.length}`}
                    color="primary"
                    sx={{ ml: 1 }}
                  />
                </Box>
              </Box>
              
              <Divider sx={{ mb: 2 }} />

              {courses.length === 0 ? (
                <Alert severity="info">Không có khóa học nào</Alert>
              ) : (
                <Grid container spacing={2} sx={{ maxHeight: 400, overflowY: 'auto' }}>
                  {courses.map((course) => {
                    const isSelected = selectedCourseIds.includes(course.courseId)
                    return (
                      <Grid item xs={12} md={6} key={course.courseId}>
                        <Card 
                          sx={{ 
                            cursor: 'pointer',
                            border: isSelected ? '2px solid' : '1px solid',
                            borderColor: isSelected ? 'primary.main' : 'divider',
                            backgroundColor: isSelected ? 'action.selected' : 'background.paper',
                            transition: 'all 0.2s',
                            '&:hover': { boxShadow: 2 }
                          }}
                          onClick={() => handleCourseToggle(course.courseId)}
                        >
                          <CardContent sx={{ py: 1.5, '&:last-child': { pb: 1.5 } }}>
                            <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
                              <Checkbox 
                                checked={isSelected} 
                                sx={{ p: 0, mr: 1 }}
                              />
                              <Box sx={{ flex: 1 }}>
                                <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                                  {course.courseName}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                  {course.courseCode} • {getInstructorName(course.instructorId)}
                                </Typography>
                                <Typography variant="body2" color="primary.main" sx={{ fontWeight: 500 }}>
                                  {formatPrice(course.price)} • {course.durationWeeks} tuần
                                </Typography>
                              </Box>
                            </Box>
                          </CardContent>
                        </Card>
                      </Grid>
                    )
                  })}
                </Grid>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={handleCloseDialog}>Hủy</Button>
          <Box sx={{ flex: 1 }} />
          {activeStep > 0 && (
            <Button onClick={handleBack} startIcon={<BackIcon />}>
              Quay lại
            </Button>
          )}
          {activeStep < steps.length - 1 ? (
            <Button onClick={handleNext} variant="contained" endIcon={<ArrowIcon />}>
              Tiếp tục
            </Button>
          ) : (
            <Button onClick={handleSubmit} variant="contained" color="success">
              {editing ? 'Cập nhật' : 'Tạo đợt đăng ký'} ({selectedCourseIds.length} khóa học)
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default RegistrationPeriods
