package models;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class UserAccount extends Model {
    @Id
    public String username; // Primary key
    public String password;
}