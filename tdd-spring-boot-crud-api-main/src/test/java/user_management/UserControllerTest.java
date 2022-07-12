package user_management;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Captor
  private ArgumentCaptor<UserRequest> userRequestArgumentCaptor;

  @Test
  public void postingANewUserShouldCreateANewUser() throws Exception {

    when(userService.createNewUser(userRequestArgumentCaptor.capture())).thenReturn(1L);

    this.mockMvc
      .perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
           "first_name": "first name",
           "last_name": "last name",
           "date_of_birth": "2000-03-05"
          }
          """))
      .andExpect(status().isCreated())
      .andExpect(header().exists("Location"))
      .andExpect(header().string("Location", "http://localhost/api/users/1"));

    assertThat(userRequestArgumentCaptor.getValue().getFirstName(), is("first name"));
    assertThat(userRequestArgumentCaptor.getValue().getLastName(), is("last name"));
    assertThat(userRequestArgumentCaptor.getValue().getDateOfBirth(), is("2000-03-05"));

  }

  @Test
  public void allUsersEndpointShouldReturnTwoUsers() throws Exception {

    when(userService.getAllUsers()).thenReturn(List.of(
      createUser(1L, "Java 11", "Duke", "1337"),
      createUser(2L, "Java EE 8", "Duke", "1338")));

    this.mockMvc
      .perform(get("/api/users"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].first_name", is("Java 11")))
      .andExpect(jsonPath("$[0].last_name", is("Duke")))
      .andExpect(jsonPath("$[0].date_of_birth", is("1337")))
      .andExpect(jsonPath("$[0].id", is(1)));

  }

  @Test
  public void getUserWithIdOneShouldReturnAUser() throws Exception {

    when(userService.getUserById(1L)).thenReturn(createUser(1L, "Java 11", "Duke", "1337"));

    this.mockMvc
      .perform(get("/api/users/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.first_name", is("Java 11")))
      .andExpect(jsonPath("$.last_name", is("Duke")))
      .andExpect(jsonPath("$.date_of_birth", is("1337")))
      .andExpect(jsonPath("$.id", is(1)));

  }

  @Test
  public void getUserWithUnknownIdShouldReturn404() throws Exception {

    when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("User with id '1' not found"));

    this.mockMvc
      .perform(get("/api/users/1"))
      .andExpect(status().isNotFound());

  }

  @Test
  public void updateUserWithKnownIdShouldUpdateTheUser() throws Exception {

    when(userService.updateUser(eq(1L), userRequestArgumentCaptor.capture()))
      .thenReturn(createUser(1L, "Java 15", "Duke", "1337"));

    this.mockMvc
      .perform(put("/api/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
           "first_name": "Duke",
           "last_name": "mark",
           "date_of_birth": "1999-09-09"
          }
          """))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.first_name", is("Duke")))
      .andExpect(jsonPath("$.last_name", is("mark")))
      .andExpect(jsonPath("$.date_of_birth", is("1999-09-09")));

    assertThat(userRequestArgumentCaptor.getValue().getFirstName(), is("Duke"));
    assertThat(userRequestArgumentCaptor.getValue().getLastName(), is("1337"));
    assertThat(userRequestArgumentCaptor.getValue().getDateOfBirth(), is("Java 15"));

  }

  @Test
  public void updateUserWithUnknownIdShouldReturn404() throws Exception {

    when(userService.updateUser(eq(42L), userRequestArgumentCaptor.capture()))
      .thenThrow(new UserNotFoundException("The user with id '42' was not found"));

    this.mockMvc
      .perform(put("/api/users/42")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
          """
            {
             "first_name": "Duke",
             "last_name": "1337",
             "date_of_birth": "Java 12"
            }
            """))
      .andExpect(status().isNotFound());

  }

  private User createUser(Long id, String first_name, String last_name, String date_of_birth) {
    User user = new User();
    user.setFirstName(first_name);
    user.setLastName(last_name);
    user.setDateOfBirth(date_of_birth);
    user.setId(id);
    return user;
  }
}
