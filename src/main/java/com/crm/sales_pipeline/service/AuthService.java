package com.crm.sales_pipeline.service;
import com.crm.sales_pipeline.dto.LoginRequest;
import com.crm.sales_pipeline.dto.LoginResponse;

import com.crm.sales_pipeline.security.JWTTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
   private final AuthenticationManager authenticationManager;
   private final JWTTokenProvider jwtTokenProvider;

   public AuthService(
           AuthenticationManager authenticationManager,
           JWTTokenProvider jwtTokenProvider
   ) {

       this.authenticationManager = authenticationManager;
       this.jwtTokenProvider = jwtTokenProvider;
   }

      public LoginResponse login(LoginRequest request){
          Authentication authentication= authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken (
                          request.getEmail(),
                          request.getPassword()

                  )
          );
          String token = jwtTokenProvider.generateToken(authentication);
          return new LoginResponse(token);
      }

}
