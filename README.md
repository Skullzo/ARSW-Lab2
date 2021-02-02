
### Escuela Colombiana de Ingeniería

### Arquitecturas de Software – ARSW
## Laboratorio Programación concurrente, condiciones de carrera, esquemas de sincronización, colecciones sincronizadas y concurrentes - Caso Dogs Race

### Descripción:
Este ejercicio tiene como fin que el estudiante conozca y aplique conceptos propios de la programación concurrente.

### Parte I 
Antes de terminar la clase.

Creación, puesta en marcha y coordinación de hilos.

1. Revise el programa “primos concurrentes” (en la carpeta parte1), dispuesto en el paquete edu.eci.arsw.primefinder. Este es un programa que calcula los números primos entre dos intervalos, distribuyendo la búsqueda de los mismos entre hilos independientes. Por ahora, tiene un único hilo de ejecución que busca los primos entre 0 y 30.000.000. Ejecútelo, abra el administrador de procesos del sistema operativo, y verifique cuantos núcleos son usados por el mismo.

**Luego de Ejecutar la clase ```Main``` del programa, al abrir Java VisualVM, podemos observar que el consumo de recursos es muy alto, como se ve en la gráfica ```CPU usage``` de la siguiente imagen, al principio el programa consumió mucho porcentaje (casi del 50%) de la CPU, dando como resultado un consumo muy alto de recursos. Asimismo, se observa también en la gráfica de ```Threads``` que el programa fue ejecutado con 1 solo hilo.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte1.1VisualVM.PNG)

**Luego de abrir el Administrador de Procesos del sistema operativo, se demuestra que el consumo de CPU fue considerablemente alto, consumiendo los 4 núcleos del computador que se dispuso para realizar el experimento, como se puede ver a continuación.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/NumeroNucleos.jpeg)

2. Modifique el programa para que, en lugar de resolver el problema con un solo hilo, lo haga con tres, donde cada uno de éstos hará la tarcera parte del problema original. Verifique nuevamente el funcionamiento, y nuevamente revise el uso de los núcleos del equipo.

**Primero, modificamos el programa de la siguiente forma, implementando dos hilos demás, y cambiando el rango de los mismos, siendo ahora, el primer hilo ```pft1``` que encuentra los números primos desde el número 0 al número 10000000, el segundo hilo ```pft2``` que encuentra los números primos desde el número 10000000 al número 20000000 y el tercer hilo ```pft3``` que encuentra los números primos desde el número 20000000 al número 30000000, quedando la clase ```Main``` de la siguiente forma.**

```java
public class Main {
	public static void main(String[] args) {
		PrimeFinderThread pft1=new PrimeFinderThread(0, 10000000);
		PrimeFinderThread pft2=new PrimeFinderThread(10000000, 20000000);
		PrimeFinderThread pft3=new PrimeFinderThread(20000000, 30000000);
		pft1.start();
		pft2.start();
		pft3.start();
	}
}
```

**Luego de abrir nuevamente Java VisualVM, se evidencia un menor consumo de recursos, principalmente de CPU, ya que como se puede ver en la gráfica ```CPU usage```, consume un máximo de 30% de porcentaje de CPU al ejecutar el programa, representando así un mejor rendimiento ya que el programa encuentra los números primos en un menor tiempo. También en la gráfica ```Threads```, se evidencia el consumo de los 3 hilos implementados en el código mostrado con anterioridad, demostrando el funcionamiento correcto del código en cuanto al número de hilos y el porcentaje de CPU consumido comparado con el punto anterior.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte1.2VisualVM.PNG)

3. Lo que se le ha pedido es: debe modificar la aplicación de manera que cuando hayan transcurrido 5 segundos desde que se inició la ejecución, se detengan todos los hilos y se muestre el número de primos encontrados hasta el momento. Luego, se debe esperar a que el usuario presione ENTER para reanudar la ejecución de los mismo.

**Para realizar esta parte, primero se importaron las librerías ```java.io.*``` y ```java.util.*```, para poder implementar ```resume()``` y ```suspend()```, los cuales serán ejecutados dentro de un Timer de 5 segundos (5000 milisegundos), para luego detener todos los hilos con el ```suspend()```, y reanudarlos luego de realizar la lectura del salto de línea registrado por el teclado luego de presionar ENTER, usando ```BufferedReader```, para así reanudar todos los hilos y seguir con la ejecución del programa. El código de esta parte quedó de la siguiente forma.**

