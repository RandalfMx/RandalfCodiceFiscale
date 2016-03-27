/**
 * 
 */
package mx.randalf.codiceFiscale;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import mx.randalf.codiceFiscale.exception.RandalfCodiceFiscaleExceltion;
import mx.randalf.codiceFiscale.tools.CodiceCatastale;

/**
 * @author massi
 *
 */
public class RandalfCodiceFiscale {

	private Logger log = Logger.getLogger(getClass());

	private CodiceCatastale codiceCatastale = null;

	/**
	 * @throws RandalfCodiceFiscaleExceltion 
	 * 
	 */
	public RandalfCodiceFiscale(File fileSqlite) throws RandalfCodiceFiscaleExceltion {
		this.codiceCatastale = new CodiceCatastale(fileSqlite);
	}

	/**
	 * @throws RandalfCodiceFiscaleExceltion 
	 * 
	 */
	public RandalfCodiceFiscale(URL codiceCatastale, File fileSqlite) throws RandalfCodiceFiscaleExceltion {
		this.codiceCatastale = new CodiceCatastale(codiceCatastale, fileSqlite);
	}

	public List<String[]> findComuni(String comune) throws SQLException{
		return findComuni(comune, true);
	}

	public List<String[]> findComuni(String comune, boolean troncamento) throws SQLException{
		return codiceCatastale.findComuni(comune, troncamento);
	}

	public boolean valid(String cognome, String nome, GregorianCalendar dataNascita, String sesso,
			String comune, String codiceFiscale) throws RandalfCodiceFiscaleExceltion{
		Vector<String> codfiscali=null;
		boolean ris = false;

		try {
			codfiscali = genCodiceFiscale(cognome, nome, dataNascita, sesso, comune);
			for (int x=0; x<codfiscali.size(); x++){
				if (codfiscali.get(x).equals(codiceFiscale)){
					ris = true;
					break;
				}
			}
		} catch (RandalfCodiceFiscaleExceltion e) {
			throw e;
		}
		return ris;
	}

