package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@Validated
public class TaskController {

    private long userCounter = 1;

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;


    @PostMapping("/api/recipe/new")
    public Map<String, Long> newRecipe(@Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails details) {
        recipe.setDateTime(LocalDateTime.now());
        User tempUser = userService.getUserByEmail(details.getUsername()).get();
        recipe.setUser(tempUser);
        recipeService.saveRecipe(recipe);
        return new HashMap<>(1) {{
            put("id", recipe.getId());
        }};
    }

    @PostMapping("/api/register")
    public void newUser(@Valid @RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            user.setId(userCounter);
            user.setPassword(encoder.encode(user.getPassword()));
            userService.saveUser(user);
            userCounter++;
            throw new ResponseStatusException(HttpStatus.OK);
        }
    }

    @PutMapping("/api/recipe/{id}")
    public void updateRecipe(@Valid @RequestBody Recipe recipe, @PathVariable Long id, @AuthenticationPrincipal UserDetails details) throws ResponseStatusException {
        if (recipeService.getRecipeById(id).isPresent()) {
            Recipe tempRecipe = recipeService.getRecipeById(id).get();
            User tempUser = userService.getUserByEmail(details.getUsername()).get();
            if (tempRecipe.getUser().equals(tempUser)) {
                Recipe recipeFound = recipeService.getRecipeById(id).get();
                recipeFound.setName(recipe.getName());
                recipeFound.setCategory(recipe.getCategory());
                recipeFound.setDescription(recipe.getDescription());
                recipeFound.setIngredients(recipe.getIngredients());
                recipeFound.setDirections(recipe.getDirections());
                recipeService.saveRecipe(recipeFound);
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/api/recipe/{id}")
    public Map<String, Object> getRecipeById(@PathVariable Long id) {
        try {
            if (recipeService.getRecipeById(id).isPresent()) {
                Recipe recipeFound = recipeService.getRecipeById(id).get();
                Map<String, Object> recipeToReturn = new LinkedHashMap<>();
                recipeToReturn.put("name", recipeFound.getName());
                recipeToReturn.put("category", recipeFound.getCategory());
                recipeToReturn.put("date", recipeFound.getDateTime());
                recipeToReturn.put("description", recipeFound.getDescription());
                recipeToReturn.put("ingredients", recipeFound.getIngredients());
                recipeToReturn.put("directions", recipeFound.getDirections());
                return recipeToReturn;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/recipe/search")
    public List<Object> getRecipeByParam(@RequestParam(required = false) Optional<String> category, @RequestParam(required = false) Optional<String> name) {
        if (category.isPresent() && name.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (category.isEmpty() && name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (category.isPresent()) {
            List<Recipe> tempRecipe = recipeService.getRecipesByCategory(category.get());
            List<Object> listToReturn = new ArrayList<>();
            for (int i = 0; i < tempRecipe.size(); i++) {
                Map<String, Object> recipeToReturn = new LinkedHashMap<>();
                recipeToReturn.put("name", tempRecipe.get(i).getName());
                recipeToReturn.put("category", tempRecipe.get(i).getCategory());
                recipeToReturn.put("date", tempRecipe.get(i).getDateTime());
                recipeToReturn.put("description", tempRecipe.get(i).getDescription());
                recipeToReturn.put("ingredients", tempRecipe.get(i).getIngredients());
                recipeToReturn.put("directions", tempRecipe.get(i).getDirections());
                listToReturn.add(recipeToReturn);
            }
            return listToReturn;
        } else if (name.isPresent()) {
            List<Recipe> tempRecipe = recipeService.getRecipesByName(name.get());
            List<Object> listToReturn = new ArrayList<>();
            for (int i = 0; i < tempRecipe.size(); i++) {
                Map<String, Object> recipeToReturn = new LinkedHashMap<>();
                recipeToReturn.put("name", tempRecipe.get(i).getName());
                recipeToReturn.put("category", tempRecipe.get(i).getCategory());
                recipeToReturn.put("date", tempRecipe.get(i).getDateTime());
                recipeToReturn.put("description", tempRecipe.get(i).getDescription());
                recipeToReturn.put("ingredients", tempRecipe.get(i).getIngredients());
                recipeToReturn.put("directions", tempRecipe.get(i).getDirections());
                listToReturn.add(recipeToReturn);
            }
            return listToReturn;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/recipe/all")
    public List<Recipe> getAllRecipe() {
        List<Recipe> recipeAll = new ArrayList<>();
        recipeAll = recipeService.getAllRecipes();
        return recipeAll;
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<?> deleteRecipe(@Valid @PathVariable Long id, @AuthenticationPrincipal UserDetails details) {
        if (recipeService.getRecipeById(id).isPresent()) {
            Recipe tempRecipe = recipeService.getRecipeById(id).get();
            User tempUser = userService.getUserByEmail(details.getUsername()).get();
            if (tempRecipe.getUser().equals(tempUser)) {
                userService.getUserByEmail(details.getUsername()).get().deleteRecipe(recipeService.getRecipeById(id).get());
                recipeService.deleteRecipeById(id);
                return ResponseEntity.noContent().build();
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/recipe/deleteALL")
    public void deleteRecipe() {
        recipeService.deleteAllRecipe();
    }
}

