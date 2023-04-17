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

Esto mismo podriamos hacerlo usando un metodo para llevar adelante el cargado dentro del scope de la corrutina IO así: 

init{}

```kotlin
    init {
        // Lanzo la corrutina principal y dentro de ella una secundaria del tipo IO para armar mi lista.
        // La corrutina secundaria debe devolverle a la corrutina primaria la lista de terremotos al
        // finalizar su ejecución.
        coroutineScope.launch {
            _eqList.value = fetchEarthquakes()
        }
    }
```

private **suspend** fun fetchEarthquakes() {}

```kotlin
    private suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
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
    }
```

La regla es que si utilizamos withContext() y el mismo no está dentro de una corrutina sino en un metodo. El mismo debe ser declarado como private **suspend** fun para que pueda ejecutarse

Todo esto está muy bien pero, en la actualidad existe una forma mucho mas sencilla de armar nuestras corrutinas dentro del ViewModel y sae hace con "viewModelScope" en reemplazando todo el codigo del job y el coroutineScope así como también la función para detener el job al cerrar el ViewModel.

El codigo de nuestro viewModel finalmente quedaría así: 

```kotlin
class MainViewModel: ViewModel() {

    private var _eqList = MutableLiveData<MutableList<Earthquake>>()

    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    init {
        viewModelScope.launch {
            _eqList.value = fetchEarthquakes()
        }
    }

    private suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            val eqList =  mutableListOf<Earthquake>()

            eqList.add(Earthquake("1","Buenos Aires",4.3,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("2","Lima",2.9,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("3","Ciudad de México",6.0,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("4","Bogotá",4.1,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("5","Caracas",2.5,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("6","Madrid",3.3,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("7","Acra",6.3,275349574L, -102.4756, 28.47365))

            eqList
        }
    }
}
```

Las dos formas de trabajar con corrutinas conviven en la actualidad. Esta forma última solo sirve si encendemos las corrutinas en nuestro viewModel. Pero si lo hacemos en otra clase necesariamente debemos trabajar las corrutinas con la primera forma. 

-----------------

Ahora debemos reemplazar nuestra lista hardcodeada por la lista que traemos desde la API. 

Consideraciones: 

1- Como el metodo de service para la request "GET" lo estamos llamando desde dentro de una corrutina debemos agregarle el suspend al mismo. Así: 

```kotlin
interface EqApiService {
    // Aquí podría agregar todos tipos de request que quisiera. En este caso solo voy a usar el GET.
    @GET("all_hour.geojson")
    suspend fun getLastHourEarthquakes(): String
}
```

Luego en nuestro AndroidManifest que es donde van nuestros permisos debemos darle a la app el permiso para que el usuario acceda a internet agregando lo siguiente antes de <application...>: 

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

Luego vamos a modificar el hardcodeo de nuestro lista en el metodo fetch... para traer los terremotos desde la API. Recordemos acá que los vamos a traer en formato String por lo que debemos transformarlos a una MutableList para poder usarlos y pintarlos. 

**Friendly Reminder:** Lo ultimo que escribimos dentro de una corrutina será lo que devolverán.  

Este String que recibimos en realidad es JSON. Solo que como tal no es un dato nativo de Kotlin. Una vez que visualizamos que el JSON esté viniendo como queremos (podemos usar un Log.d y luego ver lo que nos trae a traves del Logcat). Con este JSON lo que ahora debemos hacer es "PARSEARLO" para convertirlo en objetos terremotos dentro de una mutable list. 

### Componentes de un JSON Object: 

- Otros JSON Objects
- JSON Arrays
- Elementos (de diversos tipos como Double, Int, String, Bool, etc)

**¿Como parseamos entonces?**  

Usando un metodo para esta tarea que vamos a crear y recurriendo a alguna librería (todos los lenguajes la tienen) que nos permita realizar este parseo de forma simple y en pocas lineas. 

Dentro de mi JSON obtenido y parseado voy a encontrar una parte que se llama "features" y dentro del mismo van a estar las properties que necesito para crear mis objetos terremos. Debo entonces acceder a las mismas para construir mis objetos. 

