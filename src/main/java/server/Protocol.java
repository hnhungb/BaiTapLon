package server;
//các lệnh giao tiếp giữa client và server
public class Protocol {
    // Port server sử dụng
    public static final int PORT = 9090;
    public static final String REGISTER = "REGISTER";        // Đăng ký tài khoản
    public static final String LOGIN = "LOGIN";          // Đăng nhập
    //Auction
    public static final String LIST_AUCTIONS = "LIST_AUCTIONS";       // Lấy danh sách đấu giá
    public static final String GET_AUCTION = "GET_AUCTION";          // Lấy thông tin 1 auction
    public static final String PLACE_BID = "PLACE_BID";              // Đặt giá
    public static final String CREATE_AUCTION = "CREATE_AUCTION";    // Tạo auction mới
    public static final String CLOSE_AUCTION = "CLOSE_AUCTION";       // Đóng auction
    public static final String CANCEL_AUCTION = "CANCEL_AUCTION";       // Huỷ auction
    //Auto Bid
    public static final String AUTO_BID = "AUTO_BID";         // Đấu giá tự động
    //Server push
    public static final String BID_UPDATE = "BID_UPDATE";         // Server gửi khi có bid mới
}