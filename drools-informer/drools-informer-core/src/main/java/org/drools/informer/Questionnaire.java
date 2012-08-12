/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.informer;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.drools.definition.type.Modifies;
import org.drools.definition.type.PropertyReactive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * Represents a general questionnaire containing a hierarchical arrangement of questions, and notes about how to complete the
 * questionnaire.
 * </p>
 * 
 * <p>
 * Note that the name <code>Questionnaire</code> is not intended to limit its use to specific cases, but rather it should be
 * considered a general-purpose model suitable for most types of form-based scenarios (which aren't usually referred to as a
 * questionnaire but are conceptually similar).
 * </p>
 * 
 * @author Damon Horrell
 */
@PropertyReactive
public class Questionnaire extends Group {

	private static final String DEFAULT_CLIENT_DATE_FORMAT = "dd/mm/yyyy";

	private static final Logger logger = LoggerFactory.getLogger(Questionnaire.class);

	private static final long serialVersionUID = 1L;

	public static final String COMPLETION_ACTION_RETURN = "#return";

	private String defaultLanguage = "EN";

    private String activeItem;

	private transient ArrayList<NavigationStackEntry> navigationStack = new ArrayList<NavigationStackEntry>();

	private String completionAction;

	private boolean invalidAnswers;

	private boolean enableActionValidation;

	private Set<String> availableItems;

	private String clientDateFormat = DEFAULT_CLIENT_DATE_FORMAT;
	
	/** Is HTML markup allowed in questions, errors, etc. */
	private boolean markupAllowed;



	public Questionnaire() {
		super.setActive(true);
        this.availableItems = new HashSet<String>();
	}


	public Questionnaire(String type) {
		super(type);
		super.setActive(true);
        this.availableItems = new HashSet<String>();
	}

	public Questionnaire(String type, String label) {
		super(type, label);
		super.setActive(true);
        this.availableItems = new HashSet<String>();
	}

	public String getActiveItem() {
		return activeItem;
	}

	/**
	 * Sets the active child <code>Item</code> within the questionnaire.
	 * 
	 * A typical scenerio is to place a number of <code>Group</code>s within the questionnaire and use them to represent the
	 * pages/screens of your application.
	 * 
	 * If <code>activeItem</code> is null then all items are considered to be active.
	 * 
	 * @param activeItem
	 */
	public void setActiveItem(String activeItem) {
		this.activeItem = activeItem;
	}


	/**
	 * Sets the action to be performed when the questionnaire is complete.
	 * 
	 * The meaning of the action may vary from one UI implementation to another but it could be used for a URL to redirect to or
	 * the name of a method to invoke.
	 * 
	 * @return
	 */
	public String getCompletionAction() {
		return completionAction;
	}

	public void setCompletionAction(String completionAction) {
		this.completionAction = completionAction;
	}

	public boolean isInvalidAnswers() {
		return invalidAnswers;
	}

	/**
	 * This is invoked by the Tohu built-in rules. Do not call it directly.
	 * 
	 * @param invalidAnswers
	 */
	public void setInvalidAnswers(boolean invalidAnswers) {
		this.invalidAnswers = invalidAnswers;
	}

    @Override
    public String toString() {
        return "Questionnaire{" +
                "activeItem='" + activeItem + '\'' +
                ", navigationStack=" + navigationStack +
                ", completionAction='" + completionAction + '\'' +
                ", invalidAnswers=" + invalidAnswers +
                ", enableActionValidation=" + enableActionValidation +
                ", availableItems=" + availableItems +
                ", clientDateFormat='" + clientDateFormat + '\'' +
                ", markupAllowed=" + markupAllowed +
                "} " + super.toString();
    }

    /**
	 * This is used to push a new navigation path onto the questionnaire. On completion, the user will be returned to the previous
	 * path.
	 * 
	 * @param newItems
	 * @param newActiveItem
	 * @return
	 */
    @Modifies( { "navigationStack", "items", "activeItem", "completionAction" } )
	public void navigationBranch(String[] newItems, String newActiveItem) {
		navigationBranch(newItems, newActiveItem, COMPLETION_ACTION_RETURN);
	}

	/**
	 * This is used to push a new navigation path onto the questionnaire with a new completionAction.
	 * 
	 * @param newItems
	 * @param newActiveItem
	 * @param newCompletionAction
	 * 
	 * @return
	 */
    @Modifies( { "navigationStack", "items", "activeItem", "completionAction" } )
	public void navigationBranch(String[] newItems, String newActiveItem, String newCompletionAction) {
		if ((newItems == null) || (newItems.length == 0)) {
			throw new IllegalArgumentException("There are no valid new items to push onto stack.");
		}
		NavigationStackEntry entry = new NavigationStackEntry(this);
		navigationStack.add(0, entry);
		this.setItems(newItems);
		this.activeItem = newActiveItem;
		this.completionAction = newCompletionAction;
	}

