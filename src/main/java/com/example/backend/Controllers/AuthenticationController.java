package com.example.backend.Controllers;

import com.example.backend.conf.JwtTokenUtil;
import com.example.backend.entities.LoginUser;
import com.example.backend.entities.User;
import com.example.backend.entities.customLoginResp;
import com.example.backend.services.ModuleService;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;
    private String tokenResp;
    private Optional<User> userResp;

    @RequestMapping(value = "token/generate-token", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final Optional<User> user = userService.findByUsername(loginUser.getUsername());

            final String token = jwtTokenUtil.generateToken(user.get());
            System.out.println("token:"+token);
            // Return token in response header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization",token);
            if(user.get()!=null){
                loginUser.setuId(user.get().getuId());
                loginUser.setuMail(user.get().getuMail());
                customLoginResp response = new customLoginResp();
                if(user.get().getUser_group()!= null){
                    response.setModules(moduleService.findModuleByGroup(user.get().getUser_group().getgId()));
                }else{
                    response.setModules(null);
                }
                response.setUser(loginUser);
                return ResponseEntity.ok().headers(headers).body(response);
            }else{
                return ResponseEntity.ok().headers(headers).build();
            }
    }

    @RequestMapping(value="/check", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> test(@RequestParam(name = "token") String token) {
        if(token!=null){
            final User user = jwtTokenUtil.extractUser(token);
            final String username = jwtTokenUtil.getUsernameFromToken(token);
            final Optional<User> u = userService.findByUsername(username);
            if(username.equals(u.get().getUsername()) && !jwtTokenUtil.isTokenExpired(token)) {
                return ResponseEntity.ok(true);

            }else {
                return ResponseEntity.ok(false);
            }
        }else{
            return ResponseEntity.ok(false);
        }

    }


    @RequestMapping(value="/loginResp", method = RequestMethod.GET)
    @Transactional
    public User getUserData(@RequestParam(name = "token") String token) {
        final Optional<User> user = userService.findByUsername(jwtTokenUtil.extractUser(token).getUsername());
        System.out.println(user.get().getUsername());
        if(token!=null){
            //System.out.println(moduleService.findModuleByGroup(user.get().getUser_group().getgId()));
            return user.get();
        }else{
            return null;
        }

    }


    public static final String endpoint = "http://ip-api.com/json";




    @RequestMapping(value="/list", method = RequestMethod.GET)
    public List<User> getList(){
        return userService.getListUser();

    }
}
