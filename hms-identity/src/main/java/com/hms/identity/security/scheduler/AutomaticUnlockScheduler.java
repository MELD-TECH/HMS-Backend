package com.hms.identity.security.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.service.AccountLockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutomaticUnlockScheduler {

    private final UserRepository repository;

    private final AccountLockService service;

    @Scheduled(fixedDelayString = "${security.lock.scheduler:60000}")
//    @Transactional
    public void unlockExpiredAccounts() {

    	List<User> expiredUsers = repository.findAccountsToUnlock();
    	
    	expiredUsers.forEach(service::automaticUnlock); 
    	
    //	repository.findAccountsToUnlock()
    //    .forEach(this::automaticUnlock);
    }

}