package com.example.file_app;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Route("")
public class UploadView extends VerticalLayout {

    private final FileService service;
    private final FileGrid fileGrid;
    private final FileUploadForm uploadForm;


    public UploadView(FileService service, FileRepository fileRepository) {
        this.service = service;
        this.fileGrid = new FileGrid(service);
        this.uploadForm = new FileUploadForm(service);

        setHeightFull();
        setMaxWidth("500px");
        addClassNames(LumoUtility.Margin.Horizontal.AUTO);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(new H1("File Uploads"), uploadForm, fileGrid);
        uploadForm.addFileUploadedListener(e -> {
            loadFiles(fileRepository);
        });

        loadFiles(fileRepository);
    }

    private void loadFiles(FileRepository fileRepository) {
        List<File> files = fileRepository.findAll();
        fileGrid.setItems(files);
    }

}
