package com.modocaverna.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Fundo = Color(0xFF0D0B09)
val Fundo2 = Color(0xFF17130F)
val CardCor = Color(0xFF1E1914)
val Linha = Color(0xFF2E2620)
val Texto = Color(0xFFEFE7DC)
val Mudo = Color(0xFF9A8D7D)
val Fogo = Color(0xFFF29A2E)
val Fogo2 = Color(0xFFFFC163)
val Verde = Color(0xFF7FBF6A)
val Escuro = Color(0xFF1A1007)
val Brasa = Color(0xFF3A2512)

@Composable
fun CavernaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Fogo,
            onPrimary = Escuro,
            background = Fundo,
            onBackground = Texto,
            surface = CardCor,
            onSurface = Texto,
            onSurfaceVariant = Mudo,
            outline = Linha,
            error = Color(0xFFD9634C)
        ),
        content = content
    )
}
