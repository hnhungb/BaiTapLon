package model.user;

// Người tham gia đấu giá
public class Bidder extends User {

    private int soLanThang; // đếm số phiên đã thắng

    public Bidder(String id, String username, String password, String email) {
        super(id, username, password, email);
        this.soLanThang = 0;
    }

    public Bidder(String id, String username) {
        this(id, username, "123456", username.toLowerCase() + "@gmail.com");
    }

    @Override
    public String getRole() { return "BIDDER"; }

    public int getSoLanThang()    { return soLanThang; }
    public void tangSoLanThang()  { soLanThang++; }

    @Override
    public String toString() {
        return super.toString() + " | Số lần thắng: " + soLanThang;
    }
}
