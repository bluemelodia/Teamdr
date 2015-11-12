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
    private static final Form<UserAccount> LoginForm = Form.form(UserAccount.class);
    public Result createUser() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<UserAccount> form = AccountForm.bindFromRequest();
        if (form.hasErrors()) { // Redirect with error
            return badRequest(account.render(form));
        }
        // convert HTML form to an Account model object, containing the params
        UserAccount newAccount = form.get();

        if (UserAccount.exists(newAccount.username)) {
            form.reject("username", "User already exists.");
            return badRequest(account.render(form));
        }
        // save the data sent through HTTP POST
        newAccount.save();
        return redirect(routes.Profile.viewProfile());
    }

    // Get the signup form
    public Result signUp() {
        return ok(account.render(AccountForm));
    }

    public Result signIn() {
        return ok(login.render(LoginForm));
    }

    public Result checkExistingUser() {
        Form<UserAccount> form = LoginForm.bindFromRequest();
        if (form.hasErrors()) { // Redirect with error
            return badRequest(login.render(form));
        }
        UserAccount getAccount = form.get();
        if (!UserAccount.exists(getAccount.username)) {
            form.reject("username", "User does not exist.");
            return badRequest(login.render(form));
        }
        UserAccount getUser = UserAccount.getUser(getAccount.username);
        if (!getUser.password.equals(getAccount.password)) {
            form.reject("password", "Incorrect password.");
            return badRequest(login.render(form));
        }
        return redirect(routes.Profile.viewProfile());
    }
}