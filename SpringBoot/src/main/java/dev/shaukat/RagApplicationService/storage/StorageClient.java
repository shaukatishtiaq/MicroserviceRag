package dev.shaukat.RagApplicationService.storage;

import dev.shaukat.RagApplicationService.exceptions.CustomException;
import dev.shaukat.RagApplicationService.file.FileUtils;
import dev.shaukat.RagApplicationService.utils.GeneralUtils;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

@Component
public class
StorageClient {
    @Value("${storage.url}") String storageUrl;
    @Value("${storage.access.key}") String storageAccessKey;
    @Value("${storage.secret.key}") String storageSecretKey;

    Logger LOGGER = LoggerFactory.getLogger(StorageClient.class);

    private final MinioClient client;
    private final String bucket;

    public StorageClient(
            @Value("${storage.url}") String storageUrl,
            @Value("${storage.access.key}") String storageAccessKey,
            @Value("${storage.secret.key}") String storageSecretKey,
            @Value("${storage.bucket}") String bucket
) {
        LOGGER.info("Connecting to Object Storage with config:\nStorage URL: {} \nStorage Access Key: {} \nStorage Secret Key: {} \nStorage Bucket: {}", storageUrl, GeneralUtils.maskString(storageAccessKey), GeneralUtils.maskString(storageSecretKey), bucket);

        this.bucket = bucket;
        try{
            client =  MinioClient.builder()
                .endpoint(storageUrl)
                .credentials(storageAccessKey, storageSecretKey)
                .build();
        }catch (Exception e){
            LOGGER.error("Error occured when connecting to the Storage.", e);
            throw new CustomException("Error connecting to storage!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try{
            BucketExistsArgs bucketArgs = BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build();
            boolean isFound = client.bucketExists(bucketArgs);
            if(isFound){
                LOGGER.info("CONNECTED TO OBJECT STORAGE BUCKET!");
            }else{
                LOGGER.error("BUCKET = {} DOESN'T EXIST!!!", bucket);
            }
        } catch (Exception e){
            LOGGER.error("Error occured when connecting to the Storage Bucket.", e);
            throw new CustomException("Error connecting to storage bucket!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<String> saveFile(MultipartFile file) {
        String filename = FileUtils.getFormattedFilename(file.getOriginalFilename());
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object("uploads/" + filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            ObjectWriteResponse response =  client.putObject(putObjectArgs);
            String filePath = response.object();

            LOGGER.info("File uploaded to storage with path: {}", filePath);

            return Optional.ofNullable(filePath);
        } catch (Exception e){
            LOGGER.error("Error occured when uploading file: {} to storage.", filename, e);
            return Optional.empty();
        }
    }
}
