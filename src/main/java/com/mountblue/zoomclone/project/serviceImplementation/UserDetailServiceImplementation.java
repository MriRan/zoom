package com.mountblue.zoomclone.project.serviceImplementation;

import com.mountblue.zoomclone.project.model.Users;
import com.mountblue.zoomclone.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImplementation implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Users user = repository.findByName(name).orElseThrow(()->new UsernameNotFoundException("User 404"));
        return new UserPrincipal(user);
    }
}
