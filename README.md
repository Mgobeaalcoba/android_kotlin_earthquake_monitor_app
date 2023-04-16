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

---------------------

Construimos nuestro ViewModel: 

1- Implementamos la dependencia necesaria para trabajar con ViewModels: 

```kotlin
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'
```

**Trucazo en Android Studio e Intellij IDEA:** Presionando Crtl + Alt + M y teniendo seleccionado una porción de codigo podemos separar el mismo y asignarlo a un metodo (función de una clase) independiente de donde estamos programando el codigo.  

-----------------------

¿Que son los thread en Android? 

Son procesos que corre un programa. Cada proceso puede correr en un mismo thread o dividirse en threads distintos. El riesgo de que todo vaya en el main thread es que la aplicación colapse. Por lo que se recomienda llevar adelante las operaciones mas pesadad como descargar una imagen o información de un servidor en thread separados. 

Los threads o hilos secundarios normalmente decimos que corren en el background de la aplicación. 

Por ej: podemos tener una app con tres threads corriendo a la vez distribuidos así: 

- Main thread (UI)
- Thread 2: Descarga de una imagen
- Thread 3: Inserta en la base de datos

Para crear hilos o threads diferentes en Android debemos usar algo llamado "corroutines" o "corrutinas en español". 

Las corrutinas son procesos a los que les podemos indicar en qué hilo queremos que se ejecuten.
Y se ven, así como el init, por ejemplo, o como un método normal.

Para crear una corrutina lo primero que tenemos que hacer es inicializar una private val "job"(o cualquier otro nombre) que va a ser asignada como Job()

Luego debemos crear otra private val que va a definir el Scope o alcance de nuestra Coroutine. Para ello debemos seleccionar cual va a ser el dispatcher. Entre los mas usuales están: 

- Main: se usa para ejecutar todo lo relacionado con la experiencia del usuario (UI)
- IO (Input/Output): Se usa para todos los procesos que implican manejar datos. 
- Default: Otros usos
- Unconfined: Hacer testing

Finalmente le agregamos el job creado al scope de nuestra coroutineScope. 

```kotlin
private val job = Job()
private val coroutineScope = CoroutineScope(Dispatchers.Main+ job)
```

Explicación del codigo de arriba: 

Entonces lo que acabamos de hacer aquí es que creamos este job que se inicializa automáticamente al crearlo y luego creamos este scope o este alcance que va a ejecutarse en el hilo principal y va a estar vivo mientras esté vivo el job.

¿Y cuánto tiempo queremos que esté vivo el job?

Pues el mismo tiempo que esté vivo el ViewModel.

Entonces al final del ViewModel vamos a agregar algo que se llama onCleared() que se va a ejecutar cuando el ViewModel vaya a morir y aquí vamos a matar el job.

```kotlin
override fun onCleared() {
    super.onCleared()
    job.cancel()
}
```

Si no hacemos esto la aplicación va a seguir el job y el telefono seguirá destinando recursos a esta tarea. Por eso es importante terminar el job al terminar de ejecutar nuestro ViewModel.

**IMPORTANTE**: Las corrutinas adicionales a la principal se deben crear dentro de la corrutina main. Y no fuera. Ahora mostramos como: 

En el init{} es donde vamos a "lanzar" el trabajo de nuestra corrutina principal y nuestras corrutinas secundarias en caso de que sea necesario. 

Luego por fuera de la corrutina IO pero dentro de la corrutina Main voy a actualizar mi LiveData con la info traida de la primera

```kotlin
    init {
        // Lanzo la corrutina principal y dentro de ella una secundaria del tipo IO para armar mi lista.
        // La corrutina secundaria debe devolverle a la corrutina primaria la lista de terremotos al
        // finalizar su ejecución.
        coroutineScope.launch {
            val eqList = withContext(Dispatchers.IO) {
                val eqList =  mutableListOf<Earthquake>()

                // Hardcodeo mi lista vacia para que tenga elementos que mostrar:
                eqList.add(Earthquake("1","Buenos Aires",4.3,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("2","Lima",2.9,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("3","Ciudad de México",6.0,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("4","Bogotá",4.1,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("5","Caracas",2.5,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("6","Madrid",3.3,275349574L, -102.4756, 28.47365))
                eqList.add(Earthquake("7","Acra",6.3,275349574L, -102.4756, 28.47365))

                eqList
            }
            _eqList.value = eqList
        }
    }
```

Hecho esto la obtencion de la información, que por el momento está hardcodeada se va a realizar en backgorund y la asignación de esa información al LiveData para luego pintarla en pantalla se realizará en el Main Thread. 


















