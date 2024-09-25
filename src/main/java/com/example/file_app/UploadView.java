package com.example.file_app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Route("")
public class UploadView extends VerticalLayout {

    private final FileService service;
    private final Grid<File> fileGrid = new Grid<>();

    public UploadView(FileService service) {
        this.service = service;


        add(new H1("File Uploads"), getForm(), fileGrid);

        fileGrid.addColumn(File::getFileName).setHeader("File Name").setAutoWidth(true);
        fileGrid.addColumn(File::getDescription).setHeader("Description").setAutoWidth(true);

        fileGrid.addComponentColumn(fileEntity -> {
            Button downloadButton = new Button("Download");
            downloadButton.addClickListener(e -> downloadFile(fileEntity));

            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> openEditDialog(fileEntity));

            Button deleteButton = new Button("Delete");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                service.deleteFile(fileEntity);
                loadFiles();
                Notification.show("File deleted!");
            });

            return new HorizontalLayout(downloadButton, editButton, deleteButton);
        }).setHeader("Actions");

        loadFiles();
    }

    private HorizontalLayout getForm() {
        TextArea descriptionInput = new TextArea("Description");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            String description = descriptionInput.getValue();

            try {
                service.saveFile(buffer.getInputStream(), event.getFileName(), description);
                loadFiles();
                descriptionInput.clear();
                upload.clearFileList();
                Notification.show("File uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
                Notification.show("Error uploading file!");
            }
        });

        HorizontalLayout formLayout = new HorizontalLayout(descriptionInput, upload);
        return formLayout;
    }

    private void loadFiles() {
        List<File> files = service.findAll();
        fileGrid.setItems(files);
    }

    private void openEditDialog(File file) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit File Description");

        TextArea descriptionTextArea = new TextArea("Description");
        descriptionTextArea.setValue(file.getDescription());
        Binder<File> binder = new Binder<>(File.class);
        binder.bind(descriptionTextArea, File::getDescription, File::setDescription);
        binder.setBean(file);

        Button saveButton = new Button("Save", e -> {
            if (binder.writeBeanIfValid(file)) {
                service.updateFile(file);
                loadFiles();
                Notification.show("Description updated!");
                dialog.close();
            } else {
                Notification.show("Please fix the validation errors");
            }
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(descriptionTextArea, saveButton, cancelButton);
        dialog.open();
    }

    private void downloadFile(File file) {
        StreamResource resource = new StreamResource(file.getFileName(),
                () -> new ByteArrayInputStream(file.getFileData()));
        resource.setCacheTime(0);
        resource.setContentType("application/octet-stream");
        UI.getCurrent().navigate(String.valueOf(resource));
    }
}
