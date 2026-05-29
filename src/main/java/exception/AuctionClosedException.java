package exception;
//khi đặt giá mà phiên kết thúc
public class AuctionClosedException extends Exception {
    public AuctionClosedException(String msg) {
        super(msg);
    }
}