Como estoy trayendo mas de un terremoto. Traigo todos los ocurridos en la ultima hora. Entonces debo armar un array de terremotos o de mis features de terremotos. 

Debemos observar con detenimiento como están encadenados los json object dentro de la respuesta de la API. Dado que en función de esto es que vamos a poder obtener la información que necesitamos. 

Por ejemplo. El valor "id" del terremoto viene dentro del JSON object "features" pero el valor "place", "time" y otros vienen dentro del JSON object "properties" que es un sub objeto dentro del objeto features. 


```kotlin
class MainViewModel: ViewModel() {
    // Migramos la lista de terremotos como Mutable live data:
    private var _eqList = MutableLiveData<MutableList<Earthquake>>()

    // Creamos también el LiveData que sea "pareja" de nuestro mutable live data de arriba
    // Esta variable es la que voy a observar desde el main activity para que cuando cambie pinte
    // los cambios ocurridos en nuestra activity:
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    init {
        // Lanzo la corrutina principal y dentro de ella una secundaria del tipo IO para armar mi lista.
        // La corrutina secundaria debe devolverle a la corrutina primaria la lista de terremotos al
        // finalizar su ejecución.
        viewModelScope.launch {
            _eqList.value = fetchEarthquakes()
        }
    }

    private suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            // Guardo en String lo que viene de la API.
            val eqListString =  service.getLastHourEarthquakes()
            // Transitoriamente voy a visualizar que es lo que viene con un Log.d:
            // Log.d("TEST_API", eqListString)
            // Invoco mi función para "parsear" mis datos JSON/String obtenidos de la API:
            val eqList = parseEqResult(eqListString)
            eqList
        }
    }

    private fun parseEqResult(eqListString: String): MutableList<Earthquake> {
        // Convierto mi String en un JSON Object:
        val eqJsonObject = JSONObject(eqListString)

        // Armo entonces un array de las "features" que vienen en mi json para luego armar terremotos
        val featuresJsonArray = eqJsonObject.getJSONArray("features")

        // Armo la lista vacia que luego de completar voy a devolver:
        val eqList = mutableListOf<Earthquake>()

        // Itero mi arreglo para armar mis featuresJsonObject que luego serán la base de mis terremotos:
        for (i in 0 until featuresJsonArray.length()) {
            val featuresJsonObject = featuresJsonArray[i] as JSONObject
            // Ya tenemos el primer objetoJSON separado por lo que podemos ir guardando su información para
            // luego contruir nuestro objeto:
            val id = featuresJsonObject.getString("id")

            // magnitude, places, time,etc están dentro de un sub objeto de features
            val propertiesJsonObject = featuresJsonObject.getJSONObject("properties")
            val magnitude = propertiesJsonObject.getDouble("mag")
            val place = propertiesJsonObject.getString("place")
            val time = propertiesJsonObject.getLong("time")

            // Finalmente longitude y latitude están dentro de otro sub objeto de features
            val geometryJsonObject = featuresJsonObject.getJSONObject("geometry")
            val coordinatesJsonArray = geometryJsonObject.getJSONArray("coordinates")
            val longitude = coordinatesJsonArray.getDouble(0)
            val latiitude = coordinatesJsonArray.getDouble(1)

            val earthquake = Earthquake(id,place,magnitude,time,longitude,latiitude)
            eqList.add(earthquake)
        }
        return eqList
    }
```

Esta es entonces una de las formas en la que podemos traernos información desde internet directamente usando **retrofit**. 

Veamos ahora otra alternativa para parsear JSON: 

------------------------------

**Moshi:** (Existe otra que se llama Gson y es muy similar)

Moshi en lugar de obtener de la API un String nos va a devolver directamente un objeto JSON. 

Para agregarlo lo primero es sumar la implementation en build:gradle: 

```xml
implementation 'com.squareup.retrofit2:converter-moshi:2.5.0'
```

Luego vamos a cambiar la configuración de nuestro EqApiService. 

Habiamos setiado el addConverterFactory para que convierta a String la respuesta de la API a la que le pegamos. 

