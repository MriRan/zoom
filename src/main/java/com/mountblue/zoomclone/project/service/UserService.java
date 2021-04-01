package com.mountblue.zoomclone.project.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void registerNewUser(String name, String password);
}
