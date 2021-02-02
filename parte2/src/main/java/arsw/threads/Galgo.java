package arsw.threads;

/**
 * Un galgo que puede correr en un carril
 * 
 * @author rlopez
 * 
 */
public class Galgo extends Thread {
	private int paso;
	private Carril carril;
	RegistroLlegada regl;
	boolean inPause;
	public Galgo(Carril carril, String name, RegistroLlegada reg) {
		super(name);
		this.carril = carril;
		paso = 0;
		this.regl=reg;
		inPause=false;
	}
	public void corra() throws InterruptedException {
		while (paso < carril.size()) {
			if(!inPause) {
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
			else {
				pause();
			}
		}
	}
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
	public void setPause( boolean p) {
		inPause = p;
	}
	@Override
	public void run() {
		try {
			corra();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}