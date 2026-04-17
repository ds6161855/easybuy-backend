package com.easybuy.service;

import com.easybuy.entity.EasybuyUser;
import com.easybuy.repository.EasybuyUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EasybuyUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String mobile) {

        EasybuyUser user = repo.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMobile())
                .password("")
                .authorities("USER")
                .build();
    }
}
