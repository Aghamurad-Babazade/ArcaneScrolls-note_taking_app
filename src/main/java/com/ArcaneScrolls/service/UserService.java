package com.ArcaneScrolls.service;

import com.ArcaneScrolls.config.EncryptionUtil;
import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.repository.NoteRepository;
import com.ArcaneScrolls.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;
    NoteRepository noteRepository;
    PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();

    }

    public void save(String username, String password, String email) throws Exception {

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        SecretKey encryptionKey = EncryptionUtil.generateEncryptionKey();
        byte[] encryptedEncryptionKey = EncryptionUtil.encryptEncryptionKey(encryptionKey, user.getPassword());

        user.setEncryptionKey(encryptedEncryptionKey);

        userRepository.save(user);
    }

    public User findByUsernameAndPassword(String usernameOrEmail, String password) {

        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional.get();
        }

        return null;
    }
}
