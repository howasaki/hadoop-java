from pyspark.sql import SparkSession
from pyspark.sql.functions import explode, split, lower, col, regexp_replace
import sys

def main(input_file):
    # Inicializar la sesión de Spark
    spark = SparkSession.builder \
        .appName("WordCount") \
        .getOrCreate()

    # Leer el archivo de texto
    # Usamos .read.text() para leer el archivo línea por línea
    try:
        df = spark.read.text(input_file)
    except Exception as e:
        print(f"Error al leer el archivo: {e}")
        sys.exit(1)

    # Limpiar y procesar el texto:
    # 1. Convertir a minúsculas
    # 2. Eliminar puntuación básica (puedes añadir más si es necesario)
    # 3. Dividir las líneas en palabras usando espacios
    # 4. 'explode' convierte la lista de palabras en filas individuales
    words_df = df.select(
        explode(
            split(
                regexp_replace(lower(col("value")), r'[^\w\s]', ''),
                r"\s+"
            )
        ).alias("word")
    )

    # Filtrar palabras vacías (que pueden aparecer por múltiples espacios)
    words_df = words_df.filter(col("word") != "")

    # Contar las palabras y ordenar por frecuencia descendente
    word_counts = words_df.groupBy("word") \
        .count() \
        .orderBy(col("count").desc())

    # Mostrar los resultados (los primeros 20 por defecto)
    print("Conteo de palabras completado:")
    word_counts.show()

    # Si quisieras guardar los resultados en un archivo:
    word_counts.write.csv("output_word_count")

    # Detener la sesión de Spark
    spark.stop()

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Uso: spark-submit wordcount.py <ruta_del_archivo>")
    else:
        main(sys.argv[1])
