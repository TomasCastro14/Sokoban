# Sokoban
Repositorio para la entrega del TPO de Proceso en Desarrollo de Software

## Progreso (solo en memoria)
El progreso vive únicamente en memoria mientras la aplicación está abierta: no se escribe nada en
disco. Cada vez que se abre el juego se arranca con el nivel 1 desbloqueado y 0 estrellas en todos.

- Se recuerda qué niveles están desbloqueados, la mejor cantidad de estrellas (0 a 3) y el mejor
  puntaje de cada nivel durante la sesión.
- Solo se actualiza al ganar un nivel; perder o salir a mitad de un nivel no lo modifica.
- No se guarda una partida en curso: entrar a un nivel (o reiniciarlo) siempre lo construye de nuevo
  desde su `.txt`.
- En la selección de niveles, el botón "Reiniciar progreso" (con confirmación) vuelve al estado inicial.