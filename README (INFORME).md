# Informe - Gestor de Paradas y Rutas

## Antes de leer
Este es el informe de este proyecto. Solo incluye la documentación para lo más importante, fuera de lo fácilmente entendible y los algoritmos ya hechos que se implementaron.

**Algunas aclaraciones**:

- Con "ruta óptima" nos referimos al conjunto de instancias de Parada y Ruta que conforman un "camino" entre dos paradas; mientras con simplemente "ruta" nos referimos a una instancia de Ruta.

## Clases fundamentales importantes


### Clase Parada:

Esta es la clase que representa las paradas (equivalente a los nodos en el grafo).

**Atributos especiales:**

- *id:* identificador único de la parada (utilizado como clave en los mapas de GestorRuta, la clase controladora del backend);

- *paradasApuntadas:* lista de adyacencia de las paradas a las cuáles se puede llegar desde esta parada (es decir, que hay rutas [aristas] desde la parada que apuntan a ellas, siendo que el grafo es dirigido);

- *rutas:* rutas a dichas paradasApuntadas, están en el mismo orden que paradasApuntadas, así se garantiza un correcto acceso a dichas paradas, pues la ruta por la cual se llega a cada parada está conectada con dicha parada por sus índices en estas listas;

- *paradasApuntadoras:* paradas que apuntan a esta, es decir, desde las cuáles se llegan a esta parada (esta lista facilitará luego eliminar los enlaces de esas paradas cuando se elimine esta parada, también ayudará a tratar el grafo como bidireccional cuando se use expansión mínima).


**Métodos importantes:**

- `public void agregarAdyacencia(Parada neoAd, Ruta ruta)`: agrega una parada adyacente con su respectiva ruta, en el mismo índice (pues siempre coincidirán por la gestión que se hace de estas listas). También agrega esta parada como parada apuntadora de la parada a la que se hace apuntar esta parada (neoAd). Nota: neoAd es por neo-adyacencia.

**Explicación de las estructuras de datos utilizadas:**

Utilizamos ArrayLists para guardar las rutas y las paradas; para las rutas sobre todo para acceder a ellas en tiempo constante cuando se esté iterando sobre la lista de adyacencia de paradas para ejecutar los algoritmos de buscar rutas óptimas; y para las paradas utilizamos esta estructura ya que pueden haber más de dos rutas a una misma parada, por lo que para garantizar que la ruta apunte a su respectiva parada, dicha parada puede aparecer dos veces en esta lista; entonces usamos ArrayList, que permite esto y además permite un acceso en tiempo constante a estas paradas.

También utilizamos un Set para gestionar las paradas apuntadoras, ya que no necesitamos que se repitan, solo queremos saber cuáles son dichas paradas que apuntan a esta parada.

### Clase Ruta:

Esta es la clase que representa las rutas (equivalente a las aristas en el grafo).

**Atributos especiales:**

- *id:* identificador único de la ruta, y clave para acceder a la ruta en el mapa de GestorRutas.

**Métodos importantes**

- `Public float getCostoNeto()`: retorna el costo por usar la ruta tras aplicarle el descuento al costoBruto



## Clases controladoras

### GestorRutas (controladora del backend)

Esta es la clase que gestiona el backend.

**Atributos importantes:**

- *idParadaActual:* id autoincrementado para las paradas, su valor es el id correspondiente a la siguiente parada que se cree;

- *idRutaActual:* id autoincrementado para las rutas, su valor es el id correspondiente a la siguiente ruta que se cree;

- *paradas:* paradas en todo el sistema;

- *rutas:* rutas en todo el sistema;

- *nombresParadas:* Set que contiene todos los nombres de las paradas registrados; se utiliza para confirmar que los nombres de las paradas no se repitan, lo cual es el criterio principal para crear paradas (los nombres no se pueden repetir);

- *prefFWListo:* preferencias en las que están basadas las últimas rutas calculadas con Floyd-Warshall (esto se utiliza para confirmar si se puede retornar en O(1 )con coherencia la ruta pre-calculada):

- *fwListo:* valor booleano que indica si hay rutas pre-calculadas con Floyd-Warshall listas:

- *fwEnProgreso:* valor booleano que indica si ya hay un Thread ejecutando Floyd-Warshall en paralelo; esto para evitar que se corran dos Floyd-Warshall al mismo tiempo; 

- *mstActual:* árbol de expansión mínima correspondiente al grafo actual (equivalente no-dirigido);

- *expMinActivado:* valor booleano que indica si se está trabajando con el árbol de expansión mínima no dirigido equivalente al grafo dirigido actual, en vez de con el grafo actual.

**Métodos importantes:**

- `private boolean prefsIguales(Preferencias prefs[])`: compara las preferencias actuales para buscar rutas óptimas con las del último mapa de rutas óptimas de Floyd-Warshall, si hay uno calculado.

