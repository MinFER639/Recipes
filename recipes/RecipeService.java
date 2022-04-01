package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepository recipeRepository;


    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeAll = new ArrayList<>();
        recipeRepository.findAll().forEach(recipeAll::add);
        return recipeAll;
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return new ArrayList<>(recipeRepository.findByCategoryIgnoreCaseOrderByDateTimeDesc(category));
    }

    public List<Recipe> getRecipesByName(String name) {
        return new ArrayList<>(recipeRepository.findByNameContainingIgnoreCaseOrderByDateTimeDesc(name));
    }

    public Optional<Recipe> getRecipeById(Long id) {
        if (recipeRepository.findById(id).isPresent()) {
            return Optional.of(recipeRepository.findById(id).get());
        } else {
            return Optional.empty();
        }
    }

    public void saveRecipe(Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
    }


    public void deleteRecipeById(Long id) {

        recipeRepository.deleteById(id);

    }

    public void deleteAllRecipe() {
        recipeRepository.deleteAll();
    }
}
