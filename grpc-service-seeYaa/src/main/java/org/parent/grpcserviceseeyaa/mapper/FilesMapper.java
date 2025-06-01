package org.parent.grpcserviceseeyaa.mapper;

import com.google.protobuf.ByteString;
import com.seeYaa.proto.email.Files;
import com.seeYaa.proto.email.service.storage.FileMetadata;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LetterMapper.class})
public interface FilesMapper {
    FilesMapper INSTANCE = Mappers.getMapper(FilesMapper.class);

    @Mapping(target = "data", qualifiedByName = "bytesToByteString")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Files toFilesProto(org.parent.grpcserviceseeyaa.entity.Files files);

    @Mapping(target = "data", qualifiedByName = "bytesToByteString")
    @Mapping(target = "size", source = "size")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    FileMetadata toMetaProto(org.parent.grpcserviceseeyaa.entity.Files src);

    @Named("bytesToByteString")
    default ByteString bytesToByteString(byte[] data) {
        return data == null ? ByteString.EMPTY : ByteString.copyFrom(data);
    }
}
