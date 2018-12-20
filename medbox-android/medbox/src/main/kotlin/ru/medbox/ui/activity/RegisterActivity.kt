package ru.medbox.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import org.jboss.aerogear.android.authorization.AuthorizationManager
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthzModule
import org.jboss.aerogear.android.core.Callback
import ru.medbox.R
import ru.medbox.databinding.ActivityRegisterBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.handler.*
import ru.medbox.ui.viewmodel.RegisterViewModel
import ru.medbox.utils.Loggable
import java.net.URL
import java.util.*


class RegisterActivity : BaseActivity(), Loggable {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(RegisterViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.viewModel = viewModel
        binding.loginFb.setOnClickListener {
            val authzModule = AuthorizationManager.getModule(MODULE_FACEBOOK) as OAuth2AuthzModule
            authzModule.requestAccess(this, object : Callback<String> {
                override fun onSuccess(data: String?) {
                    log(this, "login success")
                    log(this, data)
                    viewModel.facebookToken.value = data
                }

                override fun onFailure(e: Exception?) {
                    log(this, "login fail")
                    log(this, e?.message.toString())
                }
            }
            )
        }
        binding.loginGg.setOnClickListener {
            val authzModule = AuthorizationManager.getModule(MODULE_GOOGLE) as OAuth2AuthzModule
            authzModule.requestAccess(this, object : Callback<String> {
                override fun onSuccess(data: String?) {
                    log(this, "login success")
                    log(this, data)
                    viewModel.googleToken.value = data
                }

                override fun onFailure(e: Exception?) {
                    log(this, "login fail")
                    log(this, e?.message.toString())
                }
            }
            )
        }
        binding.loginVk.setOnClickListener {
            val authzModule = AuthorizationManager.getModule(MODULE_VKONTAKTE) as OAuth2AuthzModule
            authzModule.requestAccess(this, object : Callback<String> {
                override fun onSuccess(data: String?) {
                    log(this, "login success")
                    log(this, data)
                    viewModel.vkToken.value = data
                }

                override fun onFailure(e: Exception?) {
                    log(this, "login fail")
                    log(this, e?.message.toString())
                }
            }
            )
        }

        viewModel.accessToken.observeForever {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            log(this, "Google token " + account.idToken)
            viewModel.googleToken.value = account.idToken
        }
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
                .setAccessTokenEndpoint("bx.zdorovkin.org:9443/auth/realms/jhipster/broker/vk/endpoint")
                .setAccountId("vk-token")
                .setClientId("1594555937316066")
                .setClientSecret("9f8c5754ece8996817eb4a6066bf1a14")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/jhipster/broker/vk/endpoint")
                .addAdditionalAccessParam(Pair.create("response_type", "code"))
                .setScopes(Arrays.asList("email"))
                .asModule()

        AuthorizationManager.config(MODULE_GOOGLE, OAuth2AuthorizationConfiguration::class.java)
                .setBaseURL(URL("https://"))
                .setAuthzEndpoint("accounts.google.com/o/oauth2/v2/auth")
                .setAccessTokenEndpoint("accounts.google.com/o/oauth2/v2/token")
                .setAccountId("google-token")
                .setClientId("1594555937316066")
                .setClientSecret("9f8c5754ece8996817eb4a6066bf1a14")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/jhipster/broker/google/endpoint")
                .setScopes(Arrays.asList("email"))
                .addAdditionalAccessParam(Pair.create("response_type", "code"))
                .asModule()

        AuthorizationManager.config(MODULE_KEYCLOAK, OAuth2AuthorizationConfiguration::class.java)
                .setBaseURL(URL("https://bx.zdorovkin.org:9443"))
                .setAuthzEndpoint("/auth/realms/jhipster/protocol/openid-connect/auth")
                .setAccessTokenEndpoint("/auth/realms/jhipster/protocol/openid-connect/token")
                .setRefreshEndpoint("/auth/realms/jhipster/protocol/openid-connect/token")
                .setAccountId("account-id")
                .setClientId("android-client")
                .setRedirectURL("https://bx.zdorovkin.org:9443/auth/realms/jhipster/broker/keycloak-oidc/endpoint")
                .asModule()
    }
}
