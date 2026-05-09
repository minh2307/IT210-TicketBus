-- =====================================================
-- FULL RESET & SEAT GENERATION SCRIPT
-- =====================================================

DROP DATABASE IF EXISTS Booking_Car;
CREATE DATABASE Booking_Car;
USE Booking_Car;

-- Lưu ý: Sau khi chạy script này, hãy chạy ứng dụng Spring Boot 
-- để Hibernate tự tạo lại cấu trúc bảng (ddl-auto=update) 
-- TRƯỚC KHI chạy phần INSERT dưới đây. 
-- HOẶC: Tôi sẽ chèn dữ liệu vào bảng sau khi bạn khởi động ứng dụng.

-- Tuy nhiên, để tiện nhất, tôi sẽ viết script INSERT có tính đến việc bảng đã tồn tại.

-- 1. Locations
INSERT INTO locations (id, name, provinceCode) VALUES
(1, 'Hà Nội', 'HN'), (2, 'TP. Hồ Chí Minh', 'HCM'), (3, 'Đà Nẵng', 'DN'),
(4, 'Hải Phòng', 'HP'), (5, 'Cần Thơ', 'CT'), (6, 'Huế', 'HUE'),
(7, 'Nha Trang', 'NT'), (8, 'Đà Lạt', 'DL'), (9, 'Vũng Tàu', 'VT'), (10, 'Quy Nhơn', 'QN');

-- 2. Users (pass: 123456789Ls@)
-- Hash này là mã giả định cho 123456789Ls@, Spring Security sẽ nhận diện được nếu bạn dùng BCrypt
INSERT INTO users (id, username, passwordHash, role, created_at, updated_at) VALUES
(1, 'admin', '$2a$12$UwvlLF5etGXvfvwluJdRQOrTS08oy7xc2bpjAmmQRMIHGzEPUoC0m', 'ADMIN', NOW(), NOW()),
(2, 'staff', '$2a$12$UwvlLF5etGXvfvwluJdRQOrTS08oy7xc2bpjAmmQRMIHGzEPUoC0m', 'STAFF', NOW(), NOW()),
(3, 'user', '$2a$12$UwvlLF5etGXvfvwluJdRQOrTS08oy7xc2bpjAmmQRMIHGzEPUoC0m   ', 'PASSENGER', NOW(), NOW());

INSERT INTO user_profiles (id, user_id, fullName, email, phone, address) VALUES
(1, 1, 'Admin System', 'admin@ticketbus.com', '0901234567', 'Hà Nội'),
(2, 2, 'Staff Member', 'staff@ticketbus.com', '0901234568', 'TP.HCM'),
(3, 3, 'Nguyễn Văn Người Dùng', 'user@gmail.com', '0901234569', 'Sài Gòn');

-- 3. Routes
INSERT INTO routes (id, origin_id, destination_id, distance) VALUES
(1, 1, 2, 1730), (2, 2, 1, 1730), (3, 1, 3, 760), (4, 3, 1, 760),
(5, 2, 8, 310), (6, 8, 2, 310), (7, 2, 9, 100), (8, 9, 2, 100);

-- 4. Buses
INSERT INTO buses (id, license_plate, bus_type, total_seats, company, status, created_at, updated_at) VALUES
(1, '29B-00001', 'SLEEPER_40', 40, 'Hà Nội Bus', 'ACTIVE', NOW(), NOW()),
(2, '51B-00002', 'SEATS_45', 45, 'Phương Trang', 'ACTIVE', NOW(), NOW()),
(3, '43B-00003', 'SLEEPER_34', 34, 'Thành Bưởi', 'ACTIVE', NOW(), NOW());

-- 5. Trips (Cho 30 ngày tới để lúc nào cũng tìm thấy chuyến)
DELIMITER //
CREATE PROCEDURE GenerateDailyTrips()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i <= 30 DO
        INSERT INTO trips (departure_time, status, route_id, bus_id) VALUES
        (DATE_ADD(CURDATE(), INTERVAL i DAY) + INTERVAL 8 HOUR, 'SCHEDULED', 2, 1), -- HCM -> HN Sáng
        (DATE_ADD(CURDATE(), INTERVAL i DAY) + INTERVAL 20 HOUR, 'SCHEDULED', 2, 2), -- HCM -> HN Tối
        (DATE_ADD(CURDATE(), INTERVAL i DAY) + INTERVAL 7 HOUR, 'SCHEDULED', 5, 3); -- HCM -> DL
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;
CALL GenerateDailyTrips();
DROP PROCEDURE GenerateDailyTrips;

-- 6. TỰ ĐỘNG TẠO GHẾ CHO TẤT CẢ CHUYẾN XE (Đầy đủ số lượng ghế theo Bus)
DELIMITER //
CREATE PROCEDURE GenerateSeatsForTrips()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE t_id INT;
    DECLARE b_type VARCHAR(50);
    DECLARE t_seats INT;
    DECLARE i INT;
    DECLARE prefix CHAR(1);
    DECLARE seat_num VARCHAR(10);
    
    DECLARE cur CURSOR FOR SELECT t.id, b.bus_type, b.total_seats FROM trips t JOIN buses b ON t.bus_id = b.id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO t_id, b_type, t_seats;
        IF done THEN LEAVE read_loop; END IF;

        SET i = 1;
        WHILE i <= t_seats DO
            -- Quy tắc đặt tên ghế: A cho tầng 1 (hoặc xe ghế ngồi), B cho tầng 2
            IF b_type LIKE 'SLEEPER%' AND i > (t_seats / 2) THEN
                SET prefix = 'B';
                SET seat_num = CONCAT(prefix, LPAD(i - (t_seats / 2), 2, '0'));
            ELSE
                SET prefix = 'A';
                SET seat_num = CONCAT(prefix, LPAD(i, 2, '0'));
            END IF;

            INSERT INTO seats (seat_number, floor, status, trip_id, price, version) 
            VALUES (seat_num, IF(prefix='B', 2, 1), 'AVAILABLE', t_id, 0, 0);
            
            SET i = i + 1;
        END WHILE;
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

CALL GenerateSeatsForTrips();
DROP PROCEDURE GenerateSeatsForTrips;

-- 7. Cập nhật giá ghế dựa trên trip và vị trí
UPDATE seats s JOIN trips t ON s.trip_id = t.id 
SET s.price = (CASE 
    WHEN t.route_id IN (1, 2) THEN 850000 
    WHEN t.route_id IN (3, 4) THEN 450000 
    ELSE 250000 END) 
    + (CASE WHEN s.floor = 2 THEN 50000 ELSE 0 END);

-- 8. Thêm vé mẫu
INSERT INTO tickets (ticket_code, passenger_name, passenger_phone, passenger_email, total_price, status, booked_at, seat_id, user_id)
SELECT 'TK-RESET-001', 'Nguyễn Văn Test', '0912345678', 'test@gmail.com', s.price, 'PAID', NOW(), s.id, 3
FROM seats s JOIN trips t ON s.trip_id = t.id WHERE t.id = 1 AND s.seat_number = 'A01' LIMIT 1;

UPDATE seats s SET status = 'BOOKED' WHERE id IN (SELECT seat_id FROM tickets);
