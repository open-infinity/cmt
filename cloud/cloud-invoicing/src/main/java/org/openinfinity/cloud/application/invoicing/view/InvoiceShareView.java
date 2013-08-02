package org.openinfinity.cloud.application.invoicing.view;

import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;


/**
 * View displays data and receives user interaction
 * @author kilpopas
 *
 */
public interface InvoiceShareView{

    public interface InvoiceShareViewListener{
        public void buttonClick(final String buttonName, final Object item);
        public void instanceSelected(InstanceSelectionBean value);
        public void instanceShareSelected(InstanceShareBean value);
        public void instanceShareDetailSelected(InstanceShareDetailBean itemId);
    }

    public void addListener(InvoiceShareViewListener listener);

    public void setInstances(Collection<Instance> instances);

    public void setInstanceShares(Collection<InstanceShare> instanceShares);

    public void setInstanceShareDetails(InstanceShare instanceShare);

    public InstanceShareComponent getInstanceShareComponent();

    public void editInstanceShare(InstanceShareBean instanceShare);

    public void editInstanceShareDetail(InstanceShareDetailBean item);

}