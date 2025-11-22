#!/bin/bash

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║          THIẾT LẬP DATABASE CHO DỰ ÁN                         ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

cd "$(dirname "$0")"

echo "[1/3] Kiểm tra MariaDB/MySQL..."
if ! command -v mysql &> /dev/null; then
    echo "[ERROR] Không tìm thấy MySQL/MariaDB!"
    exit 1
fi

echo "✓ Đã tìm thấy MySQL/MariaDB"
echo ""

echo "[2/3] Tạo database và import dữ liệu..."
mysql -u root -phungkiro < setup_mariadb.sql

if [ $? -ne 0 ]; then
    echo "[ERROR] Không thể tạo database!"
    exit 1
fi

echo ""
echo "✓ Database đã được tạo thành công!"
echo ""

echo "[3/3] Kiểm tra dữ liệu..."
mysql -u root -phungkiro -e "USE course_management; SELECT COUNT(*) as 'Số giảng viên' FROM instructors; SELECT COUNT(*) as 'Số khóa học' FROM courses; SELECT COUNT(*) as 'Số sinh viên' FROM students; SELECT COUNT(*) as 'Số đăng ký' FROM enrollments;"

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                    THIẾT LẬP THÀNH CÔNG!                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"

