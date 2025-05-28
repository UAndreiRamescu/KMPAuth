package com.mmk.kmpauth.firebase.facebook

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.core.UiContainerScope
import com.mmk.kmpauth.core.getActivity

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FacebookAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

public val callbackManager: CallbackManager = CallbackManager.Factory.create()

/**
 * FacebookButton Ui Container Composable that handles all sign-in functionality for Facebook.
 * Child of this Composable can be any view or Composable function.
 * You need to call [UiContainerScope.onClick] function on your child view's click function.
 *
 * [onResult] callback will return [Result] with [FirebaseUser] type.
 * @param requestScopes list of request scopes type of [FacebookSignInRequestScope].
 * @param linkAccount if true, it will link the account with the current user. Default value is false
 * Example Usage:
 * ```
 * //Facebook Sign-In with Custom Button and authentication with Firebase
 * FacebookButtonUiContainer(onResult = onFirebaseResult) {
 *     Button(onClick = { this.onClick() }) { Text("Facebook Sign-In (Custom Design)") }
 * }
 *
 * ```
 *
 */
@OptIn(KMPAuthInternalApi::class)
@Composable
public actual fun FacebookButtonUiContainer(
    modifier: Modifier,
    requestScopes: List<FacebookSignInRequestScope>,
    onResult: (Result<FirebaseUser?>) -> Unit,
    linkAccount: Boolean,
    content: @Composable UiContainerScope.() -> Unit,
) {
    val updatedOnResult by rememberUpdatedState(onResult)
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current.getActivity()
    val uiContainerScope = remember {
        object : UiContainerScope {
            override fun onClick() {
                LoginManager.getInstance().registerCallback(
                    callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            val accessToken = loginResult.accessToken.token
                            val authCredential = FacebookAuthProvider.credential(accessToken)
                            coroutineScope.launch {
                                try {
                                    val auth = Firebase.auth
                                    val currentUser = auth.currentUser
                                    val result = if (linkAccount && currentUser != null) {
                                        currentUser.linkWithCredential(authCredential)
                                    } else {
                                        auth.signInWithCredential(authCredential)
                                    }
                                    if (result.user == null) updatedOnResult(Result.failure(IllegalStateException("Firebase Null user")))
                                    else updatedOnResult(Result.success(result.user))
                                    print("Facebook sign-in successful: ${result.user?.uid}")
                                } catch (e: Exception) {
                                    if (e is CancellationException) throw e
                                    updatedOnResult(Result.failure(e))
                                }
                            }
                        }

                        override fun onCancel() {
                            updatedOnResult(Result.failure(IllegalStateException("Facebook sign-in cancelled")))
                        }

                        override fun onError(error: FacebookException) {
                            updatedOnResult(Result.failure(IllegalStateException("Facebook sign-in error: ${error.message}")))
                        }
                    })

                LoginManager.getInstance().logInWithReadPermissions(activity as Activity, listOf("email", "public_profile"))
            }
        }
    }
    Box(modifier = modifier) { uiContainerScope.content() }
}

@Deprecated(
    "Use FacebookButtonUiContainer with the linkAccount parameter, which defaults to false.",
    ReplaceWith(""),
    DeprecationLevel.WARNING
)
@Composable
public actual fun FacebookButtonUiContainer(
    modifier: Modifier,
    requestScopes: List<FacebookSignInRequestScope>,
    onResult: (Result<FirebaseUser?>) -> Unit,
    content: @Composable UiContainerScope.() -> Unit,
) {
    FacebookButtonUiContainer(modifier, requestScopes, onResult, false, content)
}