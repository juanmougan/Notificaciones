Usando branch initial_configuration_activity
============================================

Notificaciones
==============

Notificaciones PUSH usando GCM para multicastear mensajes desde Secretaría a aquellos alumnos que deseen recibirlos. Los interesados proporcionarán:

-   Nombre y apellido
-   Nro. registro?
-   Listas a las que deseen subscribirse

Adicionalmente, habrá un servidor que maneje la mensajería a est aplicación cliente.

Configuración
-------------

1. Clonar el proyecto
2. Setear el Project number - GCM sender ID.
3. ~~Setear la API key~~ esto es sólo para el server?

Plantilla para reportar bugs
----------------------------

Por favor, usar la siguiente plantilla para reportar errores. Aquellos errores que no estén reportados según la plantilla, **no serán** tenidos en cuenta.

Título: Aquí iría un resumen del error

Dispositivo: Información sobre el celular/tablet en el que se probó, especialmente la versión *completa* de Android (ej: 4.2.1)

Pasos para reproducir: Secuencia de pasos a seguir para llegar al error

Obtenido: El comportamiento erróneo que mostró la aplicación

Esperado: El comportamiento correcto, que no se produjo

Ejemplo:

###Título:
Error al abrir una notificación, cuando se enviaron varias

###Dispositivo: 

- Acer Iconia 7
- Android 4.2.1 
- API 17

###Pasos para reproducir:

1. Enviar "notificación 1"
2. Enviar "notificación 2"
3. Clickear en "notificación 2", la muestra y la saca de la barra de notificaciones (esto es correcto)
4. Presionar back
5. Clickear en "notificación 1"

###Obtenido:

- Se muestra la notificación 2 nuevamente

###Esperado:

- Se debería mostrar la notificación 1
