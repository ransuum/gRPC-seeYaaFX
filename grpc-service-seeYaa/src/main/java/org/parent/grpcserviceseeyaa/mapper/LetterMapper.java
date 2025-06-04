package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Letter;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {UserMapper.class, AnswerMapper.class})
public interface LetterMapper {
    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getCreatedAt()))")
    @Mapping(target = "deleteTime", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(letter.getDeleteTime()))")
    @Mapping(target = "answersList", ignore = true)
    @Mapping(target = "watched", source = "watched")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Letter toLetterProto(org.parent.grpcserviceseeyaa.entity.Letter letter);
}