```java
public class Main {
	public static void main(String[] args) {
		PrimeFinderThread pft1=new PrimeFinderThread(0, 10000000);
		PrimeFinderThread pft2=new PrimeFinderThread(10000000, 20000000);
		PrimeFinderThread pft3=new PrimeFinderThread(20000000, 30000000);
		pft1.start();
		pft2.start();
		pft3.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		new Timer().schedule( 
		        new TimerTask() {
		            @Override
		            public void run() {
		                try {
		                	pft1.suspend();
							pft2.suspend();
							pft3.suspend();
							while(br.read() != '\n') {
								br.read();
							}
							pft1.resume();
							pft2.resume();
							pft3.resume();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        },5000);
	}
}
```

### Parte II 


Para este ejercicio se va a trabajar con un simulador de carreras de galgos (carpeta parte2), cuya representación gráfica corresponde a la siguiente figura:

![](./img/media/image1.png)

En la simulación, todos los galgos tienen la misma velocidad (a nivel de programación), por lo que el galgo ganador será aquel que (por cuestiones del azar) haya sido más beneficiado por el *scheduling* del
procesador (es decir, al que más ciclos de CPU se le haya otorgado durante la carrera). El modelo de la aplicación es el siguiente:

![](./img/media/image2.png)

Como se observa, los galgos son objetos ‘hilo’ (Thread), y el avance de los mismos es visualizado en la clase Canodromo, que es básicamente un formulario Swing. Todos los galgos (por defecto son 17 galgos corriendo en una pista de 100 metros) comparten el acceso a un objeto de tipo
RegistroLLegada. Cuando un galgo llega a la meta, accede al contador ubicado en dicho objeto (cuyo valor inicial es 1), y toma dicho valor como su posición de llegada, y luego lo incrementa en 1. El galgo que
logre tomar el ‘1’ será el ganador.

Al iniciar la aplicación, hay un primer error evidente: los resultados (total recorrido y número del galgo ganador) son mostrados antes de que finalice la carrera como tal. Sin embargo, es posible que una vez corregido esto, haya más inconsistencias causadas por la presencia de condiciones de carrera.

**Luego de iniciar la aplicación, se ve claramente el error de los resultados. Al realizar clic en ```Start```, el aviso emergente que muestra los resultados que debería salir al finalizar la carrera para retornar el ganador de la carrera, se muestra justo al iniciar la carrera, lo que muestra una clara inconsistencia de la aplicación al retornar el ganador cuando la carrera aún no ha empezado, como se ve a continuación.**
	
![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte2.PNG)

### Parte III

1.  Corrija la aplicación para que el aviso de resultados se muestre
    sólo cuando la ejecución de todos los hilos ‘galgo’ haya finalizado.
    Para esto tenga en cuenta:

    a.  La acción de iniciar la carrera y mostrar los resultados se realiza a partir de la línea 38 de MainCanodromo.

    b.  Puede utilizarse el método join() de la clase Thread para sincronizar el hilo que inicia la carrera, con la finalización de los hilos de los galgos.
    
**Para corregir la aplicación, para que el aviso de resultados muestre sólo cuando la ejecución de todos los hilos ‘galgo’ haya finalizado, se utilizó método ```join()``` de la clase Thread del método Main, con la cual se sincronizó el hilo que inicia la carrera, con la finalización de los hilos de los galgos, utilizando un booleano encargado de mostrar el aviso de resultados solamente cuando los hilos finalizann, como se muestra en el siguiente código.**

```java
public static void main(String[] args) {
        can = new Canodromo(17, 100);
        galgos = new Galgo[can.getNumCarriles()];
        can.setVisible(true);
        //Acción del botón start
        can.setStartAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
			//como acción, se crea un nuevo hilo que cree los hilos
                        //'galgos', los pone a correr, y luego muestra los resultados.
                        //La acción del botón se realiza en un hilo aparte para evitar
                        //bloquear la interfaz gráfica.
                        ((JButton) e.getSource()).setEnabled(false);
                        new Thread() {
                            public void run() {
                                for (int i = 0; i < can.getNumCarriles(); i++) {
                                    //crea los hilos 'galgos'
                                    galgos[i] = new Galgo(can.getCarril(i), "" + i, reg);
                                    //inicia los hilos
                                    galgos[i].start();
                                }
                                boolean var = true;
                                for (Galgo j : galgos) {
                                	try {
                        			j.join();
                        			if (var) {
                        				can.winnerDialog(reg.getGanador(),reg.getUltimaPosicionAlcanzada() - 1); 
                        				System.out.println("El ganador fue:" + reg.getGanador());
                        				var = false;
                        			}
                        		} catch (InterruptedException e1) {
                        			e1.printStackTrace();
                        	        }
                                }
                            }
                        }.start();
                    }
                }
        );
```

