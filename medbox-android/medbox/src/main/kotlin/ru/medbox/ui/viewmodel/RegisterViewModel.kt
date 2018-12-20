package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import ru.medbox.api.AccessToken
import ru.medbox.api.AccessTokenJsonAdapter
import ru.medbox.api.AuthApi
import ru.medbox.ui.BaseViewModel
import ru.medbox.utils.Prefs
import javax.inject.Inject

class RegisterViewModel : BaseViewModel() {

    @Inject lateinit var api: AuthApi
    @Inject lateinit var prefs: Prefs
    @Inject lateinit var moshi: Moshi

    val googleToken = MutableLiveData<String>()
    val facebookToken = MutableLiveData<String>()
    val vkToken = MutableLiveData<String>()

    val accessToken = MutableLiveData<AccessToken>()

    init {
        googleToken.observeForever {
            log(this, "observed google token")
            exchangeToken("google", it!!)
        }
        facebookToken.observeForever {
            log(this, "observed facebook token")
            exchangeToken("facebook", it!!)
        }
        vkToken.observeForever {
            log(this, "observed facebook token")
            exchangeToken("vk", it!!)
        }
    }

    private fun exchangeToken(issuer: String, token: String) {
        log(this, "Let's try to exchange token...")
        val clientId = "social_client"
        val clientSecret = "4289bbb2-fbc1-4d08-a46c-3fc6aa0fbb1e"
        val grantType = "urn:ietf:params:oauth:grant-type:token-exchange"
        val tokenType = "urn:ietf:params:oauth:token-type:access_token"
        val requestedTokenType = "urn:ietf:params:oauth:token-type:refresh_token"
        subscribeOnRequest(api.exchangeToken(clientId, clientSecret, grantType, token, issuer, tokenType, requestedTokenType))
    }

    override fun onRetrieveDataSuccess(data: Any) {
        try {
            if (data is ResponseBody) {
                val jsonToken = data.string()
                log(this, "ACCESS TOKEN RECEIVED: $jsonToken")

                val token: AccessToken? = AccessTokenJsonAdapter(moshi).fromJson(jsonToken.trim())
                prefs.token = token!!.accessToken
                accessToken.value = token
                log(this, "ACCESS TOKEN PARSED: " + token.accessToken)
                log(this, "ACCESS TOKEN EXPIRED: " + token.expiresIn)

            } else {
                log(this, "Unknown response type!")
                log(this, data.toString())
            }
        } catch (e: Exception) {
            log(this, e.message.toString())
            e.printStackTrace()
        }
    }
}