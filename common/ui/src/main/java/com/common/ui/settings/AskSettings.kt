package com.common.ui.settings

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Justify
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.data.model.settings.ModelSettings
import com.google.ai.client.generativeai.type.BlockThreshold

@Preview(showSystemUi = true)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    onBackClick: () -> Unit = {},
    updateTemp: (Float) -> Unit = {},
    updateTopK: (Int) -> Unit = {},
    updateTopP: (Float) -> Unit = {},
    changeHarassment: (Float) -> Unit = {},
    changeHateSpeech: (Float) -> Unit = {},
    changeSexualContent: (Float) -> Unit = {},
    changeDangerousContent: (Float) -> Unit = {},
    modelSettings: ModelSettings = ModelSettings(),
    onDone: () -> Unit = {}

) {

    BackHandler {
        onBackClick()
    }

    Column(
      modifier = modifier
          .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .weight(0.8f)
        ) {
            item {
                Text(
                    text = "Model Parameters",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Serif,
                    fontStyle = Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = modifier
                        .padding(vertical = 12.dp)
                )
                IndicatorElement(
                    indicatorName = "• Temperature",
                    indicatorDescription = "The degree of randomness in token selection.",
                    indicatorValue = modelSettings.temperature,
                    indicatorRange = 0f..1f,
                    indicatorValueChange = {
                        updateTemp(it)
                    }
                )
                IndicatorElement(
                    indicatorName = "• Top K",
                    indicatorDescription = "The sum of probabilities to collect to during token selection",
                    indicatorValue = modelSettings.topK.toFloat(),
                    indicatorRange = 1f..100f,
                    indicatorValueChange = {
                        updateTopK(it.toInt())
                    },
                )
                IndicatorElement(
                    indicatorName = "• Top P",
                    indicatorDescription = "How many tokens to select amongst the highest probabilities.",
                    indicatorValue = modelSettings.topP,
                    indicatorRange = 0f..1f,
                    indicatorValueChange = {
                        updateTopP(it)
                    },
                    indicatorValueChangeFinished = {
                    }
                )

            }
            item {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = modifier
                        .padding(vertical = 12.dp)
                )
                Text(
                    text = "Safety Settings",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Serif,
                    fontStyle = Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                Text(
                    text = "Adjust how likely you are to see responses that could be harmful. Content is blocked based on the probability that is harmful.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Serif,
                    fontStyle = Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = Justify
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = modifier
                        .padding(vertical = 12.dp)
                )
                SafetyIndicatorElement(
                    indicatorName = "• Harassment",
                    indicatorValue = returnSliderValue(modelSettings.harassment),
                    indicatorSteps = 2,
                    indicatorRange = 0f..3f,
                    indicatorValueChange = {
                        changeHarassment(it)
                    },
                    indicatorValueChangeFinished = {

                    }
                )
                SafetyIndicatorElement(
                    indicatorName = "• Hate Speech",
                    indicatorValue = returnSliderValue(modelSettings.hateSpeech),
                    indicatorSteps = 2,
                    indicatorRange = 0f..3f,
                    indicatorValueChange = {
                        changeHateSpeech(it)
                    },
                    indicatorValueChangeFinished = {

                    }
                )
                SafetyIndicatorElement(
                    indicatorName = "• Sexually Explicit",
                    indicatorValue = returnSliderValue(modelSettings.sexualContent),
                    indicatorSteps = 2,
                    indicatorRange = 0f..3f,
                    indicatorValueChange = {
                        changeSexualContent(it)
                    },
                    indicatorValueChangeFinished = {

                    }
                )
                SafetyIndicatorElement(
                    indicatorName = "• Dangerous",
                    indicatorValue = returnSliderValue(modelSettings.dangerous),
                    indicatorSteps = 2,
                    indicatorRange = 0f..3f,
                    indicatorValueChange = {
                        changeDangerousContent(it)
                    },
                    indicatorValueChangeFinished = {

                    }
                )

            }

        }
        HorizontalDivider(
            thickness = 2.dp,
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = {
                onBackClick()
            }) {
                Text(
                    text = "Cancel",
                    fontFamily = Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontStyle = Italic
                )

            }
            FilledTonalButton(onClick = {
                onDone()
                onBackClick()
            }) {
                Text(
                    text = "Done",
                    fontFamily = Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontStyle = Italic
                )

            }

        }
    }

}


@Preview(showSystemUi = true)
@Composable
fun IndicatorElement(
    modifier: Modifier = Modifier,
    indicatorValue: Float = 0f,
    indicatorName: String = "",
    indicatorDescription: String = "",
    indicatorSteps: Int = 0,
    indicatorValueChange: (Float) -> Unit = {},
    indicatorValueChangeFinished: () -> Unit = {},
    indicatorRange: ClosedFloatingPointRange<Float> = 0f..1f
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        )
        {
            Text(
                text = "$indicatorName: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Serif,
                fontStyle = Italic,
                color = MaterialTheme.colorScheme.secondary,
            )
            Crossfade(
                targetState = indicatorValue,
                label = indicatorName,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            ) {
                Text(
                    text = it.toString().take(4),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Serif,
                    fontStyle = Italic,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Text(
            text = indicatorDescription,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Serif,
            fontStyle = Italic,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = Justify
        )
        Slider(
            value = indicatorValue,
            onValueChange = {
                indicatorValueChange(it)
            },
            valueRange = indicatorRange,
            steps = indicatorSteps,
        )
    }


}
@Preview(showSystemUi = true)
@Composable
fun SafetyIndicatorElement(
    modifier: Modifier = Modifier,
    indicatorValue: Float = 0f,
    indicatorName: String = "",
    indicatorSteps: Int = 0,
    indicatorValueChange: (Float) -> Unit = {},
    indicatorValueChangeFinished: () -> Unit = {},
    indicatorRange: ClosedFloatingPointRange<Float> = 0f..1f
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "$indicatorName: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Serif,
                fontStyle = Italic,
                color = MaterialTheme.colorScheme.secondary,
            )
            Crossfade(
                targetState = returnBlockThreshold(indicatorValue),
                label = indicatorName,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearEasing
                )
            ) {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Serif,
                    fontStyle = Italic,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Slider(
            value = indicatorValue,
            onValueChange = {
                indicatorValueChange(it)
            },
            valueRange = indicatorRange,
            steps = indicatorSteps
        )
    }

}

fun returnSliderValue(value: String): Float = when(value) {
    BlockThreshold.NONE.name -> 0f
    BlockThreshold.LOW_AND_ABOVE.name -> 1f
    BlockThreshold.MEDIUM_AND_ABOVE.name -> 2f
    BlockThreshold.ONLY_HIGH.name -> 3f
    else -> 0f
}
fun returnBlockThreshold(value: Float): String {
    return when(value) {
        0f -> BlockThreshold.NONE.name
        1f -> BlockThreshold.LOW_AND_ABOVE.name
        2f -> BlockThreshold.MEDIUM_AND_ABOVE.name
        3f -> BlockThreshold.ONLY_HIGH.name
        else -> BlockThreshold.UNSPECIFIED.name
    }
}










