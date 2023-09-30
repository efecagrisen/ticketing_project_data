package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository.findAll(Sort.by("firstName"));

        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        return userMapper.convertToDto(userRepository.findByUserName(username));

    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public void deleteByUserName(String username) {

        userRepository.deleteByUserName(username);


    }

    @Override
    public UserDTO update(UserDTO user) {
        //Find current user
        User user1 = userRepository.findByUserName(user.getUserName()); //has id

        //Map updated user dto to entity object
        User convertedUser = userMapper.convertToEntity(user); // does not have id

        //set the id of updated user in the db
        convertedUser.setId(user1.getId());

        //save the updated user in DB
        userRepository.save(convertedUser);

        return findByUserName(user.getUserName());

    }

    @Override
    public void delete(String username) {

        User user = userRepository.findByUserName(username);
        user.setIsDeleted(true);
        userRepository.save(user);


    }
}
