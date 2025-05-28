package org.parent.grpcserviceseeyaa.mapper;

import com.seeYaa.proto.email.Files;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilesMapper {
    FilesMapper INSTANCE = Mappers.getMapper(FilesMapper.class);

    Files toFilesProto(org.parent.grpcserviceseeyaa.entity.Files files);

    @Mapping(target = "data", expression = "java(ByteString.copyFrom(src.getData()))")
    com.seeyaa.proto.email.service.storage.FileMetadata toMetaProto(org.parent.grpcserviceseeyaa.entity.Files src);
}
