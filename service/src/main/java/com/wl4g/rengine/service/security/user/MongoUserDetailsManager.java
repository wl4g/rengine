package com.wl4g.rengine.service.security.user;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link MongoUserDetailsManager}
 * 
 * @author James Wong
 * @version 2023-02-22
 * @since v1.0.0
 * @see {@link org.springframework.security.provisioning.InMemoryUserDetailsManager}
 */
@Getter
@CustomLog
public final class MongoUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

    private final RengineWebSecurityProperties config;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MongoCollection<Document> userCollection;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    public MongoUserDetailsManager(RengineWebSecurityProperties config, BCryptPasswordEncoder passwordEncoder,
            MongoCollection<Document> userCollection, AuthenticationService authenticationService,
            AuthenticationManager authenticationManager) {
        this.config = notNullOf(config, "config");
        this.passwordEncoder = notNullOf(passwordEncoder, "passwordEncoder");
        this.userCollection = notNullOf(userCollection, "userCollection");
        this.authenticationManager = authenticationManager;
        this.authenticationService = notNullOf(authenticationService, "authenticationService");
    }

    @Override
    public void createUser(UserDetails user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority auth : user.getAuthorities()) {
            authorities.add(auth.getAuthority());
        }

        Document document = BsonEntitySerializers.toDocument(toEntityUser(user));

        // Check for already
        if (userExists(user.getUsername())) {
            throw new RengineException(format("Already the user '%s'", user.getUsername()));
        }

        userCollection.insertOne(document);
    }

    @Override
    public void updateUser(UserDetails user) {
        // final List<String> authorities =
        // safeList(user.getAuthorities()).stream().map(u ->
        // u.getAuthority()).collect(toList());

        final Document document = BsonEntitySerializers.toDocument(toEntityUser(user));

        userCollection.updateOne(new BasicDBObject("username", user.getUsername()), new Document("$set", document));
    }

    @Override
    public void deleteUser(String username) {
        userCollection.deleteOne(new BasicDBObject("username", username));
    }

    @Override
    public void changePassword(String cipherOldPassword, String cipherNewPassword) {
        final Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(currentUser)) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        final String username = currentUser.getName();
        log.debug(format("Changing password for user '%s'", username));

        try {
            // Resolve the passwords to plain.
            final var plainOldPassword = authenticationService.resolveCipher(currentUser.getName(), cipherOldPassword, false);

            final var user = (SpringSecurityUser) loadUserByUsername(username);
            Assert.state(nonNull(user), "Current user doesn't exist in database.");

            if (!passwordEncoder.matches(plainOldPassword.toString(), user.getPassword())) {
                throw new BadCredentialsException("Old password is incorrect.");
            }

            // If an authentication manager has been set, re-authenticate the
            // user with the supplied password.
            if (nonNull(authenticationManager)) {
                log.debug(format("Reauthenticating user '%s' for password change request.", username));
                authenticationManager
                        .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, plainOldPassword));
            } else {
                log.debug("No authentication manager set. Password won't be re-checked.");
            }

            updatePassword(user, cipherNewPassword);
        } catch (Throwable ex) {
            throw new IllegalStateException(format("Failed to change password for %s. %s", username, ex.getMessage()), ex);
        }
    }

    @Override
    public boolean userExists(String username) {
        return userCollection.find(new BasicDBObject("username", username)).first() != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Document query = new Document("username", username);
        final Document userDoc = userCollection.find(query).first();
        if (isNull(userDoc)) {
            throw new UsernameNotFoundException(format("User %s not found", username));
        }

        final com.wl4g.rengine.common.entity.sys.User user = BsonEntitySerializers.fromDocument(userDoc,
                com.wl4g.rengine.common.entity.sys.User.class);
        return fromEntityUser(user);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String cipherNewPassword) {
        final var plainNewPassword = authenticationService.resolveCipher(user.getUsername(), cipherNewPassword, true);
        final var encodedNewPassword = passwordEncoder.encode(plainNewPassword.toString());

        final var u = (SpringSecurityUser) user;
        final var newUser = new SpringSecurityUser(u.getUserId(), u.getEnable(), u.getLabels(), u.getRemark(), u.getUsername(),
                encodedNewPassword, u.isEnabled(), u.isAccountNonExpired(), u.isCredentialsNonExpired(), u.isAccountNonLocked(),
                u.getAuthorities());

        updateUser(newUser);

        return newUser;
    }

    public static com.wl4g.rengine.common.entity.sys.User toEntityUser(UserDetails userDetails) {
        final var _user = (SpringSecurityUser) userDetails;
        return com.wl4g.rengine.common.entity.sys.User.builder()
                .id(_user.getUserId())
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .enable(userDetails.isEnabled() ? BaseBean.ENABLED : BaseBean.DISABLED)
                .accountNonExpired(userDetails.isAccountNonExpired())
                .accountNonLocked(userDetails.isAccountNonLocked())
                .credentialsNonExpired(userDetails.isCredentialsNonExpired())
                .authorities(safeList(userDetails.getAuthorities()).stream().map(auth -> {
                    if (auth instanceof SimpleGrantedAuthority) {
                        return new com.wl4g.rengine.common.entity.sys.User.SimpleGrantedAuthority(auth.getAuthority());
                    } else if (auth instanceof OAuth2UserAuthority) {
                        return new com.wl4g.rengine.common.entity.sys.User.OAuth2UserAuthority(auth.getAuthority(),
                                ((OAuth2UserAuthority) auth).getAttributes());
                    } else if (auth instanceof OidcUserAuthority) {
                        final var oidcAuth = ((OidcUserAuthority) auth);
                        final var oidcIdToken = oidcAuth.getIdToken();
                        final var newIdToken = new com.wl4g.rengine.common.entity.sys.User.OidcIdToken(
                                oidcIdToken.getTokenValue(), oidcIdToken.getIssuedAt(), oidcIdToken.getExpiresAt(),
                                oidcIdToken.getClaims());
                        final var newUserInfo = new com.wl4g.rengine.common.entity.sys.User.OidcUserInfo(
                                oidcAuth.getUserInfo().getClaims());
                        return new com.wl4g.rengine.common.entity.sys.User.OidcUserAuthority(auth.getAuthority(), newIdToken,
                                newUserInfo);
                    }
                    throw new UnsupportedOperationException(format("No supported granted authority for %s", auth));
                }).collect(toSet()))
                .build();
    }

    public static UserDetails fromEntityUser(com.wl4g.rengine.common.entity.sys.User user) {
        return new SpringSecurityUser(user.getId(), user.getEnable(), user.getLabels(), user.getRemark(), user.getUsername(),
                user.getPassword(), (user.getEnable() == BaseBean.ENABLED ? true : false), user.isAccountNonExpired(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                safeList(user.getAuthorities()).stream().map(grantAuth -> {
                    if (grantAuth instanceof com.wl4g.rengine.common.entity.sys.User.SimpleGrantedAuthority) {
                        return new SimpleGrantedAuthority(grantAuth.getAuthority());
                    } else if (grantAuth instanceof com.wl4g.rengine.common.entity.sys.User.OAuth2UserAuthority) {
                        return new OAuth2UserAuthority(grantAuth.getAuthority(),
                                ((OAuth2UserAuthority) grantAuth).getAttributes());
                    } else if (grantAuth instanceof com.wl4g.rengine.common.entity.sys.User.OidcUserAuthority) {
                        final var oidcAuth = ((com.wl4g.rengine.common.entity.sys.User.OidcUserAuthority) grantAuth);
                        final var oidcIdToken = oidcAuth.getIdToken();
                        final var newIdToken = new OidcIdToken(oidcIdToken.getTokenValue(), oidcIdToken.getIssuedAt(),
                                oidcIdToken.getExpiresAt(), oidcIdToken.getClaims());
                        final var newUserInfo = new OidcUserInfo(oidcAuth.getUserInfo().getClaims());
                        return new OidcUserAuthority(grantAuth.getAuthority(), newIdToken, newUserInfo);
                    }
                    throw new UnsupportedOperationException(format("No supported granted authority for %s", grantAuth));
                }).collect(toSet()));
    }

    @Getter
    @Setter
    @ToString
    public static class SpringSecurityUser extends User {
        private static final long serialVersionUID = 570;

        private Long userId;
        private Integer enable;
        private List<String> labels;
        private String remark;

        public SpringSecurityUser(Long userId, Integer enable, List<String> labels, String remark, String username,
                String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
            super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            this.userId = userId;
            this.enable = enable;
            this.labels = labels;
            this.remark = remark;
        }
    }

}