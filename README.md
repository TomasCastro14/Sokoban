# Sokoban
Repositorio para la entrega del TPO de Proceso en Desarrollo de Software

## Progreso guardado
El juego guarda el progreso (último nivel desbloqueado y el mejor resultado de cada nivel ganado) en
`~/.new-sokoban/progress.json`, dentro de la carpeta personal del usuario (`user.home`). No se guarda
en `resources` ni dentro del `.jar`, que son de solo lectura.

- Si el archivo no existe, el juego arranca desde el nivel 1.
- Si el archivo está dañado o fue editado con datos inválidos, el juego arranca desde el nivel 1 y
  conserva el archivo dañado como `progress.json.corrupt-<fecha>`.
- Para empezar de cero, se puede borrar el archivo.