package com.example.file_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileService {

    private final FileEntityRepository repository;

    @Autowired
    public FileService(FileEntityRepository repository) {
        this.repository = repository;
    }

    public FileEntity saveFile(InputStream fileStream, String fileName, String description) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(fileName);
        fileEntity.setDescription(description);
        fileEntity.setFileData(fileStream.readAllBytes());
        return repository.save(fileEntity);
    }

    public List<FileEntity> findAll() {
        return repository.findAll();
    }

    public void updateFile(FileEntity fileEntity) {
        repository.save(fileEntity);
    }

    public void deleteFile(FileEntity fileEntity) {
        repository.delete(fileEntity);
    }
}