Ahora debemos cambiar esa config para que sea Moshi el que ocupe su lugar así: 

```kotlin
interface EqApiService {
    @GET("all_hour.geojson")
    suspend fun getLastHourEarthquakes(): EqJsonResponse
}

private var retrofit = Retrofit.Builder()
    .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

var service: EqApiService = retrofit.create(EqApiService::class.java)
```

Luego armomos una clase que nos sirva para recibir lo que Moshi nos devuelve. 

La llamaremos EqJsonresponse: 

En ella vamos a declarar los parametros de la respuesta de la API pero solo los que necesitamos. En este caso por ejemplo no nos sirve agregar el parametro de la respuesta llamado "bbox" o "metadata"

Por cada parametro que quiero recibir voy a armar una clase para recibirlo como parametro de mi class EqJsonResponse dentro de una lista: 

Dentro del objeto/parametro feature que es uno de los que vamos a recibir por ejemplo tengo que seleccionar también que parametros del mismo van a conformar el objeto porque luego serán usados para conformar mi Json con Moshi: 

Quedarían conformados entonces las siguientes clases: 

- EqJsonResponse: 
```kotlin
class EqJsonResponse(val features: List<Feature>)
```
- Feature:
```kotlin
class Feature(val id: String, val properties: Properties, val geometry: Geometry)
```
- Properties:
```kotlin
class Properties(val mag: Double, val place: String, val time: Long)
```
- Geometry:
```kotlin
class Geometry(val coordinates: Array<Double>) {
    // Uso getters para obtener los valores del array que recibo en el JSON.
    val longitude: Double
        get() = coordinates[0]

    val latitude: Double
        get() = coordinates[1]
}
```

Y ahora con Moshi instalado podemos hacer que el resultado de la request GET venga directamente como EqJsonResponse que es el otro cambio que le hicimos al EqApiService arriba.

Luego vamos a modificar nuestra función de parseEqResult dado que ya no recibiremos un string sino un EqJsonResponse para que quedé así nuestro ViewModel: 

```kotlin
class MainViewModel: ViewModel() {

    private var _eqList = MutableLiveData<MutableList<Earthquake>>()
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    init {
        viewModelScope.launch {
            _eqList.value = fetchEarthquakes()
        }
    }

    private suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            val eqList = service.getLastHourEarthquakes()
            parseEqResult(eqList)
        }
    }

    private fun parseEqResult(eqJsonResponse: EqJsonResponse): MutableList<Earthquake> {
        // Armo la lista vacia que luego de completar voy a devolver:
        val eqList = mutableListOf<Earthquake>()

        // Obtengo los distintos features de mi eqJsonResponse
        val featureList = eqJsonResponse.features

        // Ahora convertimos nuestro objeto EqJsonResponse en Terremotos así:
        for (feature in featureList) {
            val id = feature.id
            val place = feature.properties.place
            val magnitude = feature.properties.mag
            val time = feature.properties.time
            val longitude = feature.geometry.longitude
            val latitude = feature.geometry.latitude
            val earthquake = Earthquake(id, place, magnitude, time, longitude,latitude)
            eqList.add(earthquake)
        }
        return eqList
    }
}
```

**Importante:** para que Moshi funcione los nombres de los atributos de las clases que construimos con el fin de recibir el objeto en formato JSON deben ser exactamente los mismos que como están escritos en el JSON de la API a la que le estamos pegando.  

Pero existe una forma de trabajarlo con un nombre diferente como por ejemplo "magnitude". Para ello debemos agregar antes de nuestro atributo un "@Json(name="mag")" y luego definir la variable como queramos. por ejemplo así: 

```kotlin
import com.squareup.moshi.Json

class Properties(@Json(name = "mag") val magnitude: Double, val place: String, val time: Long)
```

------------------------

Muy bien. Ya tenemos entonces nuestra **Activity**, tenemos nuestro **ViewModel** y tenemos nuestro **Remote Data Source** al que conectamos con un **webservice** usando **Retrofit**

¿Que nos falta para respetar nuestra **arquitectura MVVM**?

