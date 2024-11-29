package com.nemisolv.repository;


import com.nemisolv.entity.ConfirmationEmail;
import com.nemisolv.entity.User;
import com.nemisolv.entity.type.MailType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConfirmationEmailRepository extends CrudRepository<ConfirmationEmail, Long> {
    List<ConfirmationEmail> findByUserId(Long userId);

    Optional<ConfirmationEmail> findByUserAndToken(User user, String token);

    List<ConfirmationEmail> findByTypeAndUserId(MailType type, Long userId);
}
