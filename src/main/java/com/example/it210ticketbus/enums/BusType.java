// === FILE: com/example/it210ticketbus/enums/BusType.java ===
package com.example.it210ticketbus.enums;

public enum BusType {
    SEATS_29("29 chỗ", 29),
    SEATS_45("45 chỗ", 45),
    SLEEPER_34("Giường nằm 34", 34),
    SLEEPER_40("Giường nằm 40", 40),
    SLEEPER_22("Giường nằm 22", 22),
    LIMOUSINE_9("Limousine 9 chỗ", 9);

    private final String displayName;
    private final int defaultSeats;

    BusType(String displayName, int defaultSeats) {
        this.displayName = displayName;
        this.defaultSeats = defaultSeats;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultSeats() {
        return defaultSeats;
    }
}
