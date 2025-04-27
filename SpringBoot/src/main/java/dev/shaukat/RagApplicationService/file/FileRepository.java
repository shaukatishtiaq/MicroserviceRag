package dev.shaukat.RagApplicationService.file;

import com.zaxxer.hikari.HikariDataSource;
import dev.shaukat.RagApplicationService.file.models.FileModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileRepository {
    private final HikariDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public FileRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void executeDDL(){
        String sql = """
                CREATE TABLE IF NOT EXISTS files (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    filename VARCHAR(255),
                    filetype VARCHAR(100),
                    path VARCHAR(500),
                    username VARCHAR(100),
                    status VARCHAR(50)
                );
                
                """;
    }
    public void save(FileModel fileModel){
        String sql = """
                INSERT INTO files (filename, filetype, path, username, status)
                VALUES (?, ?, ?, ?, ?);
                """;
        jdbcTemplate.update(sql, fileModel.getFilename(), fileModel.getFiletype(), fileModel.getPath(), fileModel.getUsername(), fileModel.getStatus());
    }

    public List<FileModel> findAll(){
        String sql = """
                SELECT * FROM files;
                """;
        List<FileModel> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FileModel fileModel = new FileModel();
            fileModel.setId(rs.getInt("id"));
            fileModel.setFilename(rs.getString("filename"));
            fileModel.setFiletype(rs.getString("filetype"));
            fileModel.setUsername(rs.getString("username"));
            fileModel.setStatus(rs.getString("status"));
            fileModel.setPath(rs.getString("path"));

            return fileModel;
        });

        System.out.println("RESULT ==>> " + result);
        return result;
    }

    public List<FileModel> findByUsername(String username){
        String sql = String.format("SELECT * FROM files where username = '%s';", username);
        System.out.println("SQL ---  " + sql);

        List<FileModel> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FileModel fileModel = new FileModel();
            fileModel.setId(rs.getInt("id"));
            fileModel.setFilename(rs.getString("filename"));
            fileModel.setFiletype(rs.getString("filetype"));
            fileModel.setUsername(rs.getString("username"));
            fileModel.setStatus(rs.getString("status"));
            fileModel.setPath(rs.getString("path"));

            return fileModel;
        });

        return result;
    }

    public boolean existsByFilename(String filename){
        String sql = """
                SELECT COUNT(id) FROM files WHERE filename = ?
                """;
        int result =  jdbcTemplate.queryForObject(sql, Integer.class, filename);

        return result > 0;
    }

    public void updateStatus(FileModel file) {
        String sql = String.format("UPDATE FILES SET status = '%s' WHERE id = %d;",file.getStatus(), file.getId());

        jdbcTemplate.execute(sql);
    }
}
