package com.sample.contact;

import com.sample.contact.ContactEditor.EditorSavedEvent;
import com.sample.contact.ContactEditor.EditorSavedListener;
import com.sample.contact.domain.Contact;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author 
 *
 */
public class ContactMainView extends VerticalLayout implements ComponentContainer {
	
	/**	 */
	private static final long serialVersionUID = -1885394934694179350L;

	private Table contactTable;
	
    private Button newButton;
    private Button deleteButton;
    private Button editButton;
    
    private TextField searchField;
    
    private JPAContainer<Contact> contacts;
	
	public ContactMainView() {
		setSizeFull();
		buildMainArea();
    }
	
	/**
	 * 
	 */
	private void buildMainArea() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.addComponents(buildToolbar(), buildTable());
        verticalLayout.setExpandRatio(contactTable, 1);

        addComponent(verticalLayout);
    }
	
	/**
	 * 
	 * @return
	 */
	private Component buildTable() {
		
		contacts = JPAContainerFactory.make(Contact.class, ContactUI.PERSISTENCE_UNIT);
        
        contactTable = new Table("Contact List", contacts);
        contactTable.setSizeFull();
        contactTable.setSelectable(true);
        contactTable.setImmediate(true);
        
        contactTable.setVisibleColumns(new Object[] { "firstName", "lastName", "phoneNumber", "street", "city", "zipCode" });
        contactTable.setColumnHeaders("First Name", "Last Name", "Phone Number", "Street", "City", "Zip Code");
        
        contactTable.addValueChangeListener(new Property.ValueChangeListener() {
            /**	 */
			private static final long serialVersionUID = -5337656432263520045L;

			@Override
            public void valueChange(ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        contactTable.addItemClickListener(new ItemClickListener() {
            /**		*/
			private static final long serialVersionUID = 495576039533401504L;

			@Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    contactTable.select(event.getItemId());
                }
            }
        });
       
        contactTable.setVisibleColumns(new Object[] { "firstName", "lastName", "phoneNumber", "street", "city", "zipCode" });
        contactTable.setColumnHeaders("First Name", "Last Name", "Phone Number", "Street", "City", "Zip Code");
        
        return contactTable;
	}
	
	/**
	 * 
	 * @return
	 */
	private Component buildToolbar() {
		newButton = new Button("Add");
		deleteButton = new Button("Delete");
		editButton = new Button("Edit");
		
		searchField = new TextField();
        searchField.setInputPrompt("Search by name");
		
		HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);
        toolbar.addComponents(newButton, deleteButton, editButton, searchField);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(searchField, 1);
        toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
        
        newButton.addClickListener(new Button.ClickListener() {

            /**	 */
			private static final long serialVersionUID = -6085518068370742211L;

			@Override
            public void buttonClick(ClickEvent event) {
                final BeanItem<Contact> newContactItem = new BeanItem<Contact>(new Contact());
                ContactEditor personEditor = new ContactEditor(newContactItem);
                personEditor.addListener(new EditorSavedListener() {
                    /**	 */
					private static final long serialVersionUID = 3711166893561429243L;

					@Override
                    public void editorSaved(EditorSavedEvent event) {
                        contacts.addEntity(newContactItem.getBean());
                    }
                });
                UI.getCurrent().addWindow(personEditor);
            }
        });

        
        deleteButton.addClickListener(new Button.ClickListener() {

            /**	 */
			private static final long serialVersionUID = 8449506381041803404L;

			@Override
            public void buttonClick(ClickEvent event) {
                contacts.removeItem(contactTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        
        editButton.addClickListener(new Button.ClickListener() {

            /**	 */
			private static final long serialVersionUID = 7113079427476490488L;

			@Override
            public void buttonClick(ClickEvent event) {
				if (contactTable.getValue() != null) {
					Contact contact = (Contact) ((JPAContainerItem<?>) 
							contactTable.getItem(contactTable.getValue())).getEntity();
					final BeanItem<Contact> editContactItem = new BeanItem<Contact>(contact);
	                ContactEditor personEditor = new ContactEditor(editContactItem);
	                personEditor.addListener(new EditorSavedListener() {
	                    /**	 */
						private static final long serialVersionUID = 3711166893561429243L;
	
						@Override
	                    public void editorSaved(EditorSavedEvent event) {
							contacts.addEntity(editContactItem.getBean());
	                    }
	                });
	                UI.getCurrent().addWindow(personEditor);
				}
            }
			
        });
        editButton.setEnabled(false);

        searchField.addTextChangeListener(new TextChangeListener() {

            /**	 */
			private static final long serialVersionUID = 3881848414923410575L;

			@Override
            public void textChange(TextChangeEvent event) {
                updateFilters(event.getText());
            }
        });
        
        return toolbar;
	}
	
	/**
	 * 
	 */
	private void updateFilters(String textFilter) {
        contacts.setApplyFiltersImmediately(false);
        contacts.removeAllContainerFilters();
        if (textFilter != null && !textFilter.equals("")) {
            Or or = new Or(new Like("firstName", textFilter + "%", false),
                    new Like("lastName", textFilter + "%", false));
            contacts.addContainerFilter(or);
        }
        contacts.applyFilters();
    }
}
