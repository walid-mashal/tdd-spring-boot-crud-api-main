package user_management;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Long createNewUser(UserRequest userRequest) {
    User user = new User();
    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    user.setDateOfBirth(userRequest.getDateOfBirth());

    user = userRepository.save(user);

    return user.getId();
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(Long id) {
    Optional<User> requestedUser = userRepository.findById(id);

    if (requestedUser.isEmpty()) {
      throw new UserNotFoundException(String.format("User with id: '%s' not found", id));
    }

    return requestedUser.get();
  }

  @Transactional
  public User updateUser(Long id, UserRequest userToUpdateRequest) {

    Optional<User> userFromDatabase = userRepository.findById(id);

    if (userFromDatabase.isEmpty()) {
      throw new UserNotFoundException(String.format("User with id: '%s' not found", id));
    }

    User userToUpdate = userFromDatabase.get();

    userToUpdate.setFirstName(userToUpdateRequest.getFirstName());
    userToUpdate.setLastName(userToUpdateRequest.getLastName());
    userToUpdate.setDateOfBirth(userToUpdateRequest.getDateOfBirth());

    return userToUpdate;
  }

  public void deleteUserById(Long id) {
    userRepository.deleteById(id);
  }
}