**Para comprobar el funcionamiento correcto del código, se ejecutó desde el ```main``` el código, mostrando primero la carrera, como se ve a continuación.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte3.1.1.PNG)

**Luego de que la carrera ha finalizado, se muestra el respectivo aviso del ganador, como se ve a continuación.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte3.1.2.PNG)

**Finalmente, el programa retorna las posiciones de la carrera en la Consola de Java de la siguiente forma.**

```
El galgo 1 llego en la posicion 1
El galgo 0 llego en la posicion 2
El galgo 15 llego en la posicion 2
El galgo 9 llego en la posicion 3
El galgo 10 llego en la posicion 4
El galgo 13 llego en la posicion 5
El galgo 2 llego en la posicion 6
El galgo 5 llego en la posicion 8
El galgo 7 llego en la posicion 7
El galgo 6 llego en la posicion 9
El galgo 8 llego en la posicion 12
El galgo 11 llego en la posicion 12
El galgo 14 llego en la posicion 13
El galgo 3 llego en la posicion 11
El galgo 16 llego en la posicion 10
El galgo 4 llego en la posicion 14
El galgo 12 llego en la posicion 15
El ganador fue:1
```

2.  Una vez corregido el problema inicial, corra la aplicación varias
    veces, e identifique las inconsistencias en los resultados de las
    mismas viendo el ‘ranking’ mostrado en consola (algunas veces
    podrían salir resultados válidos, pero en otros se pueden presentar
    dichas inconsistencias). A partir de esto, identifique las regiones
    críticas () del programa.

**Luego de corregir el problema inicial de mostrar el aviso del ganador luego de que finalizara la carrera, al realizar la primera prueba, puesta en el numeral anterior, al retornar las posiciones de la carrera en la Consola de Java, se ve que las posiciones se repiten, como fue en el caso del galgo 8 y del galgo 11, como se ve en las líneas 2 y 3 respectivamente del siguiente fragmento de consola de Java. Ésta inconsistencia fue identificada en el momento justo de realizar la corrección parcial del programa, se identificaron al momento de acceder a las posiciones como las regiones críticas principales.**

```
El galgo 6 llego en la posicion 9
El galgo 8 llego en la posicion 12
El galgo 11 llego en la posicion 12
El galgo 14 llego en la posicion 13
```

3.  Utilice un mecanismo de sincronización para garantizar que a dichas
    regiones críticas sólo acceda un hilo a la vez. Verifique los
    resultados.
    
**Luego de realizar la respectiva identificación de las regiones críticas del programa, se implementa ```synchronized``` en la clase ```Galgo``` justamente en el método ```corra()``` para realizar la respectiva identificación y posteriormente corrección del código de la siguiente forma, evitando con ```synchronized``` que las posiciones de los galgos se repitieran, solucionando así el problema realizando la siguiente modificación del código.**

```java
public void corra() throws InterruptedException {
		while (paso < carril.size()) {			
			Thread.sleep(100);
			carril.setPasoOn(paso++);
			carril.displayPasos(paso);
			if (paso == carril.size()) {						
				carril.finish();
				synchronized (regl){
					int ubicacion=regl.getUltimaPosicionAlcanzada();
					regl.setUltimaPosicionAlcanzada(ubicacion+1);
					System.out.println("El galgo "+this.getName()+" llego en la posicion "+ubicacion);
					if (ubicacion==1){
						regl.setGanador(this.getName());
					}
				}
			}
		}
	}
```

**Para comprobar que la solución planteada ha servido, primero se ejecuta la aplicación de la siguiente forma.**

