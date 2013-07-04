package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import org.openinfinity.cloud.domain.InstanceShare;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class InstanceShareComponent extends CustomComponent{
	
	/* User interface components are stored in session. */
	private Table sharesList = new Table();		
    BeanItemContainer<InstanceShare> instanceShareContainer;

    public BeanItemContainer<InstanceShare> getInstanceShareContainer() {
        return instanceShareContainer;
    }

	public void setInstanceShareContainer(BeanItemContainer<InstanceShare> instanceShareContainer) {
        this.instanceShareContainer = instanceShareContainer;
               
        if (instanceShareContainer.size() == 0) {
        	sharesList.setVisible(false);
        }
        else {
        	sharesList.setVisible(true);
			sharesList.setContainerDataSource(instanceShareContainer);
			sharesList.setVisibleColumns(new String[] { "period_start" });
			sharesList.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
			sharesList.setSelectable(true);
			sharesList.setImmediate(true);
	
			sharesList.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					Object id = sharesList.getValue();
	
					/*
					 * When a share is selected from the list, it is shown 
					 * in our editor on the right. 
					 */
	/*				
					if (id != null)
						editorFields.setItemDataSource(sharesList
								.getItem(id));
					
					editorLayout.setVisible(id != null);
	*/				
				}
			});
        }
    }
    
    
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InstanceShareComponent(String message){
        // A layout structure used for composition
        HorizontalLayout mainLayout = new HorizontalLayout();

        // Shares
        Panel sharePanel = new Panel("Shares");
        mainLayout.addComponent(sharePanel);                       
        VerticalLayout shareLayout = new VerticalLayout();
        sharePanel.setContent(shareLayout);

        // Compose from multiple components
        Label label = new Label(message);
        label.setSizeUndefined(); // Shrink
        shareLayout.addComponent(sharesList);
        shareLayout.addComponent(new Button("Create new"));
        
        // Share details
        Panel detailPanel = new Panel("Details");
        mainLayout.addComponent(detailPanel);        
        VerticalLayout detailLayout = new VerticalLayout();
        detailPanel.setContent(detailLayout);

        // Compose from multiple components
        Label label2 = new Label(message);
        label2.setSizeUndefined(); // Shrink
        detailLayout.addComponent(label2);
        detailLayout.addComponent(new Button("Save"));
        
        // Set the size as undefined at all levels
        sharePanel.getContent().setSizeUndefined();
        sharePanel.setSizeUndefined();
        detailPanel.getContent().setSizeUndefined();
        detailPanel.setSizeUndefined();
        mainLayout.setSizeFull();
        setSizeUndefined();

        // The composition root MUST be set
        setCompositionRoot(mainLayout);
    }

}
