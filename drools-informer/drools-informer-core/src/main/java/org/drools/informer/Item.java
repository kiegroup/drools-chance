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

import java.util.*;

/**
 * Base class for all items contained within a <code>Questionnaire</code>.
 * 
 * @author Damon Horrell
 */
public abstract class Item extends InformerObject {

	private static final long serialVersionUID = 1L;

    private static final String COMMA_DELIMITER = ",";

	private String id;



	/**
	 * Styles are represented internally as a comma-delimited string for efficient XML transport.
	 */
	private List<String> presentationStyles;
	


    public Item() {
        setId(UUID.randomUUID().toString());
        presentationStyles = new ArrayList<String>();
	}

	public Item(String type) {
		setType(type);
        setId(UUID.randomUUID().toString());
        presentationStyles = new ArrayList<String>();
	}

	/**
	 * @see InformerObject#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique id for this item which must be non-null and cannot contain any commas or dots.
	 * 
	 * @param newId
	 */
	public void setId(String newId) {
        if (newId == null || newId.contains(",") || newId.contains(" ")) {
			throw new IllegalArgumentException("Invalid item id");
		}
		if (this.id != null && !this.id.equals(newId)) {
			throw new IllegalStateException("id may not be changed");
		}
		this.id = newId;
	}

    /**
     * Unsafe setId, for testing purposes only
      * @param newId
     */
    public void forceId(String newId) {
        if (newId == null || newId.contains(",") || newId.contains(" ")) {
			throw new IllegalArgumentException("Invalid item id");
		}
		this.id = newId;
    }

	/**
	 * Gets list of presentation styles.
	 * 
	 * @return
	 */
	public String[] getPresentationStyles() {
		return (presentationStyles == null || presentationStyles.size() == 0)
                ? null : presentationStyles.toArray(new String[presentationStyles.size()]);
	}

	/**
	 * <p>
	 * Sets the list of presentation styles for this item.
	 * </p>
	 * 
	 * <p>
	 * <code>presentationStyles</code> is used to control how the item is rendered by the particular UI implementation. e.g.
	 * </p>
	 * 
	 * <ul>
	 * <li>whether a yes/no question shows as a checkbox, a drop down list, or a series of radio buttons</li>
	 * <li>whether a note shows next to a question, or as a mouseover tip, or in a separate panel at the side/bottom</li>
	 * <li>whether a group represents a section with a heading, a multi-column layout, or centre-aligning the items within it</li>
	 * </ul>
	 * 
	 * @param presentationStyles
	 */
	public void setPresentationStyles(String[] presentationStyles) {
		if (presentationStyles == null) {
			this.presentationStyles.clear();
		} else {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < presentationStyles.length; i++) {
//				if (presentationStyles[i] != null) {
//					if (sb.length() > 0) {
//						sb.append(",");
//					}
//					if (presentationStyles[i].contains(",")) {
//						throw new IllegalArgumentException();
//					}
//					sb.append(presentationStyles[i]);
//				}
//			}
//			this.presentationStyles = sb.toString();
            for (String ps : presentationStyles) {
                addPresentationStyle(ps);
            }
		}
	}

	/**
	 * Sets list of presentation styles.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * <p>
	 * <code>item.setPresentationStyles({"a", "b"});</code>
	 * </p>
	 * 
	 * @param presentationStyles
	 */
	public void setPresentationStyles(Object[] presentationStyles) {
		if (presentationStyles == null || presentationStyles.length == 0) {
			this.presentationStyles = null;
		} else {
			setPresentationStyles((String[]) Arrays.asList(presentationStyles).toArray(new String[] {}));
		}
	}

	/**
	 * Adds a presentation style to the list. Duplicates and nulls ignored.
	 * 
	 * @param presentationStyle
	 */
	public void addPresentationStyle(String presentationStyle) {
		if (presentationStyle != null) {
//			if (this.presentationStyles == null) {
//				this.presentationStyles = presentationStyle;
//			} else {
//				this.presentationStyles = this.presentationStyles + "," + presentationStyle;
//			}
            if (presentationStyle.indexOf(COMMA_DELIMITER) >= 0) {
                throw new IllegalArgumentException("No commas allowed in presentationStyle " + presentationStyle);
            }
            this.presentationStyles.add(presentationStyle);
		}
	}

	/**
	 * Removes a presentation style from the list. If it was the only one, the list will be set to null.
	 * 
	 * @param presentationStyle
	 */
	public void removePresentationStyle(String presentationStyle) {
//		if (presentationStyle != null) {
//			List<String> presentationStyles = new ArrayList<String>(Arrays.asList(getPresentationStyles()));
//			int pos = presentationStyles.indexOf(presentationStyle);
//			if (pos >= 0) {
//				if (presentationStyles.size() == 1) {
//					this.presentationStyles = null;
//				} else {
//					presentationStyles.remove(presentationStyle);
//					setPresentationStyles(presentationStyles.toArray());
//				}
//			}
//		}
        this.presentationStyles.remove(presentationStyle);
	}

	/**
	 * Gets list of presentation styles as a comma delimited string.
	 * 
	 * TODO this method can be removed when Guvnor supports String[]
	 * 
	 * @return
	 * @deprecated
	 */
	public String getPresentationStylesAsString() {
        if (this.presentationStyles == null || this.presentationStyles.size() == 0) {
            return null;
        }
		StringBuilder sb = new StringBuilder();
        Iterator<String> iter = this.presentationStyles.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(COMMA_DELIMITER);
            }
        }
        return sb.toString();
	}

	/**
	 * Set list of presentation styles as a comma-delimited string.
	 * 
	 * TODO this method can be removed when Guvnor supports String[]
	 * 
	 * @param presentationStyles
	 * @deprecated
	 */
	public void setPresentationStylesAsString(String presentationStyles) {
		this.presentationStyles.clear();
        if (presentationStyles != null && presentationStyles.length() != 0) {
            StringTokenizer tok = new StringTokenizer(presentationStyles,COMMA_DELIMITER);
            while (tok.hasMoreTokens()) {
                this.presentationStyles.add(tok.nextToken());
            }
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", presentationStyles=" + presentationStyles +
                "} " + super.toString();
    }



    public List<String> getStylesList() {
	List list = new ArrayList();
	String[] styles = getPresentationStyles();
	if (styles != null) {
		list.addAll(Arrays.asList(styles));
	}
	return list;
}

}
