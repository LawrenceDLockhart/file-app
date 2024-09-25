package com.example.file_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileService {

    private final FileRepository repository;

    @Autowired
    public FileService(FileRepository repository) {
        this.repository = repository;

    }

    public File saveFile(InputStream fileStream, String fileName, String description) throws IOException {
        File fileEntity = new File();
        fileEntity.setFileName(fileName);
        fileEntity.setDescription(description);
        fileEntity.setFileData(fileStream.readAllBytes());
        return repository.save(fileEntity);
    }

    public List<File> findAll() {
        return repository.findAll();
    }

    public void updateFile(File file) {
        repository.save(file);
    }

    public void deleteFile(File file) {
        repository.delete(file);
    }
}
