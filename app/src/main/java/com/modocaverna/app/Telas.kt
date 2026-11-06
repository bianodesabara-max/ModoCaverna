package com.modocaverna.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* ---------- Componentes ---------- */

@Composable
fun Tela(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .background(Fundo)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 20.dp),
        content = content
    )
}

@Composable
fun Cartao(borda: Color = Linha, content: @Composable ColumnScope.() -> Unit) {
    val forma = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .clip(forma)
            .background(CardCor)
            .border(1.dp, borda, forma)
            .padding(18.dp),
        content = content
    )
}

@Composable
fun Titulo(t: String, tamanho: Int = 22) =
    Text(t.uppercase(), fontSize = tamanho.sp, fontWeight = FontWeight.Black, color = Texto, letterSpacing = 0.5.sp)

@Composable
fun Etiqueta(t: String) = Text(
    t,
    Modifier
        .clip(RoundedCornerShape(50))
        .background(Brasa)
        .padding(horizontal = 10.dp, vertical = 4.dp),
    color = Fogo2, fontSize = 12.sp, fontWeight = FontWeight.Bold
)

@Composable
fun Frase(t: String) = Text(
    "\"$t\"",
    Modifier
        .fillMaxWidth()
        .padding(top = 20.dp, bottom = 12.dp),
    color = Color(0xFFD8CDBF), fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, fontSize = 15.sp
)

@Composable
fun Botao(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) = Button(
    onClick = onClick,
    modifier = modifier.height(54.dp),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Fogo, contentColor = Escuro)
) { Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

@Composable
fun BotaoContorno(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) = OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(54.dp),
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, Linha),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = Texto)
) { Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 15.sp) }

@Composable
fun ItemCheck(texto: String, marcado: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = marcado,
            onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(checkedColor = Fogo, uncheckedColor = Mudo, checkmarkColor = Escuro)
        )
        Text(
            texto,
            color = if (marcado) Mudo else Texto,
            textDecoration = if (marcado) TextDecoration.LineThrough else null,
            fontSize = 15.sp
        )
    }
}

@Composable
fun Barra(p: Float) = LinearProgressIndicator(
    progress = { p.coerceIn(0f, 1f) },
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
        .height(8.dp)
        .clip(RoundedCornerShape(8.dp)),
    color = Fogo,
    trackColor = Fundo2,
    drawStopIndicator = {}
)

@Composable
fun Estatistica(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 30.sp, fontWeight = FontWeight.Black, color = Fogo2)
        Text(rotulo, fontSize = 13.sp, color = Mudo, textAlign = TextAlign.Center)
    }
}

@Composable
fun campoCores() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Fogo,
    unfocusedBorderColor = Linha,
    focusedLabelColor = Fogo,
    cursorColor = Fogo,
    focusedContainerColor = Fundo2,
    unfocusedContainerColor = Fundo2
)

/* ---------- Tela inicial ---------- */

