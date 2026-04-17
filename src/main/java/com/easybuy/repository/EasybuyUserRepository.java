package com.easybuy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.easybuy.entity.EasybuyUser;

public interface EasybuyUserRepository extends JpaRepository<EasybuyUser, Long> {

    Optional<EasybuyUser> findByMobile(String mobile);
    

}