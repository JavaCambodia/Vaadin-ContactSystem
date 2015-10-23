package com.sample.contact;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * 
 * @author 
 *
 */
@Theme("contact")
@Widgetset("com.sample.contact.ContactAppWidgetset")
public class ContactUI extends UI {

	/**	 */
	private static final long serialVersionUID = 5520383943418368112L;
	
	public static final String PERSISTENCE_UNIT = "contact";

    static {
        DemoDataGenerator.create();
    }
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	setContent(new ContactMainView());
    }

    @WebServlet(urlPatterns = "/*", name = "ContactUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ContactUI.class, productionMode = false)
    public static class ContactUIServlet extends VaadinServlet {
		/**	 */
		private static final long serialVersionUID = 3641098721773718119L;
    }
}
