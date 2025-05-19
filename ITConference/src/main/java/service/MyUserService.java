// service/MyUserService.java
package service;

import domain.MyUser;

public interface MyUserService {
    MyUser findByUsername(String username);
    MyUser saveUser(MyUser user); // Nieuwe methode
}