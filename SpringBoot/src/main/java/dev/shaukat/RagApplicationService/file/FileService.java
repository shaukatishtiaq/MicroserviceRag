package dev.shaukat.RagApplicationService.file;

import dev.shaukat.RagApplicationService.exceptions.CustomException;
import dev.shaukat.RagApplicationService.file.models.FileModel;
import dev.shaukat.RagApplicationService.messaging.MessagingPublisher;
import dev.shaukat.RagApplicationService.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class FileService {
    @Value("${messaging.exchange.name}")
    private String exchangeName;

    @Value("${messaging.request.queue}")
    private String requestQueue;

    @Value("${messaging.request.queue.routing.key}")
    private String requestQueueRoutingKey;

    private final StorageService storageService;
    private final FileRepository fileRepository;
    private final MessagingPublisher messagingPublisher;

    private final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    public FileService(StorageService storageService, FileRepository fileRepository, MessagingPublisher messagingPublisher) {
        this.storageService = storageService;
        this.fileRepository = fileRepository;
        this.messagingPublisher = messagingPublisher;
    }

    public List<FileModel> uploadFiles(MultipartFile[] files, String username) {
        List<FileModel> result = new ArrayList<>();
        List<String> supportedFiles = Arrays.asList("pdf", "txt");

        for(MultipartFile file: files){
            if(file.isEmpty()){
                LOGGER.info("File: {} is empty.", file.getName());
                continue;
            }

            String filename = FileUtils.getFormattedFilename(file.getOriginalFilename());
            String filetype = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();

            FileModel fileModel = new FileModel(filename,filetype,username);

            if(!supportedFiles.contains(filetype)){
                fileModel.setStatus("UNSUPPORTED");
                result.add(fileModel);
                LOGGER.info("Upload file: {} with type: {} is unsupported.", filename, filetype);
                continue;
            }

            // CHECK IF FILE EXISTS.
            try{
                boolean fileExists = fileRepository.existsByFilename(filename);

                if(fileExists){
                    fileModel.setStatus("FILENAME ALREADY EXISTS.");
                    System.out.println("File already exists " + filename);
                    result.add(fileModel);
                    continue;
                }
            }catch (Exception e){
                fileModel.setStatus("ERROR VALIDATING FILENAME.");
                result.add(fileModel);
                LOGGER.error("Error checking if file exists!!!", e);
                continue;
            }

            // Save file to bucket
            Optional<String> filePathOptional = storageService.saveFile(file);

            if(filePathOptional.isEmpty()){
                fileModel.setStatus("BUCKET UPLOAD ERROR");
                result.add(fileModel);
                continue;
            }
            fileModel.setPath(filePathOptional.get());
            try{
                fileModel.setStatus("UPLOADED");
                result.add(fileModel);
                fileRepository.save(fileModel);
            }catch (Exception e){
                fileModel.setStatus("FAILED.");
                result.add(fileModel);
                LOGGER.error("Error saving file to the db.!!!", e);
                continue;
            }

            LOGGER.info("Uploaded File Data. {}", fileModel);
        }

        return result;
    }

    public List<FileModel> getAllUploadedFiles() {
     return fileRepository.findAll();
    }

    public List<FileModel> addFilesToKnowledgeBase(List<String> filenames, String collectionName, String username) {
        List<FileModel> selectedFiles = fileRepository.findByUsername(username)
                .stream()
                .filter(selectedFile -> filenames.contains(selectedFile.getFilename()))
                .toList();

        if(selectedFiles.isEmpty())
            throw new CustomException("No files found.", HttpStatus.OK);

        Map<String, Object> messagePayload = new HashMap<>();

        messagePayload.put("files", selectedFiles);
        messagePayload.put("collection_name", collectionName);

        messagingPublisher.publishMessage(requestQueue,exchangeName,requestQueueRoutingKey, messagePayload);

        LOGGER.info("File data sent to Messaging Queue with files.size() = {}.", selectedFiles.size());

        for (FileModel selectedFile : selectedFiles) {
            selectedFile.setStatus("PENDING");
            fileRepository.updateStatus(selectedFile);
        }
        return selectedFiles;
    }

    public void updateFileStatus(List<FileModel> files){
        System.out.println("\n\nINSIDE UPDATE FILES " + files);
        files.forEach(file -> fileRepository.updateStatus(file));
    }
}
