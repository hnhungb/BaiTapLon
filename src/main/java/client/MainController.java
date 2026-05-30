package client;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import server.Protocol;

public class MainController {

    // Tab 1: Danh sách phiên đấu giá
    @FXML private TableView<AuctionRow>          table;
    @FXML private TableColumn<AuctionRow,String> colTen;
    @FXML private TableColumn<AuctionRow,String> colLoai;
    @FXML private TableColumn<AuctionRow,Double> colGia;
    @FXML private TableColumn<AuctionRow,String> colTrangThai;
    @FXML private TableColumn<AuctionRow,String> colKetThuc;
    @FXML private TableColumn<AuctionRow,String> colId;

    // Tab 2: Đặt giá
    @FXML private Label          lblTenSanPham;
    @FXML private Label          lblGiaHienTai;
    @FXML private Label          lblNguoiDanDau;
    @FXML private Label          lblTrangThai;
    @FXML private Label          lblKetThuc;
    @FXML private TextField      tfSoTien;
    @FXML private TextField      tfMaxBid;
    @FXML private TextField      tfBuocGia;
    @FXML private Label          lblBidStatus;
    @FXML private ListView<String> listHistory;

    // Tab 3: Đăng bán (chỉ hiện với Seller/Admin)
    @FXML private TextField      tfItemName;
    @FXML private ComboBox<String> cbItemType;
    @FXML private TextArea       taItemDesc;
    @FXML private TextField      tfStartPrice;
    @FXML private TextField      tfDuration;
    @FXML private Label          lblSellStatus;

    @FXML private TabPane tabPane;
    @FXML private Tab     tabBid;
    @FXML private Tab     tabSell;

