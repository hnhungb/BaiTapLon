package model.user;

// Người bán: đăng sản phẩm lên hệ thống
public class Seller extends User {

    private int soSanPhamDaBan;

    public Seller(String id, String username, String password, String email) {
        super(id, username, password, email);
        this.soSanPhamDaBan = 0;
    }

    public Seller(String id, String username) {
        this(id, username, "123456", username.toLowerCase() + "@gmail.com");
    }

    @Override
    public String getRole() { return "SELLER"; }

    public int getSoSanPhamDaBan()    { return soSanPhamDaBan; }
    public void tangSoSanPhamDaBan()  { soSanPhamDaBan++; }

    @Override
    public String toString() {
        return super.toString() + " | Đã bán: " + soSanPhamDaBan;
    }
}
