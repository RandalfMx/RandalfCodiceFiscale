/**
 * 
 */
package mx.randalf.codiceFiscale.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import mx.randalf.codiceFiscale.exception.RandalfCodiceFiscaleExceltion;

import org.apache.log4j.Logger;

/**
 * @author massi
 *
 */
public class CodiceCatastale {

	private Logger log = Logger.getLogger(getClass());
	
	private static CodiceCatastaleDB comuni = null;

	/**
	 * @throws RandalfCodiceFiscaleExceltion 
	 * 
	 */
	public CodiceCatastale(File fileSqlite) throws RandalfCodiceFiscaleExceltion {
		load(CodiceCatastale.class.getResource("codici_catastali.txt"), fileSqlite);
	}

	/**
	 * @throws RandalfCodiceFiscaleExceltion 
	 * 
	 */
	public CodiceCatastale(URL codCatastale, File fileSqlite) throws RandalfCodiceFiscaleExceltion {
		load(codCatastale, fileSqlite);
	}

	private void load(URL codCatastale, File fileSqlite) throws RandalfCodiceFiscaleExceltion{
		BufferedReader br = null;
		FileReader fr = null;
		String line = null;
		String codice = null;
		String comune = null;
		String provincia = null;

		try {
			if (comuni == null){
				if (fileSqlite.exists()){
					comuni = new CodiceCatastaleDB(fileSqlite.getAbsolutePath());
				} else {
					comuni = new CodiceCatastaleDB(fileSqlite.getAbsolutePath());
					fr = new FileReader(codCatastale.getFile());
					br = new BufferedReader(fr);
					while((line = br.readLine())!= null){
						if (! line.startsWith("Page") &&
								! line.startsWith("oblazione")){
							line = line.trim();
							codice = line.substring(0, 4).trim();
							line = line.substring(4).trim();
							comune = line.substring(0, line.length()-2).trim();
							provincia = line.substring(line.length()-2).trim();
							comuni.insert(codice, comune.replace("'", "''"), provincia);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RandalfCodiceFiscaleExceltion(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RandalfCodiceFiscaleExceltion(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RandalfCodiceFiscaleExceltion(e.getMessage(), e);
		} finally {
			try {
				if (br != null){
					br.close();
				}
				if (fr != null){
					fr.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new RandalfCodiceFiscaleExceltion(e.getMessage(), e);
			}
		}
	}

	public List<String[]> findComuni(String comune) throws SQLException{
		return findComuni(comune, true);
	}

	public List<String[]> findComuni(String comune, boolean troncamento) throws SQLException{
		String provincia = null;
		int pos = -1;

		pos = comune.indexOf("(");
		if (pos >-1){
			provincia = comune.substring(pos+1, pos+3).trim();
			comune = comune.substring(0, pos);
		}
		return comuni.find(null, comune.trim(), provincia, troncamento);
	}
}