	public Vector<String> genCodiceFiscale(String cognome, String nome, GregorianCalendar dataNascita, String sesso,
			String comune) throws RandalfCodiceFiscaleExceltion{
		Vector<String> codFiscali = null;
		String codFiscale = null;
		String cNome = null;
		Vector<String> mese = null;
		DecimalFormat df2 = new DecimalFormat("00");
		List<String[]> comuni=null;

		try {
			mese = new Vector<String>();
			mese.add("A");
			mese.add("B");
			mese.add("C");
			mese.add("D");
			mese.add("E");
			mese.add("H");
			mese.add("L");
			mese.add("M");
			mese.add("P");
			mese.add("R");
			mese.add("S");
			mese.add("T");

			codFiscale = (read(cognome,false)+read(cognome,true)+"XXX").substring(0, 3);
			cNome = read(nome,false);
			if (cNome.length()>=4){
				codFiscale +=cNome.substring(0,1);
				codFiscale +=cNome.substring(2,4);
			} else {
				codFiscale += (cNome+read(nome,true)+"XXX").substring(0, 3);
			}
			
			codFiscale += Integer.toString(dataNascita.get(Calendar.YEAR)).subSequence(2, 4);
			codFiscale += mese.get(dataNascita.get(Calendar.MONTH));
			codFiscale += df2.format((dataNascita.get(Calendar.DAY_OF_MONTH)+(sesso.equalsIgnoreCase("m")?0:40)));

			comuni = findComuni(comune, false);
			if (comuni == null){
				throw new RandalfCodiceFiscaleExceltion("Comune mancante");
			} else if (comuni.size()==1){
				codFiscale += comuni.get(0)[0];
			} else {
				throw new RandalfCodiceFiscaleExceltion("Riscontrato comuni multipli");
			}

			codFiscali = new Vector<String>();
			codFiscali.add(codFiscale+genCarCont(codFiscale));
			for ( int x=codFiscale.length(); x>=0; x--){
				try{
					codFiscale = codFiscale.substring(0, x-1)+calcOmonimo(Integer.parseInt(codFiscale.substring(x-1, x)))+codFiscale.substring(x);
					codFiscali.add(codFiscale+genCarCont(codFiscale));
				} catch(Exception e){
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new RandalfCodiceFiscaleExceltion(e.getMessage(), e);
		} catch (RandalfCodiceFiscaleExceltion e) {
			throw e;
		}
		return codFiscali;
	}

	private String calcOmonimo(int num){
		Hashtable<Integer, String> codifica = null;
		
		codifica = new Hashtable<Integer, String>();

		codifica.put(0,"L");
		codifica.put(1,"M");
		codifica.put(2,"N");
		codifica.put(3,"P");
		codifica.put(4,"Q");
		codifica.put(5,"R");
		codifica.put(6,"S");
		codifica.put(7,"T");
		codifica.put(8,"U");
		codifica.put(9,"V");
		
		return codifica.get(num);
	}

	private String genCarCont(String testo){
		String pari = "";
		int nPari = 0;
		String disp = "";
		int nDisp = 0;
		int somma = 0;
		int resto = 0;
		boolean dispari = true;

		for (int x=0; x<testo.length(); x++){
			if (dispari){
				disp += testo.substring(x, x+1);
				dispari= false;
			}else{
				pari += testo.substring(x, x+1);
				dispari= true;
			}
		}

		nPari = calcPari(pari);
		nDisp = calcDisp(disp);
		
		somma = (nPari+nDisp);
		resto = somma%26;
		return calcResto(resto);
	}

	private String calcResto(int num){
		Hashtable<Integer, String> codifica = null;
		
		codifica = new Hashtable<Integer, String>();

		codifica.put(0,"A");
		codifica.put(7,"H");
		codifica.put(14,"O");
		codifica.put(21,"V");
		codifica.put(1,"B");
		codifica.put(8,"I");
		codifica.put(15,"P");
		codifica.put(22,"W");
		codifica.put(2,"C");
		codifica.put(9,"J");
		codifica.put(16,"Q");
		codifica.put(23,"X");
		codifica.put(3,"D");
		codifica.put(10,"K");
		codifica.put(17,"R");
		codifica.put(24,"Y");
		codifica.put(4,"E");
		codifica.put(11,"L");
		codifica.put(18,"S");
		codifica.put(25,"Z");
		codifica.put(5,"F");
		codifica.put(12,"M");
		codifica.put(19,"T");
		codifica.put(6,"G");
		codifica.put(13,"N");
		codifica.put(20,"U");
	
		
		return codifica.get(num);
	}
	
	private int calcDisp(String testo){
		int num = 0;
		Hashtable<String, Integer> codifica = null;
		
		codifica = new Hashtable<String, Integer>();
		
		codifica.put("0",1);	
		codifica.put("9",21);	
		codifica.put("I",19);	
		codifica.put("R",8);	
		codifica.put("1",0);	
		codifica.put("A",1);	
		codifica.put("J",21);	
		codifica.put("S",12);	
		codifica.put("2",5);	
		codifica.put("B",0);	
		codifica.put("K",2);	
		codifica.put("T",14);	
		codifica.put("3",7);	
		codifica.put("C",5);	
		codifica.put("L",4);	
		codifica.put("U",16);	
		codifica.put("4",9);	
		codifica.put("D",7);	
		codifica.put("M",18);	
		codifica.put("V",10);	
		codifica.put("5",13);	
		codifica.put("E",9);	
		codifica.put("N",20);	
		codifica.put("W",22);	
		codifica.put("6",15);	
		codifica.put("F",13);	
		codifica.put("O",11);	
		codifica.put("X",25);	
		codifica.put("7",17);	
		codifica.put("G",15);	
		codifica.put("P",3);	
		codifica.put("Y",24);	
		codifica.put("8",19);	
		codifica.put("H",17);	
		codifica.put("Q",6);	
		codifica.put("Z",23);	
	
		for (int x=0; x<testo.length(); x++){
			num += codifica.get(testo.substring(x,x+1));
		}
		
		return num;
	}

	private int calcPari(String testo){
		int num = 0;
		Hashtable<String, Integer> codifica = null;
		
		codifica = new Hashtable<String, Integer>();
		
		codifica.put("0",0);	
		codifica.put("1",1);
		codifica.put("2",2);
		codifica.put("3",3);
		codifica.put("4",4);
		codifica.put("5",5);
		codifica.put("6",6);
		codifica.put("7",7);
		codifica.put("8",8);
		codifica.put("9",9);
		codifica.put("A",0);
		codifica.put("B",1);
		codifica.put("C",2);
		codifica.put("D",3);
		codifica.put("E",4);
		codifica.put("F",5);
		codifica.put("G",6);
		codifica.put("H",7);
		codifica.put("I",8);
		codifica.put("J",9);
		codifica.put("K",10);
		codifica.put("L",11);
		codifica.put("M",12);
		codifica.put("N",13);
		codifica.put("O",14);
		codifica.put("P",15);
		codifica.put("Q",16);
		codifica.put("R",17);
		codifica.put("S",18);
		codifica.put("T",19);
		codifica.put("U",20);
		codifica.put("V",21);
		codifica.put("W",22);
		codifica.put("X",23);
		codifica.put("Y",24);
		codifica.put("Z",25);
	
		for (int x=0; x<testo.length(); x++){
			num += codifica.get(testo.substring(x,x+1));
		}
		
		return num;
	}

	private String read(String parola, boolean vocali){
		String ris = "";
		String lettera = "";
		
		parola = parola.replace(" ", "");
		parola = parola.replace("'", "");
		parola = parola.toUpperCase();
		while (parola.length()>0){
			lettera = parola.substring(0, 1);
			parola = parola.substring(1);
			if (lettera.equals("A") ||
					lettera.equals("E") ||
					lettera.equals("I") ||
					lettera.equals("O") ||
					lettera.equals("U")){
				if (vocali){
					ris +=lettera;
				}
			} else {
				if (!vocali){
					ris +=lettera;
				}
			}
		}
		return ris;
	}
}
