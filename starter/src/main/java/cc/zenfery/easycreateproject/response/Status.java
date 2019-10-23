package cc.zenfery.easycreateproject.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    SUCCESS(0),
    ERROR(1);

    private int stat = 0;

    private Status(int stat) {
        this.stat = stat;
    }

    @JsonValue
    public int getStat() {
        return this.stat;
    }


    static {

    }
}
