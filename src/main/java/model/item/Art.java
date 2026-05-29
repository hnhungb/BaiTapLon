package model.item;

// Tác phẩm nghệ thuật
public class Art extends Item {

    private String artist;  // tác giả
    private int    year;    // năm sáng tác

    public Art(String id, String name, String description,
               double startPrice, String artist, int year) {
        super(id, name, description, startPrice);
        this.artist = artist;
        this.year   = year;
    }

    public Art(String id, String name, String description, double startPrice) {
        this(id, name, description, startPrice, "Không rõ", 2024);
    }

    @Override
    public String getType() { return "Art"; }

    public String getArtist() { return artist; }
    public int    getYear()   { return year; }

    @Override
    public String toString() {
        return super.toString() + " | Tác giả: " + artist + " | Năm: " + year;
    }
}
