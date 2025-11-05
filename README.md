El proyecto Check-in Aula es una aplicación desarrollada en Java que permite registrar inscripciones de estudiantes al inicio de clase de forma simple y eficiente. Está pensada como una herramienta educativa que puede utilizarse tanto desde la consola como a través de una interfaz gráfica (GUI) construida con Swing, ofreciendo así dos modos de interacción según las necesidades del usuario o del entorno de uso.

Su funcionamiento se basa en registrar los datos principales de cada estudiante —nombre, documento, curso y hora automática de ingreso—, permitiendo además listar todas las inscripciones realizadas, realizar búsquedas por texto y generar un resumen básico agrupado por curso. A diferencia de otros sistemas, Check-in Aula no utiliza bases de datos ni archivos JSON, sino un sistema de persistencia en formato CSV, que facilita el almacenamiento y recuperación de la información sin necesidad de configuraciones adicionales.

Al ejecutar la aplicación, el sistema crea automáticamente un archivo llamado checkin.csv dentro de la carpeta data/. En dicho archivo se guardan todas las inscripciones realizadas y, cada vez que el programa se inicia, los registros se cargan nuevamente para mantener la continuidad de la información. El formato es simple y legible, lo que permite abrirlo con cualquier editor de texto o planilla de cálculo para revisar los datos almacenados.

El proyecto está estructurado siguiendo buenas prácticas de programación orientada a objetos, con paquetes que separan el modelo de datos (model), la lógica del programa (logic), la interfaz de usuario (ui), la versión de consola (console) y la capa de persistencia (persist). Esta organización facilita la comprensión, mantenimiento y ampliación del código.

Check-in Aula fue desarrollado en IntelliJ IDEA utilizando Java 23. Su ejecución es muy sencilla: al correr el archivo App.java, la aplicación se inicia por defecto en modo gráfico; si se desea utilizar por consola, basta con agregar el argumento --nogui en la configuración de ejecución.

En definitiva, este proyecto constituye un ejemplo práctico de cómo implementar un sistema básico de registro con persistencia en archivos, integrando conceptos fundamentales de la programación orientada a objetos, manejo de archivos y diseño de interfaces gráficas en Java.

* Link a GitHub con:

  * Código fuente (`src/...`) con la estructura mínima indicada.
  * `README.md` breve con comandos de compilación/ejecución por consola y cómo lanzar con `--gui`.
  * `.gitignore` básico.
