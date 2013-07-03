package org.openinfinity.cloud.application.invoicing.service.component;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class InstanceSelectionComponent extends CustomComponent{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InstanceSelectionComponent(){
        
        VerticalLayout main=new VerticalLayout();
        this.setCompositionRoot(main);

        //Instances drop down
        AbstractSelect selectInstance = new NativeSelect("Select an instance");
        for (int i = 0; i < 6; i++) {
            selectInstance.addItem(i);
            selectInstance.setItemCaption(i, "Instance " + i);
        }

        selectInstance.setNullSelectionAllowed(false);
        selectInstance.setValue(2);
        selectInstance.setImmediate(true);
        
        main.addComponent(selectInstance);

    }

}
