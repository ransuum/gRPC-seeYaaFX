package org.parent.grpcserviceseeyaa.util.time;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeProtoUtil {
    public static Timestamp toProto(LocalDateTime ldt) {
        if (ldt == null) return null;
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }

    public static LocalDateTime fromProto(Timestamp ts) {
        if (ts == null) return null;
        return LocalDateTime.ofEpochSecond(ts.getSeconds(), ts.getNanos(), ZoneOffset.UTC);
    }
}
