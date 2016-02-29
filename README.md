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

### Tutorial
* 1) En la pantalla de bienvenida darle a "Check out project from Version Control" y seleccionar github
* 2) Clonar el repositorio de "AplicacionPS" donde querais
* 3) Preguntara que has bajado un projecto de Studio, que si quieres abrirlo. Dadle a yes. (Puede no salir este paso)
* 4) Preguntará para importar el proyecto de Gradle, dadle a OK, saldrá un warning diciendo que va a cambiar una linea del fichero properties, decidle que OK.
* 5) Pulsando Alt+1 aparece la vista del proyecto. Ir a File -> Project Structure
* 6) Click en el modulo app y darle al '-' de arriba. Ok, ok y cerrarlo.
* 7) Volver a abrir el project structure y en la sección modulos eliminar todo lo que este (haciendo click y darle al '-'). Ok y cerrar.
* 8) Volved a abrir el project structure y darle al botón '+', import module y seleccionar 'AplicacionPS.iml'. Debería aparecer un desplegable que contenga 'java-Gradle'.
* 9) Darle al botón '+', import module y dentro del directorio app seleccionar 'app.iml'. Deberían aparecer dos sub-modulos en app llamados 'Android' y 'Android-Gradle'. Ok y cerrar la ventana de project structure.
* 10) 
