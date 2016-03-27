/**
 * 
 */
package mx.randalf.codiceFiscale;

import java.io.File;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import mx.randalf.codiceFiscale.exception.RandalfCodiceFiscaleExceltion;

/**
 * @author massi
 * 
 */
public class RandalfCodiceFiscaleTest {

	/**
	 * 
	 */
	public RandalfCodiceFiscaleTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RandalfCodiceFiscale codiceFiscale = null;
		List<String[]> comuni = null;

		try {
			codiceFiscale = new RandalfCodiceFiscale(
					new File("/Users/massi/Desktop/Lavoro/Sorgenti/ACCVC/ACCVC/ACCVCGestionale/sqlite/CodiceFiscale.db"));
			
			comuni = codiceFiscale.findComuni("prato");
			for (int x=0; x<comuni.size(); x++){
				for(int y=0; y<comuni.get(x).length; y++){
					System.out.print(comuni.get(x)[y]+"\t");
				}
				System.out.println();
			}
			System.out.println(codiceFiscale.genCodiceFiscale("Randazzo",
					"Massimiliano", new GregorianCalendar(1974, 06, 19), "m",
					"FIRENZE (FI)"));
			System.out.println(codiceFiscale.valid("Randazzo",
					"Massimiliano", new GregorianCalendar(1974, 06, 19), "m",
					"FIRENZE (FI)","RNDMSM74L19D6MNF")+" - "+codiceFiscale.valid("Randazzo",
							"Massimiliano", new GregorianCalendar(1974, 06, 19), "m",
							"FIRENZE (FI)","RNDMSM74L19D6MNX"));
			System.out.println(codiceFiscale.genCodiceFiscale("Randazzo",
					"Alessandra", new GregorianCalendar(1979, 06, 24), "f",
					"FIRENZE (FI)"));
		} catch (RandalfCodiceFiscaleExceltion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
