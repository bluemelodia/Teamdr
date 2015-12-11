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
import models.TeamRecord;
import models.UserAccount;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import play.mvc.Http.RequestBuilder;

import play.db.Database;
import play.db.Databases;
import play.db.evolutions.*;
import java.sql.Connection;

import play.libs.ws.*;
import play.libs.F.*;


import com.avaje.ebean.Ebean;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class TeamTest extends WithApplication {

    // private final WSClient WS;

    @Test
    public void simplerCheck() {
        // List<TeamRecord> b = TeamRecord.findAll();
        // System.out.println(b.get(1).tid);
        int a = 1 * 1;
        assertEquals(1, a);
    }

    // @Test
    // public void testInServer() {
    //     running(testServer(3333), () -> {
    //         assertEquals(OK, WS.url("http://localhost:3333").get().get(1000).getStatus());
    //     });
    // }

    // @Test
    // public void testTeamPage() {

    //     // Result result = new Application().index();
       
    //     // UserAccount ab = new UserAccount();
    //     // ab.username = "Anfal";
    //     // ab.password = "b";
    //     // ab.addProfile("ba", "baa");

    //     // TeamRecord.createTeamRecord("4111", ab, "baaaaa", "COMS4112");

    //     // Team bab = new Team();
    //     // // String r = bab.showError();
    //     // System.out.println(bab.showError());


    //     // RequestBuilder request = new RequestBuilder()
    //     //     .method(GET)
    //     //     .uri("/team");

    //     // Result result = route(request);

    //     // System.out.println("RESULT:" + contentAsString(result));

    //     // Ebean.delete(TeamRecord.getTeam("4111"));

    //     // assertEquals(OK, result.status());
    //     // assertEquals("text/html", result.contentType());
    //     // assertEquals("utf-8", result.charset());
    //     // assertTrue(contentAsString(result).contains("Username"));
    //     // assertTrue(contentAsString(result).contains("Password"));

    // }

    // Testing Team-related routes
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
