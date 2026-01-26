# Hadoop MapReduce MVP

Este proyecto implementa un ejemplo clásico de **MapReduce** para contar la frecuencia de palabras en archivos de texto utilizando el framework Apache Hadoop.

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado y configurado lo siguiente en tu entorno:

- **Java JDK 8**: Necesario para compilar y ejecutar aplicaciones Hadoop.
- **Apache Maven**: Para la gestión de dependencias y construcción del proyecto.
- **Apache Hadoop 3.4.x**: El clúster o instalación local de Hadoop debe estar en ejecución.

## 🛠️ Dependencias

El proyecto utiliza las siguientes dependencias principales (definidas en el `pom.xml`):

- `hadoop-common` (3.4.2)
- `hadoop-client` (3.4.2)
- `hadoop-mapreduce-client-core` (3.4.2)

## 📦 Compilación

Para compilar el proyecto y generar el archivo JAR ejecutable, sitúate en la raíz del proyecto y ejecuta:

```bash
mvn clean package
```

Una vez finalizado, se generará el archivo `word-count-1.0.jar` dentro del directorio `target/`.

## 🚀 Ejecución en Hadoop

Sigue estos pasos para ejecutar el trabajo de MapReduce en tu clúster Hadoop:

### 1. Preparar los datos de entrada
Crea un archivo de texto de prueba (ej. `input`) con algún contenido y súbelo al sistema de archivos distribuido (HDFS).

```bash
# Crear un archivo de prueba
bin/hdfs dfs -put etc/hadoop/*.xml input

# Crear directorio en HDFS
hdfs dfs -mkdir -p /user/hadoop/wordcount/input

```
### 2. Sube el archivo jar al servidor Hadoop

Para subir el archivo jar al cluster de hadoop se puede utilizar un software de gestion de archivos como winscp o ftp client como FileZilla (Buscar en google). Aquí estoy utilizando el comando scp desde la terminal de línea de comandos.

```bash
scp word-count-1.0.jar ubuntu@<IP>:/home/ubuntu/
```
Esto copiará el archivo jar con el usuario ubuntu, sin embargo para poder ejecutarlo desde la sesion del usuario hadoop es necesario cambiar el propietario del archivo, esto se hace con el comando chown. Una vez conectado al server con el usuario ubuntu, ejecutar los siguietes comandos en el directorio donde está el archivo jar para poder disponer del archivo desde la sesión del usuario hadoop.

```bash
sudo chown hadoop:hadoop word-count-1.0.jar
sudo mv word-count-1.0.jar /home/hadoop
sudo su hadoop
```

### 3. Ejecutar el trabajo (Job)
Utiliza el comando `hadoop jar` para enviar el trabajo al clúster. Asegúrate de especificar la aplicación a ejecutar (`wordcount`, `topten`, o `toptenletters`), la ruta de entrada y la de salida (la ruta de salida no debe existir previamente).

```bash
bin/hadoop jar word-count-1.0.jar <app_name> input output
```

Donde `<app_name>` puede ser una de las siguientes opciones:
- `wordcount`: para contar la frecuencia de todas las palabras.
- `topten`: para obtener las diez palabras más frecuentes, excluyendo preposiciones.
- `toptenletters`: para obtener las diez letras más frecuentes.

Ejemplo:
```bash
bin/hadoop jar word-count-1.0.jar topten input output
```

*Nota: La clase principal está configurada en el manifiesto del JAR (`com.ue.hadoop.MainApp`), por lo que no es estrictamente necesario especificarla en el comando, pero si lo fuera, el comando sería:*
`hadoop jar target/word-count-1.0.jar com.ue.hadoop.MainApp <app_name> /user/hadoop/wordcount/input /user/hadoop/wordcount/output`

### 4. Verificar los resultados
Una vez que el trabajo haya finalizado correctamente, puedes verificar la salida generada en HDFS.

```bash
# Listar los archivos de salida
bin/hdfs dfs -ls output

# Ver el contenido del resultado (normalmente part-r-00000). Con head solo vemos el principio del archivo
bin/hdfs dfs -head output/part-r-00000 
```

### Salida esperada
Para el ejemplo anterior, la salida debería ser similar a:
```"*"	27
"AS	10
"License");	10
"alice,bob	27
"clumping"	1
"full_queue_name"	1
"priority".	1
"workflowId"	1
(ASF)	1
(as	1
(or	1
(root	1
(the	10
-->	20
-1	1
-1,	1
0.0	1
1-MAX_INT.	1
1.	1
1.0.	1
2.0	10
40	2
40+20=60	1
:	2
<!--	20
</configuration>	10
</description>	36
</name>	2
</property>	69
<?xml	9
<?xml-stylesheet	4
<configuration>	10
<description>	33

```

## 🧹 Limpieza
Para volver a ejecutar el trabajo, debes eliminar el directorio de salida en HDFS, ya que Hadoop no sobrescribe directorios existentes por seguridad.

```bash
bin/hdfs dfs -rm -r output