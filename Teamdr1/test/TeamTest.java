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

import controllers.Team;

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
public class TeamTest extends WithApplication {

    @Test
    public void simplerCheck() {
        int a = 1 * 1;
        assertEquals(1, a);
    }



    //Testing Team-related routes
    @Test
    public void testTeamRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/team");

        Result result = route(request);
        assertEquals(303, result.status());
    }

    @Test
    public void testLeaveTeamRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/leaveTeam");

        Result result = route(request);
        assertEquals(400, result.status());
    }

    @Test
    public void testCreateTeamRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/createTeam");

        Result result = route(request);
        assertEquals(303, result.status());
    }

    @Test
    public void testTeamDetailRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/teamDetails");

        Result result = route(request);
        assertEquals(400, result.status());
    }


}
