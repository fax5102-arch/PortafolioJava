# Usamos Java 21 JDK
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiamos todo el proyecto al contenedor
COPY . /app

# Creamos una carpeta para los binarios compilados
RUN mkdir -p out/production/PortafolioJava

# Buscamos todos los archivos .java y los compilamos incluyendo el jar de SQLite del directorio lib en el classpath
RUN find src -name "*.java" > sources.txt
RUN javac -cp "lib/*" -d out/production/PortafolioJava @sources.txt

# Puerto donde corre tu servidor HttpServer
EXPOSE 8085

# Ejecutamos la aplicación incluyendo el classpath con las clases compiladas y el jar de SQLite
CMD ["java", "-cp", "out/production/PortafolioJava:lib/*", "com.portafolio.Main"]