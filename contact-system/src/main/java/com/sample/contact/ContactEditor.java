/**
 * 
 */
package com.sample.contact;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.sample.contact.domain.Contact;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author 
 *
 */
public class ContactEditor extends Window implements ClickListener {

    /**	 */
	private static final long serialVersionUID = 8594434139900953820L;
	
	private Item personItem;
    private FieldGroup fieldGroup;
    private Button saveButton;
    private Button cancelButton;

    public ContactEditor(Item personItem) {
    	setModal(true);
    	setWidth(650, Unit.PIXELS);
		setHeight(470, Unit.PIXELS);
		
    	this.personItem = personItem;
        
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.addComponents(buildToolbar(), buildForm());
        setContent(mainLayout);
        setCaption(buildCaption());
    }
    
    /**
     * 
     * @return
     */
    private Component buildToolbar() {
    	saveButton = new Button("Save", this);
        cancelButton = new Button("Cancel", this);
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.addComponents(saveButton, cancelButton);
        
        return buttonLayout;
    }
    
    /**
     * 
     * @return
     */
    private Component buildForm() {
    	fieldGroup = new BeanFieldGroup<Contact>(Contact.class);
        fieldGroup.setItemDataSource(this.personItem);

        TextField id = new TextField("Id :");
        TextField firstName = new TextField("First Name :");
        TextField lastName = new TextField("Last Name :");
        TextField phoneNumber = new TextField("Phone Number :");
        TextField street = new TextField("Street :");
        street.setWidth(370, Unit.PIXELS);
        TextField city = new TextField("City :");
        TextField zipCode = new TextField("Zip Code :");
        
        FormLayout formLayout = new FormLayout(id, firstName, lastName, phoneNumber, street, city, zipCode);

        fieldGroup.bind(id, "id");
        fieldGroup.bind(firstName, "firstName");
        fieldGroup.bind(lastName, "lastName");
        fieldGroup.bind(phoneNumber, "phoneNumber");
        fieldGroup.bind(street, "street");
        fieldGroup.bind(city, "city");
        fieldGroup.bind(zipCode, "zipCode");
        
        return formLayout;
    }

    /**
     * @return the caption of the editor window
     */
    private String buildCaption() {
    	if (personItem.getItemProperty("id").getValue() == null) {
    		return "New Contact";
    	} else {
    		return String.format("%s %s", personItem.getItemProperty("firstName").getValue(), 
    				personItem.getItemProperty("lastName").getValue());
    	}
    }

    /**
     * 
     */
    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == saveButton) {
            try {
            	fieldGroup.commit();
			} catch (CommitException e) {
				e.printStackTrace();
			}
            fireEvent(new EditorSavedEvent(this, personItem));
        } else if (event.getButton() == cancelButton) {
        	fieldGroup.discard();
        }
        close();
    }
    
    public void addListener(EditorSavedListener listener) {
        try {
            Method method = EditorSavedListener.class.getDeclaredMethod(
                    "editorSaved", new Class[] { EditorSavedEvent.class });
            addListener(EditorSavedEvent.class, listener, method);
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException("Internal error, editor saved method not found");
        }
    }

    public void removeListener(EditorSavedListener listener) {
        removeListener(EditorSavedEvent.class, listener);
    }

    public static class EditorSavedEvent extends Component.Event {

        /**	 */
		private static final long serialVersionUID = -100255761281754363L;
		
		private Item savedItem;

        public EditorSavedEvent(Component source, Item savedItem) {
            super(source);
            this.savedItem = savedItem;
        }

        public Item getSavedItem() {
            return savedItem;
        }
    }

    public interface EditorSavedListener extends Serializable {
        public void editorSaved(EditorSavedEvent event);
    }

}
