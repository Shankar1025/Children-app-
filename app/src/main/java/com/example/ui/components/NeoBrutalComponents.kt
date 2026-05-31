package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = BrutalBlack,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 16.dp,
    shadowColor: Color = BrutalBlack,
    shadowOffset: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(modifier = modifier) {
        // Shadow base layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(
                    color = shadowColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
        // Main content layer
        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius))
                .then(clickModifier)
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NeoBrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoYellow,
    borderColor: Color = BrutalBlack,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 16.dp,
    shadowColor: Color = BrutalBlack,
    shadowOffset: Dp = 6.dp,
    isEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bouncy press effect: button moves closer to shadow when pressed!
    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) 1.dp else shadowOffset,
        animationSpec = tween(durationMillis = 80),
        label = "press_offset"
    )

    val currentShadowOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) 1.dp else shadowOffset,
        animationSpec = tween(durationMillis = 80),
        label = "shadow_offset"
    )

    val clickModifier = if (isEnabled) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null, // Disables default ripple so we control the physical press animation
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Shadow Layer (Slightly static but mimics compression)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(
                    color = if (isEnabled) shadowColor else Color.Gray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
        // Main Button Layer
        Row(
            modifier = Modifier
                .offset(x = currentOffset - shadowOffset, y = currentOffset - shadowOffset)
                .background(
                    color = if (isEnabled) backgroundColor else Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(
                    width = borderWidth,
                    color = if (isEnabled) borderColor else Color.Gray,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius))
                .then(clickModifier)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun NeoBrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    maxLines: Int = 1
) {
    NeoBrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        shadowOffset = 4.dp
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            maxLines = maxLines,
            singleLine = maxLines == 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrutalBlack
            ),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun NeoBrutalProgressBar(
    progress: Float, // 0.0 to 1.0
    color: Color = NeoGreen,
    modifier: Modifier = Modifier,
    height: Dp = 24.dp
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(color = LightGrey, shape = RoundedCornerShape(12.dp))
            .border(width = 3.dp, color = BrutalBlack, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Highlight indicator
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(clampedProgress)
                .background(color = color)
                .border(
                    width = if (clampedProgress > 0) 3.dp else 0.dp,
                    color = BrutalBlack,
                    shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp, topEnd = if (clampedProgress >= 0.98f) 12.dp else 0.dp, bottomEnd = if (clampedProgress >= 0.98f) 12.dp else 0.dp)
                )
        )

        // Fun striped grid over top for detail
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepVal = 20.dp.toPx()
            val strokeThick = 2.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = BrutalBlack.copy(alpha = 0.15f),
                    start = Offset(x, 0f),
                    end = Offset(x - size.height, size.height),
                    strokeWidth = strokeThick
                )
                x += stepVal
            }
        }
    }
}

// Custom animated wavy water glass
@Composable
fun AnimatedWaterGlass(
    glassesFilled: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val fraction = if (goal <= 0) 0f else (glassesFilled.toFloat() / goal).coerceAtMost(1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, animationSpec = tween(1000))

    // Wave movement state
    var wavePhase by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30)
            wavePhase += 0.1f
            if (wavePhase > 2 * Math.PI) wavePhase = 0f
        }
    }

    NeoBrutalCard(
        modifier = modifier
            .size(160.dp, 200.dp)
            .rotate(-2f),
        backgroundColor = Color.White,
        cornerRadius = 24.dp,
        shadowOffset = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Glass container
            Canvas(modifier = Modifier.fillMaxSize()) {
                val glassWidth = size.width
                val glassHeight = size.height
                val fillHeight = glassHeight * animatedFraction

                // Draw Water region
                if (fillHeight > 0) {
                    val path = Path().apply {
                        moveTo(0f, glassHeight)
                        lineTo(glassWidth, glassHeight)
                        lineTo(glassWidth, glassHeight - fillHeight)

                        // Sine wave surface
                        val waveHeight = 8.dp.toPx()
                        var currentX = glassWidth
                        while (currentX >= 0f) {
                            val relativeX = currentX / glassWidth
                            val waveY = glassHeight - fillHeight + sin(relativeX * 3 * Math.PI + wavePhase).toFloat() * waveHeight
                            lineTo(currentX, waveY)
                            currentX -= 5f
                        }
                        close()
                    }
                    drawPath(path = path, color = NeoBlue)

                    // Draw reflections
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.3f),
                        topLeft = Offset(12.dp.toPx(), glassHeight - fillHeight + 15.dp.toPx()),
                        size = Size(10.dp.toPx(), fillHeight - 25.dp.toPx()),
                        cornerRadius = CornerRadius(5.dp.toPx())
                    )
                }

                // Grid/Tick marks on side of cup
                val ticksCount = 4
                for (i in 1..ticksCount) {
                    val tickY = glassHeight - (glassHeight * (i.toFloat() / (ticksCount + 1)))
                    drawLine(
                        color = BrutalBlack,
                        start = Offset(10.dp.toPx(), tickY),
                        end = Offset(25.dp.toPx(), tickY),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            // Glass outline overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 4.dp, color = BrutalBlack, shape = RoundedCornerShape(24.dp))
            )

            // Percentage overlay
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(fraction * 100).toInt()}%",
                    color = if (fraction > 0.45f) Color.White else BrutalBlack,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$glassesFilled / $goal",
                    color = if (fraction > 0.45f) Color.White.copy(alpha = 0.8f) else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
