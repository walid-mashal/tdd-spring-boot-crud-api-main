package user_management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserInitializer implements CommandLineRunner {

  private final UserRepository userRepository;

  public UserInitializer(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    log.info("Starting user initialization ...");

    for (int i = 0; i < 10; i++) {

      User user = new User();
      user.setFirstName("First Name " + (i + 1));
      user.setLastName("Last Name " + (i + 1));
      user.setDateOfBirth("2000-05-05");
      user.setEmail("w"+ (i + 1) +"@w.w");

      userRepository.save(user);
    }

    log.info("... finished user initialization");

  }
}
