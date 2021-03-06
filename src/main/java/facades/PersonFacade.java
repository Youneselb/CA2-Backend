package facades;

import dto.CityInfoDTO;
import dto.HobbyDTO;
import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import utils.EMF_Creator;

public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    public List<PersonDTO> getAllPersons() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> persons = query.getResultList();
        List<PersonDTO> personsDTOs = new ArrayList();
        persons.forEach((Person person) -> {
            personsDTOs.add(new PersonDTO(person));
        });
        return personsDTOs;
    }
    
    public List<PersonDTO> getPersonsByHobby(String hobby) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT h.persons FROM Hobby h WHERE h.name = :hobby");
        query.setParameter("hobby", hobby);
        List<Person> persons = query.getResultList();
        List<PersonDTO> personsDTOs = new ArrayList();
        persons.forEach((Person person) -> personsDTOs.add(new PersonDTO(person)));
        return personsDTOs;

    }
    
    public List<HobbyDTO> getHobbies() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Hobby> query = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
        List<Hobby> hobbies = query.getResultList();
        List<HobbyDTO> hobbyDTOList = new ArrayList<>();
        hobbies.forEach((Hobby hobby) -> hobbyDTOList.add(new HobbyDTO(hobby)));
        return hobbyDTOList;
    }

    public List<CityInfoDTO> getAllZipCodes() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> query = em.createQuery("SELECT c FROM CityInfo c", CityInfo.class);
        List<CityInfo> cityinfos = query.getResultList();
        List<CityInfoDTO> zipcodes = new ArrayList();
        cityinfos.forEach((CityInfo cityinfo) -> {
            zipcodes.add(new CityInfoDTO(cityinfo));
        });
        return zipcodes;

    }

    public PersonDTO addPerson(String email, String fName, String lName, String street, String additionalinfo,
            int number, String description, String cityinfo, String hobby) throws MissingInputException {
        if ((fName.length() == 0) || (lName.length() == 0)) {
            throw new MissingInputException("First Name and/or last name is missing");
        }
        EntityManager em = getEntityManager();
        Person person = new Person(email, fName, lName);

        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.addtionalinfo = :addtionalinfo");
			query.setParameter("street", street);
			query.setParameter("additionalinfo",additionalinfo);
                        Query query2 = em.createQuery("SELECT p FROM Phone p WHERE p.number = :number AND p.description = :description");
			query2.setParameter("number", number);
			query2.setParameter("description",description);
			List<Address> addresses = query.getResultList();
			if(addresses.size() > 0){
		           person.setAddress(addresses.get(0));
			} else {
			   person.setAddress(new Address(street,additionalinfo, em.find(CityInfo.class, cityinfo)));
			}
                        List<Phone> phones = query2.getResultList();
                        if(phones.size() > 0){
		           person.addPhone(phones.get(0));
			} else {                           
                           person.addPhone(new Phone(number,description));
                           
			}
                        person.addHobby(em.find(Hobby.class, hobby));
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public List<PersonDTO> getPersonsByCity(String city) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM Person p JOIN p.address a JOIN a.cityinfo c WHERE c.city = :city");
        query.setParameter("city", city);
        List<Person> persons = query.getResultList();
        List<PersonDTO> personsDTOs = new ArrayList();
        persons.forEach((Person person) -> personsDTOs.add(new PersonDTO(person)));
        return personsDTOs;

    }

    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        if ((p.getfName().length() == 0) || (p.getlName().length() == 0)) {
            throw new MissingInputException("First name and/or Last Name is missing");
        }

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            Person person = em.find(Person.class, p.getId());
            if (person == null) {
                throw new PersonNotFoundException(String.format("Person with id: (%d) not found", p.getId()));
            } else {
                person.setEmail(p.getEmail());
                person.setfName(p.getfName());
                person.setlName(p.getlName());
            }
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

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

        Person p1 = new Person("someemail", "inferno", "mirage");
        Phone ph1 = new Phone(25252525, "yes");
        Address a1 = new Address("groovestreet", "yes", em.find(CityInfo.class, "0800"));

        p1.setAddress(a1);
        p1.addPhone(ph1);
        p1.addHobby(em.find(Hobby.class, "Airsoft"));
        //addPerson("addpersonemail","Nuke","Train","bhops","script",30303030,"+45","2765","Trapshooting");
        try {
            em.getTransaction().begin();
            em.persist(p1);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
//        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
//        EntityManager em = emf.createEntityManager();
//        Person p1 = new Person("someemail", "inferno", "mirage");
//        Phone ph1 = new Phone(25252525, "yes");
//        Address a1 = new Address("groovestreet", "yes", em.find(CityInfo.class, "0800"));
//
//        p1.setAddress(a1);
//        p1.addPhone(ph1);
//        p1.addHobby(em.find(Hobby.class, "Airsoft"));
//
//        try {
//            em.getTransaction().begin();
//            em.persist(p1);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//        PersonFacade pf = getPersonFacade(emf);
//        System.out.println(pf.getPersonsByHobby("Airsoft"));
    }

}
