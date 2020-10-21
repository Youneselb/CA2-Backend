package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    private String lName;
    private String fName;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "hobby_person",
            joinColumns = @JoinColumn(name = "persons_ID"),
            inverseJoinColumns = @JoinColumn(name = "Hobby_ID"))
    private List<Hobby> hobbys;
    @OneToMany(mappedBy = "person")
    private List<Phone> phones;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Address address;

    public Person() {
    }

    public Person(String email, String fName, String lName) {
        this.email = email;
        this.lName = fName;
        this.fName = lName;
        hobbys = new ArrayList<>();
    }

    public void addHobby(Hobby hobby) {
        if (hobby != null) {
            hobbys.add(hobby);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address != null) {
            this.address = address;
            address.addPerson(this);
        } else {
            this.address = null;
        }
    }

    public void addPhone(Phone phone) {
        if (phone != null) {
            phones.add(phone);
        }
    }
}
