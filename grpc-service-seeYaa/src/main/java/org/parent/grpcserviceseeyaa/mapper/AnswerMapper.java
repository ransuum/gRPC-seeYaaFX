package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Answer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {LetterMapper.class})
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(target = "createdAt", expression = "java(org.parent.grpcserviceseeyaa.util.time.TimeProtoUtil.toProto(answer.getCreatedAt()))")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Answer toAnswerProto(org.parent.grpcserviceseeyaa.entity.Answer answer);

    List<Answer> toAnswerList(List<org.parent.grpcserviceseeyaa.entity.Answer> answerList);
}