	/**
	 * This is used to pop the new navigation path off the questionnaire, reverting to the previous one.
	 * 
	 * @return
	 */
    @Modifies( { "navigationStack", "items", "activeItem", "completionAction" } )
	public void navigationReturn() {
		if (navigationStack.size() == 0) {
			throw new IllegalStateException();
		}
		NavigationStackEntry entry = navigationStack.remove(0);
		this.activeItem = entry.activeItem;
		this.setItems(entry.items);
		this.setCompletionAction(entry.completionAction);
	}

	/**
	 * Returns true if the questionnaire is in a navigation branch, or false if it is in the main flow.
	 * 
	 * @return
	 */
	public boolean isBranched() {
		return !navigationStack.isEmpty();
	}

	private class NavigationStackEntry {

		private String[] items;
		private String activeItem;
		private String completionAction;

		private NavigationStackEntry(Questionnaire questionnaire) {
			this.items = questionnaire.getItems();
			this.activeItem = questionnaire.getActiveItem();
			this.completionAction = questionnaire.getCompletionAction();
		}

	}

	public boolean isEnableActionValidation() {
		return enableActionValidation;
	}

	public void setEnableActionValidation(boolean enableActionValidation) {
		this.enableActionValidation = enableActionValidation;
	}

	/**
	 * Gets list of available item ids.
	 * 
	 * @return
	 */
	public String[] getAvailableItems() {
        if (availableItems == null || availableItems.size() == 0) {
            return null;
        }
        return availableItems.toArray(new String[availableItems.size()]);
	}

    public int getNumAvailableItems() {
        return availableItems.size();
    }

//    public List<String> getAvailableItemList() {
//        return new ArrayList( availableItems );
//	}
//
    public Set<String> getAvailableItemSet() {
        return availableItems;
    }


	/**
	 * This is invoked by the Tohu built-in rules. Do not call it directly.
	 * 
	 * @param availableItems
	 */
	public void setAvailableItems(String[] availableItems) {
		if (logger.isDebugEnabled()) {
			logger.debug("Available Items are: " + Arrays.toString(availableItems));
		}
        this.availableItems.clear();
        for (String item : availableItems) {
            if (item != null) {
                this.availableItems.add(item);
            }
        }
	}


    public void setAvailableItems(Collection<String> availableItems) {
		if (logger.isDebugEnabled()) {
			logger.debug("Available Items are: " + availableItems);
		}
        this.availableItems.clear();
        this.availableItems.addAll( availableItems );
	}


    @Modifies( "availableItems" )
    public void addAvailableItems(List<String> availableItems) {
        this.availableItems.addAll( availableItems );
	}

    @Modifies( "availableItems" )
    public void addAvailableItem( String availableItem ) {
        this.availableItems.add( availableItem );
    }




	public String getClientDateFormat() {
		return clientDateFormat;
	}

	/**
	 * Set the format used to display/enter dates on the client.
	 * 
	 * TODO this should use some industry-standard for specifying date formats. No change required at this end to achieve that but
	 * we will need to change the javascript end to convert the standard format into whatever is required by JQuery.
	 * 
	 * @param clientDateFormat
	 */
	public void setClientDateFormat(String clientDateFormat) {
		this.clientDateFormat = clientDateFormat;
	}

	/**
	 * True if text in Notes, labels, pre-labels and error messages contain HTML markup.
	 * Note that this defaults to false for security purposes to defend against XSS.
	 * @return
	 */
	public boolean isMarkupAllowed() {
		return markupAllowed;
	}

	/**
	 * Allow or disallow HTML markup in Notes, labels, pre-labels and error messages
	 * @param markupAllowed
	 */
	public void setMarkupAllowed(boolean markupAllowed) {
		this.markupAllowed = markupAllowed;
	}

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }



    @Modifies( "items" )
    public void addItem(String itemId) {
        super.addItem(itemId);
    }

    @Modifies( "items" )
    public void insertItem(String itemId, String beforeItemId) {
        super.insertItem(itemId, beforeItemId);
    }

    @Modifies( "items" )
    public void appendItem(String itemId, String afterItemId) {
        super.appendItem(itemId, afterItemId);
    }

    @Modifies( "items" )
    public int removeItem(String itemId) {
        return super.removeItem(itemId);
    }

    @Modifies( { "presentationStyles", "stylesList" } )
    public void setPresentationStyles(String[] presentationStyles) {
        super.setPresentationStyles(presentationStyles);
    }

    @Modifies( { "presentationStyles", "stylesList" } )
    public void setPresentationStyles(Object[] presentationStyles) {
        super.setPresentationStyles(presentationStyles);
    }

    @Modifies( { "presentationStyles", "stylesList" } )
    public void addPresentationStyle(String presentationStyle) {
        super.addPresentationStyle(presentationStyle);
    }

    @Modifies( { "presentationStyles", "stylesList" } )
    public void removePresentationStyle(String presentationStyle) {
        super.removePresentationStyle(presentationStyle);
    }
}
