@echo off
chcp 65001 >nul
echo ╔════════════════════════════════════════════════════════════════╗
echo ║          THIẾT LẬP DATABASE CHO DỰ ÁN                         ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo [1/3] Kiểm tra MariaDB/MySQL...
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Không tìm thấy MySQL/MariaDB!
    pause
    exit /b 1
)

echo ✓ Đã tìm thấy MySQL/MariaDB
echo.

echo [2/3] Tạo database và import dữ liệu...
mysql -u root -phungkiro < setup_mariadb.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Không thể tạo database!
    pause
    exit /b 1
)

echo.
echo ✓ Database đã được tạo thành công!
echo.

echo [3/3] Kiểm tra dữ liệu...
mysql -u root -phungkiro -e "USE course_management; SELECT COUNT(*) as 'Số giảng viên' FROM instructors; SELECT COUNT(*) as 'Số khóa học' FROM courses; SELECT COUNT(*) as 'Số sinh viên' FROM students; SELECT COUNT(*) as 'Số đăng ký' FROM enrollments;"

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║                    THIẾT LẬP THÀNH CÔNG!                      ║
echo ╚════════════════════════════════════════════════════════════════╝
pause

