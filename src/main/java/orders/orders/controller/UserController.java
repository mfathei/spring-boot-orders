package orders.orders.controller;

import orders.orders.model.User;
import orders.orders.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = this.userService.findAllUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User result = this.userService.createUser(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
