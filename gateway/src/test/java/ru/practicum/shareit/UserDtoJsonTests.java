package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTests {

    @Autowired
    private JacksonTester<UserRequestDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setId(1L);
        userRequestDto.setName("testName");
        userRequestDto.setEmail("test@example.com");

        assertThat(json.write(userRequestDto)).isEqualToJson("userRequestDto.json");
        assertThat(json.write(userRequestDto)).hasJsonPath("$.id");
        assertThat(json.write(userRequestDto)).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(json.write(userRequestDto)).hasJsonPath("$.name");
        assertThat(json.write(userRequestDto)).extractingJsonPathValue("$.name").isEqualTo("testName");
        assertThat(json.write(userRequestDto)).hasJsonPath("$.email");
        assertThat(json.write(userRequestDto)).extractingJsonPathValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"testName\", \"email\": \"test@example.com\"}";
        UserRequestDto userRequestDto = json.parseObject(jsonContent);

        assertThat(userRequestDto).isNotNull();
        assertThat(userRequestDto.getId()).isEqualTo(1L);
        assertThat(userRequestDto.getName()).isEqualTo("testName");
        assertThat(userRequestDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testInvalidEmail() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testName");
        userRequestDto.setEmail("invalid-email");

        assertThat(json.write(userRequestDto)).isEqualToJson("invalidUserRequestDto.json");
        assertThat(json.write(userRequestDto)).hasJsonPath("$.email");
    }
}