- `public void  eliminarParada(int idParada)`: elimina del sistema la parada con el id pasado como parámetro. Para esto, se eliminan todas las instancias de dicha parada en las listas de adyacencias de otras paradas que la contenían, y también se eliminan todas las rutas que iban hacia dicha parada desde otras paradas. Luego, se elimina la parada del sistema.

- `public void  eliminarRuta (int idRuta)`: elimina del sistema la ruta con el id pasado como parámetro. Para esto, se eliminan todas las instancias de dicha ruta en todas las paradas que la contenían como medio para llegar a otras paradas en sus listas de adyacencias, y también esas paradas a las que apuntaban, en el índice que estaba la ruta eliminada. 

- `public boolean modificarDescuentoRuta(float neoDescuento, int idRuta)`: método que retorna un booleano que indica si se puede añadir cierto descuento, de tal manera que no quede un ciclo negativo. Para esto, utiliza el algoritmo Bellman-Ford para detectar si habrá un ciclo negativo en el caso que se añada el descuento en cuestión. La decisión de este algoritmo de indicar que no se puede crear este descuento es segura, ya que Bellman-Ford solo puede retornar null si hay un ciclo negativo o si no hay ruta entre las paradas extremos de la ruta óptima que busca; y cómo este método usa Bellman-Ford con las dos paradas origen y destino de la ruta, siendo que existe la ruta, es seguro que existirá al menos una ruta entre las paradas (la que se está evaluando para aplicar el descuento).

- `private List<ParParadaRuta> rutaOptima(Map<Integer,ParParadaRuta> predecesores, int idDestino)`:  retorna la ruta óptima implícita en los predecesores que se reciben. Se empieza desde el predecesor de la parada destino y se sigue hasta que se llegue a la parada origen, cuyo predecesor es `null`.

- `private void llenarInfoBasica(Map<Integer, RegistroDiscriminates> discriminantes, Map<Integer,ParParadaRuta> predecesores, Map<Integer,Boolean> visitados, Map<Integer,Integer> distancias)`: es solo una función auxiliar para iniciar los discriminantes para los algoritmos de rutas óptimas, excluyendo Floyd-Warshall, ya que funciona algo diferente (con listas directamente en vez de pares ParParadaRuta).

- `private int compararMultiPrefs(RegistroDiscriminates discr1, RegistroDiscriminates discr2, Preferencias[] prefs)`: método principal de comparación de multipreferencias. Compara los registros de discriminantes según el orden de las preferencias. Si las preferencias principales de dos registros son iguales, entonces se sigue hasta llegar a la diferencia más pronta entre discriminantes según el orden de preferencias.

- `private List<ParParadaRuta> rutaTransbordosMinimos(int idOrigen, int idDestino, Preferencias[] preferencias)`: método para encontrar ruta óptima basada en Transbordos como preferencia principal. Es un BFS, pero algo modificado, por ejemplo, aquí el hecho de que una parada haya sido visitada no significa que deba ser descartada para visitarla de nuevo desde otra ruta, porque se está trabajando con optimización basada en multi-preferencias, y puede ser que otra ruta se adecúe mejor a esta búsqueda, aún teniendo la misma cantidad de transbordos.

- `public void desactivarExpMin()`: método que desactiva el “modo expansión mínima”.

- `private List<ParParadaRuta> rutaExpMin(int idOrigen, int idDestino, Map<Integer, Ruta> arbolExpMin)` y  `private List<ParParadaRuta> recursionRutaExpMin(int idActual, int idDestino, Map<Integer, Ruta> arbolExpMin, Map<Integer, Boolean> paradasVisitadas)`: métodos para buscar una ruta óptima en “modo expansión mínima”, tratando al grafo dirigido como dirigido, para darle coherencia al resultado del resultado de Kruskal. Es un DFS que no solo toma en cuenta la lista de adyacencia y rutas del nodo actual que se evalúa para llegar a otros nodos (dicha lista se evalúa en el primer for), sino también las de todos los nodos que apuntan a él (en el segundo for) porque el grafo se está tratando como dirigido y no importan de dónde vengan las rutas, siendo que conecten a dos nodos. Para ser coherente con las aristas correspondientes al árbol de expansión mínima generado por Kruskal en forma de mapa de rutas, antes de evaluar cada arista, confirma si está contenida en dicho mapa; si no está, la ignora totalmente.
- `public List<ParParadaRuta> encontrarRuta(int idOrigen, int idDestino, Preferencias preferencias[])`: método definitivo para gestionar la búsqueda rutas óptimas. Dependiendo de la preferencia principal, ejecuta el método apropiado. También determina si puede retornar la ruta deseada desde el mapa de Floyd-Warshall pre-calculado y manda a preparar dicho mapa (en un Thread, ya que es el método que más complejidad temporal tiene de todos, y así se puede ejecutar otro método menos complejo mientras se calcula este mapa) para usarlo en caso de que se vuelvan a usar las mismas preferencias para buscar otra ruta óptima.


