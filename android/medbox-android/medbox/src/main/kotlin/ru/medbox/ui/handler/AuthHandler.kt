package ru.medbox.ui.handler

import android.app.Activity
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.jboss.aerogear.android.authorization.AuthorizationManager
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthzModule
import ru.medbox.utils.Loggable
import java.net.URL
import java.util.*

const val GOOGLE_REQUEST = 26;
const val MODULE_KEYCLOAK = "keycloak"
const val MODULE_VKONTAKTE = "vkontake"
const val MODULE_FACEBOOK = "facebook"
const val MODULE_GOOGLE = "google"

class AuthHandler(val activity: Activity) : Loggable {
    fun register() {
        val authzModule = AuthorizationManager.getModule(MODULE_KEYCLOAK) as OAuth2AuthzModule
//        authzModule.requestAccess(activity, requestCallback)
    }

    fun login() {
        val authzModule = AuthorizationManager.getModule(MODULE_KEYCLOAK) as OAuth2AuthzModule
//        authzModule.requestAccess(activity, requestCallback)
    }

    fun loginVk() {
        val authzModule = AuthorizationManager.getModule(MODULE_VKONTAKTE) as OAuth2AuthzModule
//        authzModule.requestAccess(activity, requestCallback)
    }

    fun loginFb() {
        val authzModule = AuthorizationManager.getModule(MODULE_FACEBOOK) as OAuth2AuthzModule
        if (!AuthorizationManager.getModule(MODULE_FACEBOOK).isAuthorized) {
            log(this, "go to auth")
//            authzModule.requestAccess(activity, requestCallback)
        } else {
            log(this, "already authorized")

        }
    }

    fun loginGoogle() {
        activity.startActivityForResult(GoogleSignIn.getClient(activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken("564261371701-9ph4orrm1fbj2plbefdn9o6nenpejv9v.apps.googleusercontent.com")
                .build()).signInIntent, GOOGLE_REQUEST
        )
    }

    init {
        AuthorizationManager.config(MODULE_FACEBOOK, OAuth2AuthorizationConfiguration::class.java)
                .setBaseURL(URL("https://"))
                .setAuthzEndpoint("www.facebook.com/v3.1/dialog/oauth")
                .setAccessTokenEndpoint("graph.facebook.com/v3.1/oauth/access_token")
                .setAccountId("facebook-token")
                .setClientId("1594555937316066")
                .setClientSecret("9f8c5754ece8996817eb4a6066bf1a14")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/jhipster/broker/facebook/endpoint")
                .setRefreshEndpoint("graph.facebook.com/v3.1/oauth/access_token")
                .addAdditionalAccessParam(Pair.create("response_type", "code"))
                .setScopes(Arrays.asList("email"))
                .asModule()

        AuthorizationManager.config(MODULE_VKONTAKTE, OAuth2AuthorizationConfiguration::class.java)
                .setBaseURL(URL("https://"))
                .setAuthzEndpoint("oauth.vk.com/authorize")
//                .setAccessTokenEndpoint("bx.zdorovkin.org:9443/auth/realms/Android/broker/vk/endpoint")
                .setAccountId("vk-token")
                .setClientId("6704352")
//                .setClientSecret("pBk2eT1GjF16TFyc72mb")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/Android/broker/vk/endpoint")
                .addAdditionalAccessParam(Pair.create("response_type", "code"))
                .setScopes(Arrays.asList("email"))
                .asModule()

        AuthorizationManager.config(MODULE_KEYCLOAK, OAuth2AuthorizationConfiguration::class.java)
                .setBaseURL(URL("https://bx.zdorovkin.org:9443"))
                .setAuthzEndpoint("/auth/realms/Android/protocol/openid-connect/auth")
                .setAccessTokenEndpoint("/auth/realms/Android/protocol/openid-connect/token")
                .setRefreshEndpoint("/auth/realms/Android/protocol/openid-connect/token")
                .setAccountId("account-id")
//                .addAdditionalAuthorizationParam((Pair.create("grant_type", "password")))
//                .addAdditionalAuthorizationParam((Pair.create("username", "aUserName")))
//                .addAdditionalAuthorizationParam((Pair.create("password", "aPassword")))
                .setClientId("android-client")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/Android/broker/keycloak-oidc/endpoint")
                .asModule()

    }
}