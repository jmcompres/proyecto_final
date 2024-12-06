Informe - Gestor de Paradas y Rutas
## Antes de leer
Este es el informe de este proyecto. Solo incluye la documentación para lo más importante, fuera de lo fácilmente entendible.

## Clases fundamentales importantes



### Clase Parada:

Esta es la clase que representa las paradas.


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

Esta es la clase que representa las rutas.

**Atributos especiales:**

- *id:* identificador único de la ruta, y clave para acceder a la ruta en el mapa de GestorRutas.

**Métodos importantes**

- `Public float getCostoNeto()`: retorna el costo por usar la ruta tras aplicarle el descuento al costoBruto








## Records importantes

### ParParadaRuta

Este es un record que guarda una parada y una ruta. Se utiliza para retonar las listas de las rutas óptimas. La ruta es la que se usa para llegar a la siguiente parada en la lista de la ruta óptima; por lo tanto, el último ParParadaRuta de las listas de rutas óptimas siempre tiene una ruta con valor `null`.

