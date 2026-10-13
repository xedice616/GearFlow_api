package com.xadice.gearflow.security;

import com.jayway.jsonpath.JsonPath;

import com.xadice.gearflow.model.Role;
import com.xadice.gearflow.model.User;
import com.xadice.gearflow.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final String USER_EMAIL =
            "security-user@test.com";

    private static final String ADMIN_EMAIL =
            "security-admin@test.com";

    private static final String PASSWORD =
            "Test12345!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        userRepository
                .findByEmail(USER_EMAIL)
                .ifPresent(userRepository::delete);

        userRepository
                .findByEmail(ADMIN_EMAIL)
                .ifPresent(userRepository::delete);

        User user = new User();

        user.setFullName(
                "Security Test User"
        );

        user.setEmail(
                USER_EMAIL
        );

        user.setPassword(
                passwordEncoder.encode(PASSWORD)
        );

        user.setRole(
                Role.USER
        );

        user.setEnabled(true);

        userRepository.save(user);

        User admin = new User();

        admin.setFullName(
                "Security Test Admin"
        );

        admin.setEmail(
                ADMIN_EMAIL
        );

        admin.setPassword(
                passwordEncoder.encode(PASSWORD)
        );

        admin.setRole(
                Role.ADMIN
        );

        admin.setEnabled(true);

        userRepository.save(admin);
    }

    @Test
    void adminEndpoint_withoutToken_shouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                );
    }

    @Test
    void authMe_withoutToken_shouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/auth/me")
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                );
    }

    @Test
    void user_withValidJwt_shouldAccessAuthenticatedEndpoint()
            throws Exception {

        String token =
                loginAndGetToken(
                        USER_EMAIL,
                        PASSWORD
                );

        mockMvc.perform(
                        get("/api/v1/auth/me")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(USER_EMAIL)
                )
                .andExpect(
                        jsonPath("$.role")
                                .value("USER")
                );
    }

    @Test
    void user_shouldNotAccessAdminEndpoint()
            throws Exception {

        String token =
                loginAndGetToken(
                        USER_EMAIL,
                        PASSWORD
                );

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isForbidden()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(403)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Forbidden")
                );
    }

    @Test
    void admin_shouldAccessAdminEndpoint()
            throws Exception {

        String token =
                loginAndGetToken(
                        ADMIN_EMAIL,
                        PASSWORD
                );

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void invalidJwt_shouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/auth/me")
                                .header(
                                        "Authorization",
                                        "Bearer invalid.jwt.token"
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                );
    }

    @Test
    void disabledUser_shouldNotUsePreviouslyIssuedJwt()
            throws Exception {

        /*
         * 1. User aktiv vəziyyətdə login edir
         * və valid JWT alır.
         */
        String token =
                loginAndGetToken(
                        USER_EMAIL,
                        PASSWORD
                );

        /*
         * 2. Sonra user disable edilir.
         */
        User user =
                userRepository
                        .findByEmail(USER_EMAIL)
                        .orElseThrow();

        user.setEnabled(false);

        userRepository.saveAndFlush(user);

        /*
         * 3. JWT hələ expire olmayıb.
         *
         * Buna baxmayaraq filter DB-dən user-i
         * yenidən yoxlamalı və disabled olduğu
         * üçün authentication yaratmamalıdır.
         */
        mockMvc.perform(
                        get("/api/v1/auth/me")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                );
    }

    private String loginAndGetToken(
            String email,
            String password
    ) throws Exception {

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(
                email,
                password
        );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestBody)
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                jsonPath("$.token")
                                        .exists()
                        )
                        .andReturn();

        String responseBody =
                result.getResponse()
                        .getContentAsString();

        return JsonPath.read(
                responseBody,
                "$.token"
        );
    }
}