@Composable
fun TelaInicio(onComecar: (Desafio) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var habito by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    Tela(Modifier.systemBarsPadding()) {
        Text("🔥", fontSize = 56.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
        Text(
            "MODO CAVERNA", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Texto,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text("Desafio de 7 dias", color = Mudo, modifier = Modifier.align(Alignment.CenterHorizontally))

        Cartao {
            Titulo("As regras")
            Spacer(Modifier.height(6.dp))
            REGRAS.forEach { Text("•  $it", color = Color(0xFFD8CDBF), fontSize = 14.sp, modifier = Modifier.padding(vertical = 3.dp)) }
        }

        Cartao {
            OutlinedTextField(
                value = nome, onValueChange = { nome = it },
                label = { Text("Seu nome") }, singleLine = true,
                colors = campoCores(), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = habito, onValueChange = { habito = it; erro = false },
                label = { Text("Hábito que você vai cortar") },
                placeholder = { Text("Ex.: Instagram, açúcar, games") },
                isError = erro, singleLine = true,
                supportingText = { if (erro) Text("Escolha um hábito para cortar") },
                colors = campoCores(), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = objetivo, onValueChange = { objetivo = it },
                label = { Text("Seu objetivo nesses 7 dias") },
                placeholder = { Text("Ex.: estudar para a prova") },
                singleLine = true, colors = campoCores(), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(18.dp))
            Botao("Entrar na caverna", Modifier.fillMaxWidth()) {
                if (habito.isBlank()) erro = true
                else onComecar(
                    Desafio(nome.trim(), habito.trim(), objetivo.trim(), LocalDate.now(), List(7) { Dia() })
                )
            }
        }
        Frase(FRASES[1])
    }
}

/* ---------- Hoje ---------- */

@Composable
fun LinhaDias(d: Desafio, selecionado: Int, onSelecionar: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 0..6) {
            val bloqueado = i > d.diaAtual && !d.terminou
            val atual = i == d.diaAtual && !d.terminou
            val feito = d.vencido(i)
            val forma = RoundedCornerShape(12.dp)
            val borda = when {
                i == selecionado -> Fogo2
                feito -> Color(0xFF4D6B3C)
                atual -> Fogo
                else -> Linha
            }
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(forma)
                    .background(if (feito) Color(0xFF2B3A22) else Fundo2)
                    .border(if (i == selecionado) 2.dp else 1.dp, borda, forma)
                    .clickable(enabled = !bloqueado) { onSelecionar(i) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DIA", fontSize = 9.sp, color = Mudo.copy(alpha = if (bloqueado) 0.4f else 1f))
                    Text(
                        if (feito) "✓" else "${i + 1}",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = when {
                            feito -> Verde
                            atual -> Fogo2
                            bloqueado -> Mudo.copy(alpha = 0.4f)
                            else -> Mudo
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TelaHoje(d: Desafio, salvar: (Desafio) -> Unit) {
    var sel by remember { mutableStateOf<Int?>(null) }
    val i = sel ?: d.diaAtual
    val dia = d.dias[i]
    val lista = pilares(d.habito)
    val feitos = dia.pilares.count { it }

    Tela {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (d.nome.isBlank()) "Força, guerreiro" else "Força, ${d.nome}", color = Mudo, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Titulo("Dia ${i + 1}", 34)
                    Text(" /7", color = Mudo, fontSize = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Text("🔥", fontSize = 38.sp)
        }

        if (d.terminou) {
            Cartao(borda = Verde) {
                Text("🏆 Você completou os 7 dias!", fontWeight = FontWeight.Bold, color = Texto)
                Text("Veja seu resumo na aba Jornada.", color = Mudo, fontSize = 13.sp)
            }
        }

        LinhaDias(d, i) { sel = it }

        Cartao {
            Etiqueta("MISSÃO DO DIA")
            Spacer(Modifier.height(10.dp))
            Titulo(MISSOES[i].first)
            Row(Modifier.padding(top = 10.dp).height(IntrinsicSize.Min)) {
                Box(Modifier.width(3.dp).fillMaxHeight().background(Fogo))
                Text(MISSOES[i].second, color = Texto, fontSize = 15.sp, modifier = Modifier.padding(start = 12.dp))
            }
            Spacer(Modifier.height(6.dp))
            ItemCheck("Missão cumprida", dia.missao) { salvar(d.comDia(i, dia.copy(missao = !dia.missao))) }
        }

        Cartao {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { Titulo("Pilares") }
                Text("$feitos/${lista.size}", color = Mudo, fontSize = 13.sp)
            }
            Barra(feitos / lista.size.toFloat())
            Spacer(Modifier.height(6.dp))
            lista.forEachIndexed { k, p ->
                ItemCheck(p, dia.pilares[k]) {
                    salvar(d.comDia(i, dia.copy(pilares = dia.pilares.toMutableList().also { it[k] = !it[k] })))
                }
            }
        }

        Cartao {
            Titulo("Diário")
            Text("Como foi o dia? O que foi difícil? O que você aprendeu?", color = Mudo, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = dia.diario,
                onValueChange = { salvar(d.comDia(i, dia.copy(diario = it))) },
                placeholder = { Text("Escreva aqui...") },
                minLines = 4,
                colors = campoCores(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Frase(FRASES[i])
    }
}

/* ---------- Foco ---------- */

fun formatar(s: Int) = "%02d:%02d".format(s / 60, s % 60)

@Composable
fun TelaFoco(d: Desafio, crono: Cronometro) {
    Tela {
        Titulo("Foco", 34)
        Text(
            "Blocos de 25 min sem celular. Ao terminar um, o pilar de foco do dia é marcado.",
            color = Mudo, fontSize = 14.sp
        )
        Cartao {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Etiqueta(if (crono.modoFoco) "BLOCO DE FOCO" else "PAUSA")
                Text(
                    formatar(crono.restante), fontSize = 64.sp, fontWeight = FontWeight.Black,
                    color = Texto, modifier = Modifier.padding(vertical = 8.dp)
                )
                Barra(1f - crono.restante / crono.total.toFloat())
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotaoContorno("Reiniciar", Modifier.weight(1f)) { crono.reiniciar() }
                    Botao(if (crono.rodando) "Pausar" else "Começar", Modifier.weight(1f)) { crono.alternar() }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotaoContorno("Foco 25", Modifier.weight(1f)) { crono.definir(true) }
                    BotaoContorno("Pausa 5", Modifier.weight(1f)) { crono.definir(false) }
                }
            }
        }
        Cartao {
            Row {
                Estatistica("${d.blocos}", "blocos feitos", Modifier.weight(1f))
                Estatistica("%.1fh".format(d.blocos * 25 / 60f), "de foco total", Modifier.weight(1f))
            }
        }
        Frase("Deixe o celular em outro cômodo enquanto o tempo corre.")
    }
}

/* ---------- Jornada ---------- */

@Composable
fun TelaJornada(d: Desafio, onRecomecar: () -> Unit) {
    var confirmar by remember { mutableStateOf(false) }
    val vencidos = (0..6).count { d.vencido(it) }
    val missoes = d.dias.count { it.missao }
    val media = ((0..6).sumOf { d.pct(it).toDouble() } / 7 * 100).toInt()

    Tela {
        Titulo("Jornada", 34)
        Cartao {
            Text("Cortando", color = Mudo, fontSize = 13.sp)
            Titulo(d.habito)
            if (d.objetivo.isNotBlank()) {
                Text("Objetivo", color = Mudo, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                Text(d.objetivo, color = Texto)
            }
            Text(
                "Início: " + d.inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                color = Mudo, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp)
            )
        }
        Cartao {
            Row {
                Estatistica("$vencidos/7", "dias vencidos", Modifier.weight(1f))
                Estatistica("$missoes/7", "missões", Modifier.weight(1f))
                Estatistica("$media%", "pilares", Modifier.weight(1f))
            }
            Barra(vencidos / 7f)
        }
        Cartao {
            Titulo("Seu diário")
            d.dias.forEachIndexed { i, dia ->
                Column(Modifier.padding(vertical = 10.dp)) {
                    Text("Dia ${i + 1} · ${MISSOES[i].first}", color = Fogo2, fontWeight = FontWeight.Bold)
                    Text(
                        dia.diario.ifBlank { "Sem anotações." },
                        color = if (dia.diario.isBlank()) Mudo else Texto,
                        fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (i < 6) HorizontalDivider(color = Linha)
            }
        }
        Cartao {
            BotaoContorno("Recomeçar desafio", Modifier.fillMaxWidth()) { confirmar = true }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            containerColor = CardCor,
            title = { Text("Recomeçar?", color = Texto) },
            text = { Text("Todo o progresso e o diário serão apagados.", color = Mudo) },
            confirmButton = {
                TextButton(onClick = { confirmar = false; onRecomecar() }) { Text("Confirmar", color = Fogo) }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text("Cancelar", color = Texto) }
            }
        )
    }
}
