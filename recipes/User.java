package recipes;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Recipe> recipes = new ArrayList<>();

    public void deleteRecipe(Recipe recipe) {
        this.recipes.remove(recipe);
    }

    @NotBlank
    @Pattern(regexp=".+@.+\\..+")
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(name = "password")
    private String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Recipe recipe) {
        this.recipes.add(recipe);
    }
}
