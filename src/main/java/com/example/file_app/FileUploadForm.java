package com.example.file_app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
@RouteScope
public class FileUploadForm extends Dialog {
    TextArea description = new TextArea("Description");
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Binder<FileEntity> binder = new BeanValidationBinder<>(FileEntity.class);
    private final FileService service;

    public FileUploadForm(FileService service) {
        this.service = service;
        add(new VerticalLayout(
                description,
                new HorizontalLayout(save, cancel)
        ));
        binder.bindInstanceFields(this);

        save.addClickListener(e -> {
            service.updateFile(binder.getBean());
            close();
        });

        cancel.addClickListener(e -> {
            close();
        });

    }

    public void editFile(FileEntity fileEntity) {
        String headerTitle = (fileEntity.getId() != null ? "Edit File:" : "Save File:")
                + fileEntity.getFileName();
        setHeaderTitle(headerTitle);

        binder.setBean(fileEntity);
        open();
        description.focus();
    }

}
