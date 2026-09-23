package com.crm.sales_pipeline.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


   @Override
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      return null;
   }
}
