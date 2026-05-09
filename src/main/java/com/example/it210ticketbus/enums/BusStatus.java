// === FILE: com/example/it210ticketbus/enums/BusStatus.java ===
package com.example.it210ticketbus.enums;

public enum BusStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    MAINTENANCE("Bảo trì");

    private final String displayName;

    BusStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
