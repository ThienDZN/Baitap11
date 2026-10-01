# baitap11 — Giỏ hàng, thanh toán và lịch sử đơn hàng

Project này là bản sao độc lập từ `DeThiQuaTrinh03-Video`, được mở rộng cho vai trò **User** theo đề bài:

1. Giỏ hàng: thêm, xóa và cập nhật số lượng.
2. Thanh toán: nhập thông tin nhận hàng, lưu đơn và chi tiết đơn.
3. Lịch sử đơn hàng: lọc theo 8 trạng thái yêu cầu.

## Công nghệ

- Java 17, Maven WAR, Spring Boot 4 / Servlet / JSP / JSTL
- JPA (Hibernate), MySQL, Bootstrap và SiteMesh

## Luồng chức năng

- `GET /product`: xem catalog đang hoạt động, thêm sản phẩm vào giỏ.
- `GET /cart`: xem giỏ; `POST /cart/add`, `/cart/update`, `/cart/remove`: thay đổi giỏ.
- `GET /checkout`, `POST /checkout`: xác nhận thông tin giao nhận và tạo đơn.
- `GET /orders?status=...`: lịch sử đơn hàng của chính user đăng nhập.

Mọi thay đổi giỏ và checkout đều yêu cầu session đăng nhập role `USER`, HTTP `POST` và CSRF token. Khi checkout, hệ thống đọc lại giá/tồn kho trong database, khóa sản phẩm và ghi đơn cùng chi tiết đơn trong một transaction; giỏ chỉ bị xóa sau khi transaction thành công.

## Cơ sở dữ liệu độc lập

Project dùng database `baitap11`; script đầy đủ ở `src/main/resources/database.sql`.

```sql
CREATE DATABASE baitap11;
```

Sau đó chạy toàn bộ `database.sql`. URL database có thể ghi đè qua biến môi trường `APP_DB_URL`; username/password vẫn lấy từ cấu hình môi trường hiện có, không đưa secrets vào repository.

Hai bảng mới:

- `customer_orders`: người mua, giao nhận, thanh toán, tổng tiền, trạng thái.
- `order_items`: snapshot sản phẩm, đơn giá, số lượng, thành tiền.

## Trạng thái đơn hàng và cách kiểm tra đề bài

Các mã lưu trong `customer_orders.Status`:

| Mã DB | Hiển thị |
| --- | --- |
| `NEW` | Đơn hàng mới |
| `CONFIRMED` | Đã xác nhận |
| `PREPARING` | Chuẩn bị hàng |
| `SHIPPING` | Vận chuyển |
| `DELIVERING` | Giao hàng |
| `DELIVERED` | Đã giao |
| `CANCELLED` | Đơn hàng hủy |
| `RETURNED` | Đơn hàng hoàn |

Tạo một đơn bằng UI, sau đó đổi trạng thái trực tiếp trong MySQL để quan sát giao diện cập nhật:

```sql
USE baitap11;
SELECT OrderId, Status, TotalAmount, CreatedAt FROM customer_orders ORDER BY OrderId DESC;
UPDATE customer_orders SET Status = 'CONFIRMED' WHERE OrderId = 1;
```

Mở lại `/orders` hoặc chọn bộ lọc tương ứng. Chỉ dùng một trong tám mã ở bảng trên.

## Build và test

```bash
mvn test
mvn package
```

Test kiểm tra thêm/cập nhật/xóa giỏ, chặn vượt tồn kho, validation checkout, đủ 8 trạng thái và việc service không gọi persistence khi dữ liệu checkout sai. Integration test H2 riêng biệt còn thực thi transaction JPA: tạo đơn, giảm tồn kho, rollback khi tồn kho thay đổi, cập nhật `Status` trực tiếp trong database và lọc lịch sử theo trạng thái. H2 chỉ là dependency `test` để kiểm thử tự động, không có trong WAR production.

## Giới hạn có chủ đích

Hai lựa chọn thanh toán (`COD`, `BANK_TRANSFER`) chỉ được ghi nhận trong đơn hàng; project không tích hợp cổng thanh toán bên thứ ba hoặc xử lý tiền thật.
