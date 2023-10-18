package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy ProjectService projectService, @Lazy TaskService taskService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
    }


    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(false);

        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        return userMapper.convertToDto(userRepository.findByUserNameAndIsDeleted(username, false));

    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

//    @Override //hard deletion
//    public void deleteByUserName(String username) {
//
//        userRepository.deleteByUserName(username);
//
//
//    }

    @Override
    public UserDTO update(UserDTO user) {
        //Find current user
        User user1 = userRepository.findByUserNameAndIsDeleted(user.getUserName(),false); //has id

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

        User user = userRepository.findByUserNameAndIsDeleted(username, false);

        if (checkIfUserCanBeDeleted(user)){
            user.setIsDeleted(true);
            user.setUserName(user.getUserName() + "-" + user.getId()); // to be able to use the same username in future we change the username to sth else
            userRepository.save(user);
        }

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {

        List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);

        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }


    private boolean checkIfUserCanBeDeleted(User user){

        switch (user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedProjectsByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;

            case "Employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedTasksByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            default:
                return true;
        }

    }


}
