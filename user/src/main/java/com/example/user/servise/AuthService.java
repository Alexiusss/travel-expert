package com.example.user.servise;

import com.example.amqp.RabbitMQMessageProducer;
import com.example.clients.notification.NotificationRequest;
import com.example.user.AuthUser;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.dto.AuthRequest;
import com.example.user.model.dto.RegistrationDTO;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.UUID;

import static com.example.user.util.UserUtil.prepareToSave;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitMQMessageProducer rabbitMQMessageProducer;

    @Value("${myhostname}")
    private String myHostName;

    public AuthUser getAuthUser(AuthRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(), request.getPassword()
                        )
                );
        return (AuthUser) authentication.getPrincipal();
    }


    public User registerUser(RegistrationDTO registrationDTO) {
        Assert.notNull(registrationDTO, "registration must not be null");

        User user = User.builder()
                .email(registrationDTO.getEmail())
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .password(registrationDTO.getPassword())
                .enabled(false)
                .roles(Set.of(Role.USER))
                .activationCode(UUID.randomUUID().toString())
                .build();

        userRepository.save(prepareToSave(user));
        sendMessage(user);

        return user;
    }

    private void sendMessage(User user) {
        if (!ObjectUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Restaurant Advisor. Please, visit next link: http://%s/api/v1/auth/activate/%s",
                    user.getFirstName(),
                    myHostName,
                    user.getActivationCode()
            );

            NotificationRequest notificationRequest = new NotificationRequest(user.id(), user.getEmail(), message, "Activation code", "Restaurant advisor");

            rabbitMQMessageProducer.publish(
                    notificationRequest,
                    "internal.exchange",
                    "internal.notification.routing-key"
            );
        }
    }

    @Transactional
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setEnabled(true);

        return true;
    }
}