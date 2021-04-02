package com.mountblue.zoomclone.project.serviceImplementation;

import com.mountblue.zoomclone.project.model.Users;
import com.mountblue.zoomclone.project.repository.UserRepository;
import com.mountblue.zoomclone.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public void registerNewUser(String name, String password) {
        repository.save(new Users(name, bCryptPasswordEncoder.encode(password)));
    }
}