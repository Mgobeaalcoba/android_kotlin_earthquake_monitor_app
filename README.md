# android_kotlin_earthquake_monitor_app

## Arquitectura MVVM en Android

Vamos a empezar a implementar la arquitectura MVVM en la app de Monitor de Terremotos, empezaremos por la descarga de datos de internet:

![Captura 1](images/screen_1.png)

Temas que vamos a trabajar para esto son: 

- API´s y como comunicarte con ella.

- Qué es JSON

- Retrofit, una de las librerías más usadas en Android para descargar datos de internet.

- Aprenderás qué son coroutines (Corutinas) y por qué son tan necesarias.

- Implementarás la parte señalada en la imagen de la arquitectura MVVM.

Recordemos que la parte de la arquitectura que une Activities/Fragments con los ViewModels usando LiveData ya la aplicamos en la arquitectura de la Basketball Score App. 

¿Que es una API? 

Es una "Interfaz de programación de aplicaciones" que conecta una aplicación (Logistics App por ej) con un servidor (Fleet Management por ejemplo).

Una API que usa el protocolo HTTP para comunicarse es comunmente denominada como API REST. Normalmente, los softwares que trabajan con este tipo de API´s lo hacen a traves de codigo XML o JSON. 

Desde nuestra APP Android podemos obtener información en estos formatos pegandole a esta API, por ejemplo con una GET request y usar esa información para pintarla en pantalla o para realizar operaciones lógicas, según nuestra necesidad. 

--------------------------

https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php

En ese link vamos a encontrar la API de terremotos reales del centro USGS. Allí vamos a encontrar el formato en que esta API nos devuelve la información y como pegarle a la mismo (RFC de una API). 

RFC = Request for comments

Algunas API´s te pidan una API Key para poder pegarle pero en este caso, vamos a usar una que es completamente libre. Por lo que no será necesario. 

Para descargar datos de esta API o cualquier otra en nuestra APP Android vamos a usar una librería llamada Retrofit. 

Voy a crear un file Kotlin donde guardaré mi conexión con la API. (Ej EqApiService)

Podemos copiar y pegar el codigo desde la docu de retrofit (Está en Java pero Android Studio lo va a convertir en Kotlin directamente)

Le cambiamos el nombre a nuestra interface según nuestra necesidad. En este caso EqApiService. 

Tmabién vamos a cambiar algunos otros parametros en función de nuestra necesidad, la URL de la API a la que vamos a pegar, etc. 

Una vez lista copiamos y pegamos de la docu oficial la siguiente parte del codigo: 

Docu oficial aquí: https://square.github.io/retrofit/

Base URL de nuestra API de terremotos sería: https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/ para cada caso lo debemos entontrar viendo como varía la URL en función de los distintos endpoints. 

Luego buscamos la parte de la dirección del endpoint que necesitamos para cada request. Por ejemplo el endpoint de todos los terremotos de la ultima hora es: 
all_hour.geojson

Finalmente vamos setear a la var retrofit como private var y vamos a agregar la dependencia de scalar converter a nuestro build.gradle para poder lograr que lo que nos devuelve la API sea formato string. 

```kotlin
implementation 'com.squareup.retrofit2:converter-scalars:2.5.0'
```

Así nos quedaría entonces nuestro EqApiService: 

```kotlin
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


interface EqApiService {
    // Aquí podría agregar todos tipos de request que quisiera. En este caso solo voy a usar el GET.
    @GET("all_hour.geojson")
    fun getLastHourEarthquakes(): String
}

private var retrofit = Retrofit.Builder()
    .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

var service: EqApiService = retrofit.create(EqApiService::class.java)
```

Finalmente sumo en el MainActivity el encendido de mi conexión con la API: 

```kotlin
// Me conecto desde el main con la API de terremotos. 
service.getLastHourEarthquakes()
```

En realidad esta conexión con la API la debemos hacer en un ViewModel. Por lo que construiremos el mismo y pasaremos no solo la conexión con la API sino toda la "lógica de negocio" del MainActivity al MainViewModel dejando en MainActivity solamente aquello que refiera a las interacciones entre el usuario y la app. 








