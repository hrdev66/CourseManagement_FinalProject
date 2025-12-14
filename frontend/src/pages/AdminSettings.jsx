import React, { useState, useEffect } from 'react'
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  Select,
  MenuItem,
  FormControl,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
  Tooltip,
  TextField,
  InputAdornment,
} from '@mui/material'
import {
  Delete as DeleteIcon,
  Search as SearchIcon,
  AdminPanelSettings as AdminIcon,
  School as InstructorIcon,
  Person as StudentIcon,
} from '@mui/icons-material'
import { useAuth } from '../context/AuthContext'
import userService from '../services/userService'

const roleOptions = [
  { value: 'ADMIN', label: 'Admin', color: 'error', icon: <AdminIcon /> },
  { value: 'INSTRUCTOR', label: 'Giảng viên', color: 'primary', icon: <InstructorIcon /> },
  { value: 'STUDENT', label: 'Sinh viên', color: 'success', icon: <StudentIcon /> },
]

function AdminSettings() {
  const { user } = useAuth()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [deleteDialog, setDeleteDialog] = useState({ open: false, user: null })
  const [updatingUserId, setUpdatingUserId] = useState(null)

  useEffect(() => {
    fetchUsers()
  }, [])

  const fetchUsers = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await userService.getAllUsers()
      setUsers(data)
    } catch (err) {
      const errorMsg = typeof err.response?.data === 'string' 
        ? err.response.data 
        : err.response?.data?.message || err.message
      setError('Không thể tải danh sách người dùng. ' + errorMsg)
    } finally {
      setLoading(false)
    }
  }

  const handleRoleChange = async (userId, newRole) => {
    try {
      setUpdatingUserId(userId)
      setError(null)
      setSuccess(null)
      
      await userService.updateUserRole(userId, newRole)
      
      setUsers(users.map(u => 
        u.userId === userId ? { ...u, role: newRole } : u
      ))
      
      setSuccess('Cập nhật quyền thành công!')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      const errorMsg = typeof err.response?.data === 'string' 
        ? err.response.data 
        : err.response?.data?.message || 'Không thể cập nhật quyền người dùng'
      setError(errorMsg)
    } finally {
      setUpdatingUserId(null)
    }
  }

  const handleDeleteClick = (userToDelete) => {
    setDeleteDialog({ open: true, user: userToDelete })
  }

  const handleDeleteConfirm = async () => {
    const userToDelete = deleteDialog.user
    setDeleteDialog({ open: false, user: null })
    
    try {
      setError(null)
      setSuccess(null)
      
      await userService.deleteUser(userToDelete.userId)
      
      setUsers(users.filter(u => u.userId !== userToDelete.userId))
      setSuccess('Xóa người dùng thành công!')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      const errorMsg = typeof err.response?.data === 'string' 
        ? err.response.data 
        : err.response?.data?.message || 'Không thể xóa người dùng'
      setError(errorMsg)
    }
  }

  const handleDeleteCancel = () => {
    setDeleteDialog({ open: false, user: null })
  }

  const getRoleChip = (role) => {
    const roleConfig = roleOptions.find(r => r.value === role)
    return (
      <Chip
        icon={roleConfig?.icon}
        label={roleConfig?.label || role}
        color={roleConfig?.color || 'default'}
        size="small"
        sx={{ fontWeight: 500 }}
      />
    )
  }

  const formatDate = (dateString) => {
    if (!dateString) return 'Chưa đăng nhập'
    return new Date(dateString).toLocaleString('vi-VN')
  }

  const filteredUsers = users.filter(u => 
    u.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.role.toLowerCase().includes(searchTerm.toLowerCase())
  )

  // Kiểm tra quyền admin
  if (user?.role !== 'ADMIN') {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Bạn không có quyền truy cập trang này. Chỉ Admin mới có thể quản lý tài khoản người dùng.
        </Alert>
      </Box>
    )
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom sx={{ 
        display: 'flex', 
        alignItems: 'center', 
        gap: 1,
        color: 'primary.main',
        fontWeight: 600,
        mb: 3
      }}>
        <AdminIcon fontSize="large" />
        Quản lý Tài khoản
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <TextField
          fullWidth
          placeholder="Tìm kiếm theo tên đăng nhập, email hoặc quyền..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ mb: 2 }}
        />

        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Chip 
            label={`Tổng: ${users.length} người dùng`} 
            color="default" 
            variant="outlined"
          />
          <Chip 
            icon={<AdminIcon />}
            label={`Admin: ${users.filter(u => u.role === 'ADMIN').length}`} 
            color="error" 
            variant="outlined"
          />
          <Chip 
            icon={<InstructorIcon />}
            label={`Giảng viên: ${users.filter(u => u.role === 'INSTRUCTOR').length}`} 
            color="primary" 
            variant="outlined"
          />
          <Chip 
            icon={<StudentIcon />}
            label={`Sinh viên: ${users.filter(u => u.role === 'STUDENT').length}`} 
            color="success" 
            variant="outlined"
          />
        </Box>
      </Paper>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper} sx={{ 
          boxShadow: 3,
          borderRadius: 2,
          overflow: 'hidden'
        }}>
          <Table>
            <TableHead sx={{ backgroundColor: 'primary.main' }}>
              <TableRow>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>ID</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Tên đăng nhập</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Email</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Quyền hiện tại</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Đổi quyền</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Đăng nhập lần cuối</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Ngày tạo</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }} align="center">Hành động</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredUsers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={8} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">
                      {searchTerm ? 'Không tìm thấy người dùng phù hợp' : 'Chưa có người dùng nào'}
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                filteredUsers.map((u) => (
                  <TableRow 
                    key={u.userId}
                    sx={{ 
                      '&:hover': { backgroundColor: 'action.hover' },
                      backgroundColor: u.userId === user?.userId ? 'action.selected' : 'inherit'
                    }}
                  >
                    <TableCell>{u.userId}</TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography fontWeight={500}>{u.username}</Typography>
                        {u.userId === user?.userId && (
                          <Chip label="Bạn" size="small" color="info" variant="outlined" />
                        )}
                      </Box>
                    </TableCell>
                    <TableCell>{u.email || '-'}</TableCell>
                    <TableCell>{getRoleChip(u.role)}</TableCell>
                    <TableCell>
                      <FormControl size="small" sx={{ minWidth: 140 }}>
                        <Select
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.userId, e.target.value)}
                          disabled={updatingUserId === u.userId}
                          sx={{ 
                            '& .MuiSelect-select': { 
                              display: 'flex', 
                              alignItems: 'center',
                              gap: 1
                            }
                          }}
                        >
                          {roleOptions.map((option) => (
                            <MenuItem key={option.value} value={option.value}>
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                {option.icon}
                                {option.label}
                              </Box>
                            </MenuItem>
                          ))}
                        </Select>
                        {updatingUserId === u.userId && (
                          <CircularProgress 
                            size={20} 
                            sx={{ position: 'absolute', right: 30, top: '50%', mt: -1 }}
                          />
                        )}
                      </FormControl>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {formatDate(u.lastLogin)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {formatDate(u.createdAt)}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title={u.userId === user?.userId ? 'Không thể xóa chính mình' : 'Xóa người dùng'}>
                        <span>
                          <IconButton
                            color="error"
                            onClick={() => handleDeleteClick(u)}
                            disabled={u.userId === user?.userId}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </span>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog.open} onClose={handleDeleteCancel}>
        <DialogTitle sx={{ color: 'error.main' }}>
          Xác nhận xóa người dùng
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bạn có chắc chắn muốn xóa người dùng <strong>{deleteDialog.user?.username}</strong>?
            <br />
            Hành động này không thể hoàn tác.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} color="inherit">
            Hủy
          </Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default AdminSettings