    private ServerConnection             connection;
    private String                       currentUsername;
    private String                       currentRole;
    private String                       selectedAuctionId;
    private ObservableList<AuctionRow>   rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gắn cột bảng với thuộc tính của AuctionRow
        if (colTen       != null) colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        if (colLoai      != null) colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        if (colGia       != null) colGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        if (colKetThuc   != null) colKetThuc.setCellValueFactory(new PropertyValueFactory<>("ketThuc"));
        if (colId        != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        if (table != null) {
            table.setItems(rows);
            // Double-click vào một hàng để mở phiên đấu giá
            table.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    AuctionRow row = table.getSelectionModel().getSelectedItem();
                    if (row != null) openAuction(row.getId());
                }
            });
        }

        if (cbItemType != null) {
            cbItemType.getItems().addAll("ELECTRONICS", "ART", "VEHICLE");
            cbItemType.setValue("ELECTRONICS");
        }
    }

    public void setConnection(ServerConnection conn) { this.connection = conn; }

    public void setCurrentUser(String username, String role) {
        this.currentUsername = username;
        this.currentRole     = role;
        // Ẩn tab Đăng bán nếu không phải Seller hoặc Admin
        if (tabSell != null && !role.equals("SELLER") && !role.equals("ADMIN")) {
            tabPane.getTabs().remove(tabSell);
        }
    }

    // ── Tab 1: Danh sách phiên ────────────────────────────────────────

    public void loadDanhSachPhien() {
        new Thread(() -> {
            JsonObject req  = ServerConnection.req(Protocol.LIST_AUCTIONS);
            JsonObject resp = connection.send(req);

            Platform.runLater(() -> {
                rows.clear();
                if (resp.get("ok").getAsBoolean()) {
                    for (JsonElement el : resp.getAsJsonArray("data")) {
                        JsonObject a = el.getAsJsonObject();
                        rows.add(new AuctionRow(
                                a.get("id").getAsString(),
                                a.get("itemName").getAsString(),
                                a.get("itemType").getAsString(),
                                a.get("currentPrice").getAsDouble(),
                                a.get("status").getAsString(),
                                a.get("endTime").getAsString()
                        ));
                    }
                }
            });
        }).start();
    }

    @FXML private void handleRefresh() { loadDanhSachPhien(); }

    // ── Tab 2: Đặt giá ────────────────────────────────────────────────

    private void openAuction(String auctionId) {
        selectedAuctionId = auctionId;

        new Thread(() -> {
            JsonObject req = ServerConnection.req(Protocol.GET_AUCTION);
            req.addProperty("auctionId", auctionId);
            JsonObject resp = connection.send(req);

            Platform.runLater(() -> {
                if (resp.get("ok").getAsBoolean()) {
                    hienThiPhien(resp.getAsJsonObject("data"));
                    if (tabBid != null) tabPane.getSelectionModel().select(tabBid);
                    batDauLangNghe(); // bắt đầu nhận push update từ server
                }
            });
        }).start();
    }

    private void hienThiPhien(JsonObject a) {
        if (lblTenSanPham != null)
            lblTenSanPham.setText(a.get("itemName").getAsString()
                    + " [" + a.get("itemType").getAsString() + "]");
        capNhatGia(a);

        // Hiển thị lịch sử đặt giá
        if (listHistory != null) {
            listHistory.getItems().clear();
            for (JsonElement el : a.getAsJsonArray("history")) {
                JsonObject b = el.getAsJsonObject();
                listHistory.getItems().add(formatBidLine(b));
            }
        }
    }

    private void capNhatGia(JsonObject a) {
        if (lblGiaHienTai  != null) lblGiaHienTai.setText(String.format("%.0f VNĐ", a.get("currentPrice").getAsDouble()));
        if (lblNguoiDanDau != null) lblNguoiDanDau.setText("Dẫn đầu: " + a.get("leader").getAsString());
        if (lblTrangThai   != null) lblTrangThai.setText("Trạng thái: " + a.get("status").getAsString());
        if (lblKetThuc     != null) lblKetThuc.setText("Kết thúc: " + a.get("endTime").getAsString());
    }

    @FXML
    private void handleDatGia() {
        if (selectedAuctionId == null) {
            showBidStatus("Chọn một phiên trước (double-click)!", true);
            return;
        }
        String soTienStr = tfSoTien.getText().trim();
        if (soTienStr.isEmpty()) { showBidStatus("Nhập số tiền muốn đặt!", true); return; }

        double soTien;
        try {
            soTien = Double.parseDouble(soTienStr);
        } catch (NumberFormatException e) {
            showBidStatus("Số tiền không hợp lệ!", true);
            return;
        }

        new Thread(() -> {
            JsonObject req = ServerConnection.req(Protocol.PLACE_BID);
            req.addProperty("auctionId", selectedAuctionId);
            req.addProperty("amount",    soTien);
            JsonObject resp = connection.send(req);

            Platform.runLater(() -> {
                if (resp.get("ok").getAsBoolean()) {
                    JsonObject a = resp.getAsJsonObject("data");
                    capNhatGia(a);
                    // Thêm bid mới nhất vào danh sách history
                    JsonArray history = a.getAsJsonArray("history");
                    if (history.size() > 0) {
                        JsonObject lastBid = history.get(history.size() - 1).getAsJsonObject();
                        if (listHistory != null) listHistory.getItems().add(formatBidLine(lastBid));
                    }
                    showBidStatus("Đặt giá thành công!", false);
                    loadDanhSachPhien();
                } else {
                    showBidStatus(resp.get("error").getAsString(), true);
                }
            });
        }).start();
    }

    @FXML
    private void handleAutoBid() {
        if (selectedAuctionId == null) {
            showBidStatus("Chọn một phiên trước!", true);
            return;
        }
        try {
            double maxBid  = Double.parseDouble(tfMaxBid.getText().trim());
            double buocGia = Double.parseDouble(tfBuocGia.getText().trim());

            new Thread(() -> {
                JsonObject req = ServerConnection.req(Protocol.AUTO_BID);
                req.addProperty("auctionId", selectedAuctionId);
                req.addProperty("maxBid",    maxBid);
                req.addProperty("step",      buocGia);
                JsonObject resp = connection.send(req);

                Platform.runLater(() -> {
                    if (resp.get("ok").getAsBoolean()) {
                        showBidStatus("Đăng ký auto-bid thành công! (tối đa: "
                                + maxBid + ", bước: " + buocGia + ")", false);
                    } else {
                        showBidStatus(resp.get("error").getAsString(), true);
                    }
                });
            }).start();

        } catch (NumberFormatException e) {
            showBidStatus("Nhập đúng định dạng số!", true);
        }
    }

    // Lắng nghe push update từ server (chạy trên background thread)
    private void batDauLangNghe() {
        new Thread(() -> {
            try {
                while (connection.isConnected()) {
                    String line = connection.readLine();
                    if (line == null) break;

                    JsonObject msg = JsonParser.parseString(line).getAsJsonObject();
                    if (!msg.has("action")) continue;

                    if (msg.get("action").getAsString().equals(Protocol.BID_UPDATE)) {
                        double soTienMoi = msg.get("amount").getAsDouble();
                        String bidder    = msg.get("bidder").getAsString();
                        String time      = msg.get("time").getAsString();
                        boolean isAuto   = msg.get("isAuto").getAsBoolean();

                        Platform.runLater(() -> {
                            // Cập nhật giá và người dẫn đầu ngay lập tức
                            if (lblGiaHienTai  != null) lblGiaHienTai.setText(String.format("%.0f VNĐ", soTienMoi));
                            if (lblNguoiDanDau != null) lblNguoiDanDau.setText("Dẫn đầu: " + bidder);

                            // Thêm vào lịch sử
                            if (listHistory != null) {
                                String dong = String.format("%-12s  %.0f VNĐ  %s%s",
                                        bidder, soTienMoi, time, isAuto ? "  [auto]" : "");
                                listHistory.getItems().add(dong);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // Client đã ngắt kết nối - dừng lắng nghe
            }
        }).start();
    }

    // ── Tab 3: Đăng bán ───────────────────────────────────────────────

    @FXML
    private void handleDangBan() {
        String ten    = tfItemName.getText().trim();
        String loai   = cbItemType.getValue();
        String moTa   = taItemDesc != null ? taItemDesc.getText().trim() : "";
        String giaStr = tfStartPrice.getText().trim();
        String tgStr  = tfDuration.getText().trim();

        if (ten.isEmpty() || giaStr.isEmpty()) {
            showSellStatus("Vui lòng nhập tên và giá khởi điểm!", true);
            return;
        }

        try {
            double gia      = Double.parseDouble(giaStr);
            int    thoiGian = tgStr.isEmpty() ? 60 : Integer.parseInt(tgStr);

            new Thread(() -> {
                JsonObject req = ServerConnection.req(Protocol.CREATE_AUCTION);
                req.addProperty("itemType",   loai);
                req.addProperty("itemName",   ten);
                req.addProperty("itemDesc",   moTa);
                req.addProperty("startPrice", gia);
                req.addProperty("duration",   thoiGian);
                JsonObject resp = connection.send(req);

                Platform.runLater(() -> {
                    if (resp.get("ok").getAsBoolean()) {
                        showSellStatus("Đăng bán thành công!", false);
                        loadDanhSachPhien();
                    } else {
                        showSellStatus(resp.get("error").getAsString(), true);
                    }
                });
            }).start();

        } catch (NumberFormatException e) {
            showSellStatus("Giá hoặc thời gian không hợp lệ!", true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    // Format 1 dòng lịch sử bid cho dễ đọc
    private String formatBidLine(JsonObject b) {
        return String.format("%-12s  %.0f VNĐ  %s%s",
                b.get("bidder").getAsString(),
                b.get("amount").getAsDouble(),
                b.get("time").getAsString(),
                b.get("isAuto").getAsBoolean() ? "  [auto]" : "");
    }

    private void showBidStatus(String msg, boolean isError) {
        if (lblBidStatus == null) return;
        lblBidStatus.setText(msg);
        lblBidStatus.setStyle(isError ? "-fx-text-fill:red;" : "-fx-text-fill:green;");
    }

    private void showSellStatus(String msg, boolean isError) {
        if (lblSellStatus == null) return;
        lblSellStatus.setText(msg);
        lblSellStatus.setStyle(isError ? "-fx-text-fill:red;" : "-fx-text-fill:green;");
    }

    // ── AuctionRow - dùng cho TableView ──────────────────────────────

    public static class AuctionRow {
        private String id, ten, loai, trangThai, ketThuc;
        private double gia;

        public AuctionRow(String id, String ten, String loai,
                          double gia, String trangThai, String ketThuc) {
            this.id = id; this.ten = ten; this.loai = loai;
            this.gia = gia; this.trangThai = trangThai; this.ketThuc = ketThuc;
        }

        public String getId()        { return id; }
        public String getTen()       { return ten; }
        public String getLoai()      { return loai; }
        public double getGia()       { return gia; }
        public String getTrangThai() { return trangThai; }
        public String getKetThuc()   { return ketThuc; }
    }
}