![img](https://github.com/Skullzo/ARSW-Lab2/blob/main/img/media/Parte3.3.1.PNG)

**Luego se procede a ver la Consola de Java. Como se puede observar a continuación, usando ```synchronized```, la aplicación retorna en consola las posiciones en órden, asegurando que no se repite ninguna.**

```
El galgo 8 llego en la posicion 1
El galgo 13 llego en la posicion 2
El galgo 1 llego en la posicion 3
El galgo 16 llego en la posicion 4
El galgo 2 llego en la posicion 5
El galgo 7 llego en la posicion 6
El galgo 15 llego en la posicion 7
El galgo 4 llego en la posicion 8
El galgo 9 llego en la posicion 9
El galgo 11 llego en la posicion 10
El galgo 12 llego en la posicion 11
El galgo 6 llego en la posicion 12
El galgo 0 llego en la posicion 13
El galgo 5 llego en la posicion 14
El galgo 10 llego en la posicion 15
El galgo 3 llego en la posicion 16
El galgo 14 llego en la posicion 17
El ganador fue:8
```

4.  Implemente las funcionalidades de pausa y continuar. Con estas,
    cuando se haga clic en ‘Stop’, todos los hilos de los galgos
    deberían dormirse, y cuando se haga clic en ‘Continue’ los mismos
    deberían despertarse y continuar con la carrera. Diseñe una solución que permita hacer esto utilizando los mecanismos de sincronización con las primitivas de los Locks provistos por el lenguaje (wait y notifyAll).
    
**Para realizar la implementación de las funcionalidades de pausar y continuar, en la cual se habilitan los botones ```Stop``` y ```Continue``` para así dormir y despertar los hilos de los galgos respctivamente, se optó por modificar el código en la clase ```MainCanodromo```, precisamente en los métodos que implementaban esta funcionalidad pero estaban erróneamente implementados, que son ```can.setStopAction``` y ```can.setContinueAction``` respectivamente. Para el correcto funcionamiento de los mismos, se implementó ```setPause()``` en ambos y ```notifyAll()``` en ```can.setContinueAction```, quedando el código de la siguiente forma.**

```java
can.setStopAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (Galgo j : galgos) {
                        	j.setPause(true);
                        }
                        System.out.println("Carrera pausada!");
                    }
                }
        );

can.setContinueAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	synchronized (reg) {
                    		reg.notifyAll();
                    		for (Galgo j : galgos) {
                            	j.setPause(false);
                            }
						}
                        System.out.println("Carrera reanudada!");
                    }
                }
        );
```

**Adicionalmente se crearon métodos para poder que sirviera ```Stop``` y ```Continue```, en la clase ```MainCanodromo``` se creó el método ```RegistroLlegada``` que gestiona el monitoreo del registro de la llegada. Asimismo se adicionaron métodos a la clase ```Galgo``` como ```setPause``` que se encarga de llevar a cabo la pausa o ```Stop``` de la aplicación estableciendo un booleano, y el método ```pause``` que también se encarga de gestionar la pausa de la carrera en cuestión.**

**Método ```RegistroLlegada``` de la clase ```MainCanodromo```.**

```java
public static RegistroLlegada getMonitor(){
    	return reg;
}
```

**Método ```setPause``` de la clase ```Galgo```.**

```java
public void setPause( boolean p) {
	inPause = p;
}
```

**Método ```pause``` de la clase ```Galgo```.**

```java
public void pause() {
		if (inPause) {
			synchronized (MainCanodromo.getMonitor()) {
				try {
					MainCanodromo.getMonitor().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}
	}
	inPause=false;
}
```

**Para probar que la aplicación sirve, primero se presiona clic sobre el botón ```Stop``` para comprobar que el programa se detiene. Como se puede ver en la siguiente imágen, el programa se detiene correctamente.**

Imagen

**Luego, al realizar clic en el botón ```Continue```, se observa que la aplicación se reanuda, reanudando la carrera.**

Imagen

**Finalmente, se observa que la aplicación se acaba luego de realizar clic en el botón ```Continue```, mostrando el aviso del galgo ganador.**

Imagen

**Al realizar la respectiva revisión en la consola de Java, se observa que se muestran los mensajes de que la carrera ha sido pausada y reanudada respectivamente, probando el funcionamiento correcto del código luego de realizar las implementaciones descritas anteriormente.**

```
Carrera pausada!
Carrera reanudada!
Carrera pausada!
Carrera reanudada!
Carrera pausada!
Carrera reanudada!
Carrera pausada!
Carrera reanudada!
El galgo 14 llego en la posicion 1
El galgo 3 llego en la posicion 2
El galgo 6 llego en la posicion 3
El galgo 7 llego en la posicion 4
El galgo 16 llego en la posicion 5
El galgo 13 llego en la posicion 6
El galgo 5 llego en la posicion 7
El galgo 12 llego en la posicion 8
El galgo 4 llego en la posicion 9
El galgo 10 llego en la posicion 10
El galgo 0 llego en la posicion 11
El galgo 2 llego en la posicion 12
El galgo 8 llego en la posicion 13
El galgo 9 llego en la posicion 14
El galgo 1 llego en la posicion 15
El galgo 11 llego en la posicion 16
El galgo 15 llego en la posicion 17
El ganador fue:14
```

## Autores
[Alejandro Toro Daza](https://github.com/Skullzo)

[David Fernando Rivera Vargas](https://github.com/DavidRiveraRvD)
## Licencia
Licencia bajo la [GNU General Public License](https://github.com/Skullzo/ARSW-Lab2/blob/main/LICENSE).
