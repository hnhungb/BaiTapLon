package exception;
//lỗi đặt giá kh hợp lệ(thấp hơn)
public class InvalidBidException extends Exception {
    public InvalidBidException(String msg) {
        super(msg);
    }
}
