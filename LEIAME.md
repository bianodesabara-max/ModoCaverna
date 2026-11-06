# Modo Caverna 7 Dias (Android nativo)

App em Kotlin + Jetpack Compose. Android 8.0 ou superior.

## Funções
- **Início:** escolha o hábito a cortar e seu objetivo
- **Hoje:** missão do dia, 9 pilares diários, diário (dia vencido com 70% dos pilares)
- **Foco:** Pomodoro 25/5 com vibração e notificação; marca o pilar de foco sozinho
- **Jornada:** estatísticas, diário completo e recomeçar
- **Lembrete diário às 20h** com quantos pilares faltam

## Como gerar o APK

### Opção 1: Android Studio (computador)
1. Descompacte o zip e abra a pasta `ModoCaverna` no Android Studio
2. Espere o Gradle sincronizar
3. Menu **Build > Build App Bundle(s) / APK(s) > Build APK(s)**
4. O APK fica em `app/build/outputs/apk/debug/app-debug.apk`

### Opção 2: GitHub (sem instalar nada)
1. Crie um repositório no GitHub e envie estes arquivos
2. A aba **Actions** roda o workflow "Gerar APK" sozinha
3. Ao terminar, baixe o artefato **ModoCaverna-apk** e instale no celular

Para instalar, permita "instalar apps de fontes desconhecidas" quando o Android pedir.
