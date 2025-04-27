package dev.shaukat.RagApplicationService.file;

import dev.shaukat.RagApplicationService.file.dtos.FileResponseDto;
import dev.shaukat.RagApplicationService.file.dtos.KnowledgeBaseFileRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    private final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<List<FileResponseDto>> uploadFiles(@RequestPart("files") MultipartFile[] files, @RequestParam("username") String username){

        LOGGER.info("Upload files request from {} received with files.length(): {}",username ,files.length);

        List<FileResponseDto> result = fileService.uploadFiles(files, username).stream()
               .map(fileModel -> new FileResponseDto(
                       fileModel.getId(),
                       fileModel.getFilename(),
                       fileModel.getFiletype(),
                       fileModel.getStatus()
                       )
               ).toList();

       return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FileResponseDto>> getAllUploadedFiles(){
        LOGGER.info("Get all files request received.");

        List<FileResponseDto> result = fileService.getAllUploadedFiles()
                .stream()
                .map(fileModel -> new FileResponseDto(
                        fileModel.getId(),
                        fileModel.getFilename(),
                        fileModel.getFiletype(),
                        fileModel.getStatus()
                        )
                ).toList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add-to-knowledge-base")
    public ResponseEntity<List<FileResponseDto>> addFilesToKnowledgeBase(@RequestBody KnowledgeBaseFileRequestDto payload, @RequestParam("username") String username){

        List<FileResponseDto> result = fileService.addFilesToKnowledgeBase(
                    payload.getFilenames(),
                    payload.getCollectionName(),username)
                        .stream()
                        .map(fileModel -> new FileResponseDto(fileModel.getId(),fileModel.getFilename(), fileModel.getFiletype(), fileModel.getStatus()))
                        .toList();
        return ResponseEntity.ok(result);
    }
}
