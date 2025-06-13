package org.parent.util;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateConfigurer {
    private static final DateTimeFormatter formatterToday = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter formatterYesterday = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getDate(Timestamp timestamp) {
        if (timestamp == null) return null;

        final LocalDateTime localDateTime = TimeProtoUtil.fromProto(timestamp);

        if (localDateTime.getMonth().equals(LocalDateTime.now().getMonth())
                &&  localDateTime.getYear() == LocalDateTime.now().getYear()
        && localDateTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) return localDateTime.format(formatterToday);

        return localDateTime.format(formatterYesterday);
    }
}
