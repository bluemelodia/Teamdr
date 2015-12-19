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
		assertEquals(p.pic_url, null);
    }

	@Test
    public void updateProfileTest() {
        UserProfile p = new UserProfile();
		String uname = "JohnDoe";
		String email = "john@john.com";
		String d = "I am John.";
		String p1 = "https://upload.wikimedia.org/wikipedia/commons/d/d9/Florida_Box_Turtle_Digon3a.jpg";
		
		p.updateProfile(uname, email, p1, d);
		assertEquals(p.username, uname);
		assertEquals(p.email, email);
		assertEquals(p.description, d);
		assertEquals(p.pic_url, p1);
		
		String uname2 = "JohnDo";
		String email2 = "john@john@john.co";
		String d2 = "I am John";
		String p2 = "http://www.ecology.com/wp-content/uploads/2012/05/eastern-box-turtle.jpg";
		assertNotEquals(p.username, uname2);
		assertNotEquals(p.email, email2);
		assertNotEquals(p.description, d2);
		assertNotEquals(p.pic_url, p2);
		
		p.updateProfile(uname2, email2, p2, d2);
		assertEquals(p.username, uname2);
		assertEquals(p.email, email2);
		assertEquals(p.description, d2);
		assertEquals(p.pic_url, p2);
		
		String uname3 = null;
		String email3 = null;
		String d3 = null;
		String p3 = null;
		p.updateProfile(uname3, email3, p3, d3);
		assertEquals(p.username, null);
		assertEquals(p.email, null);
		assertEquals(p.description, null);
		assertEquals(p.pic_url, null);
    }
	
}
