package com.modocaverna.app

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.util.Calendar

object Lembrete {
    private const val CANAL = "lembretes"

    private fun criarCanal(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CANAL, "Lembretes do desafio", NotificationManager.IMPORTANCE_DEFAULT)
        )
    }

    /** Agenda um lembrete diário às 20h. */
    fun agendar(ctx: Context) {
        criarCanal(ctx)
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        val pi = PendingIntent.getBroadcast(
            ctx, 1, Intent(ctx, LembreteReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    fun notificar(ctx: Context, titulo: String, texto: String, id: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        criarCanal(ctx)
        val abrir = PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        val n = Notification.Builder(ctx, CANAL)
            .setSmallIcon(R.drawable.ic_notificacao)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(Notification.BigTextStyle().bigText(texto))
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .build()
        ctx.getSystemService(NotificationManager::class.java).notify(id, n)
    }

    fun vibrar(ctx: Context) {
        val v: Vibrator? = if (Build.VERSION.SDK_INT >= 31) {
            ctx.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Vibrator::class.java)
        }
        v?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400, 200, 400), -1))
    }
}

class LembreteReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Lembrete.agendar(ctx)
            return
        }
        val d = Store.load(ctx) ?: return
        if (d.terminou) return
        val i = d.diaAtual
        val feitos = d.dias[i].pilares.count { it }
        val texto = if (feitos < 9)
            "Você fez $feitos de 9 pilares hoje. Ainda dá tempo. E não esqueça do diário."
        else
            "Todos os pilares feitos! Escreva no diário e descanse."
        Lembrete.notificar(ctx, "🔥 Dia ${i + 1} do Modo Caverna", texto, 10)
    }
}
