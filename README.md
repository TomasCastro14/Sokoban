# Sokoban
Repositorio para la entrega del TPO de Proceso en Desarrollo de Software

## Progreso guardado (archivo local, sin base de datos)
El progreso se guarda en un archivo de texto en la computadora del jugador, así que si se cierra el
juego y se vuelve a abrir se recupera lo logrado:

- Ubicación: `~/.sokoban/progress.properties` (en Windows, `C:\Users\<usuario>\.sokoban\progress.properties`).
  Está fuera del proyecto y del `.jar`, así que sobrevive a recompilar el juego.
- Se recuerda qué niveles están desbloqueados, la mejor cantidad de estrellas (0 a 3) y el mejor
  puntaje de cada nivel. Es un `.properties` legible, por ejemplo:
  ```
  lastUnlockedLevel=3
  level.1.stars=2
  level.1.score=1500
  ```
- Se guarda automáticamente cada vez que cambia: al ganar un nivel y al reiniciar el progreso.
  Perder o salir a mitad de un nivel no lo modifica.
- La escritura es atómica (archivo temporal + reemplazo), así que un cierre abrupto no lo deja a medias.
  Si el archivo falta o está roto, el juego arranca de cero sin fallar (los valores inválidos se ignoran).
- No se guarda una partida en curso: entrar a un nivel (o reiniciarlo) siempre lo construye de nuevo
  desde su `.txt`.
- En la selección de niveles, el botón "Reiniciar progreso" (con confirmación) vuelve al estado inicial
  y también lo guarda. Para borrarlo a mano alcanza con eliminar el archivo.

Diseño: `Progress` (modelo) no sabe nada de archivos; entrega una foto `ProgressData` y se puede
restaurar desde una. El puerto `port.ProgressStore` oculta dónde se guarda y `service.FileProgressStore`
lo implementa con un archivo. `controller.ProgressAutosave` lo carga al iniciar y lo guarda en cada
cambio (Observer sobre `ProgressListener`).