package ra.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.model.dto.RegisterDTO;
import ra.model.dto.UserDTO;
import ra.model.entity.User;
import ra.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping( "/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user" , new UserDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestParam("username") String username
            , @RequestParam("password") String password
            , @ModelAttribute("user") UserDTO userDTO, RedirectAttributes redirectAttributes , BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "auth/login";
        }
        userDTO.setUsername(username);
        userDTO.setPassword(password);
        if (username.equals("admin") && password.equals("admin123")) {
            session.setAttribute("userLogin", "admin");
            return "redirect:/admin/products";
        } else {
            Optional<User> user = Optional.ofNullable(userService.login(username, password));
            if (user.isPresent()) {
                session.setAttribute("userLogin", user.get().getUsername());
                return "redirect:/client/home";
            } else {
                redirectAttributes.addFlashAttribute("error" , "Tài khoản hoặc mật khẩu không chính xác !");
                return "redirect:/auth/login";
            }
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user" , new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterDTO registerDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        userService.createUser(user);
        return "/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userLogin");
        return "redirect:/auth/login";
    }
}
