package com.kett.TicketSystem.project.domain;

import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({ "test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectTests {
    private final ProjectRepository projectRepository;

    private String validName0;
    private String validName1;
    private String validName2;
    private String validName3;

    private String emptyName;
    private String nullName;

    private String description0;
    private String description1;
    private String description2;
    private String description3;

    private String emptyDescription;
    private String nullDescription;

    private Project project0;
    private Project project1;
    private Project project2;
    private Project project3;
    private Project emptyDescriptionProject;
    private Project nullDescriptionProject;

    @Autowired
    public ProjectTests(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    @BeforeEach
    public void buildUp() {
        validName0 = "Develop new Javascript Framework";
        validName1 = "Calculate TSM in O(n)";
        validName2 = "Do Stuff.";
        validName3 = "Get some coffee";

        emptyName = "";
        nullName = null;

        description0 = "Something better than Angular and React.";
        description1 = "I mean, it can't be too hard right?";
        description2 = "The same procedure as every year James.";
        description3 = "Black, not from Starbucks.";

        emptyDescription = "";
        nullDescription = null;

        project0 = new Project(validName0, description0);
        project1 = new Project(validName1, description1);
        project2 = new Project(validName2, description2);
        project3 = new Project(validName3, description3);

        emptyDescriptionProject = new Project(validName0, emptyDescription);
        nullDescriptionProject = new Project(validName1, nullDescription);
    }

    @AfterEach
    public void tearDown() {
        validName0 = null;
        validName1 = null;
        validName2 = null;
        validName3 = null;

        emptyName = null;
        nullName = null;

        description0 = null;
        description1 = null;
        description2 = null;
        description3 = null;

        project0 = null;
        project1 = null;
        project2 = null;
        project3 = null;
        emptyDescriptionProject = null;
        nullDescriptionProject = null;

        projectRepository.deleteAll();
    }

    @Test
    public void checkValidConstructorParameters() {
        new Project(validName0, description0);
        new Project(validName1, description1);
        new Project(validName2, description2);
        new Project(validName3, description3);

        new Project(validName0, emptyDescription);
        new Project(validName1, emptyDescription);
        new Project(validName2, emptyDescription);
        new Project(validName3, emptyDescription);

        new Project(validName0, nullDescription);
        new Project(validName1, nullDescription);
        new Project(validName2, nullDescription);
        new Project(validName3, nullDescription);
    }

    @Test
    public void checkInvalidConstructorParameters() {
        assertThrows(ProjectException.class, () -> new Project(emptyName, description0));
        assertThrows(ProjectException.class, () -> new Project(emptyName, description1));
        assertThrows(ProjectException.class, () -> new Project(emptyName, description2));
        assertThrows(ProjectException.class, () -> new Project(emptyName, description3));

        assertThrows(ProjectException.class, () -> new Project(nullName, description0));
        assertThrows(ProjectException.class, () -> new Project(nullName, description1));
        assertThrows(ProjectException.class, () -> new Project(nullName, description2));
        assertThrows(ProjectException.class, () -> new Project(nullName, description3));
    }

    @Test
    public void checkEquals() {
        // without id
        assertEquals(project0, project0);
        assertEquals(project1, project1);
        assertEquals(project2, project2);
        assertEquals(project3, project3);
        assertEquals(emptyDescriptionProject, emptyDescriptionProject);
        assertEquals(nullDescriptionProject, nullDescriptionProject);

        // add id
        projectRepository.save(project0);
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);
        projectRepository.save(emptyDescriptionProject);
        projectRepository.save(nullDescriptionProject);

        // with id
        assertEquals(project0, project0);
        assertEquals(project1, project1);
        assertEquals(project2, project2);
        assertEquals(project3, project3);
        assertEquals(emptyDescriptionProject, emptyDescriptionProject);
        assertEquals(nullDescriptionProject, nullDescriptionProject);
    }

    @Test
    public void checkNotEquals() {
        Project project0Copy = new Project(validName0, description0);
        Project project1Copy = new Project(validName1, description1);
        Project project2Copy = new Project(validName2, description2);
        Project project3Copy = new Project(validName3, description3);
        Project emptyDescriptionProjectCopy = new Project(validName0, emptyDescription);
        Project nullDescriptionProjectCopy = new Project(validName1, nullDescription);

        // without id
        assertNotEquals(project0, project0Copy);
        assertNotEquals(project1, project1Copy);
        assertNotEquals(project2, project2Copy);
        assertNotEquals(project3, project3Copy);

        assertNotEquals(project0, project1);
        assertNotEquals(project1, project2);
        assertNotEquals(project2, project3);
        assertNotEquals(project3, project0);

        assertNotEquals(emptyDescriptionProject, emptyDescriptionProjectCopy);
        assertNotEquals(nullDescriptionProject, nullDescriptionProjectCopy);

        assertNotEquals(emptyDescriptionProject, project0);
        assertNotEquals(nullDescriptionProject, project1);
        assertNotEquals(emptyDescriptionProject, project2);
        assertNotEquals(nullDescriptionProject, project3);


        // add id
        projectRepository.save(project0);
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        projectRepository.save(emptyDescriptionProject);
        projectRepository.save(nullDescriptionProject);

        projectRepository.save(project0Copy);
        projectRepository.save(project1Copy);
        projectRepository.save(project2Copy);
        projectRepository.save(project3Copy);

        projectRepository.save(emptyDescriptionProjectCopy);
        projectRepository.save(nullDescriptionProjectCopy);

        // with id
        assertNotEquals(project0, project0Copy);
        assertNotEquals(project1, project1Copy);
        assertNotEquals(project2, project2Copy);
        assertNotEquals(project3, project3Copy);

        assertNotEquals(project0, project1);
        assertNotEquals(project1, project2);
        assertNotEquals(project2, project3);
        assertNotEquals(project3, project0);

        assertNotEquals(emptyDescriptionProject, emptyDescriptionProjectCopy);
        assertNotEquals(nullDescriptionProject, nullDescriptionProjectCopy);

        assertNotEquals(emptyDescriptionProject, nullDescriptionProject);
        assertNotEquals(emptyDescriptionProject, project0);
        assertNotEquals(nullDescriptionProject, project1);
        assertNotEquals(emptyDescriptionProject, project2);
        assertNotEquals(nullDescriptionProject, project3);
    }

    @Test
    public void checkName() {
        assertEquals(validName0, project0.getName());
        assertEquals(validName1, project1.getName());
        assertEquals(validName2, project2.getName());
        assertEquals(validName3, project3.getName());

        assertNotEquals(validName1, project0.getName());
        assertNotEquals(validName2, project1.getName());
        assertNotEquals(validName3, project2.getName());
        assertNotEquals(validName0, project3.getName());
    }

    @Test
    public void checkSetNameEmpty() {
        assertThrows(ProjectException.class, () -> project0.setName(""));
        assertThrows(ProjectException.class, () -> project1.setName(""));
        assertThrows(ProjectException.class, () -> project2.setName(""));
        assertThrows(ProjectException.class, () -> project3.setName(""));
        assertThrows(ProjectException.class, () -> emptyDescriptionProject.setName(""));
        assertThrows(ProjectException.class, () -> nullDescriptionProject.setName(""));
    }

    @Test
    public void checkSetNameNull() {
        assertThrows(ProjectException.class, () -> project0.setName(null));
        assertThrows(ProjectException.class, () -> project1.setName(null));
        assertThrows(ProjectException.class, () -> project2.setName(null));
        assertThrows(ProjectException.class, () -> project3.setName(null));
        assertThrows(ProjectException.class, () -> emptyDescriptionProject.setName(null));
        assertThrows(ProjectException.class, () -> nullDescriptionProject.setName(null));
    }

    @Test
    public void checkDescription() {
        assertEquals(description0, project0.getDescription());
        assertEquals(description1, project1.getDescription());
        assertEquals(description2, project2.getDescription());
        assertEquals(description3, project3.getDescription());

        assertEquals(emptyDescription, emptyDescriptionProject.getDescription());
        assertEquals(nullDescription, nullDescriptionProject.getDescription());

        assertNotEquals(description1, project0.getDescription());
        assertNotEquals(description2, project1.getDescription());
        assertNotEquals(description3, project2.getDescription());
        assertNotEquals(description0, project3.getDescription());

        assertNotEquals(emptyDescription, project0.getDescription());
        assertNotEquals(emptyDescription, project1.getDescription());
        assertNotEquals(emptyDescription, project2.getDescription());
        assertNotEquals(emptyDescription, project3.getDescription());

        assertNotEquals(nullDescription, project0.getDescription());
        assertNotEquals(nullDescription, project1.getDescription());
        assertNotEquals(nullDescription, project2.getDescription());
        assertNotEquals(nullDescription, project3.getDescription());

        assertNotEquals(description0, emptyDescriptionProject.getDescription());
        assertNotEquals(description1, emptyDescriptionProject.getDescription());
        assertNotEquals(description2, nullDescriptionProject.getDescription());
        assertNotEquals(description3, nullDescriptionProject.getDescription());
    }
}