**Clases definidas aquí**

- *ParNodoDiscriminante*: clase que combina un nodo (parada) y el discriminante asociado para llegar a él en la búsqueda de una ruta óptima. Es una clase porque se usa en PriorirtyQueue, como en Dijkstra, y necesita implementar Comparable. También contiene un arreglo de preferencias que define el orden de preferencias para su implementación del método compareTo, el cual es `compararMultiPrefs()`.

- *ParRutaDiscriminante*: clase que combina una (ruta) con sus atributos como discriminantes. Es una clase porque se usa en PriorirtyQueue para Kruskal, y no queríamos hacer que Ruta implementara Comparable, pues el método que se usaría en su implementación de compareTo también sería `compararMultiPrefs()`, y queríamos gestionar todo eso en la clase controladora del backend (esta).


**Records definidos aquí**

- *RegistroDiscriminates*: (debió ser RegistroDiscrimina**n**tes, pero bueno, así se quedó) **Ver en la sección de Records Importantes**

**Explicación de las estructuras de datos utilizadas y clases externas utilizadas:**

Utilizamos HashMaps para guardar las rutas (para el caso de todas las rutas del grafo y el caso de las rutas que forman parte del mstActual), paradas, o listas (para el caso del mapa de rutas que genera Floyd-Warshall) gracias a la recuperación en tiempo constante que ofrecen con su uso del hash de la clave para calcular el índice donde se encuentra o encontrará un objeto.

Utilizamos un LinkedHashSet para guardar los nombres de las paradas ya registrados porque no queremos nombres repetidos, siendo que solo necesitamos saber si ya se encuentran registrados o no; y también permite esta comprobación en tiempo constante gracias a su uso de hash para calcular los índices y acceder a ellos.

Utilizamos AtomicBoolean para fwlisto y fwEnProgreso, ya que estos valores podrían ser accedidos varios hilos al mismo tiempo (en caso de que salga algo mal en la gestión que hacemos) y queremos evitar posibles conflictos causados por ello.



### GestorArchivos

Esta es la clase que gestiona la escritura y lectura del fichero para el almacenamiento permanente de este programa.

**Métodos:**

- `public static void guardarData()`: método para guardar la data. Guarda toda la instancia de la clase controladora del backend (que implementa el patrón Singleton) en un solo archivo.

- `public static GestorRutas cargarData()`: método para cargar la data del único archivo: el que guarda la data de la última instancia guardada de la clase GestorRutas.



## Records importantes

### ParParadaRuta

Este es un record que guarda una parada y una ruta. Se utiliza para retonar las listas de las rutas óptimas. La ruta es la que se usa para llegar a la siguiente parada en la lista de la ruta óptima; por lo tanto, el último ParParadaRuta de las listas de rutas óptimas siempre tiene una ruta con valor `null`.

### RegistroDiscriminates

Record utilizado para guardar valores de discriminantes para la búsqueda de rutas óptimas. Puede ser creado basado en una ruta y un RegistroDiscriminates previo, o en dos RegistroDiscriminates. La primera opción es la más usada en el programa, útil para guardar el determinante total tras recorrer una ruta, y así ver si conviene tomar esa ruta; mientras que la otra solo se utiliza en Floyd-Warshall, ya que los discriminantes allí son más complejos, siendo que las comparaciones que hace es con rutas óptimas temporales totales, no simples rutas, lo cuál requiere un discriminante que combine los dos discrimianntes complejos de aquellas dos rutas óptimas temporales a comparar. El primer constructor también recibe unos valores booleanos para ajustar mejor la creación del registro de discriminantes, dependiendo si es para un origen (con todos los discriminantes en cero), o para un relleno (que necesita discriminantes "infinitos").

Este record guarda dichos discriminantes en un arreglo con tantos índices como opciones de preferencias hay, teniendo cada uno reservado para reservar un discriminante correspondiente a cada una de dichas preferencias, según el valor que tengan (para más información ver el apartado de Preferencias en la sección de **Enums importantes**).


## Enums importantes

### Preferencias

Este enum representa las diferentes preferencias (discriminantes) que se pueden tener a la hora de buscar una ruta óptima.

**Atributo importante:**

valor: este es el valor asociado a cada preferencia. Se utiliza en el sistema de multipreferencias de este programa, del que forma parte RegistroDiscriminante, y se usa para acceder a un índice específico de dicho registro de discriminantes. En concreto, este programa trabaja con arreglos de este enum organizados según las prioridades de las preferencias, por lo que se puede acceder con los valores de cada preferencia a los respectivos discriminantes en el orden que se debe, según esté organizado el arreglo de preferencias.
