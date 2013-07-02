package org.openinfinity.cloud.application.invoicing.view;


/**
 * View displays data and receives user interaction
 * @author kilpopas
 *
 */
public interface InvoiceShareView{
    
    public interface InvoiceShareViewListener{
        void buttonClick(String buttonName);
    }
    
    public void addListener(InvoiceShareViewListener listener);
    
}