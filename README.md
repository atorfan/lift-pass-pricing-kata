# Lift Pass Pricing Kata

You can find out more about this exercise in [this web page](https://www.exeal.com/katas/lift-pass-pricing-kata/).

## Tu tarea

Esta aplicación resuelve el problema de calcular el precio de los forfaits, es decir, los pases para acceder a esquiar.
Existe una lógica intrincada vinculada al tipo de forfait que deseas, tu edad y la fecha específica en la que te
gustaría esquiar.

Se pide implementar una nueva funcionalidad: poder obtener el precio de varios forfaits, no solo uno.

Actualmente, el precio de un solo forfait está implementado; desafortunadamente, el código tal como está diseñado no es
reutilizable. Podrías implementar algunos tests de alto nivel para realizar una refactorización preparatoria de modo que
la nueva funcionalidad requiera un esfuerzo mínimo para implementarse.

> [!NOTE]
> Esta kata modela un problema común: código que no tiene sentido testear unitariamente debido a un mal diseño.

## ¿Cuándo he terminado?

Hay algunos pasos, podrías realizar cualquiera de ellos.

* Cubrir con tests de alto nivel.
* Refactorizar el código para habilitar la implementación de tests unitarios y reutilización para la nueva funcionalidad.
* Reducir la cantidad de tests de alto nivel.
* Implementar la nueva característica utilizando tests unitarios y 1 o 2 tests de alto nivel.

## Instalación

Descarga el código inicial aquí, y elige el lenguaje en el que desees resolver la kata:<br>
https://github.com/martinsson/Refactoring-Kata-Lift-Pass-Pricing

Configura una base de datos MySQL en localhost:3306 con el usuario root y la contraseña mysql. Si tienes Docker
instalado, lo más fácil es usar este script, que inicializará una MariaDB.

```bash
./runLocalDatabase.sh
```

Inyecta los datos con

```bash
mysql -u root -p mysql < ./database/initDatabase.sql
```

Luego dirígete al lenguaje de tu elección y sigue el Readme que está allí. Algunos de los lenguajes tienen una prueba
fallida que podrías terminar de escribir.


## Cómo resolverla

Existe una buena posibilidad de que puedas encontrar un diseño que sea más fácil de probar, más rápido de trabajar y que
resuelva el problema con la cantidad mínima de código. Un diseño así sería desacoplar la mayor parte de la lógica del
framework HTTP/REST y de las especificidades de SQL. A esto a veces se le llama arquitectura hexagonal y facilita
respetar la Pirámide de Tests, lo cual actualmente no es posible, ya que solo puede haber pruebas de nivel superior.

El flujo de trabajo típico sería:

* Cubrir todo desde la capa HTTP, usando una base de datos real.
* Separar la extracción de datos de la solicitud y el envío de la respuesta de la lógica.
* Extraer un método con la lógica pura, mover ese método a un objeto (por ejemplo, <span style="color: pink">PricingLogic</span>).
* Ahora extraer las cosas de SQL de <span style="color: pink">PricingLogic</span>, primero a algún método con una firma que no tenga nada que ver con SQL,
luego mover estos métodos a una nueva clase (por ejemplo, <span style="color: pink">PricingDao</span>).
* Debería haber ~3/4 elementos, la capa HTTP debería tener <span style="color: pink">PricingLogic</span> como una dependencia inyectada y <span style="color: pink">PricingLogic</span>
debería tener <span style="color: pink">PricingDao</span> como una dependencia inyectada.
* Mover la mayor parte de las pruebas de alto nivel hacia abajo en <span style="color: pink">PricingLogic</span> usando un dao falso, escribir algunas
pruebas de integración enfocadas para <span style="color: pink">PricingDao</span> usando una base de datos real, deberían ser solo unas pocas.
* Ahora la capa HTTP y la integración de las partes pueden ser probadas con muy pocas (una o dos) pruebas de alto nivel.

## Recursos

* [Repositorio de Github original](https://github.com/martinsson/Refactoring-Kata-Lift-Pass-Pricing?tab=readme-ov-file)
* [Explicación del ejercicio (Johan Martinsson, inglés)](https://www.youtube.com/watch?v=-gSyD60WAvc&ab_channel=JohanMartinsson)
