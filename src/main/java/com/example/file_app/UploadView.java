package com.example.file_app;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.IOException;

@Route("")
public class UploadView extends VerticalLayout {

    private final FileService service;
    private final FileGrid fileGrid;
    private final FileUploadForm uploadForm;

    public UploadView(FileService service, FileUploadForm fileUploadForm, FileGrid fileGrid) {
        this.service = service;
        this.uploadForm = fileUploadForm;
        this.fileGrid = fileGrid;
        uploadForm.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                fileGrid.refreshGrid();
            }
        });

        Upload upload = new Upload();
        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);
        upload.addSucceededListener(event -> {
            upload.clearFileList();

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(event.getFileName());
            try {
                fileEntity.setFileData(buffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            uploadForm.editFile(fileEntity);
        });

        add(new H1("File Uploads"), upload, fileGrid);
        fileGrid.refreshGrid();

        setHeightFull();
        addClassNames(LumoUtility.Margin.Horizontal.AUTO);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

    }

}
