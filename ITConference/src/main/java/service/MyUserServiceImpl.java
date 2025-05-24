package service;

import domain.MyUser;
import repository.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserServiceImpl implements MyUserService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public MyUser findByUsername(String username) {
        return myUserRepository.findByUsername(username);
    }

    @Override
    public MyUser saveUser(MyUser user) {
        return myUserRepository.save(user);
    }
}