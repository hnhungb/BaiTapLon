package model.auction;

// Trạng thái phiên đấu giá
public enum AuctionStatus {
    OPEN,      // vừa tạo, chưa bắt đầu
    RUNNING,   // đang chạy
    FINISHED,  // hết giờ / đã đóng
    PAID,      // đã thanh toán
    CANCELED   // đã hủy
}
