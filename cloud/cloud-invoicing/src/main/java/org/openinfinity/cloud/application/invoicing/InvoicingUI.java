package org.openinfinity.cloud.application.invoicing;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;
import org.openinfinity.cloud.application.invoicing.model.user.DummyUserImpl;
import org.openinfinity.cloud.application.invoicing.model.user.User;
import org.openinfinity.cloud.application.invoicing.presenter.InvoiceSharePresenter;
import org.openinfinity.cloud.application.invoicing.utility.LiferayServiceImpl;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;

import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class InvoicingUI extends UI{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private User user;

    @Override
    protected void init(VaadinRequest request) {
        if (request instanceof VaadinServletRequest){
            user=new DummyUserImpl();
        }else if (request instanceof VaadinPortletRequest) {
            //TODO: implement getting of user using LiferayServiceImpl, implement getting suborganizations
            user = LiferayServiceImpl.getUser( ((VaadinPortletRequest)request).getPortletRequest());
        }

        initLayout();

        if (user==null){
            initUserNotLoggedIn();
        }else{
            initInvoicing();
        }

    }

    private void initUserNotLoggedIn() {
        Label label=new Label("User not logged in");
        main.addComponent(label);

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

    }

}
