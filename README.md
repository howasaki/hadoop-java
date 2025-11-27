# Hadoop WordCount Project

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
Crea un archivo de texto de prueba (ej. `input.txt`) con algún contenido y súbelo al sistema de archivos distribuido (HDFS).

```bash
# Crear un archivo de prueba
bin/hdfs dfs -put etc/hadoop/*.xml input

# Crear directorio en HDFS
hdfs dfs -mkdir -p /user/hadoop/wordcount/input

```

### 2. Ejecutar el trabajo (Job)
Utiliza el comando `hadoop jar` para enviar el trabajo al clúster. Asegúrate de especificar la ruta de entrada y la de salida (la ruta de salida no debe existir previamente).

```bash
bin/hadoop jar word-count-1.0.jar input output
```

*Nota: La clase principal está configurada en el manifiesto del JAR (`com.ue.hadoop.WordCount`), por lo que no es estrictamente necesario especificarla en el comando, pero si lo fuera, el comando sería:*
`hadoop jar target/word-count-1.0.jar com.maestria.hadoop.WordCount /user/hadoop/wordcount/input /user/hadoop/wordcount/output`

### 3. Verificar los resultados
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