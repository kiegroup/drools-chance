/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;

import org.drools.shapes.terms.vocabulary.AbstractVocabularyCatalog;
import org.drools.shapes.terms.vocabulary.VocabularyCatalog;

public class TestVocabularyCatalog extends AbstractVocabularyCatalog {

    private static final VocabularyCatalog SELF = new TestVocabularyCatalog();

    public static VocabularyCatalog get() {
        return SELF;
    }
}
