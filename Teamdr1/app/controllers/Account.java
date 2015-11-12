package controllers;
import models.UserAccount;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import play.libs.Json;
import views.html.*;
import java.util.List;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    // Enables passing of params into the form
    private static final Form<UserAccount> AccountForm = Form.form(UserAccount.class);
    public Result createUser() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<UserAccount> form = AccountForm.bindFromRequest();
        // convert HTML form to an Account model object, containing the params
        UserAccount account = form.get();
        System.out.println(form);
        System.out.println(account.username + " " + account.password); // these are null!
        // save the data sent through HTTP POST
        account.save();
        return redirect(routes.Account.checkExistingUser());
        //return redirect(routes.Profile.viewProfile());
    }

    public Result newAccount() {
        return ok(account.render(AccountForm));
    }

    public Result signIn() {
        return TODO;
    }

    public Result checkExistingUser() {
        List<UserAccount> allUsers = UserAccount.findAll();
        // This isn't getting encoded correctly!
        for (int i = 0; i < allUsers.size(); i++) {
            System.out.println(allUsers.get(i).username);
            System.out.println(allUsers.get(i).password);
        }
        // Iterates through all records, dumping them into JSON format
        return ok(Json.toJson(allUsers));
    }
}