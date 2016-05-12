# AplicacionPS

## Pre-requisitos
* [Android Studio] (http://developer.android.com/intl/es/sdk/index.html)
* [Git] (https://git-scm.com/downloads)
* Tener Java y Java JDK instalados: Para ello acceder a [Link] (https://ninite.com) y seleccionar JDK y JDK x64 (si tenéis procesador de 64bits) en Developer Tools y luego Java 8 en runtimes, así os aseguraréis de que tenéis java al día.

### Tutorial
* 1) En la pantalla de bienvenida darle a "Check out project from Version Control" y seleccionar github
* 2) Clonar el repositorio de "AplicacionPS" donde querais
* 3) Preguntara que has bajado un projecto de Studio, que si quieres abrirlo. Dadle a yes. (Puede no salir este paso)
* 4) Preguntará para importar el proyecto de Gradle, dadle a OK, saldrá un warning diciendo que va a cambiar una linea del fichero properties, decidle que OK.+
* 5) Cuando aparezca un mensaje de que se han detectado fuentes de VCS dadle a 'Add root'
* 6) Pulsando Alt+1 aparece la vista del proyecto. Si se selecciona la vista de "Project" en el desplegable, se mostrará todo. Por defecto está en "Android".

### FAQ / Lecciones aprendidas
* Para borrar todo el trabajo de local y volver a bajarlo de github y acceder a la carpeta, teniendo Android Studio apagado, donde se haya clonado el proyecto y borrar el directorio a mano. Abrir Android Studio y repetir los pasos del tutorial.
* Para hacer un rollback hacer click en la parte inferior del IDE en 9:Version Control. Posteriormente darle a la pestaña de log y una vez detectado el commit al que se quiere hacer el rollback, se hace click derecho y se selecciona "Reset current branch to here..". En el diálogo que aparece se selecciona la opción "Hard" (Si se tiene dudas de qué hacer mejor contactar con el Gestor de Configuraciones) y seleccionar reset.
