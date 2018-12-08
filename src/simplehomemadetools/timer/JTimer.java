package simplehomemadetools.timer;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JTimer extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Swing attributes */

	private JLabel labelTemporizador;
	private JLabel labelMilesimos;

	public java.awt.List componenteGraficoMarcas;
	
	//public JButton btnPrincipal, btnMaisUmSegundo, btnMaisUmMinuto, btnMaisUmaHora;

	private Font fonteTemporizador = new Font("Arial", Font.BOLD, 26);
	private Font fonteMilesimos = new Font("Arial", Font.BOLD, 14);
	private Font fonteComponenteGraficoMarcas = new Font("Dialog", Font.BOLD, 16);

	/* Timer attributes */

	private boolean contando = false;
	
	private java.util.List<String> marcas = new ArrayList<>();

	private int milesimos = 0;
	private byte segundos = 0, minutos = 0, horas = 0;

	private ScheduledExecutorService repetidorDeInstrucoes;

	//private Timer timer;
	
	
	/* CONSTANTES */

	// Label constants
	public static final String FORMATO_DE_MILESIMOS = ".%03d";
	public static final String FORMATO_DE_RELOGIO = "%02dh%02dm%02ds";

	// Mode constants
	public static final int MODO_CRONOMETRO = -1;
	public static final int MODO_TEMPORIZADOR = 0;

	// Scope constants
	public static final int APENAS_ARRAYLIST = 1;
	public static final int ARRAYLIST_E_AWTLIST = 2;

	/**
	 * Inicia a contagem de tempo caso não haja nenhuma em execução.
	 * 
	 * @return contando - indica o estado do timer
	 */
	public boolean iniciar() {
		if (!contando) {
			repetidorDeInstrucoes = Executors.newScheduledThreadPool(1);

			final Runnable contador = new Runnable() {
				@Override
				public void run() {
					if (milesimos < 999) {
						milesimos++;
						labelMilesimos.setText(String.format(FORMATO_DE_MILESIMOS, milesimos));
					} else if (segundos < 59) {
						segundos++;
						milesimos = 0;
						labelTemporizador.setText(String.format(FORMATO_DE_RELOGIO, horas, minutos, segundos));
					} else if (minutos < 59) {
						minutos++;
						milesimos = 0;
						segundos = 0;
						labelTemporizador.setText(String.format(FORMATO_DE_RELOGIO, horas, minutos, segundos));
					} else {
						horas++;
						milesimos = 0;
						segundos = 0;
						minutos = 0;
						labelTemporizador.setText(String.format(FORMATO_DE_RELOGIO, horas, minutos, segundos));
					}

				}
			};
			repetidorDeInstrucoes.scheduleAtFixedRate(contador, 0, 1, TimeUnit.MILLISECONDS);

			contando = true;
		}

		return contando;
	}

	/**
	 * Interrompe a contagem
	 * 
	 * @return contando - indica o estado do timer
	 */
	public boolean parar() {
		Runnable finalizador = new Runnable() {

			@Override
			public void run() {
				// Instrução para interromper o repetidor
				// Precisa ser agendada pelo próprio repetidor
				repetidorDeInstrucoes.shutdown();
			}
		};

		// Agenda uma função autodestrutiva para 0 segundos após a chamada do método
		repetidorDeInstrucoes.schedule(finalizador, 0, TimeUnit.SECONDS);

		contando = false;

		return contando;
	}

	/**
	 * Limpa as marcas de acordo com o escopo desejado
	 * 
	 * @param escopo precisa ser umas das constantes JTimer.APENAS_ARRAYLIST ou
	 *               JTimer.ARRAYLIST_E_AWTLIST
	 */
	public void zerar(int escopo) {
		marcas.clear();
		if (escopo == ARRAYLIST_E_AWTLIST) {
			componenteGraficoMarcas.removeAll();
		}

	}

	/**
	 * Armazena uma marca na lista de marcas e retorna a lista.
	 * 
	 * @return lista de marcasprecisa ser umas das constantes
	 *         JTimer.APENAS_ARRAYLIST ou JTimer.ARRAYLIST_E_AWTLIST
	 * @param escopo
	 */
	public java.util.List<String> marcar(int escopo) {

		String tempoUtil = labelTemporizador.getText();
		String tempoNaoTaoUtil = labelMilesimos.getText();

		String marca = tempoUtil + tempoNaoTaoUtil;

		if (escopo == APENAS_ARRAYLIST)
			marcas.add(marca);
		else if (escopo == ARRAYLIST_E_AWTLIST) {
			marcas.add(tempoUtil + tempoNaoTaoUtil);
			componenteGraficoMarcas.add(marca);
		}

		return marcas;
	}

	public void alterarModo(int modo) {

		if (modo == MODO_CRONOMETRO)
			alterarParaCronometro();
		else if (modo == MODO_TEMPORIZADOR)
			alterarParaTemporizador();

	}

	private boolean alterarParaCronometro() {
		boolean estavaContando = false;
		
		if (contando) {
			parar();
			estavaContando = true;
		}
		
	
		
		return estavaContando;
	}
	
	private boolean alterarParaTemporizador() {
		boolean estavaContando = false;
		
		if (contando) {
			parar();
			estavaContando = true;
		}
		
	
		
		return estavaContando;
	}

	public JTimer() {
		/* CONSTRUÇÃO E DEFINIÇÃO DE COMPONENTES */

		setLayout(new FlowLayout());

		labelTemporizador = new JLabel("00h00m00s");
		labelTemporizador.setFont(fonteTemporizador);
		add(labelTemporizador);

		labelMilesimos = new JLabel(".000");
		labelMilesimos.setFont(fonteMilesimos);
		add(labelMilesimos);

		componenteGraficoMarcas = new List();
		componenteGraficoMarcas.setFont(fonteComponenteGraficoMarcas);
		componenteGraficoMarcas.setFocusable(false);
		add(componenteGraficoMarcas);

		JFrame frame = new JFrame();
		frame.setSize(250, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_SPACE) {
					if (JTimer.this.isContando())
						parar();
					else
						iniciar();
				} else if (keyCode == KeyEvent.VK_M) {
					marcar(ARRAYLIST_E_AWTLIST);
				} else if (keyCode == KeyEvent.VK_0) {
					zerar(ARRAYLIST_E_AWTLIST);
				}
			}
		});
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		JTimer jtimer = new JTimer();
		jtimer.iniciar();
	}

	public JLabel getLabelTemporizador() {
		return labelTemporizador;
	}

	public void setLabelTemporizador(JLabel labelTemporizador) {
		this.labelTemporizador = labelTemporizador;
	}

	public JLabel getLabelMilesimos() {
		return labelMilesimos;
	}

	public void setLabelMilesimos(JLabel labelMilesimos) {
		this.labelMilesimos = labelMilesimos;
	}

	public Font getFonteTemporizador() {
		return fonteTemporizador;
	}

	public void setFonteTemporizador(Font fonteTemporizador) {
		this.fonteTemporizador = fonteTemporizador;
	}

	public Font getFonteMilesimos() {
		return fonteMilesimos;
	}

	public void setFonteMilesimos(Font fonteMilesimos) {
		this.fonteMilesimos = fonteMilesimos;
	}

	public boolean isContando() {
		return contando;
	}

	public java.awt.List AtualizarMarcasAwt() {

		componenteGraficoMarcas.removeAll();

		for (String marca : marcas) {
			componenteGraficoMarcas.add(marca);
		}

		return componenteGraficoMarcas;
	}

	public java.util.List<String> getMarcas() {
		return marcas;
	}

}
