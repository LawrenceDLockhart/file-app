package com.example.file_app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.io.ByteArrayInputStream;

@SpringComponent
@RouteScope
public class FileGrid extends Grid<FileEntity> {

    private final FileService service;
    private final FileUploadForm uploadForm;

    public FileGrid(FileService service, FileUploadForm fileUploadForm) {
        this.service = service;
        this.uploadForm = fileUploadForm;

        addColumn(FileEntity::getFileName).setHeader("File Name").setAutoWidth(true);
        addColumn(FileEntity::getDescription).setHeader("Description").setAutoWidth(true);

        addComponentColumn(file -> {
            Button downloadButton = new Button("Download");
            downloadButton.addClickListener(e -> downloadFile(file));

            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> uploadForm.editFile(file));

            Button deleteButton = new Button("Delete");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                service.deleteFile(file);
                refreshGrid();
                Notification.show("File deleted!");
            });

            HorizontalLayout actionsLayout = new HorizontalLayout(downloadButton, editButton, deleteButton);
            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setHeader("Actions");

    }

    public void refreshGrid() {
        setItems(service.findAll());
    }

    private void openEditDialog(FileEntity fileEntity) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit File Description");

        TextArea descriptionTextArea = new TextArea("Description");
        descriptionTextArea.setValue(fileEntity.getDescription());
        Binder<FileEntity> binder = new Binder<>(FileEntity.class);
        binder.bind(descriptionTextArea, FileEntity::getDescription, FileEntity::setDescription);
        binder.setBean(fileEntity);

        Button saveButton = new Button("Save", e -> {
            if (binder.isValid()) {
                service.updateFile(fileEntity);
                refreshGrid();
                Notification.show("Description updated!");
                dialog.close();
            } else {
                Notification.show("Please fix the validation errors");
            }
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(descriptionTextArea, saveButton, cancelButton);
        dialogLayout.setSpacing(true);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void downloadFile(FileEntity fileEntity) {
        StreamResource resource = new StreamResource(fileEntity.getFileName(),
                () -> new ByteArrayInputStream(fileEntity.getFileData()));
        resource.setCacheTime(0);
        resource.setContentType("application/octet-stream");
        UI.getCurrent().navigate(String.valueOf(resource));
    }

}