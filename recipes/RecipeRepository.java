package recipes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    Recipe findRecipeById(Long id);

    List<Recipe> findByCategoryIgnoreCaseOrderByDateTimeDesc(String category);

    List<Recipe> findByNameContainingIgnoreCaseOrderByDateTimeDesc(String name);
}



