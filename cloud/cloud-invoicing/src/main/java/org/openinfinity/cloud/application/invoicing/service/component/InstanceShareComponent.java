package org.openinfinity.cloud.application.invoicing.service.component;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class InstanceShareComponent extends CustomComponent{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InstanceShareComponent(String message){

        // A layout structure used for composition
        Panel panel = new Panel("Instance Shares");
        
        VerticalLayout layout = new VerticalLayout();
        panel.setContent(layout);

        // Compose from multiple components
        Label label = new Label(message);
        label.setSizeUndefined(); // Shrink
        layout.addComponent(label);
        layout.addComponent(new Button("Ok"));

        // Set the size as undefined at all levels
        panel.getContent().setSizeUndefined();
        panel.setSizeUndefined();
        setSizeUndefined();

        // The composition root MUST be set
        setCompositionRoot(panel);
    }

}
