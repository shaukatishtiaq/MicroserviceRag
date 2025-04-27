package dev.shaukat.RagApplicationService.file.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class KnowledgeBaseFileRequestDto {
    private List<String> filenames;

    @JsonProperty("collection_name")
    private String collectionName;

    public KnowledgeBaseFileRequestDto() {
    }

    public KnowledgeBaseFileRequestDto(String collectionName) {
        this.collectionName = collectionName;
    }

    public KnowledgeBaseFileRequestDto(List<String> filenames, String collectionName) {
        this.filenames = filenames;
        this.collectionName = collectionName;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
