package com.tanfra.shopmob.smob.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KeyValueText(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = "${key}:",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 12.dp),
        )
        Text(
            modifier = modifier.weight(3.5f),
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
fun PreviewKeyValueText() {
    Column {
        KeyValueText(key = "Description", value = "the world is beautiful")
        KeyValueText(key = "Repetitions", value = "another one bites the dust")
    }
}