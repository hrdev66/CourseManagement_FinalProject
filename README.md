# 🎓 Hệ thống Quản lý Khóa học Trực tuyến

## Online Course Management System - Canvas LMS Style

Dự án cuối kỳ môn Lập trình Java - Sử dụng JDBC và Swing

**Version 3.0** - Hệ thống đăng nhập/đăng ký với phân quyền (Admin, Instructor, Student)

---

## 📋 Thông tin dự án

**Công nghệ sử dụng:**

- Java 11+
- JDBC (MariaDB/MySQL)
- Swing GUI
- Maven

**Tính năng chính (Canvas LMS Style):**

- ✅ **Dashboard** - Tổng quan hệ thống với thống kê
- ✅ **Quản lý Khóa học** (CRUD)
- ✅ **Quản lý Bài tập** (Assignments) - Giao và chấm bài
- ✅ **Quản lý Bài nộp** (Submissions) - Nộp bài và chấm điểm
- ✅ **Thông báo** (Announcements) - Thông báo khóa học
- ✅ **Quản lý Sinh viên** (CRUD)
- ✅ **Quản lý Giảng viên** (CRUD)
- ✅ **Quản lý Đăng ký** (CRUD)
- ✅ **Theo dõi tiến độ** học tập
- ✅ Tìm kiếm và lọc dữ liệu
- ✅ Validation đầy đủ
- ✅ Dữ liệu mẫu tự động

---

## 🚀 Hướng dẫn chạy

### Bước 0: Thiết lập Database (QUAN TRỌNG!)

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

### Bước 1: Mở dự án trong IntelliJ IDEA

1. File → Open
2. Chọn thư mục dự án
3. Đợi Maven tải dependencies (1-2 phút)
4. Click chuột phải vào `pom.xml` → Maven → Reload Project

### Bước 2: Chạy ứng dụng

1. Mở file `src/main/java/Main.java`
2. Click chuột phải → Run 'Main.main()'
3. Hoặc nhấn **Shift + F10**
4. **Màn hình đăng nhập sẽ hiện ra**

### Bước 3: Đăng nhập hoặc Đăng ký

**Đăng nhập với tài khoản mẫu:**

- **Admin**: `admin` / `123456` (Toàn quyền)
- **Giảng viên**: `nva` / `123456` (Quản lý khóa học)
- **Sinh viên**: `hvm` / `123456` (Xem và học tập)

**Đăng ký tài khoản mới:**

1. Click **"📝 Đăng ký"** ở màn hình đăng nhập
2. Điền thông tin đăng nhập (Username, Email, Password)
3. Điền thông tin cá nhân (Họ tên, SĐT, Ngày sinh, Địa chỉ)
4. Click **"💾 Đăng ký"**
5. **Tài khoản mới MẶC ĐỊNH là Sinh viên (Student)**

**Lưu ý:**

- ✅ Tài khoản đăng ký mới luôn là **Student** (Sinh viên)
- ✅ Hệ thống tự động tạo thông tin Sinh viên và liên kết với tài khoản
- ❌ Instructor và Admin cần được tạo bởi quản trị viên

**Xem chi tiết:**

- Đăng nhập: `HUONG_DAN_DANG_NHAP.md`
- Đăng ký: `HUONG_DAN_DANG_KY.md`

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
│   ├── model/              # 9 Model classes (thêm Assignment, Submission, Announcement)
│   ├── dao/                # 7 DAO classes (thêm AssignmentDAO, SubmissionDAO, AnnouncementDAO)
│   ├── database/           # Database connection
│   └── gui/                # 8 GUI panels (thêm Dashboard, Assignment, Announcement)
└── resources/
    └── config/
        └── database.properties
```

---

## 📊 Database Schema

**10 bảng:**

- `users` - Tài khoản đăng nhập ⭐ MỚI V3.0
- `instructors` - Giảng viên
- `courses` - Khóa học
- `students` - Sinh viên
- `enrollments` - Đăng ký khóa học
- `lessons` - Bài học
- `reviews` - Đánh giá
- `assignments` - Bài tập
- `submissions` - Bài nộp
- `announcements` - Thông báo

**Dữ liệu mẫu:**

- **10 Tài khoản** (1 admin, 4 giảng viên, 5 sinh viên) ⭐ MỚI V3.0
- 4 Giảng viên
- 5 Khóa học
- 5 Sinh viên
- 7 Đăng ký
- 5 Bài tập
- 3 Bài nộp
- 4 Thông báo

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

## 🎨 Tính năng Canvas LMS Style

### Dashboard (Tổng quan)

- Thống kê số khóa học đã đăng ký
- Bài tập sắp đến hạn
- Thông báo mới nhất
- Tiến độ học tập trung bình

### Quản lý Bài tập

- Tạo bài tập (homework, quiz, project)
- Đặt hạn nộp và điểm tối đa
- Xem danh sách bài nộp
- Chấm điểm trực tiếp trên hệ thống

### Thông báo

- Tạo thông báo cho khóa học
- Phân loại độ ưu tiên (normal, important, urgent)
- Hiển thị thông báo mới nhất trên Dashboard

### Theo dõi tiến độ

- Xem tiến độ học tập từng khóa học
- Điểm số và trạng thái hoàn thành

---

## 📝 Ghi chú

- Database sử dụng MariaDB/MySQL
- Encoding: UTF-8 (hỗ trợ tiếng Việt)
- Port mặc định: 3306
- Dữ liệu mẫu tự động được thêm khi chạy script
- **Version 3.0** - Đã thêm hệ thống đăng nhập/đăng ký với phân quyền
- **Password mặc định**: `123456` (tất cả tài khoản mẫu)
- **Cần chạy lại script** `setup_mariadb.sql` để tạo bảng `users` và tài khoản mẫu

---

## 🔐 Tài khoản mẫu (Password: 123456)

| Username | Password | Role       | Mô tả          |
| -------- | -------- | ---------- | -------------- |
| `admin`  | `123456` | Admin      | Quản trị viên  |
| `nva`    | `123456` | Instructor | Nguyễn Văn An  |
| `hvm`    | `123456` | Student    | Hoàng Văn Minh |

Xem đầy đủ danh sách: `HUONG_DAN_DANG_NHAP.md`

---

## 👨‍💻 Tác giả

Dự án cuối kỳ - Môn Lập trình Java

---

**Chúc bạn thành công! 🎉**
