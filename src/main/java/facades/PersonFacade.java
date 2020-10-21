package facades;

import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void main(String[] args) {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        
        EntityManager em = emf.createEntityManager();
        
        //Person p1 = new Person("testemail","fornavn","efternavn");
        Person p2 = new Person("someemail","inferno","mirage");
        //Hobby h1 = new Hobby("csgo","wikicsgo","gaming","spil");
        Hobby h2 = new Hobby("csgo","wikicsgo","gaming","spil");
        
        Phone ph1 = new Phone(25252525,"yes");
        Address a1 = new Address("groovestreet","yes");
        //p1.setHobby(h1);
        p2.setHobby(h2);
        p2.addPhone(ph1);
        p2.setAddress(a1);
        try {
            em.getTransaction().begin();
            em.persist(p2);
            //em.persist(p1);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
        
    }


