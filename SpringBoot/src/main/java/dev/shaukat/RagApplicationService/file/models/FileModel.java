package dev.shaukat.RagApplicationService.file.models;

public class FileModel {

    private int id;
    private String filename;
    private String filetype;
    private String path;
    private String username;
    private String status;

    public FileModel() {
    }

    public FileModel(String filename, String filetype, String url, String path, String username, String status) {
        this.filename = filename;
        this.filetype = filetype;
        this.path = path;
        this.username = username;
        this.status = status;
    }

    public FileModel(String filename, String filetype, String username) {
        this.filename = filename;
        this.filetype = filetype;
        this.username = username;
    }

    public FileModel(String filename, String filetype, String status, String username) {
        this.filename = filename;
        this.filetype = filetype;
        this.status = status;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "filename='" + filename + '\'' +
                ", filetype='" + filetype + '\'' +
                ", path='" + path + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
