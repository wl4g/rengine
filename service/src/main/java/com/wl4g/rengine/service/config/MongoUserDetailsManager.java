package com.wl4g.rengine.service.config;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.util.BsonEntitySerializers;

import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link MongoUserDetailsManager}
 * 
 * @author James Wong
 * @version 2023-02-22
 * @since v1.0.0
 * @see {@link org.springframework.security.provisioning.InMemoryUserDetailsManager}
 */
@Getter
@Setter
@CustomLog
public final class MongoUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
    private final AuthenticationManager authenticationManager;
    private final MongoCollection<Document> userCollection;

    public MongoUserDetailsManager(AuthenticationManager authenticationManager, MongoDatabase db, String userCollectionName) {
        notNullOf(db, "db");
        this.authenticationManager = authenticationManager;
        this.userCollection = db.getCollection(hasTextOf(userCollectionName, "userCollectionName"));
    }

    @Override
    public void createUser(UserDetails user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority auth : user.getAuthorities()) {
            authorities.add(auth.getAuthority());
        }

        Document document = BsonEntitySerializers.toDocument(toEntityUser(user));

        // TODO Check for already?
        userCollection.insertOne(document);
    }

    @Override
    public void updateUser(UserDetails user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority auth : user.getAuthorities()) {
            authorities.add(auth.getAuthority());
        }

        Document document = BsonEntitySerializers.toDocument(toEntityUser(user));

        userCollection.updateOne(new BasicDBObject("username", user.getUsername()), new Document("$set", document));
    }

    @Override
    public void deleteUser(String username) {
        userCollection.deleteOne(new BasicDBObject("username", username));
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        log.debug(format("Changing password for user '%s'", username));

        // If an authentication manager has been set, re-authenticate the user
        // with the supplied password.
        if (this.authenticationManager != null) {
            log.debug(format("Reauthenticating user '%s' for password change request.", username));
            this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            log.debug("No authentication manager set. Password won't be re-checked.");
        }
        User user = (User) loadUserByUsername(username);
        Assert.state(user != null, "Current user doesn't exist in database.");
        updatePassword(user, newPassword);
    }

    @Override
    public boolean userExists(String username) {
        return userCollection.find(new BasicDBObject("username", username)).first() != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Document query = new Document("username", username);
        Document userDoc = userCollection.find(query).first();
        if (userDoc == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        final com.wl4g.rengine.common.entity.User user = BsonEntitySerializers.fromDocument(userDoc,
                com.wl4g.rengine.common.entity.User.class);

        return fromEntityUser(user);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return new User(user.getUsername(), newPassword, user.isEnabled(), user.isAccountNonExpired(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
    }

    public static com.wl4g.rengine.common.entity.User toEntityUser(UserDetails userDetails) {
        return com.wl4g.rengine.common.entity.User.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .enable(userDetails.isEnabled() ? BaseBean.ENABLED : BaseBean.DISABLED)
                .accountNonExpired(userDetails.isAccountNonExpired())
                .accountNonLocked(userDetails.isAccountNonLocked())
                .credentialsNonExpired(userDetails.isCredentialsNonExpired())
                .authorities(safeList(userDetails.getAuthorities()).stream().map(auth -> {
                    if (auth instanceof SimpleGrantedAuthority) {
                        return new com.wl4g.rengine.common.entity.User.SimpleGrantedAuthority(auth.getAuthority());
                    } else if (auth instanceof OAuth2UserAuthority) {
                        return new com.wl4g.rengine.common.entity.User.OAuth2UserAuthority(auth.getAuthority(),
                                ((OAuth2UserAuthority) auth).getAttributes());
                    } else if (auth instanceof OidcUserAuthority) {
                        final var oidcAuth = ((OidcUserAuthority) auth);
                        final var oidcIdToken = oidcAuth.getIdToken();
                        final var newIdToken = new com.wl4g.rengine.common.entity.User.OidcIdToken(oidcIdToken.getTokenValue(),
                                oidcIdToken.getIssuedAt(), oidcIdToken.getExpiresAt(), oidcIdToken.getClaims());
                        final var newUserInfo = new com.wl4g.rengine.common.entity.User.OidcUserInfo(
                                oidcAuth.getUserInfo().getClaims());
                        return new com.wl4g.rengine.common.entity.User.OidcUserAuthority(auth.getAuthority(), newIdToken,
                                newUserInfo);
                    }
                    throw new UnsupportedOperationException(format("No supported granted authority for %s", auth));
                }).collect(toSet()))
                .build();
    }

    public static UserDetails fromEntityUser(com.wl4g.rengine.common.entity.User user) {
        return new User(user.getUsername(), user.getPassword(), (user.getEnable() == BaseBean.ENABLED ? true : false),
                user.isAccountNonExpired(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                safeList(user.getAuthorities()).stream().map(auth -> {
                    if (auth instanceof com.wl4g.rengine.common.entity.User.SimpleGrantedAuthority) {
                        return new SimpleGrantedAuthority(auth.getAuthority());
                    } else if (auth instanceof com.wl4g.rengine.common.entity.User.OAuth2UserAuthority) {
                        return new OAuth2UserAuthority(auth.getAuthority(), ((OAuth2UserAuthority) auth).getAttributes());
                    } else if (auth instanceof com.wl4g.rengine.common.entity.User.OidcUserAuthority) {
                        final var oidcAuth = ((com.wl4g.rengine.common.entity.User.OidcUserAuthority) auth);
                        final var oidcIdToken = oidcAuth.getIdToken();
                        final var newIdToken = new OidcIdToken(oidcIdToken.getTokenValue(), oidcIdToken.getIssuedAt(),
                                oidcIdToken.getExpiresAt(), oidcIdToken.getClaims());
                        final var newUserInfo = new OidcUserInfo(oidcAuth.getUserInfo().getClaims());
                        return new OidcUserAuthority(auth.getAuthority(), newIdToken, newUserInfo);
                    }
                    throw new UnsupportedOperationException(format("No supported granted authority for %s", auth));
                }).collect(toSet()));
    }

}