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
# Crear un archivo de prueba local
echo "hola mundo hadoop hola java mapreduce" > input.txt

# Crear directorio en HDFS (opcional)
hdfs dfs -mkdir -p /user/hadoop/wordcount/input

# Subir el archivo a HDFS
hdfs dfs -put input.txt /user/hadoop/wordcount/input/
```

### 2. Ejecutar el trabajo (Job)
Utiliza el comando `hadoop jar` para enviar el trabajo al clúster. Asegúrate de especificar la ruta de entrada y la de salida (la ruta de salida no debe existir previamente).

```bash
hadoop jar target/word-count-1.0.jar /user/hadoop/wordcount/input /user/hadoop/wordcount/output
```

*Nota: La clase principal está configurada en el manifiesto del JAR (`com.maestria.hadoop.WordCount`), por lo que no es estrictamente necesario especificarla en el comando, pero si lo fuera, el comando sería:*
`hadoop jar target/word-count-1.0.jar com.maestria.hadoop.WordCount /user/hadoop/wordcount/input /user/hadoop/wordcount/output`

### 3. Verificar los resultados
Una vez que el trabajo haya finalizado correctamente, puedes verificar la salida generada en HDFS.

```bash
# Listar los archivos de salida
hdfs dfs -ls /user/hadoop/wordcount/output

# Ver el contenido del resultado (normalmente part-r-00000)
hdfs dfs -cat /user/hadoop/wordcount/output/part-r-00000
```

### Salida esperada
Para el ejemplo anterior, la salida debería ser similar a:
```text
hadoop	1
hola	2
java	1
mapreduce	1
mundo	1
```

## 🧹 Limpieza
Para volver a ejecutar el trabajo, debes eliminar el directorio de salida en HDFS, ya que Hadoop no sobrescribe directorios existentes por seguridad.

```bash
hdfs dfs -rm -r /user/hadoop/wordcount/output