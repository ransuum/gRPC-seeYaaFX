package org.parent.grpcserviceseeyaa.util.fieldvalidation;

import com.google.protobuf.Timestamp;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldUtil {
    public static boolean isValid(String parameter) {
        return StringUtils.isNotBlank(parameter);
    }

    public static String refractorDate(Timestamp timestamp) {
        final var date = TimeProtoUtil.fromProto(timestamp);
        if (date.getDayOfYear() == LocalDateTime.now().getDayOfYear()
                && date.getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
            return (date.getHour() < 10 ? "0" + date.getHour() : date.getHour()) + ":"
                    + (date.getMinute() < 10 ? "0" + date.getMinute() : date.getMinute());
        else return date.getDayOfMonth() + " " + Month.of(date.getMonthValue());
    }

    public static String refractorField(int width, String... val) {
        String format = String.format("%%-%ds", width);
        var result = Arrays.stream(val)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        return String.format(format, result);
    }
}
