package orders.orders.controller;

import jakarta.validation.Valid;
import orders.orders.dto.CreateUserRequest;
import orders.orders.dto.UserResponse;
import orders.orders.model.User;
import orders.orders.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = this.userService.findAllUsers()
                .stream().map(UserResponse::from).toList();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<User> u = this.userService.findUserById(id);
        return u.map(user -> new ResponseEntity<>(UserResponse.from(user), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setUsername(request.username());
        user.setEnabled(request.enabled());

        User result = this.userService.createUser(user);
        return new ResponseEntity<>(UserResponse.from(result), HttpStatus.CREATED);
    }
}
