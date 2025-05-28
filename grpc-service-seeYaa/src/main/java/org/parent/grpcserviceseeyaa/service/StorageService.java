package org.parent.grpcserviceseeyaa.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Files;
import com.seeyaa.proto.email.service.storage.DownloadFileResponse;
import com.seeyaa.proto.email.service.storage.FileIdRequest;
import com.seeyaa.proto.email.service.storage.FileMetadataList;
import com.seeyaa.proto.email.service.storage.FilesList;
import com.seeyaa.proto.email.service.storage.LetterIdRequest;
import com.seeyaa.proto.email.service.storage.StorageServiceGrpc;
import com.seeyaa.proto.email.service.storage.UploadFileRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.exception.NotFoundException;
import org.parent.grpcserviceseeyaa.mapper.FilesMapper;
import org.parent.grpcserviceseeyaa.repository.FilesRepository;
import org.parent.grpcserviceseeyaa.repository.LetterRepository;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_USER')")
public class StorageService extends StorageServiceGrpc.StorageServiceImplBase {
    private final FilesRepository filesRepository;
    private final LetterRepository letterRepository;


    @Override
    public void uploadFile(UploadFileRequest request, StreamObserver<Empty> responseObserver) {
        try {
            final var letter = letterRepository.findById(request.getLetterId())
                    .orElseThrow(() -> new NotFoundException("Letter not found with id: " + request.getLetterId()));

            filesRepository.save(org.parent.grpcserviceseeyaa.entity.Files.builder()
                    .name(request.getName())
                    .type(request.getType())
                    .size(request.getSize())
                    .data(request.getData().toByteArray())
                    .letter(letter)
                    .build());

            responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException sre) {
            responseObserver.onError(sre);
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to upload file")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    @Override
    public void downloadFile(FileIdRequest request, StreamObserver<DownloadFileResponse> responseObserver) {
        try {
            org.parent.grpcserviceseeyaa.entity.Files file = filesRepository.findById(request.getFileId())
                    .orElseThrow(() ->
                            Status.NOT_FOUND
                                    .withDescription("File not found id=" + request.getFileId())
                                    .asRuntimeException());

            DownloadFileResponse out = DownloadFileResponse.newBuilder()
                    .setData(ByteString.copyFrom(file.getData()))
                    .build();

            responseObserver.onNext(out);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException sre) {
            responseObserver.onError(sre);
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Download failed")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    @Override
    public void getFilesByLetterId(LetterIdRequest request, StreamObserver<FilesList> responseObserver) {
        final var allByLetterId = filesRepository.findAllByLetterId(request.getLetterId())
                .stream()
                .map(FilesMapper.INSTANCE::toFilesProto)
                .toList();

        responseObserver.onNext(FilesList.newBuilder().addAllFiles(allByLetterId).build());
        responseObserver.onCompleted();
    }

    @Override
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
    public void getFileById(FileIdRequest request, StreamObserver<Files> responseObserver) {
        try {
            final var entity = filesRepository.findById(request.getFileId())
                    .orElseThrow(() ->
                            Status.NOT_FOUND
                                    .withDescription("File not found id=" + request.getFileId())
                                    .asRuntimeException());

            responseObserver.onNext(FilesMapper.INSTANCE.toFilesProto(entity));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException sre) {
            responseObserver.onError(sre);
        }
    }
}
