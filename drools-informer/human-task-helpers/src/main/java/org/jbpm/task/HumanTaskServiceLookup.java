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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author salaboy
 */
public class HumanTaskServiceLookup {
    private static HumanTaskServiceLookup instance;

    public static HumanTaskServiceLookup getInstance(){
        if(instance == null){
            instance = new HumanTaskServiceLookup();
        }
        return instance;
    }

    public static TaskService lookup() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            TaskService service = (TaskService) envCtx.lookup("bean/HumanTaskService");
            System.out.println("GETTING JNDI TASK SERVICE INSTANCE = " + service);
            return service;
        } catch ( NamingException ne ) {
            System.err.println(ne.getMessage());
            return null;
        }
    }

}
