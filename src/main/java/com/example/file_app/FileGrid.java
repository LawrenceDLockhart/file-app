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

import java.io.ByteArrayInputStream;

public class FileGrid extends Grid<File> {

    private final FileService service;

    public FileGrid(FileService service) {
        this.service = service;

        addColumn(File::getFileName).setHeader("File Name").setAutoWidth(true);
        addColumn(File::getDescription).setHeader("Description").setAutoWidth(true);

        addComponentColumn(file -> {
            Button downloadButton = new Button("Download");
            downloadButton.addClickListener(e -> downloadFile(file));

            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> openEditDialog(file));

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

    private void openEditDialog(File file) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit File Description");

        TextArea descriptionTextArea = new TextArea("Description");
        descriptionTextArea.setValue(file.getDescription());
        Binder<File> binder = new Binder<>(File.class);
        binder.bind(descriptionTextArea, File::getDescription, File::setDescription);
        binder.setBean(file);

        Button saveButton = new Button("Save", e -> {
            if (binder.isValid()) {
                service.updateFile(file);
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

    private void downloadFile(File file) {
        StreamResource resource = new StreamResource(file.getFileName(),
                () -> new ByteArrayInputStream(file.getFileData()));
        resource.setCacheTime(0);
        resource.setContentType("application/octet-stream");
        UI.getCurrent().navigate(String.valueOf(resource));
    }

}