/**
 * 
 */
package mx.randalf.codiceFiscale.tools;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;



/**
 * @author massi
 *
 */
public abstract class SqliteCore {

	protected Connection conn = null;
	
	/**
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public SqliteCore(String fileDb) throws FileNotFoundException, ClassNotFoundException, SQLException  {
		init(fileDb);
	}

	private void init(String fileDb) throws FileNotFoundException, ClassNotFoundException, SQLException{
		File fDb =null;
		boolean isInit = false;
		
		fDb = new File(fileDb);
	    try {
			Class.forName("org.sqlite.JDBC");
			isInit =(!fDb.exists());
			if (!fDb.getParentFile().exists()){
				if(!fDb.getParentFile().mkdirs()){
					throw new FileNotFoundException("Problemi nella creazione della cartella ["+fDb.getParentFile().getAbsolutePath()+"]");
				}
			}
			conn = DriverManager.getConnection("jdbc:sqlite:"+fDb.getAbsolutePath());
			if (isInit){
				initDb();
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		}
	}

	protected abstract void initDb() throws SQLException;

	protected String convert(Calendar cal){
		GregorianCalendar gc = null;
		
		gc = new GregorianCalendar();
		gc.setTimeInMillis(cal.getTimeInMillis());
		return convert(gc);
	}

	/**
	 * Metodo utilizzato per la conversione del GregorianCalendar in String
	 * 
	 * @param gc
	 * @return
	 */
	protected String convert(GregorianCalendar gc){
		DecimalFormat df6 = new DecimalFormat("000000");
		DecimalFormat df4 = new DecimalFormat("0000");
		DecimalFormat df2 = new DecimalFormat("00");
		String result = null;
		
		result = df4.format(gc.get(Calendar.YEAR))+"/"+
				df2.format(gc.get(Calendar.MONTH)+1)+"/"+
				df2.format(gc.get(Calendar.DAY_OF_MONTH))+" "+
				df2.format(gc.get(Calendar.HOUR_OF_DAY))+":"+
				df2.format(gc.get(Calendar.MINUTE))+":"+
				df2.format(gc.get(Calendar.SECOND))+"."+
				df6.format(gc.get(Calendar.MILLISECOND));
		return result;
	}
	public void disconnect() throws SQLException{
		conn.close();
	}
}
