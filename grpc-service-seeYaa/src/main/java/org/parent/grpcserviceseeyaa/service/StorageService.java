package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Files;
import com.seeYaa.proto.email.service.storage.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.mapper.FilesMapper;
import org.parent.grpcserviceseeyaa.repository.FilesRepository;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.parent.grpcserviceseeyaa.security.rolechecker.Authorize;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class StorageService extends StorageServiceGrpc.StorageServiceImplBase {
    private final FilesRepository filesRepository;
    private final LetterRepository letterRepository;


    @Override
    @Authorize("hasRole('ROLE_USER')")
    public void uploadFile(UploadFileRequest request, StreamObserver<Empty> responseObserver) {
        final var letter = letterRepository.findById(request.getLetterId())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Letter not found")
                        .asRuntimeException());

        filesRepository.save(org.parent.grpcserviceseeyaa.entity.Files.builder()
                .name(request.getName())
                .type(request.getType())
                .size(request.getSize())
                .data(request.getData().toByteArray())
                .letter(letter)
                .build());

        responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional
    public void downloadFile(FileIdRequest request, StreamObserver<DownloadFileResponse> responseObserver) {
        org.parent.grpcserviceseeyaa.entity.Files file = filesRepository.findById(request.getFileId())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("File not found id=" + request.getFileId())
                        .asRuntimeException());

        DownloadFileResponse out = DownloadFileResponse.newBuilder()
                .setData(ByteString.copyFrom(file.getData()))
                .build();

        responseObserver.onNext(out);
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional
    public void getFilesByLetterId(LetterIdRequest request, StreamObserver<FilesList> responseObserver) {
        final var allByLetterId = filesRepository.findAllByLetterId(request.getLetterId())
                .stream()
                .map(FilesMapper.INSTANCE::toFilesProto)
                .toList();

        responseObserver.onNext(FilesList.newBuilder().addAllFiles(allByLetterId).build());
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional
    public void getFileMetadataByLetterId(LetterIdRequest request, StreamObserver<FileMetadataList> responseObserver) {
        final var rows = filesRepository.findAllByLetterId(request.getLetterId())
                .stream()
                .map(FilesMapper.INSTANCE::toMetaProto)
                .toList();

        responseObserver.onNext(FileMetadataList.newBuilder()
                .addAllMetadata(rows)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    @Authorize("hasRole('ROLE_USER')")
    @Transactional
    public void getFileById(FileIdRequest request, StreamObserver<Files> responseObserver) {
        final var entity = filesRepository.findById(request.getFileId())
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("File not found id=" + request.getFileId())
                        .asRuntimeException());

        responseObserver.onNext(FilesMapper.INSTANCE.toFilesProto(entity));
        responseObserver.onCompleted();
    }
}
