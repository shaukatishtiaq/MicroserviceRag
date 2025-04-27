package dev.shaukat.RagApplicationService.file.dtos;

public class FileResponseDto {
    private int id;
    private String filename;
    private String filetype;
    private String status;

    public FileResponseDto() {
    }

    public FileResponseDto(int id, String filename, String filetype, String status) {
        this.id = id;
        this.filename = filename;
        this.filetype = filetype;
        this.status = status;
    }



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
