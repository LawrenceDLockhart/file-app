package com.example.file_app.views;

import com.example.file_app.entity.FileEntity;
import com.example.file_app.service.FileService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> uploadForm.editFile(file));

            Button deleteButton = new Button("Delete");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                service.deleteFile(file);
                refreshGrid();
                Notification.show("File deleted!");
            });

            HorizontalLayout actionsLayout = new HorizontalLayout(new FileDownload(file), editButton, deleteButton);
            actionsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setHeader("Actions");

    }

    public void refreshGrid() {
        setItems(service.findAll());
    }

    private static class FileDownload extends Anchor {
        public FileDownload(FileEntity fileEntity) {
            super();
            add(VaadinIcon.DOWNLOAD.create());
            StreamResource resource = new StreamResource(fileEntity.getFileName(),
                    () -> new ByteArrayInputStream(fileEntity.getFileData()));
            resource.setCacheTime(0);
            resource.setContentType("application/octet-stream");
            setHref(resource);
            getElement().setAttribute("download", true);
            setTitle("Download the file");
        }

    }


}