package org.parent.configuration.file.size;

import lombok.Getter;

@Getter
public enum FileSize {
    TEN_MB(10L * 1024L * 1024L),
    THIRTY_MB(30L * 1024L * 1024L),
    FIFTY_MB(50L * 1024L * 1024L),
    ONE_HUNDRED_MB(100L * 1024L * 1024L),
    TWO_HUNDRED_MB(200L * 1024L * 1024L),
    THREE_HUNDRED_MB(300L * 1024L * 1024L),
    FOUR_HUNDRED_MB(400L * 1024L * 1024L),
    FIVE_HUNDRED_MB(500L * 1024L * 1024L),
    SIX_HUNDRED_MB(600L * 1024L * 1024L),
    SEVEN_HUNDRED_MB(700L * 1024L * 1024L),
    EIGHT_HUNDRED_MB(800L * 1024L * 1024L),
    NINE_HUNDRED_MB(900L * 1024L * 1024L),
    ONE_GB(1000L * 1024L * 1024L),
    TWO_GB(2000L * 1024L * 1024L),
    THREE_GB(3000L * 1024L * 1024L),
    FOUR_GB(4000L * 1024L * 1024L),
    FIVE_GB(5000L * 1024L * 1024L),
    SIX_GB(6000L * 1024L * 1024L),
    SEVEN_GB(7000L * 1024L * 1024L),
    EIGHT_GB(8000L * 1024L * 1024),
    NINE_GB(9000L * 1024L * 1024L);

    private final Long size;

    FileSize(Long size) {
        this.size = size;
    }
}