- Debemos crear nuestro **Repository** (Vamos a hacerlo ahora). El objetivo del repositorio recordemos que es que nuestros datos lleguén al ViewModel siempre desde una única fuente (El Repository)

- Debemos crear nuestro **Model** (Lo haremos luego junto con la conexión con nuestra base de datos.)

### Recordemos nuestro diagrama MVVM nuevamente: 

![Captura 1](images/screen_1.png)

1- Creamos en nuestro unico paquete una clase a la que llamaremos MainRepository

2- Pasamos todo el codigo de conexión y transformación de los datos con el servicio web que usamos (La API) a nuestro repository quitandolo de nuestro ViewModel

3- Convierto mis metodos/funciones que eran privados en publicos dado que serán llamados desde nuestro ViewModel

4- Creo un objeto repositorio en mi ViewModel

5- Uso el metodo publico de mi repositorio desde mi ViewModel

El ViewModel nos queda así: 

```kotlin
class MainViewModel: ViewModel() {

    private var _eqList = MutableLiveData<MutableList<Earthquake>>()
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    private val repository = MainRepository()

    init {
        viewModelScope.launch {
            _eqList.value = repository.fetchEarthquakes()
        }
    }
}
```

El MainRepository nos queda así: 

```kotlin
class MainRepository {
    suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            val eqJsonResponse = service.getLastHourEarthquakes()
            val eqList = parseEqResult(eqJsonResponse)
            eqList
        }
    }

    private fun parseEqResult(eqJsonResponse: EqJsonResponse): MutableList<Earthquake> {
        // Armo la lista vacia que luego de completar voy a devolver:
        val eqList = mutableListOf<Earthquake>()

        // Obtengo los distintos features de mi eqJsonResponse
        val featureList = eqJsonResponse.features

        // Ahora convertimos nuestro objeto EqJsonResponse en Terremotos así:
        for (feature in featureList) {
            val id = feature.id
            val place = feature.properties.place
            val magnitude = feature.properties.magnitude
            val time = feature.properties.time
            val longitude = feature.geometry.longitude
            val latitude = feature.geometry.latitude
            val earthquake = Earthquake(id, place, magnitude, time, longitude,latitude)
            eqList.add(earthquake)
        }
        return eqList
    }
}
```

Ahora es la clase MainRepository la que se encarga de comunicarse con nuestro web service y en un futuro será la misma clase la que se comunique con nuestras bases de datos. 

Finalmente vamos a setear el formato en el que queremos que se nos muestre la magnitud de los terremotos dado que vienen algunos con multiples decimales y yo solo quiero 2 de ellos. 

Lo primero es crear un formato en strings.xml con el que forzamos a un número float o double a ser de dos decimales:

```xml
<string name="magnitude_format">%.2f</string>
```

Luego en EqAdapter, cuando insertamos la magnitud, vamos a cambiar esto:

```kotlin
binding.eqMagnitudeText.text = earthquake.magnitude.getString()
```
Por esto: 

```kotlin
binding.eqMagnitudeText.text = context.getString(R.string.magnitude_format, earthquake.magnitude)
```

Pero observando detenidamente vamos que se necesita un context para usar el getString, entonces en el constructor de EqAdapter agregaremos un context así:

```koltin
class EqAdapter(private val context: Context): ...
```

Finalmente, desde MainActivity, pasamos el context al adapter así:

```koltin
val adapter = EqAdapter(this)
```

Y listo. Tenemos seteados los Double de la magnitud con 2 decimales solamente. 

-------------------------

**¿Como estructuramos nuestra clases para que queden bien acomodadas y respeten una estructura de "Clean Code"?**

No se debe acomodar todas las activities en una carpeta llamada activities y todos los adaptar en una llamada adapter sino que por el contrario es mejor acomodar nuestras clases/archivos por "features"

Todo estará dentro de "com.mgobeaalcoba.earthquake": 

- api
  - EqApiService.kt
  - EqJsonResponse.kt
  - Feature.kt
  - Geometry.kt
  - Properties.kt
- main
  - MainActivity.kt
  - EqAdapter.kt
  - MainViewModel.kt
  - MainRespository.kt
