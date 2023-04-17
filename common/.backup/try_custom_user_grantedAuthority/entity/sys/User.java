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
package com.wl4g.rengine.common.entity.sys;

import java.io.Serializable;
import java.net.URL;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.util.spring.convert.ClaimConversionService;
import com.wl4g.rengine.common.util.spring.convert.TypeDescriptor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link User}
 * 
 * @author James Wong
 * @date 2022-09-13
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.common.entity.sys.springframework.security.core.userdetails.User}
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    private static final long serialVersionUID = -5762348176963349685L;
    private @NotBlank String username; // email
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Set<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    private List<UserRole> userRoles;

    // Ignore organization getter/setter.
    @JsonIgnore
    @Override
    public String getOrgCode() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setOrgCode(String orgCode) {
    }

    public static interface GrantedAuthority extends Serializable {
        String getAuthority();
    }

    // --- Simple authority. ---

    public static class SimpleGrantedAuthority implements GrantedAuthority {
        private static final long serialVersionUID = 570;
        private final String role;

        public SimpleGrantedAuthority(String role) {
            Assert2.hasText(role, "A granted authority textual representation is required");
            this.role = role;
        }

        @Override
        public String getAuthority() {
            return this.role;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof SimpleGrantedAuthority) {
                return this.role.equals(((SimpleGrantedAuthority) obj).role);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.role.hashCode();
        }

        @Override
        public String toString() {
            return this.role;
        }
    }

    // --- OAuth2 authority. ---

    public static class OAuth2UserAuthority implements GrantedAuthority {

        /**
         * Global Serialization value for Spring Security classes. N.B. Classes
         * are not intended to be serializable between different versions. See
         * SEC-1709 for why we still need a serial version.
         */
        private static final long serialVersionUID = 570;
        private final String authority;
        private final Map<String, Object> attributes;

        public OAuth2UserAuthority(Map<String, Object> attributes) {
            this("ROLE_USER", attributes);
        }

        public OAuth2UserAuthority(String authority, Map<String, Object> attributes) {
            Assert2.hasText(authority, "authority cannot be empty");
            Assert2.notEmpty(attributes, "attributes cannot be empty");
            this.authority = authority;
            this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        }

        @Override
        public String getAuthority() {
            return this.authority;
        }

        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            OAuth2UserAuthority that = (OAuth2UserAuthority) obj;
            if (!this.getAuthority().equals(that.getAuthority())) {
                return false;
            }
            return this.getAttributes().equals(that.getAttributes());
        }

        @Override
        public int hashCode() {
            int result = this.getAuthority().hashCode();
            result = 31 * result + this.getAttributes().hashCode();
            return result;
        }

        @Override
        public String toString() {
            return this.getAuthority();
        }

    }

    // --- OIDC authority. ---

    @SuppressWarnings("serial")
    public static class OidcUserAuthority extends OAuth2UserAuthority {
        private final OidcIdToken idToken;
        private final OidcUserInfo userInfo;

        public OidcUserAuthority(OidcIdToken idToken) {
            this(idToken, null);
        }

        public OidcUserAuthority(OidcIdToken idToken, OidcUserInfo userInfo) {
            this("ROLE_USER", idToken, userInfo);
        }

        public OidcUserAuthority(String authority, OidcIdToken idToken, OidcUserInfo userInfo) {
            super(authority, collectClaims(idToken, userInfo));
            this.idToken = idToken;
            this.userInfo = userInfo;
        }

        public OidcIdToken getIdToken() {
            return this.idToken;
        }

        public OidcUserInfo getUserInfo() {
            return this.userInfo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            if (!super.equals(obj)) {
                return false;
            }
            OidcUserAuthority that = (OidcUserAuthority) obj;
            if (!this.getIdToken().equals(that.getIdToken())) {
                return false;
            }
            return (this.getUserInfo() != null) ? this.getUserInfo().equals(that.getUserInfo()) : that.getUserInfo() == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.getIdToken().hashCode();
            result = 31 * result + ((this.getUserInfo() != null) ? this.getUserInfo().hashCode() : 0);
            return result;
        }

        static Map<String, Object> collectClaims(OidcIdToken idToken, OidcUserInfo userInfo) {
            Assert2.notNull(idToken, "idToken cannot be null");
            Map<String, Object> claims = new HashMap<>();
            if (userInfo != null) {
                claims.putAll(userInfo.getClaims());
            }
            claims.putAll(idToken.getClaims());
            return claims;
        }
    }

    public static interface ClaimAccessor {

        Map<String, Object> getClaims();

        @SuppressWarnings("unchecked")
        default <T> T getClaim(String claim) {
            return !hasClaim(claim) ? null : (T) getClaims().get(claim);
        }

        default boolean hasClaim(String claim) {
            Assert2.notNull(claim, "claim cannot be null");
            return getClaims().containsKey(claim);
        }

        @Deprecated
        default Boolean containsClaim(String claim) {
            return hasClaim(claim);
        }

        default String getClaimAsString(String claim) {
            return !hasClaim(claim) ? null
                    : ClaimConversionService.getSharedInstance().convert(getClaims().get(claim), String.class);
        }

        default Boolean getClaimAsBoolean(String claim) {
            if (!hasClaim(claim)) {
                return null;
            }
            Object claimValue = getClaims().get(claim);
            Boolean convertedValue = ClaimConversionService.getSharedInstance().convert(claimValue, Boolean.class);
            Assert2.notNull(convertedValue,
                    () -> "Unable to convert claim '" + claim + "' of type '" + claimValue.getClass() + "' to Boolean.");
            return convertedValue;
        }

        default Instant getClaimAsInstant(String claim) {
            if (!hasClaim(claim)) {
                return null;
            }
            Object claimValue = getClaims().get(claim);
            Instant convertedValue = ClaimConversionService.getSharedInstance().convert(claimValue, Instant.class);
            Assert2.isTrue(convertedValue != null,
                    () -> "Unable to convert claim '" + claim + "' of type '" + claimValue.getClass() + "' to Instant.");
            return convertedValue;
        }

        default URL getClaimAsURL(String claim) {
            if (!hasClaim(claim)) {
                return null;
            }
            Object claimValue = getClaims().get(claim);
            URL convertedValue = ClaimConversionService.getSharedInstance().convert(claimValue, URL.class);
            Assert2.isTrue(convertedValue != null,
                    () -> "Unable to convert claim '" + claim + "' of type '" + claimValue.getClass() + "' to URL.");
            return convertedValue;
        }

        @SuppressWarnings("unchecked")
        default Map<String, Object> getClaimAsMap(String claim) {
            if (!hasClaim(claim)) {
                return null;
            }
            final TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
            final TypeDescriptor targetDescriptor = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class),
                    TypeDescriptor.valueOf(Object.class));
            Object claimValue = getClaims().get(claim);
            Map<String, Object> convertedValue = (Map<String, Object>) ClaimConversionService.getSharedInstance()
                    .convert(claimValue, sourceDescriptor, targetDescriptor);
            Assert2.isTrue(convertedValue != null,
                    () -> "Unable to convert claim '" + claim + "' of type '" + claimValue.getClass() + "' to Map.");
            return convertedValue;
        }

        @SuppressWarnings("unchecked")
        default List<String> getClaimAsStringList(String claim) {
            if (!hasClaim(claim)) {
                return null;
            }
            final TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
            final TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class));
            Object claimValue = getClaims().get(claim);
            List<String> convertedValue = (List<String>) ClaimConversionService.getSharedInstance()
                    .convert(claimValue, sourceDescriptor, targetDescriptor);
            Assert2.isTrue(convertedValue != null,
                    () -> "Unable to convert claim '" + claim + "' of type '" + claimValue.getClass() + "' to List.");
            return convertedValue;
        }

    }

    /**
     * @see https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
     * @see https://openid.net/specs/openid-connect-core-1_0.html#UserInfoResponse
     * @see https://openid.net/specs/openid-connect-core-1_0.html#IDToken
     */
    public static interface StandardClaimNames {

        /**
         * {@code sub} - the Subject identifier
         */
        String SUB = "sub";

        /**
         * {@code name} - the user's full name
         */
        String NAME = "name";

        /**
         * {@code given_name} - the user's given name(s) or first name(s)
         */
        String GIVEN_NAME = "given_name";

        /**
         * {@code family_name} - the user's surname(s) or last name(s)
         */
        String FAMILY_NAME = "family_name";

        /**
         * {@code middle_name} - the user's middle name(s)
         */
        String MIDDLE_NAME = "middle_name";

        /**
         * {@code nickname} - the user's nick name that may or may not be the
         * same as the {@code given_name}
         */
        String NICKNAME = "nickname";

        /**
         * {@code preferred_username} - the preferred username that the user
         * wishes to be referred to
         */
        String PREFERRED_USERNAME = "preferred_username";

        /**
         * {@code profile} - the URL of the user's profile page
         */
        String PROFILE = "profile";

        /**
         * {@code picture} - the URL of the user's profile picture
         */
        String PICTURE = "picture";

        /**
         * {@code website} - the URL of the user's web page or blog
         */
        String WEBSITE = "website";

        /**
         * {@code email} - the user's preferred e-mail address
         */
        String EMAIL = "email";

        /**
         * {@code email_verified} - {@code true} if the user's e-mail address
         * has been verified, otherwise {@code false}
         */
        String EMAIL_VERIFIED = "email_verified";

        /**
         * {@code gender} - the user's gender
         */
        String GENDER = "gender";

        /**
         * {@code birthdate} - the user's birth date
         */
        String BIRTHDATE = "birthdate";

        /**
         * {@code zoneinfo} - the user's time zone
         */
        String ZONEINFO = "zoneinfo";

        /**
         * {@code locale} - the user's locale
         */
        String LOCALE = "locale";

        /**
         * {@code phone_number} - the user's preferred phone number
         */
        String PHONE_NUMBER = "phone_number";

        /**
         * {@code phone_number_verified} - {@code true} if the user's phone
         * number has been verified, otherwise {@code false}
         */
        String PHONE_NUMBER_VERIFIED = "phone_number_verified";

        /**
         * {@code address} - the user's preferred postal address
         */
        String ADDRESS = "address";

        /**
         * {@code updated_at} - the time the user's information was last updated
         */
        String UPDATED_AT = "updated_at";

    }

    /**
     * @see https://openid.net/specs/openid-connect-core-1_0.html#IDToken
     */
    public static interface IdTokenClaimNames {

        /**
         * {@code iss} - the Issuer identifier
         */
        String ISS = "iss";

        /**
         * {@code sub} - the Subject identifier
         */
        String SUB = "sub";

        /**
         * {@code aud} - the Audience(s) that the ID Token is intended for
         */
        String AUD = "aud";

        /**
         * {@code exp} - the Expiration time on or after which the ID Token MUST
         * NOT be accepted
         */
        String EXP = "exp";

        /**
         * {@code iat} - the time at which the ID Token was issued
         */
        String IAT = "iat";

        /**
         * {@code auth_time} - the time when the End-User authentication
         * occurred
         */
        String AUTH_TIME = "auth_time";

        /**
         * {@code nonce} - a {@code String} value used to associate a Client
         * session with an ID Token, and to mitigate replay attacks.
         */
        String NONCE = "nonce";

        /**
         * {@code acr} - the Authentication Context Class Reference
         */
        String ACR = "acr";

        /**
         * {@code amr} - the Authentication Methods References
         */
        String AMR = "amr";

        /**
         * {@code azp} - the Authorized party to which the ID Token was issued
         */
        String AZP = "azp";

        /**
         * {@code at_hash} - the Access Token hash value
         */
        String AT_HASH = "at_hash";

        /**
         * {@code c_hash} - the Authorization Code hash value
         */
        String C_HASH = "c_hash";

    }

    public static interface AddressStandardClaim {
        String getFormatted();

        String getStreetAddress();

        String getLocality();

        String getRegion();

        String getPostalCode();

        String getCountry();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DefaultAddressStandardClaim implements AddressStandardClaim {
        private static final String FORMATTED_FIELD_NAME = "formatted";
        private static final String STREET_ADDRESS_FIELD_NAME = "street_address";
        private static final String LOCALITY_FIELD_NAME = "locality";
        private static final String REGION_FIELD_NAME = "region";
        private static final String POSTAL_CODE_FIELD_NAME = "postal_code";
        private static final String COUNTRY_FIELD_NAME = "country";
        private String formatted;
        private String streetAddress;
        private String locality;
        private String region;
        private String postalCode;
        private String country;

        public DefaultAddressStandardClaim(Map<String, Object> addressFields) {
            setFormatted((String) addressFields.get(FORMATTED_FIELD_NAME));
            setStreetAddress((String) addressFields.get(STREET_ADDRESS_FIELD_NAME));
            setLocality((String) addressFields.get(LOCALITY_FIELD_NAME));
            setRegion((String) addressFields.get(REGION_FIELD_NAME));
            setPostalCode((String) addressFields.get(POSTAL_CODE_FIELD_NAME));
            setCountry((String) addressFields.get(COUNTRY_FIELD_NAME));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !AddressStandardClaim.class.isAssignableFrom(obj.getClass())) {
                return false;
            }
            AddressStandardClaim other = (AddressStandardClaim) obj;
            if ((this.getFormatted() != null) ? !this.getFormatted().equals(other.getFormatted())
                    : other.getFormatted() != null) {
                return false;
            }
            if ((this.getStreetAddress() != null) ? !this.getStreetAddress().equals(other.getStreetAddress())
                    : other.getStreetAddress() != null) {
                return false;
            }
            if ((this.getLocality() != null) ? !this.getLocality().equals(other.getLocality()) : other.getLocality() != null) {
                return false;
            }
            if ((this.getRegion() != null) ? !this.getRegion().equals(other.getRegion()) : other.getRegion() != null) {
                return false;
            }
            if ((this.getPostalCode() != null) ? !this.getPostalCode().equals(other.getPostalCode())
                    : other.getPostalCode() != null) {
                return false;
            }
            return (this.getCountry() != null) ? this.getCountry().equals(other.getCountry()) : other.getCountry() == null;
        }

        @Override
        public int hashCode() {
            int result = (this.getFormatted() != null) ? this.getFormatted().hashCode() : 0;
            result = 31 * result + ((this.getStreetAddress() != null) ? this.getStreetAddress().hashCode() : 0);
            result = 31 * result + ((this.getLocality() != null) ? this.getLocality().hashCode() : 0);
            result = 31 * result + ((this.getRegion() != null) ? this.getRegion().hashCode() : 0);
            result = 31 * result + ((this.getPostalCode() != null) ? this.getPostalCode().hashCode() : 0);
            result = 31 * result + ((this.getCountry() != null) ? this.getCountry().hashCode() : 0);
            return result;
        }
    }

    public static interface StandardClaimAccessor extends ClaimAccessor {

        default String getSubject() {
            return this.getClaimAsString(StandardClaimNames.SUB);
        }

        default String getFullName() {
            return this.getClaimAsString(StandardClaimNames.NAME);
        }

        default String getGivenName() {
            return this.getClaimAsString(StandardClaimNames.GIVEN_NAME);
        }

        default String getFamilyName() {
            return this.getClaimAsString(StandardClaimNames.FAMILY_NAME);
        }

        default String getMiddleName() {
            return this.getClaimAsString(StandardClaimNames.MIDDLE_NAME);
        }

        default String getNickName() {
            return this.getClaimAsString(StandardClaimNames.NICKNAME);
        }

        default String getPreferredUsername() {
            return this.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
        }

        default String getProfile() {
            return this.getClaimAsString(StandardClaimNames.PROFILE);
        }

        default String getPicture() {
            return this.getClaimAsString(StandardClaimNames.PICTURE);
        }

        default String getWebsite() {
            return this.getClaimAsString(StandardClaimNames.WEBSITE);
        }

        default String getEmail() {
            return this.getClaimAsString(StandardClaimNames.EMAIL);
        }

        default Boolean getEmailVerified() {
            return this.getClaimAsBoolean(StandardClaimNames.EMAIL_VERIFIED);
        }

        default String getGender() {
            return this.getClaimAsString(StandardClaimNames.GENDER);
        }

        default String getBirthdate() {
            return this.getClaimAsString(StandardClaimNames.BIRTHDATE);
        }

        default String getZoneInfo() {
            return this.getClaimAsString(StandardClaimNames.ZONEINFO);
        }

        default String getLocale() {
            return this.getClaimAsString(StandardClaimNames.LOCALE);
        }

        default String getPhoneNumber() {
            return this.getClaimAsString(StandardClaimNames.PHONE_NUMBER);
        }

        default Boolean getPhoneNumberVerified() {
            return this.getClaimAsBoolean(StandardClaimNames.PHONE_NUMBER_VERIFIED);
        }

        default AddressStandardClaim getAddress() {
            Map<String, Object> addressFields = this.getClaimAsMap(StandardClaimNames.ADDRESS);
            return (!CollectionUtils2.isEmpty(addressFields) ? new DefaultAddressStandardClaim(addressFields)
                    : new DefaultAddressStandardClaim());
        }

        default Instant getUpdatedAt() {
            return this.getClaimAsInstant(StandardClaimNames.UPDATED_AT);
        }
    }

    public static interface IdTokenClaimAccessor extends StandardClaimAccessor {

        default URL getIssuer() {
            return this.getClaimAsURL(IdTokenClaimNames.ISS);
        }

        @Override
        default String getSubject() {
            return this.getClaimAsString(IdTokenClaimNames.SUB);
        }

        default List<String> getAudience() {
            return this.getClaimAsStringList(IdTokenClaimNames.AUD);
        }

        default Instant getExpiresAt() {
            return this.getClaimAsInstant(IdTokenClaimNames.EXP);
        }

        default Instant getIssuedAt() {
            return this.getClaimAsInstant(IdTokenClaimNames.IAT);
        }

        default Instant getAuthenticatedAt() {
            return this.getClaimAsInstant(IdTokenClaimNames.AUTH_TIME);
        }

        default String getNonce() {
            return this.getClaimAsString(IdTokenClaimNames.NONCE);
        }

        default String getAuthenticationContextClass() {
            return this.getClaimAsString(IdTokenClaimNames.ACR);
        }

        default List<String> getAuthenticationMethods() {
            return this.getClaimAsStringList(IdTokenClaimNames.AMR);
        }

        default String getAuthorizedParty() {
            return this.getClaimAsString(IdTokenClaimNames.AZP);
        }

        default String getAccessTokenHash() {
            return this.getClaimAsString(IdTokenClaimNames.AT_HASH);
        }

        default String getAuthorizationCodeHash() {
            return this.getClaimAsString(IdTokenClaimNames.C_HASH);
        }

    }

    public static interface OAuth2Token {

        String getTokenValue();

        @Nullable
        default Instant getIssuedAt() {
            return null;
        }

        @Nullable
        default Instant getExpiresAt() {
            return null;
        }

    }

    public static abstract class AbstractOAuth2Token implements OAuth2Token, Serializable {
        private static final long serialVersionUID = 570;
        private final String tokenValue;
        private final Instant issuedAt;
        private final Instant expiresAt;

        protected AbstractOAuth2Token(String tokenValue) {
            this(tokenValue, null, null);
        }

        protected AbstractOAuth2Token(String tokenValue, @Nullable Instant issuedAt, @Nullable Instant expiresAt) {
            Assert2.hasText(tokenValue, "tokenValue cannot be empty");
            if (issuedAt != null && expiresAt != null) {
                Assert2.isTrue(expiresAt.isAfter(issuedAt), "expiresAt must be after issuedAt");
            }
            this.tokenValue = tokenValue;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        public String getTokenValue() {
            return this.tokenValue;
        }

        @Nullable
        public Instant getIssuedAt() {
            return this.issuedAt;
        }

        @Nullable
        public Instant getExpiresAt() {
            return this.expiresAt;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            AbstractOAuth2Token other = (AbstractOAuth2Token) obj;
            if (!this.getTokenValue().equals(other.getTokenValue())) {
                return false;
            }
            if ((this.getIssuedAt() != null) ? !this.getIssuedAt().equals(other.getIssuedAt()) : other.getIssuedAt() != null) {
                return false;
            }
            return (this.getExpiresAt() != null) ? this.getExpiresAt().equals(other.getExpiresAt())
                    : other.getExpiresAt() == null;
        }

        @Override
        public int hashCode() {
            int result = this.getTokenValue().hashCode();
            result = 31 * result + ((this.getIssuedAt() != null) ? this.getIssuedAt().hashCode() : 0);
            result = 31 * result + ((this.getExpiresAt() != null) ? this.getExpiresAt().hashCode() : 0);
            return result;
        }
    }

    @SuppressWarnings("serial")
    public static class OidcIdToken extends AbstractOAuth2Token implements IdTokenClaimAccessor {
        private final Map<String, Object> claims;

        public OidcIdToken(String tokenValue, Instant issuedAt, Instant expiresAt, Map<String, Object> claims) {
            super(tokenValue, issuedAt, expiresAt);
            Assert2.notEmpty(claims, "claims cannot be empty");
            this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(claims));
        }

        @Override
        public Map<String, Object> getClaims() {
            return this.claims;
        }

    }

    public static class OidcUserInfo implements StandardClaimAccessor, Serializable {
        private static final long serialVersionUID = 570;
        private final Map<String, Object> claims;

        public OidcUserInfo(Map<String, Object> claims) {
            Assert2.notEmpty(claims, "claims cannot be empty");
            this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(claims));
        }

        @Override
        public Map<String, Object> getClaims() {
            return this.claims;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            OidcUserInfo that = (OidcUserInfo) obj;
            return this.getClaims().equals(that.getClaims());
        }

        @Override
        public int hashCode() {
            return this.getClaims().hashCode();
        }

    }

    // --- TODO SAML authority.---

}