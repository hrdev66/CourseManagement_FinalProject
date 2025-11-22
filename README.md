# 🎓 Hệ thống Quản lý Khóa học Trực tuyến
## Online Course Management System

Dự án cuối kỳ môn Lập trình Java - Sử dụng JDBC và Swing

---

## 📋 Thông tin dự án

**Công nghệ sử dụng:**
- Java 11+
- JDBC (MariaDB/MySQL)
- Swing GUI
- Maven

**Tính năng:**
- ✅ Quản lý Khóa học (CRUD)
- ✅ Quản lý Sinh viên (CRUD)
- ✅ Quản lý Giảng viên (CRUD)
- ✅ Quản lý Đăng ký (CRUD)
- ✅ Tìm kiếm và lọc dữ liệu
- ✅ Validation đầy đủ
- ✅ Dữ liệu mẫu tự động

---

## 🚀 Hướng dẫn chạy

### Bước 1: Thiết lập Database

**Chạy script tự động:**
```bash
# Windows
setup_database.bat

# Linux/Mac
chmod +x setup_database.sh
./setup_database.sh
```

**Hoặc chạy thủ công:**
```bash
mysql -u root -phungkiro < setup_mariadb.sql
```

### Bước 2: Mở dự án trong IntelliJ IDEA

1. File → Open
2. Chọn thư mục dự án
3. Đợi Maven tải dependencies (1-2 phút)
4. Click chuột phải vào `pom.xml` → Maven → Reload Project

### Bước 3: Chạy ứng dụng

1. Mở file `src/main/java/Main.java`
2. Click chuột phải → Run 'Main.main()'
3. Hoặc nhấn **Shift + F10**

---

## ⚙️ Cấu hình Database

File: `src/main/resources/config/database.properties`

```properties
db.type=mysql
db.mysql.host=localhost
db.mysql.port=3306
db.mysql.database=course_management
db.mysql.username=root
db.mysql.password=hungkiro
```

---

## 📁 Cấu trúc dự án

```
src/main/
├── java/
│   ├── Main.java           # Entry point
│   ├── model/              # 6 Model classes
│   ├── dao/                # 4 DAO classes
│   ├── database/           # Database connection
│   └── gui/                # 5 GUI panels
└── resources/
    └── config/
        └── database.properties
```

---

## 📊 Database Schema

**6 bảng:**
- `instructors` - Giảng viên
- `courses` - Khóa học
- `students` - Sinh viên
- `enrollments` - Đăng ký khóa học
- `lessons` - Bài học
- `reviews` - Đánh giá

**Dữ liệu mẫu:**
- 4 Giảng viên
- 5 Khóa học
- 5 Sinh viên
- 7 Đăng ký

---

## 🔧 Xử lý lỗi

### Lỗi kết nối database
- Kiểm tra MariaDB đang chạy: `net start MariaDB`
- Kiểm tra username/password trong `database.properties`
- Đảm bảo đã chạy script `setup_mariadb.sql`

### Lỗi Maven
- Click chuột phải vào project → Maven → Reload Project
- File → Invalidate Caches → Invalidate and Restart

---

## 📝 Ghi chú

- Database sử dụng MariaDB/MySQL
- Encoding: UTF-8 (hỗ trợ tiếng Việt)
- Port mặc định: 3306
- Dữ liệu mẫu tự động được thêm khi chạy script

---

## 👨‍💻 Tác giả

Dự án cuối kỳ - Môn Lập trình Java

---

**Chúc bạn thành công! 🎉**
