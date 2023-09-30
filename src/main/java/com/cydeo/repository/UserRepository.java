package com.cydeo.repository;

import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    // get user based on username
    //User getUserByUserName(String username); ~same as below
    User findByUserName(String username);

    @Transactional
    void deleteByUserName(String username);



}
