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

package org.drools.semantics.lang.dl.util;/*
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


import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.io.DataIOException;
import prefuse.data.io.TreeMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DRLTreeMLFormatter {
		

	public static final String FIELD = "text";

	public static InputStream getAsStream(CommonTree resultTree) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Graph graph = buildGraph(resultTree);
		
		TreeMLWriter writer = new TreeMLWriter();				
		try {
			writer.writeGraph(graph, baos);
		} catch (DataIOException e) {
			e.printStackTrace();
		}
		
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private static Graph buildGraph(CommonTree resultTree) {
		Graph tree = new Graph(true);
			tree.addColumn(FIELD, String.class);
			Node root = tree.addNode();
			root.set(FIELD, resultTree.toString());
			visit(resultTree, root, tree);
		return tree;
	}

	private static void visit(Tree sourceTree, Node father, Graph tgtTree) {
		int N = sourceTree.getChildCount();
		
		for (int j = 0; j < N; j++) {
			Node n = tgtTree.addNode();			
			Tree child = sourceTree.getChild(j);
			n.setString(FIELD, child.toString());
			tgtTree.addEdge(father,n);
			visit(child,n,tgtTree);
		}
		
	}

}
