##  IRRIGATION SYSTEM SERVICE

### 1. Требования
Java 11+, Gradle 6.5+, Ubuntu

### 2. Сборка
`gradle build`

### 3. Запуск
Для запуска программы выполнить команду:  
`<путь до jdk11>/bin/java -jar --in=<путь до файла с данными> <путь до приложения>/build/libs/irrigation-system-service-1.0.0.jar`  
Для запуска программы средствами gradle необходимо выполнить команду:  
`gradle run --args='--in=<путь до файла с данными>' -Dorg.gradle.java.home=<путь до jdk11>`  
Пример:  
`gradle run --args='--in=/test' -Dorg.gradle.java.home=/usr/lib/jvm/jdk-11.0.1`

### 4. API
Возможные ключи запуска  
`--in` - путь до файла с данными  