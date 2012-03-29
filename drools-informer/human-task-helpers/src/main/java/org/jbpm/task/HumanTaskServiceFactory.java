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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

/**
 *
 * @author salaboy
 */
public class HumanTaskServiceFactory implements ObjectFactory {



    public HumanTaskServiceFactory() {
         
    }


    public Object getObjectInstance(Object obj,
            Name name, Context nameCtx, Hashtable environment)
            throws NamingException {

        // Acquire an instance of our specified bean class


        // Customize the bean properties from our attributes
//        Reference ref = (Reference) obj;
//        Enumeration addrs = ref.getAll();
//        while (addrs.hasMoreElements()) {
//            RefAddr addr = (RefAddr) addrs.nextElement();
//            String name = addr.getType();
//            String value = (String) addr.getContent();
//            if (name.equals("foo")) {
//                bean.setFoo(value);
//            } else if (name.equals("bar")) {
//                try {
//                    bean.setBar(Integer.parseInt(value));
//                } catch (NumberFormatException e) {
//                    throw new NamingException("Invalid 'bar' value " + value);
//                }
//            }
//        }

        // Return the customized instance
        return createTaskLocalService();

    }

    public static TaskService createTaskLocalService() {
        TaskService taskServiceObject = null;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        org.jbpm.task.service.TaskService taskService = new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        TaskServiceSession taskSession = taskService.createSession();

        taskServiceObject = new LocalTaskService(taskService);
        
//         // Add users
//        Map vars = new HashMap();
//        Reader reader = new InputStreamReader(HumanTaskServiceFactory.class.getResourceAsStream("LoadUsers.mvel"));
//        Map<String, User> users = (Map<String, User>) eval(reader, vars);
//        for (User user : users.values()) {
//            taskSession.addUser(user);
//        }
//        reader = new InputStreamReader(HumanTaskServiceFactory.class.getResourceAsStream("LoadGroups.mvel"));
//        Map<String, Group> groups = (Map<String, Group>) eval(reader, vars);
//        for (Group group : groups.values()) {
//            taskSession.addGroup(group);
//        }

        return taskServiceObject;
    }

    public static Object eval(Reader reader, Map vars) {
        try {
            return eval(readerToString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    public static String readerToString(Reader reader) throws IOException {
        int charValue = 0;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != -1) {
            //result = result + (char) charValue;
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    public static Object eval(String str, Map vars) {
        ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("java.util");

        context.addImport("AccessType", AccessType.class);
        context.addImport("AllowedToDelegate", AllowedToDelegate.class);
        context.addImport("Attachment", Attachment.class);
        context.addImport("BooleanExpression", BooleanExpression.class);
        context.addImport("Comment", Comment.class);
        context.addImport("Deadline", Deadline.class);
        context.addImport("Deadlines", Deadlines.class);
        context.addImport("Delegation", Delegation.class);
        context.addImport("Escalation", Escalation.class);
        context.addImport("Group", Group.class);
        context.addImport("I18NText", I18NText.class);
        context.addImport("Notification", Notification.class);
        context.addImport("OrganizationalEntity", OrganizationalEntity.class);
        context.addImport("PeopleAssignments", PeopleAssignments.class);
        context.addImport("Reassignment", Reassignment.class);
        context.addImport("Status", Status.class);
        context.addImport("Task", Task.class);
        context.addImport("TaskData", TaskData.class);
        context.addImport("TaskSummary", TaskSummary.class);
        context.addImport("User", User.class);

        return MVEL.executeExpression(compiler.compile(context), vars);
    }
}
