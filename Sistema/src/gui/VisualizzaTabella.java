package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import database.Database;
import entit�.Elemento;
import entit�.Personale;

public class VisualizzaTabella extends JPanel implements Visualizzatore {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pannelloColonne;
	private JPanel pannelloDati;
	private ArrayList<String> colonne;
	private Font fontColonne;
	private Font fontDati;
	private Class<? extends Elemento> tipo;
	
	public VisualizzaTabella(Class<? extends Elemento> tipo) {
		
		super();
		
		this.tipo = tipo;
		
		pannelloColonne = new JPanel();
		pannelloDati = new JPanel();
		fontColonne = new Font("Arial", Font.BOLD, 20);
		fontDati = new Font("Arial", Font.PLAIN, 14);
		
		try {
			colonne = ottieniColonne(tipo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setLayout(new BorderLayout());
		
		pannelloColonne.setLayout(new GridLayout(1, colonne.size()));
		pannelloDati.setLayout(new GridLayout(0, colonne.size()));
		
		for (int i = 0; i < colonne.size(); i++) {
			String c = colonne.get(i);
			c = c.toUpperCase();
			JLabel colonna = new JLabel(c);
			colonna.setFont(fontColonne);
			pannelloColonne.add(colonna);
		}

		try {
			caricaPannelloDati(tipo);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.add(pannelloColonne, BorderLayout.NORTH);
		this.add(pannelloDati, BorderLayout.CENTER);
	}
	
	private ArrayList<String> ottieniColonne(Class<? extends Elemento> c) throws SQLException {
		
		ArrayList<String> colonne = new ArrayList<String>();
		Database db = null;
		try {
			db = new Database();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet rs = null;
		try {
//			db.usa(); //se lo metti d� errore "use statement is not supported to switch between databases"
			rs = db.eseguiQueryRitorno("SELECT column_name FROM information_schema.columns WHERE table_name = '" + c.getSimpleName() + "'");
			while (rs.next()) {
				colonne.add(rs.getString(1));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			rs.close();
		}
		return colonne;
	}
	
	/**
	 * NON usare questo metodo in modo pubblico.
	 * E' reso pubblico solo per consentire alla classe di essere conforme all'interfaccia Visualizzatore che implementa.
	 */
	public void caricaPannelloDati(Class<? extends Elemento> c) throws ClassNotFoundException, IOException {
		
		pannelloDati.removeAll();
		
		ArrayList<String> riga = null;
		Database elementi = null;
		ResultSet rs = null;
		
		try {
			elementi = new Database();
			String query = "SELECT * FROM " + c.getSimpleName();
			rs = elementi.eseguiQueryRitorno(query);
			while (rs.next()) {
				riga = new ArrayList<String>();
				for (int i = 0; i < colonne.size(); riga.add(rs.getString(i++ + 1)));
				for (int i = 0; i < riga.size(); i++) {
					JLabel dato = new JLabel(riga.get(i));
					dato.setFont(fontDati);
					pannelloDati.add(dato);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.paintAll(this.getGraphics());
	}
}
