import models.UserProfile;
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
public class UserProfileTest extends WithApplication {

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
    public void userProfileTest() {
        UserProfile p = new UserProfile();
		assertEquals(p.username, null);
		assertEquals(p.email, null);
		assertEquals(p.description, null);
    }

	@Test
    public void updateProfileTest() {
        UserProfile p = new UserProfile();
		String uname = "JohnDoe";
		String email = "john@john.com";
		String d = "I am John.";
		
		p.updateProfile(uname, email, d);
		assertEquals(p.username, uname);
		assertEquals(p.email, email);
		assertEquals(p.description, d);
		
		String uname2 = "JohnDo";
		String email2 = "john@john@john.co";
		String d2 = "I am John";
		assertNotEquals(p.username, uname2);
		assertNotEquals(p.email, email2);
		assertNotEquals(p.description, d2);
		
		p.updateProfile(uname2, email2, d2);
		assertEquals(p.username, uname2);
		assertEquals(p.email, email2);
		assertEquals(p.description, d2);
		
		String uname3 = null;
		String email3 = null;
		String d3 = null;
		p.updateProfile(uname3, email3, d3);
		assertEquals(p.username, null);
		assertEquals(p.email, null);
		assertEquals(p.description, null);
    }
	
}
