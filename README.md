# AplicacionPS

# Descarga de Android Studio
* [Link] (http://developer.android.com/intl/es/sdk/index.html)

## Pre-requisitos
* Tener Java y Java JDK instalados
* Para ello acceder a [Link] (https://ninite.com) y seleccionar JDK y JDK x64 (si tenéis procesador de 64bits) en Developer Tools y luego Java 8 en runtimes, así os aseguraréis de que tenéis java al día.
* Descargar el instalador y ejecutarlo.

## Configuración del proyecto en Android Studio
* Hacer clone del repositorio de Github de PS a una carpeta que queráis
* Cuando se haya hecho el clone, ir a file -> project structure
* Darle al botón '+', import module y seleccionar 'AplicacionPS.iml'
* Darle al botón '+', import module y dentro del directorio app seleccionar 'app.iml'
* Saldra en la ventana principal un error de que ha fallado el sync de gradle, simplemente darle a try again y esperar a que se resuelvan todas las dependencias del proyecto
* El fichero local.properties contiene la ruta local mía al sdk de android, modificar esa ruta hacia vuestro sdk
