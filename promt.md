Bạn là senior Java developer. Hãy implement CORE-06 cho hệ thống Bus Ticket Pro.

TECH STACK:
- Backend: Java Servlet + Service + DAO (3-layer)
- Frontend: HTML5 + CSS3 + JavaScript (Fetch API) — KHÔNG dùng JSP
- Database: MySQL, dùng JDBC Transaction (hoặc @Transactional nếu Spring)
- Server: Apache Tomcat

NHIỆM VỤ:
1. Tạo trang booking.html:
    - Hiển thị tóm tắt: tuyến, ngày đi, số ghế đã chọn (đọc từ sessionStorage)
    - Form nhập: Họ tên, SĐT, Email
    - Nút "Xác nhận đặt vé" → fetch POST /api/bookings (JSON body)

2. BookingServlet.java (POST /api/bookings):
    - Parse JSON: {tripId, seatId, passengerName, phone, email}
    - Gọi BookingService.createBooking(dto)
    - Trả về {ticketCode, message} nếu thành công
    - Trả về 409 nếu ghế đã bị đặt, 500 nếu lỗi khác

3. BookingService.java — ĐÂY LÀ TRỌNG TÂM:
    - Bắt đầu Transaction thủ công: conn.setAutoCommit(false)
    - B1: SELECT status FROM seats WHERE seat_id=? FOR UPDATE
    - B2: Nếu status != 'AVAILABLE' → throw SeatUnavailableException → rollback
    - B3: INSERT INTO tickets (ticket_code, trip_id, seat_id, passenger_name, phone, email, status, created_at) VALUES (UUID(), ?, ?, ?, ?, ?, 'PENDING', NOW())
    - B4: UPDATE seats SET status='PENDING', held_until=NULL WHERE seat_id=?
    - B5: conn.commit()
    - Bất kỳ bước nào lỗi: conn.rollback() → không tạo vé "mồ côi"

4. booking-confirm.html:
    - Nhận ticketCode từ response JSON
    - Hiển thị thông báo thành công: mã vé, hướng dẫn thanh toán tại quầy
    - Nút "Tra cứu vé của tôi" → redirect sang lookup.html

OUTPUT: booking.html, booking.js, BookingServlet.java, BookingService.java, BookingDAO.java, TicketDAO.java, SeatUnavailableException.java, SQL INSERT mẫu, SQL schema bảng tickets đầy đủ.