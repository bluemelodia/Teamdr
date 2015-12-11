import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;
// import javaGuide.tests.controllers.Application;
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
import models.UserAccount;


import com.avaje.ebean.Ebean;


// import com.google.common.collect.ImmutableMap;
/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class AccountTest extends WithApplication{


    // @Test
    // public void simplestCheck() {

    //     int a = 5 * 1;
    //     assertEquals(5, a);
    // }

    @Test
    public void testExists() {

    	UserAccount acc = new UserAccount();
    	acc.username = "username";
    	acc.password = "password";
    	Ebean.save(acc);

    	Boolean exists = UserAccount.exists("username");
    	assertTrue(exists);

    	Ebean.delete(acc);
    }

    @Test
    public void testGetUser() {

    	UserAccount acc = new UserAccount();
    	acc.username = "username";
    	acc.password = "password";
    	Ebean.save(acc);

    	UserAccount acc_2 = UserAccount.getUser("username");
    	assertEquals(acc, acc_2);

    	Ebean.delete(acc);

    }

    @Test
    public void testFalseExists() {
      Boolean acc = UserAccount.exists("fake_username");

      assertFalse(acc);
    }

    @Test
    public void testInvalidExists() {
      Boolean acc = UserAccount.exists("2234234");

      assertFalse(acc);
    }

    @Test
    public void testFindList() {

      UserAccount acc = new UserAccount();
      acc.username = "username";
      acc.password = "password";
      Ebean.save(acc);


      UserAccount acc2 = new UserAccount();
      acc2.username = "username2";
      acc2.password = "password2";
      Ebean.save(acc2);

      List<UserAccount> acc_list = UserAccount.findAll();

      // System.out.println(acc_list.getClass().getName());

      assertEquals("com.avaje.ebean.common.BeanList", acc_list.getClass().getName());

      Ebean.delete(acc);
      Ebean.delete(acc2);

    }

    @Test
    public void testAllClasses() {

      UserAccount acc = new UserAccount();
      acc.username = "username";
      acc.password = "password";
      acc.allClasses = "4111";
      Ebean.save(acc);

      String acc_classes = UserAccount.allClasses("username");
      assertEquals("4111", acc_classes);

      Ebean.delete(acc);

    }

    @Test
    public void testClassList() {

      UserAccount acc = new UserAccount();
      acc.username = "username";
      acc.password = "password";
      acc.allClasses = "4111";
      Ebean.save(acc);

      List<String> acc_classes = acc.getClassList();
      List<String> comp_acc_classes = new ArrayList<String>();
      comp_acc_classes.add("4111");

      assertEquals(comp_acc_classes, acc_classes);

      Ebean.delete(acc);

    }

    @Test
    public void testAddClass() {

      UserAccount acc = new UserAccount();
      acc.username = "username";
      acc.password = "password";
      // acc.allClasses = "4111";
      Ebean.save(acc);

      acc.addClass("4111");

      assertEquals("4111", UserAccount.allClasses("username"));

      Ebean.delete(acc);

    }


    @Test
    public void testRemoveClass() {

      UserAccount acc = new UserAccount();
      acc.username = "username";
      acc.password = "password";
      acc.allClasses = "4111";
      Ebean.save(acc);

      acc.removeClass("4111");

      assertEquals("", UserAccount.allClasses("username"));

      Ebean.delete(acc);

    }


	@Test
  	public void testLogin() {
    	// Result result = new Application().index();
    	RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/login");

        Result result = route(request);
        // System.out.println(contentAsString(result));

    	assertEquals(OK, result.status());
    	assertEquals("text/html", result.contentType());
    	assertEquals("utf-8", result.charset());
    	assertTrue(contentAsString(result).contains("Username"));

  	}

  	//test Account Routes

  	@Test
    public void testSignupRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/signup");

        Result result = route(request);
        assertEquals(200, result.status());
    }

  	@Test
    public void testLoginRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/login");

        Result result = route(request);
        assertEquals(200, result.status());
    }

  	@Test
    public void testLogoutRoute() {
        RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/logout");

        Result result = route(request);
        assertEquals(303, result.status());
    }



}
