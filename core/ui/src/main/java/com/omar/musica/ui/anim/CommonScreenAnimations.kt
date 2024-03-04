package com.omar.musica.ui.anim

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically


private val FastOutVerySlowInEasing = CubicBezierEasing(0.17f, 0.67f, 0.13f, 1.0f)

const val SCALE_ENTER_DURATION = 350
const val FADE_ENTER_DURATION = 80

const val SCALE_EXIT_DURATION = 450
const val FADE_EXIT_DURATION = 80

const val SLIDE_IN_DURATION = 300
const val SLIDE_OUT_DURATION = 250

val OPEN_SCREEN_ENTER_ANIMATION =
    scaleIn(animationSpec = tween(SCALE_ENTER_DURATION, easing = FastOutVerySlowInEasing), initialScale = 0.85f) +
    fadeIn(
        animationSpec = tween(FADE_ENTER_DURATION, 30, LinearEasing),
        initialAlpha = 0.0f
    )

val OPEN_SCREEN_EXIT_ANIMATION =
    //scaleOut(tween(SCALE_EXIT_DURATION, easing = FastOutVerySlowInEasing), targetScale = 1.1f) +
    fadeOut(tween(FADE_EXIT_DURATION, delayMillis = 30, LinearEasing), targetAlpha = 0.0f)

val POP_SCREEN_ENTER_ANIMATION =
    scaleIn(tween(SCALE_ENTER_DURATION, easing = FastOutVerySlowInEasing), initialScale = 1.15f) +
    fadeIn(tween(FADE_ENTER_DURATION, delayMillis = 60, easing = LinearEasing))

val POP_SCREEN_EXIT_ANIMATION =
    //scaleOut(tween(SCALE_EXIT_DURATION, easing = FastOutVerySlowInEasing), targetScale = 0.9f) +
    fadeOut(tween(FADE_EXIT_DURATION, 60, easing = LinearEasing))

val SLIDE_UP_ENTER_ANIMATION =
    slideInVertically(tween(SLIDE_IN_DURATION, easing = FastOutVerySlowInEasing), initialOffsetY = { it/2 }) +
    fadeIn(tween(FADE_ENTER_DURATION, easing = LinearEasing))


val SLIDE_DOWN_EXIT_ANIMATION =
    slideOutVertically(tween(SLIDE_OUT_DURATION, easing = FastOutSlowInEasing), targetOffsetY = { -it / 2 }) +
    fadeOut(tween(FADE_EXIT_DURATION, easing = LinearEasing))