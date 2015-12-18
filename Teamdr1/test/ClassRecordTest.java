import models.ClassRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import play.mvc.Http.RequestBuilder;

import play.db.Database;
import play.db.Databases;
import play.db.evolutions.*;
import java.sql.Connection;



/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ClassRecordTest extends WithApplication {

    Database database;

    @Before
    public void setupDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
            1,
            "create table test (id bigint not null, name varchar(255));",
            "drop table test;"
        )));
    }

    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Test
    public void classRecordTest() {
		ClassRecord r = new ClassRecord();
		assertEquals(r.classID, null);
		assertEquals(r.className, null);
		
		String classID = "COMS4156";
		String className = "Advanced Software Engineering";
        r = new ClassRecord(classID, className);
		assertEquals(r.classID, classID);
		assertEquals(r.className, className);
    }

    @Test
    public void createNewClassTest() {
      ClassRecord.createNewClass("classID", "className");

      assertTrue(ClassRecord.exists("classID"));

      ClassRecord.deleteClass("classID");

    }

    @Test
    public void getNumClassesTest() {

      ClassRecord.createNewClass("classID", "className");

      int classSize = ClassRecord.getNumClasses();
      assertTrue(classSize >0);


      ClassRecord.deleteClass("classID");

    }

    @Test
    public void testClassFindList() {

      ClassRecord.createNewClass("classID", "className");
      ClassRecord.createNewClass("classID2", "className2");


      List<ClassRecord> class_list = ClassRecord.findAll();

      // System.out.println(acc_list.getClass().getName());

      assertEquals("com.avaje.ebean.common.BeanList", class_list.getClass().getName());

      ClassRecord.deleteClass("classID");
      ClassRecord.deleteClass("classID2");

    }
	
}
