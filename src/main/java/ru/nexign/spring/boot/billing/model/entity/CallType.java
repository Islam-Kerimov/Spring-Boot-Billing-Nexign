package ru.nexign.spring.boot.billing.model.entity;

public enum CallType {
    OUTGOING("01"),
    INCOMING("02");

    private final String index;

    CallType(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public static CallType fromString(String index) {
        for (CallType type : CallType.values()) {
            if (type.getIndex().equals(index)) {
                return type;
            }
        }
        return null;
    }
}