- database
- Earthquake.kt

**Criterio para cuando sumamos nuevas pantallas:** 

- Solo tendremos un paquete de API y un paquete de Databases. En el mismo sumaremos todas las conexiones y request necesarias. Puede ser para pegarle a una API o a muchas API´s. A una databases o a muchas databases. 
- Por cada pantalla de nuestra app si tendremos un paquete para esa pantalla y en el mismo tendremos su propio MainActivity, su propio MainViewModel y su propio MainRepository. Si es necesario (si tiene recycler views) entonces también su propio adapter. 
- Una activity también podría tener distintos fragments. Pero eso lo desarrollamos luego. 

---------------------------

**¿Que es Room?** 

Es una librería que nos va a permitir implementar bases de datos para Android. Es la forma recomendada para hacerlo. 

Room funciona ensima de SQL y nos va a facilitar bastante el uso de SQL. 

Para usar Room (Librería de base de datos) necesitarás las siguientes dependencias, agrégalas a tu archivo build.gradle (App):

```kotlin
implementation 'androidx.room:room-runtime:2.2.5'

kapt 'androidx.room:room-compiler:2.2.5'
```

¿Como comenzamos entonces a trabajar con Room? 

1- Las clases que vayamos a guardar en nuestra base de datos deben ser "data class" 

2- Debemos agregarle a nuestras data class que vayamos a guardar una anotación arriba del tipo **@Entity(tablename = "nombre_tabla")**

3- Agrego una anotación de @PrimaryKey a aquel atributo de nuestra clase que será el "id" de nuestra tabla. **@PrimaryKey**

Quedaría así nuestra clase: 

```kotlin
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earthquakes")
data class Earthquake(
    @PrimaryKey val id: String,
    val place: String,
    val magnitude: Double,
    val time: Long,
    val longitude: Double,
    val latitude: Double )
```

--------------------------

DAO´s = Interface que nos va a ayudar a insertar y tomar datos de nuestra database. 

DAO significa = Data Access Object

1- El DAO se crea dentro del package "database".

2- Debe ser una Interface de con nombre del tipo "EqDao"

3- Le agregamos a nuestra interface EqDao la anotación @Dao

4- Dentro de la interface van a estar todos los metodos que nos van a permitir manipular los datos.

5- Por tal motivo debe haber un @Dao por cada @Entity

6- En este caso vamos a hacer un metodo para insertar terremotos (los que descargamos de la API) y otro para traer de nuestra base de datos los terremotos de nuestra base

7- Agrego un @Insert arriba de la función que reciba los datos a insertar. En este caso la func se llama insertAll y recibe una lista de Earquakes. 

8- Cuando tengamos que insertar un elemento con un id que ya existe en nuestra base de datos debemos detallar en el parentesis del insert () que hay que hacer así @Insert(onConflict = OnConflictStrategy.REPLACE)

9- Luego vamos a armar el metodo para obtener todos y eso lo hacemos con la anotación @Query que lleva entre parentesis la Query que queremos consultar y traer. 

10- Tambien existen las anotaciones @Update y @Delete para actualizar datos o eliminarlos respectivamente. 

**Truco:** Con (vararg variable: tipo) puedo declarar que voy a pasar o 1 objeto o una lista de objetos de la clase señalada. 

Así quedaría entonces nuestro EqDao: 

```kotlin
package com.mgobeaalcoba.earthquakemonitor.database

import androidx.room.*
import com.mgobeaalcoba.earthquakemonitor.Earthquake

@Dao
interface EqDao {

    // Metodo para insertar terremotos:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eqList: MutableList<Earthquake>)

    // Metodo para obtener terremotos
    @Query("SELECT * FROM earthquakes")
    fun getEarthquakes(): MutableList<Earthquake>

    @Query("SELECT * FROM earthquake WHERE magnitude > :mag")
    fun getEarthquakeWithMagnitude(mag: Double): MutableList<Earthquake>

    @Update
    fun updateEq(vararg eq: Earthquake)

    @Delete
    fun deleteEq(vararg eq: Earthquake)
}
```



















































