package com.modocaverna.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Lembrete.agendar(this)
        setContent { CavernaTheme { App() } }
    }
}

/** Cronômetro de foco. Guarda o horário de término para continuar certo mesmo em segundo plano. */
class Cronometro {
    var modoFoco by mutableStateOf(true)
    var total by mutableIntStateOf(25 * 60)
    var restante by mutableIntStateOf(25 * 60)
    var fimEm by mutableStateOf<Long?>(null)
    val rodando: Boolean get() = fimEm != null

    fun definir(foco: Boolean) {
        modoFoco = foco
        total = (if (foco) 25 else 5) * 60
        restante = total
        fimEm = null
    }

    fun alternar() {
        fimEm = if (fimEm == null) System.currentTimeMillis() + restante * 1000L else null
    }

    fun reiniciar() {
        fimEm = null
        restante = total
    }
}

@Composable
fun App() {
    val ctx = LocalContext.current
    var desafio by remember { mutableStateOf(Store.load(ctx)) }
    var aba by rememberSaveable { mutableIntStateOf(0) }
    val crono = remember { Cronometro() }

    fun salvar(d: Desafio?) {
        desafio = d
        Store.save(ctx, d)
    }

    // Pede permissão de notificação (Android 13+)
    val permissao = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) permissao.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // Loop do cronômetro (fica aqui para continuar rodando ao trocar de aba)
    LaunchedEffect(crono.fimEm) {
        val fim = crono.fimEm ?: return@LaunchedEffect
        while (true) {
            val r = ((fim - System.currentTimeMillis()) / 1000).toInt()
            if (r <= 0) {
                Lembrete.vibrar(ctx)
                val eraFoco = crono.modoFoco
                if (eraFoco) {
                    desafio?.let { d ->
                        val i = if (d.terminou) 6 else d.diaAtual
                        val dia = d.dias[i]
                        val novo = dia.copy(pilares = dia.pilares.toMutableList().also { it[4] = true })
                        salvar(d.comDia(i, novo).copy(blocos = d.blocos + 1))
                    }
                    Lembrete.notificar(ctx, "✅ Bloco de foco concluído", "Hora de uma pausa de 5 minutos.", 20)
                } else {
                    Lembrete.notificar(ctx, "⏱️ Pausa encerrada", "Bora para o próximo bloco de foco.", 20)
                }
                crono.definir(!eraFoco)
                break
            }
            crono.restante = r
            delay(250)
        }
    }

    val d = desafio
    if (d == null) {
        TelaInicio { salvar(it) }
        return
    }

    Scaffold(
        containerColor = Fundo,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF14110E)) {
                listOf("🔥" to "Hoje", "⏱️" to "Foco", "🪨" to "Jornada").forEachIndexed { i, (ic, nome) ->
                    NavigationBarItem(
                        selected = aba == i,
                        onClick = { aba = i },
                        icon = { Text(ic, fontSize = 22.sp) },
                        label = { Text(nome) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = Fogo2,
                            unselectedTextColor = Mudo,
                            indicatorColor = Color(0xFF3A2512)
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            when (aba) {
                0 -> TelaHoje(d) { salvar(it) }
                1 -> TelaFoco(d, crono)
                else -> TelaJornada(d) { salvar(null); aba = 0 }
            }
        }
    }
}
