package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    Answer toAnswerProto(org.parent.grpcserviceseeyaa.entity.Answer answer);
}
