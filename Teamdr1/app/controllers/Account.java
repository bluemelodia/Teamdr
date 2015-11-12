package controllers;
import models.UserAccount;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import views.html.*;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    // Enables passing of params into the form
    private static final Form<Account> AccountForm = Form.form(Account.class);
    public Result createUser() {
        return TODO;
    }

    public Result newAccount() {
        return ok(account.render(AccountForm));
    }

    public Result signIn() {
        return TODO;
    }
}