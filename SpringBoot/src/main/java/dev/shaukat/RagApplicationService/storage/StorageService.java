package dev.shaukat.RagApplicationService.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class StorageService {
    private final StorageClient storageClient;

    public StorageService(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    public Optional<String> saveFile(MultipartFile file) {
        return storageClient.saveFile(file);
    }
}
