Vai trò: Hãy đóng vai trò là một Senior Technical Lead kiêm QA Engineer.

Nhiệm vụ: Tôi đang phát triển một hệ thống đặt vé xe khách. Dưới đây là danh sách các tính năng cốt lõi (CORE) và yêu cầu tối ưu (Hướng 4) của dự án. Hãy đối chiếu, rà soát toàn bộ phần mã nguồn (hoặc tài liệu thiết kế) mà tôi cung cấp ở cuối để đánh giá mức độ hoàn thiện của hệ thống.

Tiêu chí đánh giá chi tiết (Checklist):

Bảo mật & Phân quyền (CORE-01, CORE-02, CORE-03):

Mật khẩu lưu trữ trong DB đã được băm bằng thuật toán BCrypt hoặc PBKDF2 chưa?

Cơ chế kiểm soát truy cập (RBAC) có được cấu hình chặn đúng endpoint không? (Hành khách không được vào Staff/Admin; Staff không được vào trang cấu hình của Admin).

Các chức năng quản lý Profile cá nhân cho từng loại người dùng (Passenger, Staff, Admin) đã có đủ luồng xử lý chưa?

Quản lý Dữ liệu & Danh mục (CORE-04):

Đã có đầy đủ API CRUD cho danh mục Xe (Bus) dành cho Admin chưa?

Các danh mục cố định như Tỉnh thành, Tuyến đường có cơ chế seed data sẵn vào DB lúc khởi tạo không?

Xử lý Đồng thời & Toàn vẹn Dữ liệu (CORE-05, CORE-06):

Khi load sơ đồ ghế, trạng thái ghế (PENDING/BOOKED) đã được disable chính xác chưa? Cơ chế giữ chỗ tạm thời và chống xung đột (Race Condition/Locking) được xử lý ra sao?

Quá trình đặt vé có được đặt trong một Transaction đồng nhất không? Việc tạo Ticket (PENDING) và cập nhật trạng thái Seat (PENDING) có tự động rollback nếu xảy ra lỗi (Exception) không?

Quản lý Vòng đời Vé (CORE-08, CORE-09):

Chức năng duyệt vé của Staff (chuyển PENDING → PAID) đã hoạt động đúng logic chưa?

Cơ chế xử lý vé quá hạn (hủy vé tự động, giải phóng ghế về AVAILABLE) được cấu hình như thế nào (Cronjob/Scheduler)?

Logic hủy vé chủ động của hành khách có kiểm tra đúng điều kiện thời gian (trước 12h khởi hành) và cập nhật đồng thời vé (CANCELLED) + ghế (AVAILABLE) không?

Truy vấn Phức tạp & Tối ưu hóa (CORE-07, Hướng 4):

API tra cứu vé (nhập Mã vé + SĐT) có sử dụng câu lệnh JOIN hợp lý để lấy toàn bộ thông tin liên kết (Tuyến, Xe, Ghế, Giờ đi, Trạng thái) nhằm tránh lỗi N+1 Query không?

Phần báo cáo thống kê (Dashboard doanh thu, Top chuyến xe) có được xử lý triệt để ở tầng Database (sử dụng GROUP BY, SUM...) không? Tuyệt đối không được kéo toàn bộ dữ liệu lên tầng Backend và dùng vòng lặp (for/while) để tính toán.