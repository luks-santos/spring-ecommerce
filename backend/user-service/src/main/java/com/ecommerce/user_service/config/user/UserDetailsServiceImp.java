package com.ecommerce.user_service.config.user;

import com.ecommerce.user_service.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepo repository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository
                .findByEmail(email)
                .map(UserDetailsImp::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserEmail: " + email + " does not exist"));
    }
}
