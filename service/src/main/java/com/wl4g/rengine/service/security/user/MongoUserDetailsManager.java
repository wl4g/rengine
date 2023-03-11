package com.wl4g.rengine.service.security.user;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.util.ServiceAggregateFilters.USER_ROLE_ORGAN_MENU_LOOKUP_FILTERS;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
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
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.common.entity.sys.MenuRole;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.UserRole;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;
import com.wl4g.rengine.service.security.access.SimplePermissionGrantedAuthority;

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

    // see:org.springframework.security.access.expression.SecurityExpressionRoot#getAuthoritySet
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //// @formatter:off
        //final Document query = new Document("username", username);
        //final Document userDoc = userCollection.find(query).first();
        //if (isNull(userDoc)) {
        //    throw new UsernameNotFoundException(format("User %s not found", username));
        //}
        //final com.wl4g.rengine.common.entity.sys.User user = BsonEntitySerializers.fromDocument(userDoc,
        //        com.wl4g.rengine.common.entity.sys.User.class);
        //// @formatter:on
        // return fromEntityUser(user);

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.in("username", username)));
        USER_ROLE_ORGAN_MENU_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));
        // System.out.println(BsonUtils2.toJson(USER_ROLE_ORGAN_MENUS_LOOKUP_FILTER));

        try (var cursor = userCollection.aggregate(aggregates)
                .map(userDoc -> BsonEntitySerializers.fromDocument(userDoc, com.wl4g.rengine.common.entity.sys.User.class))
                .cursor();) {
            if (!cursor.hasNext()) {
                throw new UsernameNotFoundException(format("The user %s not found", username));
            }
            final var user = cursor.next();
            if (cursor.hasNext()) {
                throw new IllegalStateException(format("The ambiguous users found, multiple names are %s", username));
            }
            return fromEntityUser(user);
        }
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
                .authorities(userDetails.getAuthorities())
                .build();
    }

    public static UserDetails fromEntityUser(com.wl4g.rengine.common.entity.sys.User user) {
        // Transform user role menu permissions set to userDetails granted
        // authories.
        final var allRoleGrantedAuthorities = new ArrayList<GrantedAuthority>(4);
        final var allPermissionGrantedAuthorities = new ArrayList<GrantedAuthority>(16);
        for (UserRole userRole : safeList(user.getUserRoles())) {
            for (Role role : safeList(userRole.getRoles())) {
                allRoleGrantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
                for (MenuRole menuRole : safeList(role.getMenuRoles())) {
                    for (Menu menu : safeList(menuRole.getMenus())) {
                        for (String permission : safeList(menu.getPermissions())) {
                            allPermissionGrantedAuthorities.add(new SimplePermissionGrantedAuthority(permission));
                        }
                    }
                }
            }
        }
        // Merge all granted authorities.
        allRoleGrantedAuthorities.addAll(allPermissionGrantedAuthorities);

        // Wrap to spring security user.
        return new SpringSecurityUser(user.getId(), user.getEnable(), user.getLabels(), user.getRemark(), user.getUsername(),
                user.getPassword(), (user.getEnable() == BaseBean.ENABLED ? true : false), user.isAccountNonExpired(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(), allRoleGrantedAuthorities);
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