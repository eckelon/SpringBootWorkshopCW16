package net.eckelon.livedemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.HttpStatus;
import org.springframework.scripting.config.LangNamespaceHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class LivedemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivedemoApplication.class, args);
	}

    @Bean
    CommandLineRunner runner(PersonRestRepository repository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                List<Person> people = Arrays.asList(
                        new Person("Edu", 3222),
                        new Person("Marco", 3343),
                        new Person("Foo", 3454),
                        new Person("Bar", 34355)
                );

                repository.save(people);

                for (Person p : repository.findAll()) {
                    System.out.println(p.toString());
                }
            }
        };
    }

    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                configurableEmbeddedServletContainer.addErrorPages(error404Page);
            }
        };
    }
}

@Controller
class PersonController {
    @RequestMapping("/")
    String people(Model model) {
        model.addAttribute("people", repository.findAll());
        return "people";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    String add(Model model) {
        return "add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    String add(@RequestParam String name, @RequestParam Integer mobile) {
        Person p = new Person(name, mobile);
        repository.save(p);

        return "redirect:/";
    }

    @Autowired
    PersonRestRepository repository;
}


//@RestController
//class PersonRestController {
//
//    @RequestMapping("/people")
//    Collection<Person> listPeople() {
//        return personRestRepository.findAll();
//    }
//
//    @Autowired
//    PersonRestRepository personRestRepository;
//
//}

@RepositoryRestResource
interface PersonRestRepository extends JpaRepository<Person, Long> {
    Collection<Person> findByName(@Param("name") String name);
    Collection<Person> findByMobile(@Param("mobile") int mobile);
}

@Entity
class Person {

    public Person() {}

    public Person(String name, int mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    @Id @GeneratedValue
    Long id;
    String name;
    int mobile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile=" + mobile +
                '}';
    }
}
