import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Link,
} from '@mui/material'
import { useAuth } from '../context/AuthContext'
import StudentRegistrationDialog from '../components/StudentRegistrationDialog'

function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [openRegister, setOpenRegister] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const result = await login(username, password)
    if (result.success) {
      navigate('/')
    } else {
      setError(result.error || 'Đăng nhập thất bại')
    }
  }

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: '100%' }}>
          <Typography variant="h4" component="h1" gutterBottom align="center">
            🎓 Đăng nhập
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            Hệ thống Quản lý Khóa học Trực tuyến
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Tên đăng nhập"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              margin="normal"
              required
              autoFocus
            />
            <TextField
              fullWidth
              label="Mật khẩu"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              margin="normal"
              required
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, py: 1.5 }}
            >
              Đăng nhập
            </Button>
            <Box textAlign="center">
              <Link
                component="button"
                variant="body2"
                onClick={() => setOpenRegister(true)}
                sx={{ cursor: 'pointer' }}
              >
                Chưa có tài khoản? Đăng ký ngay
              </Link>
            </Box>
          </form>

          <Box sx={{ mt: 3, p: 2, bgcolor: 'grey.100', borderRadius: 1 }}>
            <Typography variant="caption" display="block" gutterBottom>
              <strong>Tài khoản mẫu:</strong>
            </Typography>
            <Typography variant="caption" display="block">
              Admin: admin / 123456
            </Typography>
            <Typography variant="caption" display="block">
              Sinh viên: hvm / 123456
            </Typography>
          </Box>
        </Paper>
      </Box>

      <StudentRegistrationDialog
        open={openRegister}
        onClose={() => setOpenRegister(false)}
      />
    </Container>
  )
}

export default Login

