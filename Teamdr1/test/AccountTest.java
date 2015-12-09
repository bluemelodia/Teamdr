import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;
import javaguide.tests.controllers.Application;
import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import controllers.Team;
import models.TeamRecord;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import play.mvc.Http.RequestBuilder;

import play.db.Database;
import play.db.Databases;
import play.db.evolutions.*;
import java.sql.Connection;

import play.libs.ws.*;
import play.libs.F.*;

import controllers.Team;

import com.google.common.collect.ImmutableMap;
/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class AccountTest extends WithApplication{


    @Test
    public void simplestCheck() {

        int a = 5 * 1;
        assertEquals(5, a);
    }

	@Override
	  protected FakeApplication provideFakeApplication() {
	    return new FakeApplication(new java.io.File("."), Helpers.class.getClassLoader(),
	        ImmutableMap.of("play.http.router", "javaguide.tests.Routes"), new ArrayList<String>(), null);
	  }

	@Test
  	public void testIndex() {
    	Result result = new Application().index();
    	assertEquals(OK, result.status());
    	assertEquals("text/html", result.contentType());
    	assertEquals("utf-8", result.charset());
    	assertTrue(contentAsString(result).contains("Welcome"));
  	}


}
