package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.service.letter.LetterWithAnswers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LetterMapper {
    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    @Mapping(target = "createdAt", expression = "java(TimeProtoUtil.toProto(entity.getCreatedAt()))")
    @Mapping(target = "deleteTime", expression = "java(TimeProtoUtil.toProto(entity.getDeleteTime()))")
    Letter toLetterProto(org.parent.grpcserviceseeyaa.entity.Letter letter);

    LetterWithAnswers toLetterWithAnswersProto(org.parent.grpcserviceseeyaa.entity.Letter letter);
}
