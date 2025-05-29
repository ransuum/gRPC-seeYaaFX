package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.service.letter.LetterWithAnswers;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface LetterMapper {
    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getCreatedAt()))")
    @Mapping(target = "deleteTime", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getDeleteTime()))")

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Letter toLetterProto(org.parent.grpcserviceseeyaa.entity.Letter letter);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    LetterWithAnswers toLetterWithAnswersProto(org.parent.grpcserviceseeyaa.entity.Letter letter);
}
