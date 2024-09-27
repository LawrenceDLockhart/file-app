package com.example.file_app;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;
import java.io.IOException;
import java.io.InputStream;

public class FileUploadForm extends HorizontalLayout {

    private final FileService service;
    private final Upload upload;
    private final MemoryBuffer buffer;

    public FileUploadForm(FileService service) {
        this.service = service;
        buffer = new MemoryBuffer();
        upload = new Upload(new MemoryBuffer());
        upload.addSucceededListener(event -> {
            openDescriptionDialog(event.getFileName(), buffer.getInputStream());

        });

        add(upload);
    }

    private void openDescriptionDialog(String fileName, InputStream inputStream) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Description");

        TextArea descriptionTextArea = new TextArea("Description");

        Button saveButton = new Button("Save", e -> {
            String description = descriptionTextArea.getValue();
            try {
                service.saveFile(inputStream, fileName, description);
                fireEvent(new FileUploadedEvent(this));
                Notification.show("File uploaded with description!");
                dialog.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                Notification.show("Error uploading file!");
            }
        });

        dialog.add(descriptionTextArea, saveButton);
        dialog.open();
    }

    public Registration addFileUploadedListener(ComponentEventListener<FileUploadedEvent> listener) {
        return addListener(FileUploadedEvent.class, listener);
    }

    public static class FileUploadedEvent extends ComponentEvent<FileUploadForm> {
        public FileUploadedEvent(FileUploadForm source) {
            super(source, false);
        }
    }

}
