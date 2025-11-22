-- Script khởi tạo database cho MariaDB
-- Chạy script này trước khi chạy ứng dụng

-- Xóa database nếu tồn tại
DROP DATABASE IF EXISTS course_management;

-- Tạo database mới
CREATE DATABASE course_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Sử dụng database
USE course_management;

-- Bảng Giảng viên (Instructors)
CREATE TABLE instructors (
    instructor_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(100),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Khóa học (Courses)
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(200) NOT NULL,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    instructor_id INT,
    duration_weeks INT,
    price DECIMAL(10, 2),
    max_students INT DEFAULT 50,
    status ENUM('active', 'inactive', 'completed') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Sinh viên (Students)
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    address TEXT,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Đăng ký khóa học (Enrollments)
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    completion_status ENUM('enrolled', 'in_progress', 'completed', 'dropped') DEFAULT 'enrolled',
    grade DECIMAL(5, 2),
    payment_status ENUM('pending', 'paid', 'refunded') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Bài học (Lessons)
CREATE TABLE lessons (
    lesson_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    lesson_title VARCHAR(200) NOT NULL,
    lesson_order INT NOT NULL,
    content TEXT,
    video_url VARCHAR(500),
    duration_minutes INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Đánh giá (Reviews)
CREATE TABLE reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm dữ liệu mẫu

-- Giảng viên mẫu
INSERT INTO instructors (full_name, email, phone, specialization, bio) VALUES
('Nguyễn Văn An', 'nva@email.com', '0901234567', 'Lập trình Java', 'Giảng viên với 10 năm kinh nghiệm'),
('Trần Thị Bình', 'ttb@email.com', '0902345678', 'Web Development', 'Chuyên gia phát triển web'),
('Lê Văn Cường', 'lvc@email.com', '0903456789', 'Data Science', 'Tiến sĩ Khoa học Dữ liệu'),
('Phạm Thị Dung', 'ptd@email.com', '0904567890', 'Mobile Development', 'Chuyên gia phát triển ứng dụng di động');

-- Khóa học mẫu
INSERT INTO courses (course_name, course_code, description, instructor_id, duration_weeks, price, max_students, status) VALUES
('Lập trình Java cơ bản', 'JAVA101', 'Khóa học Java từ cơ bản đến nâng cao', 1, 12, 2500000, 30, 'active'),
('Phát triển Web với Spring Boot', 'WEB201', 'Xây dựng ứng dụng web với Spring Boot', 2, 16, 3500000, 25, 'active'),
('Python cho Data Science', 'DS301', 'Phân tích dữ liệu với Python', 3, 14, 4000000, 20, 'active'),
('Phát triển ứng dụng Android', 'MOB401', 'Lập trình Android từ đầu', 4, 18, 4500000, 15, 'active'),
('JavaScript và React', 'WEB301', 'Xây dựng giao diện web hiện đại', 2, 10, 3000000, 30, 'active');

-- Sinh viên mẫu
INSERT INTO students (full_name, email, phone, date_of_birth, address) VALUES
('Hoàng Văn Minh', 'hvm@student.com', '0911111111', '2002-05-15', 'Hà Nội'),
('Đỗ Thị Lan', 'dtl@student.com', '0922222222', '2003-08-20', 'Hồ Chí Minh'),
('Vũ Văn Nam', 'vvn@student.com', '0933333333', '2002-12-10', 'Đà Nẵng'),
('Bùi Thị Hoa', 'bth@student.com', '0944444444', '2003-03-25', 'Hải Phòng'),
('Đặng Văn Tuấn', 'dvt@student.com', '0955555555', '2002-07-30', 'Cần Thơ');

-- Đăng ký khóa học mẫu
INSERT INTO enrollments (student_id, course_id, completion_status, grade, payment_status) VALUES
(1, 1, 'completed', 8.5, 'paid'),
(1, 2, 'in_progress', NULL, 'paid'),
(2, 1, 'in_progress', NULL, 'paid'),
(2, 3, 'enrolled', NULL, 'pending'),
(3, 4, 'in_progress', NULL, 'paid'),
(4, 2, 'completed', 9.0, 'paid'),
(5, 5, 'in_progress', NULL, 'paid');

-- Bài học mẫu
INSERT INTO lessons (course_id, lesson_title, lesson_order, content, duration_minutes) VALUES
(1, 'Giới thiệu về Java', 1, 'Tổng quan về ngôn ngữ lập trình Java', 45),
(1, 'Biến và kiểu dữ liệu', 2, 'Các kiểu dữ liệu cơ bản trong Java', 60),
(1, 'Cấu trúc điều khiển', 3, 'If-else, switch, loops', 75),
(2, 'Giới thiệu Spring Boot', 1, 'Khởi tạo dự án Spring Boot', 50),
(2, 'REST API với Spring', 2, 'Xây dựng RESTful API', 90);

-- Đánh giá mẫu
INSERT INTO reviews (course_id, student_id, rating, comment) VALUES
(1, 1, 5, 'Khóa học rất tuyệt vời, giảng viên nhiệt tình'),
(2, 4, 5, 'Nội dung chi tiết, dễ hiểu'),
(1, 2, 4, 'Khóa học tốt nhưng cần thêm bài tập thực hành');

-- Hoàn tất
SELECT 'Database course_management đã được tạo thành công!' AS Message;
SELECT 'Dữ liệu mẫu đã được thêm vào!' AS Message;

