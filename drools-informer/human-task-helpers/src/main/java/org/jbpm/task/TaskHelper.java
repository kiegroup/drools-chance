package org.jbpm.task;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TaskHelper {
    
    private static TaskService taskService = null;

    public static TaskService getTaskService() {
        if ( taskService == null ) {
            taskService = HumanTaskServiceFactory.createTaskLocalService();
        }
        return taskService;
    }

    public static User addUser( String id ) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        EntityManager em = emf.createEntityManager();
        User user = findUser( id, em );
        if ( user == null ) {
            user = new User( id );
            em.persist( user );
        }
        em.close();
        emf.close();
        return user;
    }
    
    public static User findUser( String id ) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        EntityManager em = emf.createEntityManager();
        User user = findUser( id, em );
        em.close();
        emf.close();
        return user;
    }
    
    protected static User findUser( String id, EntityManager em ) {
        return em.find( User.class, id );                                                                                     
    }


    private static long counter = 0;

    public static long getNextId() {
        return counter++;
    }



    
}
