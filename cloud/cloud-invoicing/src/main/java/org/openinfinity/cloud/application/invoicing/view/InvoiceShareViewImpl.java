package org.openinfinity.cloud.application.invoicing.view;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;


public class InvoiceShareViewImpl extends CustomComponent
implements InvoiceShareView, ClickListener {
    public InvoiceShareViewImpl() {
        super();
        
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

    private static final long serialVersionUID = 1L;

    /* Only the presenter registers one listener... */
    List<InvoiceShareViewListener> listeners =
            new ArrayList<InvoiceShareViewListener>();

    public void addListener(InvoiceShareViewListener listener) {
        listeners.add(listener);
    }

    /** Relay button clicks to the presenter with an
     *  implementation-independent event */
    @Override
    public void buttonClick(ClickEvent event) {
        for (InvoiceShareViewListener listener: listeners)
            listener.buttonClick(event.getButton().getId());
    }
}