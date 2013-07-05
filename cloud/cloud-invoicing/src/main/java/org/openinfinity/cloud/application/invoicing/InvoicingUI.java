package org.openinfinity.cloud.application.invoicing;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;
import org.openinfinity.cloud.application.invoicing.presenter.InvoiceSharePresenter;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;

import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class InvoicingUI extends UI{

    @Override
    protected void init(VaadinRequest request) {
        if (request instanceof VaadinServletRequest){
            //TODO: implement getting of user from servlet request
        }else if (request instanceof VaadinPortletRequest) {
            //TODO: implement getting of user using LiferayServiceImpl
        }
        
        initLayout();
        initInvoicing();
        
    }
    
    private AbstractLayout main=null;

    private void initLayout() {
        // TODO Auto-generated method stub
        main=new VerticalLayout();
        setContent(main);
        
        
    }

    private void initInvoicing() {
     // Create the model and the Vaadin view implementation
        
        
        InvoiceShareModel    model = new InvoiceShareModel();
        InvoiceShareViewImpl view  = new InvoiceShareViewImpl();
            
        // The presenter binds the model and view together
        new InvoiceSharePresenter(model, view);
            
        // The view implementation is a Vaadin component
        main.addComponent(view);
        // TODO Auto-generated method stub
        
    }

}
