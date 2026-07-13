# SkillConnect Android

Proyecto Android nativo en Kotlin basado en el prototipo HTML/CSS/JS de SkillConnect.

## Arquitectura

- `data/model`: modelos de dominio.
- `data/repository`: datos mock tomados del prototipo.
- `viewmodel`: estado y reglas de presentacion de la app.
- `ui`: pantallas nativas y navegacion interna.

## Pantallas incluidas

- Splash, bienvenida, login y registro.
- Inicio con categorias, SkillMatch, mentores recomendados e intercambios.
- Busqueda con filtros.
- Perfil de mentor, reserva de clase y solicitud de intercambio.
- Mensajes y chat.
- Calendario.
- Perfil, habilidades, aprendizajes, configuracion, estadisticas, logros y notificaciones.

## Abrir en Android Studio

1. Abre Android Studio.
2. Selecciona `Open`.
3. Elige la carpeta `D:\7° INTERACCION HUMANO COMPUTADOR\SkilConect Android Studio`.
4. Si Android Studio solicita SDK, configura o instala el Android SDK desde `Settings > Languages & Frameworks > Android SDK`.
5. Ejecuta la app con el modulo `app`.

El proyecto no usa Java para la aplicacion: la interfaz y logica estan implementadas en Kotlin.
