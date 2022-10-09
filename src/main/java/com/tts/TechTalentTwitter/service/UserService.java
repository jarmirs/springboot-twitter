package com.tts.TechTalentTwitter.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tts.TechTalentTwitter.model.User;
import com.tts.TechTalentTwitter.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
        
    public List<User> findAll(){
        return (List<User>) userRepository.findAll();
    }
        
    public void save(User user) {
        userRepository.save(user);
    }
    public User saveNewUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    public User getLoggedInUser() {
        String loggedInUsername = SecurityContextHolder.
          getContext().getAuthentication().getName();
        
        return findByUsername(loggedInUsername);
    }
    @GetMapping(value="/signup")
    public String registration(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping(value = "/signup")
    public String createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
        User userExists = userService.findByUsername(user.getUsername());
        if (userExists != null) {
            bindingResult.rejectValue("username", "error.user", "Username is already taken");
        }
        if (!bindingResult.hasErrors()) {
            userService.saveNewUser(user);
            model.addAttribute("success", "Sign up successful!");
            model.addAttribute("user", new User());
        }
        return "registration";
    }
}