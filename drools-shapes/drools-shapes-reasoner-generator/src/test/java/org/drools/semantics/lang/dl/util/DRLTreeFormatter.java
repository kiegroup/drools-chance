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

package org.drools.semantics.lang.dl.util;

import org.antlr.runtime.tree.CommonTree;

public class DRLTreeFormatter {

	public static String toIndentedStringTree(CommonTree tree) {
		return toIndentedStringTree(tree, "\t");
	}

	public static String toIndentedStringTree(CommonTree tree, String sep) {
		String ts = new String(tree.toStringTree());

		//ts = ts.substring(0, ts.indexOf("then"));
		StringBuilder sb = new StringBuilder();
		int dep = 0;
		for (int j = 0; j < ts.length(); j++) {
			if (ts.charAt(j) == '(') {
				dep++;

				sb.append("\n");
				for (int k = 0; k < dep; k++)
					sb.append(sep);
				sb.append(ts.charAt(j));

			} else
			if (ts.charAt(j) == ')') {
				sb.append("\n");
				for (int k = 0; k < dep; k++)
					sb.append(sep);
				sb.append(ts.charAt(j));
				dep--;
			} else {
				sb.append(ts.charAt(j));
			}
		}

		return sb.toString();
	}

}
