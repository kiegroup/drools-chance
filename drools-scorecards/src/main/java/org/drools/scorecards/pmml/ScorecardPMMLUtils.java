/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scorecards.pmml;

import java.util.List;

import org.dmg.pmml_4_1.Extension;

public class ScorecardPMMLUtils {

    public static String getDataType(org.dmg.pmml_4_1.Characteristic c) {
        for (Extension extension : c.getExtensions()) {
            if (PMMLExtensionNames.CHARACTERTISTIC_DATATYPE.equalsIgnoreCase(extension.getName())) {
                return extension.getValue();
            }
        }
        return null;
    }

    public static String getExtensionValue(List extensions, String extensionName) {
        for (Object obj : extensions) {
            if (obj instanceof Extension) {
                Extension extension = (Extension) obj;
                if (extensionName.equalsIgnoreCase(extension.getName())) {
                    return extension.getValue();
                }
            }
        }
        return null;
    }

    public static Extension getExtension(List extensions, String extensionName) {
        for (Object obj : extensions) {
            if (obj instanceof Extension) {
                Extension extension = (Extension) obj;
                if (extensionName.equalsIgnoreCase(extension.getName())) {
                    return extension;
                }
            }
        }
        return null;
    }

}
