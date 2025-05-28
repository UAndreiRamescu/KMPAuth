package com.mmk.kmpauth.uihelper.facebook



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmk.kmpauth.uihelper.theme.Fonts
import io.github.mirzemehdi.kmpauth_uihelper.generated.resources.Res
import io.github.mirzemehdi.kmpauth_uihelper.generated.resources.ic_facebook_logo_white
import io.github.mirzemehdi.kmpauth_uihelper.generated.resources.ic_facebook_logo_blue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt



/**
 * FacebookSignInButton [Composable] with icon only.
 * This follows Apple's design guidelines and can be easily customized to fit into your project.
 *
 * @param mode [FacebookButtonMode]
 */
@Composable
public fun FacebookSignInButtonIconOnly(
    modifier: Modifier = Modifier.size(44.dp),
    mode: FacebookButtonMode = FacebookButtonMode.Blue,
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
) {
    val buttonColor = getButtonColor(mode)
    val borderStroke = getBorderStroke(mode)
    var buttonHeight by remember { mutableStateOf(44) }
    val localDensity = LocalDensity.current

    Button(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                buttonHeight =
                    with(localDensity) { coordinates.size.height.toDp().value.roundToInt() }
            },
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        shape = shape,
        colors = buttonColor,
        border = borderStroke,
    ) {
        FacebookIcon(modifier = Modifier.size(buttonHeight.dp), mode = mode)

    }
}



/**
 * FacebookSignInButton [Composable] with text that you can use in your #KMP project.
 * This follows Apple's design guidelines and can be easily customized to fit into your project.
 *
 * @param mode [FacebookButtonMode]
 * @param text Button's text. As per guideline this text should be "Sign in with Apple",
 * "Sign up with Apple", or "Continue with Apple".
 */
@Composable
public fun FacebookSignInButton(
    modifier: Modifier = Modifier.height(44.dp),
    mode: FacebookButtonMode = FacebookButtonMode.Blue,
    text: String = "Sign in with Facebook",
    fontFamily: FontFamily = Fonts.robotoFontFamily,
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
) {


    val buttonColor = getButtonColor(mode)
    val borderStroke = getBorderStroke(mode)
    val horizontalPadding = 0.dp
    val iconTextPadding = 0.dp
    var fontSize by remember { mutableStateOf(19) }
    var buttonHeight by remember { mutableStateOf(44) }
    var marginEnd by remember { mutableStateOf(0) }
    val localDensity = LocalDensity.current
    Button(
        modifier = modifier
            .onGloballyPositioned { coordinates ->

                val height =
                    with(localDensity) { coordinates.size.height.toDp().value.roundToInt() }
                val width = with(localDensity) { coordinates.size.width.toDp().value.roundToInt() }
                marginEnd = (width * 0.08).roundToInt()
                buttonHeight = height
                fontSize = ((height * 0.43).roundToInt())
            }.defaultMinSize(minWidth = 140.dp, minHeight = 30.dp),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        onClick = onClick,
        shape = shape,
        colors = buttonColor,
        border = borderStroke,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FacebookIcon(modifier = Modifier.size(buttonHeight.dp), mode = mode)
            Spacer(modifier = Modifier.width(iconTextPadding))
            Text(
                modifier = Modifier.graphicsLayer {
                    translationY = (-1).dp.toPx()
                }
                    .padding(end = marginEnd.dp),
                text = text,
                fontSize = fontSize.sp,
                maxLines = 1,
                fontFamily = fontFamily,
            )
        }

    }


}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun FacebookIcon(modifier: Modifier = Modifier, mode: FacebookButtonMode) {
    val source = when (mode) {
        FacebookButtonMode.Blue -> Res.drawable.ic_facebook_logo_white
        FacebookButtonMode.White -> Res.drawable.ic_facebook_logo_blue
        FacebookButtonMode.WhiteWithOutline -> Res.drawable.ic_facebook_logo_blue
    }
    Image(
        modifier = modifier,
        painter = painterResource(source),
        contentDescription = "appleIcon"
    )
}


private fun getBorderStroke(mode: FacebookButtonMode): BorderStroke? {
    val borderStroke = when (mode) {
        FacebookButtonMode.WhiteWithOutline -> BorderStroke(
            width = 1.dp,
            color = Color.Black,
        )

        else -> null
    }
    return borderStroke
}

@Composable
private fun getButtonColor(mode: FacebookButtonMode): ButtonColors {
    val containerColor = when (mode) {
        FacebookButtonMode.Blue -> Color(0xFF1877F2)
        else -> Color.White
    }

    val contentColor = when (mode) {
        FacebookButtonMode.Blue -> Color.White
        else -> Color.Black
    }

    return ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
}