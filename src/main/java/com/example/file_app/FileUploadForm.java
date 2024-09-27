package com.example.file_app;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;

import java.io.IOException;

public class FileUploadForm extends HorizontalLayout {

    private final FileService service;
    private final TextArea descriptionInput;
    private final Upload upload;

    public FileUploadForm(FileService service) {
        this.service = service;

        descriptionInput = new TextArea("Description");

        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            String description = descriptionInput.getValue();

            try {
                service.saveFile(buffer.getInputStream(), event.getFileName(), description);
                fireEvent(new FileUploadedEvent(this));
                descriptionInput.clear();
                upload.clearFileList();
                Notification.show("File uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
                Notification.show("Error uploading file!");
            }
        });

        add(descriptionInput, upload);
    }

    public Registration addFileUploadedListener(ComponentEventListener<FileUploadedEvent> listener) {
        return addListener(FileUploadedEvent.class, listener);
    }

    // Define the custom event class
    public static class FileUploadedEvent extends ComponentEvent<FileUploadForm> {
        public FileUploadedEvent(FileUploadForm source) {
            super(source, false);
        }
    }

}
