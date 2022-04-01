package recipes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + email);
        }

        return new UserDetailsImpl(user);
    }

    public void saveUser(User user) {
        User savedUser = userRepository.save(user);
        //return savedUser.getId();
    }

    public void deleteRecipe(User user, Recipe recipe) {
        user.getRecipes().remove(recipe);
    }

    public Optional<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }
}
