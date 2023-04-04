/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.service.impl.sys;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USERS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USER_ROLES;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.USER_ROLE_LOOKUP_FILTERS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.collections.IteratorUtils;
import org.bson.conversions.Bson;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.entity.sys.UserRole;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.UserService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.UserAssignRole;
import com.wl4g.rengine.service.model.sys.UserDelete;
import com.wl4g.rengine.service.model.sys.UserDeleteResult;
import com.wl4g.rengine.service.model.sys.UserQuery;
import com.wl4g.rengine.service.model.sys.UserSave;
import com.wl4g.rengine.service.model.sys.UserSaveResult;
import com.wl4g.rengine.service.security.user.AuthenticationService;
import com.wl4g.rengine.service.security.user.AuthenticationService.UserAuthInfo;

/**
 * {@link UserServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class UserServiceImpl extends BasicServiceImpl implements UserService {

    @Override
    public PageHolder<User> query(UserQuery model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getUserId()), isCriteria("email", model.getEmail())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<User> users = mongoTemplate.find(query, User.class, SYS_USERS.getName());

        safeList(users).parallelStream().forEach(u -> u.setRoles(findRolesByUserIds(singletonList(u.getId()))));

        return new PageHolder<User>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, SYS_USERS.getName()))
                .withRecords(users);
    }

    @Override
    public UserSaveResult save(UserSave model) {
        User user = model;
        notNullOf(user, "user");

        if (isNull(user.getId())) {
            // Check for is add super administrators?
            if (AuthenticationService.isDefaultSuperAdministrator(model.getUsername())) {
                throw new RengineException(format("Denied to super administrator users"));
            }
            user.preInsert();
        } else {
            user.preUpdate();
        }

        final User saved = mongoTemplate.save(user, SYS_USERS.getName());
        return UserSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public UserDeleteResult delete(UserDelete model) {
        return UserDeleteResult.builder().deletedCount(doDeleteGracefully(model, SYS_USERS)).build();
    }

    /**
     * for example (full spring security authentication information):
     * 
     * <pre>
     *   {
     *       "code": 200,
     *       "status": "Normal",
     *       "requestId": null,
     *       "timestamp": 1677174410315,
     *       "message": "Ok",
     *       "data": {
     *           "authorities": [
     *               {
     *                   "authority": "ROLE_USER",
     *                   "attributes": {
     *                       "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                       "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                       "email_verified": false,
     *                       "iss": "https://iam.wl4g.com/realms/master",
     *                       "typ": "ID",
     *                       "preferred_username": "rengine1",
     *                       "given_name": "",
     *                       "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                       "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                       "aud": [
     *                           "rengine"
     *                       ],
     *                       "acr": "1",
     *                       "azp": "rengine",
     *                       "auth_time": "2023-02-23T17:43:49Z",
     *                       "exp": "2023-02-23T17:44:50Z",
     *                       "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                       "family_name": "",
     *                       "iat": "2023-02-23T17:43:50Z",
     *                       "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *                   },
     *                   "idToken": {
     *                       "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMVlUSmlxcE1HSUI5eEg5OERZUTlJZlJmR2RsYWRKUjNmZE95TjRzeks4In0.eyJleHAiOjE2NzcxNzQyOTAsImlhdCI6MTY3NzE3NDIzMCwiYXV0aF90aW1lIjoxNjc3MTc0MjI5LCJqdGkiOiI5ZTllMzQwMC01NzFhLTQ4ZmItYWM2Ny02ZjYwMTUxODA5MTMiLCJpc3MiOiJodHRwczovL2lhbS53bDRnLmNvbS9yZWFsbXMvbWFzdGVyIiwiYXVkIjoicmVuZ2luZSIsInN1YiI6IjQyNTVkYjU3LTZjZDItNDIxYS1iOGRkLTMyYzBjOWY2YTk5YSIsInR5cCI6IklEIiwiYXpwIjoicmVuZ2luZSIsIm5vbmNlIjoiekRRcGhrRmlWbEdxUy1IWHk2cVhCRlRfTkxBQjQwdE9OS0Y3NW9pZGRGayIsInNlc3Npb25fc3RhdGUiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJhdF9oYXNoIjoiQm9iTTZMc0NldU13eWdsNjBocW12QSIsImFjciI6IjEiLCJzaWQiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InJlbmdpbmUxIiwiZ2l2ZW5fbmFtZSI6IiIsImZhbWlseV9uYW1lIjoiIn0.wXW6fNPMilbOWlKRTtF70GqbcUN6U9h-NqlSrcgnm1lxhD2iGWcNPD3vQZPIBvEg5JShSUSIaiKLvO5b8mKa6w4EtGYd0R4AUlHZRvwcKktuW6rrr3Nwowrq2wslDRxI6uDTqpLu85iRXnQ2LPFzQ6jDxskj1_OYjnZGE3hNVvivX3vRZnPfPLTGvQvZIrbYccDg3GGntBGEOOU3iCRu71ifc4-JWncxeVYvsAlo88eDxT8lYDz3NaH0w2-XRNnSl9ByTIxrz35qIWPJrOCO0EGlr-u1I9myW5iVmSlFOWYcRDgFcFqPeDtMT7yIgyjtL0AB2zr6HS8XOor25MCnyw",
     *                       "issuedAt": "2023-02-23T17:43:50Z",
     *                       "expiresAt": "2023-02-23T17:44:50Z",
     *                       "claims": {
     *                           "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                           "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                           "email_verified": false,
     *                           "iss": "https://iam.wl4g.com/realms/master",
     *                           "typ": "ID",
     *                           "preferred_username": "rengine1",
     *                           "given_name": "",
     *                           "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                           "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                           "aud": [
     *                               "rengine"
     *                           ],
     *                           "acr": "1",
     *                           "azp": "rengine",
     *                           "auth_time": "2023-02-23T17:43:49Z",
     *                           "exp": "2023-02-23T17:44:50Z",
     *                           "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                           "iat": "2023-02-23T17:43:50Z",
     *                           "family_name": "",
     *                           "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *                       },
     *                       "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                       "audience": [
     *                           "rengine"
     *                       ],
     *                       "authenticatedAt": "2023-02-23T17:43:49Z",
     *                       "authenticationContextClass": "1",
     *                       "authenticationMethods": null,
     *                       "authorizedParty": "rengine",
     *                       "accessTokenHash": "BobM6LsCeuMwygl60hqmvA",
     *                       "authorizationCodeHash": null,
     *                       "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                       "issuer": "https://iam.wl4g.com/realms/master",
     *                       "address": {
     *                           "formatted": null,
     *                           "streetAddress": null,
     *                           "locality": null,
     *                           "region": null,
     *                           "postalCode": null,
     *                           "country": null
     *                       },
     *                       "locale": null,
     *                       "fullName": null,
     *                       "zoneInfo": null,
     *                       "email": null,
     *                       "profile": null,
     *                       "familyName": "",
     *                       "middleName": null,
     *                       "nickName": null,
     *                       "preferredUsername": "rengine1",
     *                       "picture": null,
     *                       "website": null,
     *                       "emailVerified": false,
     *                       "gender": null,
     *                       "birthdate": null,
     *                       "phoneNumber": null,
     *                       "phoneNumberVerified": null,
     *                       "updatedAt": null,
     *                       "givenName": ""
     *                   },
     *                   "userInfo": {
     *                       "claims": {
     *                           "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                           "email_verified": false,
     *                           "preferred_username": "rengine1",
     *                           "given_name": "",
     *                           "family_name": ""
     *                       },
     *                       "address": {
     *                           "formatted": null,
     *                           "streetAddress": null,
     *                           "locality": null,
     *                           "region": null,
     *                           "postalCode": null,
     *                           "country": null
     *                       },
     *                       "locale": null,
     *                       "fullName": null,
     *                       "zoneInfo": null,
     *                       "email": null,
     *                       "profile": null,
     *                       "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                       "familyName": "",
     *                       "middleName": null,
     *                       "nickName": null,
     *                       "preferredUsername": "rengine1",
     *                       "picture": null,
     *                       "website": null,
     *                       "emailVerified": false,
     *                       "gender": null,
     *                       "birthdate": null,
     *                       "phoneNumber": null,
     *                       "phoneNumberVerified": null,
     *                       "updatedAt": null,
     *                       "givenName": ""
     *                   }
     *               },
     *               {
     *                   "authority": "SCOPE_email"
     *               },
     *               {
     *                   "authority": "SCOPE_openid"
     *               },
     *               {
     *                   "authority": "SCOPE_profile"
     *               }
     *           ],
     *           "details": {
     *               "remoteAddress": "127.0.0.1",
     *               "sessionId": "4c70b110-a59a-41e5-9ef9-66ac6ab92891"
     *           },
     *           "authenticated": true,
     *           "principal": {
     *               "authorities": [
     *                   {
     *                       "authority": "ROLE_USER",
     *                       "attributes": {
     *                           "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                           "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                           "email_verified": false,
     *                           "iss": "https://iam.wl4g.com/realms/master",
     *                           "typ": "ID",
     *                           "preferred_username": "rengine1",
     *                           "given_name": "",
     *                           "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                           "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                           "aud": [
     *                               "rengine"
     *                           ],
     *                           "acr": "1",
     *                           "azp": "rengine",
     *                           "auth_time": "2023-02-23T17:43:49Z",
     *                           "exp": "2023-02-23T17:44:50Z",
     *                           "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                           "family_name": "",
     *                           "iat": "2023-02-23T17:43:50Z",
     *                           "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *                       },
     *                       "idToken": {
     *                           "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMVlUSmlxcE1HSUI5eEg5OERZUTlJZlJmR2RsYWRKUjNmZE95TjRzeks4In0.eyJleHAiOjE2NzcxNzQyOTAsImlhdCI6MTY3NzE3NDIzMCwiYXV0aF90aW1lIjoxNjc3MTc0MjI5LCJqdGkiOiI5ZTllMzQwMC01NzFhLTQ4ZmItYWM2Ny02ZjYwMTUxODA5MTMiLCJpc3MiOiJodHRwczovL2lhbS53bDRnLmNvbS9yZWFsbXMvbWFzdGVyIiwiYXVkIjoicmVuZ2luZSIsInN1YiI6IjQyNTVkYjU3LTZjZDItNDIxYS1iOGRkLTMyYzBjOWY2YTk5YSIsInR5cCI6IklEIiwiYXpwIjoicmVuZ2luZSIsIm5vbmNlIjoiekRRcGhrRmlWbEdxUy1IWHk2cVhCRlRfTkxBQjQwdE9OS0Y3NW9pZGRGayIsInNlc3Npb25fc3RhdGUiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJhdF9oYXNoIjoiQm9iTTZMc0NldU13eWdsNjBocW12QSIsImFjciI6IjEiLCJzaWQiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InJlbmdpbmUxIiwiZ2l2ZW5fbmFtZSI6IiIsImZhbWlseV9uYW1lIjoiIn0.wXW6fNPMilbOWlKRTtF70GqbcUN6U9h-NqlSrcgnm1lxhD2iGWcNPD3vQZPIBvEg5JShSUSIaiKLvO5b8mKa6w4EtGYd0R4AUlHZRvwcKktuW6rrr3Nwowrq2wslDRxI6uDTqpLu85iRXnQ2LPFzQ6jDxskj1_OYjnZGE3hNVvivX3vRZnPfPLTGvQvZIrbYccDg3GGntBGEOOU3iCRu71ifc4-JWncxeVYvsAlo88eDxT8lYDz3NaH0w2-XRNnSl9ByTIxrz35qIWPJrOCO0EGlr-u1I9myW5iVmSlFOWYcRDgFcFqPeDtMT7yIgyjtL0AB2zr6HS8XOor25MCnyw",
     *                           "issuedAt": "2023-02-23T17:43:50Z",
     *                           "expiresAt": "2023-02-23T17:44:50Z",
     *                           "claims": {
     *                               "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                               "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                               "email_verified": false,
     *                               "iss": "https://iam.wl4g.com/realms/master",
     *                               "typ": "ID",
     *                               "preferred_username": "rengine1",
     *                               "given_name": "",
     *                               "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                               "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                               "aud": [
     *                                   "rengine"
     *                               ],
     *                               "acr": "1",
     *                               "azp": "rengine",
     *                               "auth_time": "2023-02-23T17:43:49Z",
     *                               "exp": "2023-02-23T17:44:50Z",
     *                               "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                               "iat": "2023-02-23T17:43:50Z",
     *                               "family_name": "",
     *                               "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *                           },
     *                           "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                           "audience": [
     *                               "rengine"
     *                           ],
     *                           "authenticatedAt": "2023-02-23T17:43:49Z",
     *                           "authenticationContextClass": "1",
     *                           "authenticationMethods": null,
     *                           "authorizedParty": "rengine",
     *                           "accessTokenHash": "BobM6LsCeuMwygl60hqmvA",
     *                           "authorizationCodeHash": null,
     *                           "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                           "issuer": "https://iam.wl4g.com/realms/master",
     *                           "address": {
     *                               "formatted": null,
     *                               "streetAddress": null,
     *                               "locality": null,
     *                               "region": null,
     *                               "postalCode": null,
     *                               "country": null
     *                           },
     *                           "locale": null,
     *                           "fullName": null,
     *                           "zoneInfo": null,
     *                           "email": null,
     *                           "profile": null,
     *                           "familyName": "",
     *                           "middleName": null,
     *                           "nickName": null,
     *                           "preferredUsername": "rengine1",
     *                           "picture": null,
     *                           "website": null,
     *                           "emailVerified": false,
     *                           "gender": null,
     *                           "birthdate": null,
     *                           "phoneNumber": null,
     *                           "phoneNumberVerified": null,
     *                           "updatedAt": null,
     *                           "givenName": ""
     *                       },
     *                       "userInfo": {
     *                           "claims": {
     *                               "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                               "email_verified": false,
     *                               "preferred_username": "rengine1",
     *                               "given_name": "",
     *                               "family_name": ""
     *                           },
     *                           "address": {
     *                               "formatted": null,
     *                               "streetAddress": null,
     *                               "locality": null,
     *                               "region": null,
     *                               "postalCode": null,
     *                               "country": null
     *                           },
     *                           "locale": null,
     *                           "fullName": null,
     *                           "zoneInfo": null,
     *                           "email": null,
     *                           "profile": null,
     *                           "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                           "familyName": "",
     *                           "middleName": null,
     *                           "nickName": null,
     *                           "preferredUsername": "rengine1",
     *                           "picture": null,
     *                           "website": null,
     *                           "emailVerified": false,
     *                           "gender": null,
     *                           "birthdate": null,
     *                           "phoneNumber": null,
     *                           "phoneNumberVerified": null,
     *                           "updatedAt": null,
     *                           "givenName": ""
     *                       }
     *                   },
     *                   {
     *                       "authority": "SCOPE_email"
     *                   },
     *                   {
     *                       "authority": "SCOPE_openid"
     *                   },
     *                   {
     *                       "authority": "SCOPE_profile"
     *                   }
     *               ],
     *               "attributes": {
     *                   "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                   "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                   "email_verified": false,
     *                   "iss": "https://iam.wl4g.com/realms/master",
     *                   "typ": "ID",
     *                   "preferred_username": "rengine1",
     *                   "given_name": "",
     *                   "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                   "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                   "aud": [
     *                       "rengine"
     *                   ],
     *                   "acr": "1",
     *                   "azp": "rengine",
     *                   "auth_time": "2023-02-23T17:43:49Z",
     *                   "exp": "2023-02-23T17:44:50Z",
     *                   "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                   "family_name": "",
     *                   "iat": "2023-02-23T17:43:50Z",
     *                   "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *               },
     *               "idToken": {
     *                   "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMVlUSmlxcE1HSUI5eEg5OERZUTlJZlJmR2RsYWRKUjNmZE95TjRzeks4In0.eyJleHAiOjE2NzcxNzQyOTAsImlhdCI6MTY3NzE3NDIzMCwiYXV0aF90aW1lIjoxNjc3MTc0MjI5LCJqdGkiOiI5ZTllMzQwMC01NzFhLTQ4ZmItYWM2Ny02ZjYwMTUxODA5MTMiLCJpc3MiOiJodHRwczovL2lhbS53bDRnLmNvbS9yZWFsbXMvbWFzdGVyIiwiYXVkIjoicmVuZ2luZSIsInN1YiI6IjQyNTVkYjU3LTZjZDItNDIxYS1iOGRkLTMyYzBjOWY2YTk5YSIsInR5cCI6IklEIiwiYXpwIjoicmVuZ2luZSIsIm5vbmNlIjoiekRRcGhrRmlWbEdxUy1IWHk2cVhCRlRfTkxBQjQwdE9OS0Y3NW9pZGRGayIsInNlc3Npb25fc3RhdGUiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJhdF9oYXNoIjoiQm9iTTZMc0NldU13eWdsNjBocW12QSIsImFjciI6IjEiLCJzaWQiOiJmYjNmYzA3Yy0xM2Q5LTQ2NjUtOTYyOC1lYzc3NzJkYWZlM2MiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InJlbmdpbmUxIiwiZ2l2ZW5fbmFtZSI6IiIsImZhbWlseV9uYW1lIjoiIn0.wXW6fNPMilbOWlKRTtF70GqbcUN6U9h-NqlSrcgnm1lxhD2iGWcNPD3vQZPIBvEg5JShSUSIaiKLvO5b8mKa6w4EtGYd0R4AUlHZRvwcKktuW6rrr3Nwowrq2wslDRxI6uDTqpLu85iRXnQ2LPFzQ6jDxskj1_OYjnZGE3hNVvivX3vRZnPfPLTGvQvZIrbYccDg3GGntBGEOOU3iCRu71ifc4-JWncxeVYvsAlo88eDxT8lYDz3NaH0w2-XRNnSl9ByTIxrz35qIWPJrOCO0EGlr-u1I9myW5iVmSlFOWYcRDgFcFqPeDtMT7yIgyjtL0AB2zr6HS8XOor25MCnyw",
     *                   "issuedAt": "2023-02-23T17:43:50Z",
     *                   "expiresAt": "2023-02-23T17:44:50Z",
     *                   "claims": {
     *                       "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                       "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                       "email_verified": false,
     *                       "iss": "https://iam.wl4g.com/realms/master",
     *                       "typ": "ID",
     *                       "preferred_username": "rengine1",
     *                       "given_name": "",
     *                       "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                       "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                       "aud": [
     *                           "rengine"
     *                       ],
     *                       "acr": "1",
     *                       "azp": "rengine",
     *                       "auth_time": "2023-02-23T17:43:49Z",
     *                       "exp": "2023-02-23T17:44:50Z",
     *                       "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                       "iat": "2023-02-23T17:43:50Z",
     *                       "family_name": "",
     *                       "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *                   },
     *                   "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                   "audience": [
     *                       "rengine"
     *                   ],
     *                   "authenticatedAt": "2023-02-23T17:43:49Z",
     *                   "authenticationContextClass": "1",
     *                   "authenticationMethods": null,
     *                   "authorizedParty": "rengine",
     *                   "accessTokenHash": "BobM6LsCeuMwygl60hqmvA",
     *                   "authorizationCodeHash": null,
     *                   "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                   "issuer": "https://iam.wl4g.com/realms/master",
     *                   "address": {
     *                       "formatted": null,
     *                       "streetAddress": null,
     *                       "locality": null,
     *                       "region": null,
     *                       "postalCode": null,
     *                       "country": null
     *                   },
     *                   "locale": null,
     *                   "fullName": null,
     *                   "zoneInfo": null,
     *                   "email": null,
     *                   "profile": null,
     *                   "familyName": "",
     *                   "middleName": null,
     *                   "nickName": null,
     *                   "preferredUsername": "rengine1",
     *                   "picture": null,
     *                   "website": null,
     *                   "emailVerified": false,
     *                   "gender": null,
     *                   "birthdate": null,
     *                   "phoneNumber": null,
     *                   "phoneNumberVerified": null,
     *                   "updatedAt": null,
     *                   "givenName": ""
     *               },
     *               "userInfo": {
     *                   "claims": {
     *                       "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                       "email_verified": false,
     *                       "preferred_username": "rengine1",
     *                       "given_name": "",
     *                       "family_name": ""
     *                   },
     *                   "address": {
     *                       "formatted": null,
     *                       "streetAddress": null,
     *                       "locality": null,
     *                       "region": null,
     *                       "postalCode": null,
     *                       "country": null
     *                   },
     *                   "locale": null,
     *                   "fullName": null,
     *                   "zoneInfo": null,
     *                   "email": null,
     *                   "profile": null,
     *                   "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                   "familyName": "",
     *                   "middleName": null,
     *                   "nickName": null,
     *                   "preferredUsername": "rengine1",
     *                   "picture": null,
     *                   "website": null,
     *                   "emailVerified": false,
     *                   "gender": null,
     *                   "birthdate": null,
     *                   "phoneNumber": null,
     *                   "phoneNumberVerified": null,
     *                   "updatedAt": null,
     *                   "givenName": ""
     *               },
     *               "claims": {
     *                   "at_hash": "BobM6LsCeuMwygl60hqmvA",
     *                   "sub": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *                   "email_verified": false,
     *                   "iss": "https://iam.wl4g.com/realms/master",
     *                   "typ": "ID",
     *                   "preferred_username": "rengine1",
     *                   "given_name": "",
     *                   "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *                   "sid": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                   "aud": [
     *                       "rengine"
     *                   ],
     *                   "acr": "1",
     *                   "azp": "rengine",
     *                   "auth_time": "2023-02-23T17:43:49Z",
     *                   "exp": "2023-02-23T17:44:50Z",
     *                   "session_state": "fb3fc07c-13d9-4665-9628-ec7772dafe3c",
     *                   "family_name": "",
     *                   "iat": "2023-02-23T17:43:50Z",
     *                   "jti": "9e9e3400-571a-48fb-ac67-6f6015180913"
     *               },
     *               "name": "rengine1",
     *               "subject": "4255db57-6cd2-421a-b8dd-32c0c9f6a99a",
     *               "issuedAt": "2023-02-23T17:43:50Z",
     *               "expiresAt": "2023-02-23T17:44:50Z",
     *               "audience": [
     *                   "rengine"
     *               ],
     *               "authenticatedAt": "2023-02-23T17:43:49Z",
     *               "authenticationContextClass": "1",
     *               "authenticationMethods": null,
     *               "authorizedParty": "rengine",
     *               "accessTokenHash": "BobM6LsCeuMwygl60hqmvA",
     *               "authorizationCodeHash": null,
     *               "nonce": "zDQphkFiVlGqS-HXy6qXBFT_NLAB40tONKF75oiddFk",
     *               "issuer": "https://iam.wl4g.com/realms/master",
     *               "address": {
     *                   "formatted": null,
     *                   "streetAddress": null,
     *                   "locality": null,
     *                   "region": null,
     *                   "postalCode": null,
     *                   "country": null
     *               },
     *               "locale": null,
     *               "fullName": null,
     *               "zoneInfo": null,
     *               "email": null,
     *               "profile": null,
     *               "familyName": "",
     *               "middleName": null,
     *               "nickName": null,
     *               "preferredUsername": "rengine1",
     *               "picture": null,
     *               "website": null,
     *               "emailVerified": false,
     *               "gender": null,
     *               "birthdate": null,
     *               "phoneNumber": null,
     *               "phoneNumberVerified": null,
     *               "updatedAt": null,
     *               "givenName": ""
     *           },
     *           "authorizedClientRegistrationId": "default_oidc",
     *           "credentials": "",
     *           "name": "rengine1"
     *       }
     *   }
     * </pre>
     */
    @Override
    public UserAuthInfo currentUserInfo() {
        return authenticationService.currentUserInfo();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Role> findRolesByUserIds(@NotEmpty List<Long> userIds) {
        Assert2.notEmpty(userIds, "userIds");

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.in("_id", userIds)));
        USER_ROLE_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (var cursor = mongoTemplate.getCollection(SYS_USERS.getName())
                .aggregate(aggregates)
                .map(roleDoc -> BsonEntitySerializers.fromDocument(roleDoc, Role.class))
                .cursor();) {
            return IteratorUtils.toList(cursor);
        }
    }

    @Override
    public List<Long> assignRoles(UserAssignRole model) {
        notNullOf(model, "model");
        notNullOf(model.getUserId(), "model.userId");
        Assert2.notEmpty(model.getRoleIds(), "model.roleIds");

        return model.getRoleIds().parallelStream().map(roleId -> {
            final UserRole userRole = UserRole.builder().roleId(roleId).userId(model.getUserId()).build();
            userRole.preInsert();
            return mongoTemplate.save(userRole, SYS_USER_ROLES.getName()).getId();
        }).collect(toList());
    }

}
