package com.mmk.kmpauth.firebase.facebook

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.core.UiContainerScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FacebookAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import cocoapods.FacebookCore.*
import cocoapods.FacebookLogin.*
import kotlinx.coroutines.launch

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
    val uiContainerScope = remember {
        object : UiContainerScope {
            override fun onClick() {
                val loginManager = LoginManager()
                loginManager.logIn(
                    permissions = listOf("email", "public_profile"),
                    fromViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                ) { result: LoginManagerLoginResult?, error: NSError? ->
                    if (error != null) {
                        updatedOnResult(Result.failure(IllegalStateException("Facebook login error: ${error.localizedDescription}")))
                        return@logIn
                    }

                    if (result?.isCancelled == true) {
                        updatedOnResult(Result.failure(IllegalStateException("Facebook login cancelled")))
                        return@logIn
                    }

                    val accessToken = result?.token?.tokenString
                    if (accessToken == null) {
                        updatedOnResult(Result.failure(IllegalStateException("Facebook access token is null")))
                        return@logIn
                    }

                    val authCredential = FacebookAuthProvider.credential(accessToken)
                    coroutineScope.launch {
                        try {
                            val auth = Firebase.auth
                            val currentUser = auth.currentUser
                            val firebaseResult = if (linkAccount && currentUser != null) {
                                currentUser.linkWithCredential(authCredential)
                            } else {
                                auth.signInWithCredential(authCredential)
                            }
                            if (firebaseResult.user == null) updatedOnResult(Result.failure(IllegalStateException("Firebase Null user")))
                            else updatedOnResult(Result.success(firebaseResult.user))
                            print("Facebook sign-in successful: ${firebaseResult.user?.uid}")
                        } catch (e: Exception) {
                            updatedOnResult(Result.failure(e))
                        }
                    }
                }
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