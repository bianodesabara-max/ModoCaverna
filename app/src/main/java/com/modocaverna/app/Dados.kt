package com.modocaverna.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Dia(
    val pilares: List<Boolean> = List(9) { false },
    val missao: Boolean = false,
    val diario: String = ""
)

data class Desafio(
    val nome: String,
    val habito: String,
    val objetivo: String,
    val inicio: LocalDate,
    val dias: List<Dia>,
    val blocos: Int = 0
) {
    val diasPassados: Int get() = ChronoUnit.DAYS.between(inicio, LocalDate.now()).toInt()
    val terminou: Boolean get() = diasPassados > 6
    val diaAtual: Int get() = diasPassados.coerceIn(0, 6)
    fun pct(i: Int): Float = dias[i].pilares.count { it } / dias[i].pilares.size.toFloat()
    fun vencido(i: Int): Boolean = pct(i) >= 0.7f

    fun comDia(i: Int, dia: Dia): Desafio =
        copy(dias = dias.toMutableList().also { it[i] = dia })
}

object Store {
    private const val PREFS = "modo_caverna"
    private const val KEY = "desafio"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(ctx: Context): Desafio? {
        val s = prefs(ctx).getString(KEY, null) ?: return null
        return try {
            fromJson(JSONObject(s))
        } catch (e: Exception) {
            null
        }
    }

    fun save(ctx: Context, d: Desafio?) {
        val ed = prefs(ctx).edit()
        if (d == null) ed.remove(KEY) else ed.putString(KEY, toJson(d).toString())
        ed.apply()
    }

    private fun toJson(d: Desafio): JSONObject = JSONObject().apply {
        put("nome", d.nome)
        put("habito", d.habito)
        put("objetivo", d.objetivo)
        put("inicio", d.inicio.toString())
        put("blocos", d.blocos)
        put("dias", JSONArray().apply {
            d.dias.forEach { dia ->
                put(JSONObject().apply {
                    put("pilares", JSONArray().apply { dia.pilares.forEach { put(it) } })
                    put("missao", dia.missao)
                    put("diario", dia.diario)
                })
            }
        })
    }

    private fun fromJson(o: JSONObject): Desafio {
        val arr = o.getJSONArray("dias")
        val dias = (0 until arr.length()).map { i ->
            val d = arr.getJSONObject(i)
            val p = d.getJSONArray("pilares")
            Dia(
                pilares = (0 until p.length()).map { p.getBoolean(it) },
                missao = d.optBoolean("missao"),
                diario = d.optString("diario")
            )
        }
        return Desafio(
            nome = o.optString("nome"),
            habito = o.optString("habito"),
            objetivo = o.optString("objetivo"),
            inicio = LocalDate.parse(o.getString("inicio")),
            dias = dias,
            blocos = o.optInt("blocos")
        )
    }
}

val MISSOES = listOf(
    "Entrada na Caverna" to "Prepare o terreno: arrume seu espaço, desative notificações, apague ou esconda apps que te distraem e escreva por que você está fazendo isso.",
    "Silêncio" to "Passe 20 minutos sem nenhum estímulo: sem celular, música ou vídeo. Só você e seus pensamentos. Faça uma caminhada sem fone.",
    "Corpo" to "Treine mais pesado que nos outros dias: 45 minutos de exercício. O corpo forte sustenta a mente forte.",
    "Mente" to "Leia pelo menos 30 páginas de um livro que te faça crescer e anote 3 ideias que você vai aplicar.",
    "O Vale" to "Normalmente é o dia mais difícil. Quando a vontade vier, espere 10 minutos antes de agir e anote o que disparou o desejo.",
    "Construção" to "Faça 2 horas de trabalho profundo no seu objetivo principal, em blocos de foco, sem interrupções.",
    "Saída da Caverna" to "Releia seu diário. O que mudou? Escolha quais hábitos você vai manter e planeje os próximos 30 dias."
)

val FRASES = listOf(
    "A disciplina é escolher entre o que você quer agora e o que você mais quer.",
    "Ninguém vê o trabalho feito na caverna, mas todos veem o resultado.",
    "Um dia de cada vez. Hoje é o único que importa.",
    "O desconforto de hoje é a força de amanhã.",
    "Você não precisa de motivação, precisa de compromisso.",
    "Pequenas vitórias diárias constroem uma nova identidade.",
    "Quem sai da caverna não é a mesma pessoa que entrou."
)

val REGRAS = listOf(
    "Escolha um hábito para cortar por 7 dias.",
    "Cumpra os pilares diários: corpo, mente, foco e descanso.",
    "Faça a missão especial de cada dia.",
    "Escreva no diário toda noite.",
    "Um dia vale como vencido com 70% dos pilares feitos."
)

/** Índice 4 é o pilar de foco, marcado automaticamente pelo cronômetro. */
fun pilares(habito: String) = listOf(
    "Acordei no horário que defini",
    "Fiquei sem: ${habito.ifBlank { "meu hábito" }}",
    "30 min de exercício",
    "30 min de estudo ou leitura",
    "1 bloco de foco (25 min)",
    "Sem redes sociais por lazer",
    "Comi bem, sem besteira",
    "Bebi 2 litros de água",
    "Dormi antes das 23h"
)
