package user_management;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserRequest {

  @NotEmpty
  private String firstName;

  @NotEmpty
  @Size(max = 20)
  private String lastName;

  @NotEmpty
  private String dateOfBirth;

  @NotEmpty
  private String email;
}
