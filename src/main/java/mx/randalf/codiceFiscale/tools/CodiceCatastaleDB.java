/**
 * 
 */
package mx.randalf.codiceFiscale.tools;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import javax.naming.ConfigurationException;


/**
 * @author massi
 *
 */
public class CodiceCatastaleDB extends SqliteCore {

	/**
	 * @param fileDb
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws ConfigurationException 
	 */
	public CodiceCatastaleDB(String fileDB) throws FileNotFoundException,
			ClassNotFoundException, SQLException {
		super(fileDB);
	}

	/* (non-Javadoc)
	 * @see it.bncf.magazzimiDigitali.databaseSchema.sqlite.SqliteCore#initDb()
	 */
	@Override
	protected void initDb() throws SQLException {
		Statement stmt = null;
		String sql  = null;

		try {
			stmt = conn.createStatement();
			sql = "CREATE TABLE CodiceCatastale " +
			               "(CODICE VARCHAR(5) PRIMARY KEY     NOT NULL," +
			               " COMUNE VARCHAR(255) NOT NULL, " + 
			               " PROVINCIA VARCHAR(2) NOT NULL " + 
			               " ); "+
			       "CREATE INDEX CodiceCatastale01 on CodiceCatastale(CODICE); "+
			       "CREATE INDEX CodiceCatastale02 on CodiceCatastale(COMUNE); "+
			       "CREATE INDEX CodiceCatastale03 on CodiceCatastale(PROVINCIA); "
			               ;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			throw e;
		}
	}
	
	public void insert(String codice, String comune, String provincia) throws SQLException{
		Statement stmt = null;
		String sql = null;
		
		try {
			stmt = conn.createStatement();

			sql = "INSERT INTO CodiceCatastale" +
					"(CODICE, COMUNE, PROVINCIA) " +
					"VALUES ('"+codice+"', '"+comune+"', '"+provincia+"')";
			if (stmt.executeUpdate(sql)==0){
				throw new SQLException("Riscontrato un problema nell'inserimento del record nella tabella");
				
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	public List<String[]> find(String codice, String comune, String provincia) throws SQLException{
		return find(codice, comune, provincia, true);
	}

	public List<String[]> find(String codice, String comune, String provincia, boolean troncamento ) throws SQLException{
		Vector<String[]> res = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		boolean where = false;
		
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM CodiceCatastale ";
			
			if (codice != null){
				sql += (where?"AND":"WHERE")+" CODICE = '"+codice+"' ";
				where = true;
			}
			if (comune != null){
				if (troncamento){
					sql += (where?"AND":"WHERE")+" COMUNE LIKE '%"+comune+"%' ";
				} else {
					sql += (where?"AND":"WHERE")+" COMUNE = '"+comune+"' ";
				}
				where = true;
			}
			if (provincia != null){
				sql += (where?"AND":"WHERE")+" PROVINCIA = '"+provincia+"' ";
				where = true;
			}
			rs = stmt.executeQuery( sql+";" );
			res = (Vector<String[]>) convert(rs);
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (rs != null){
				    rs.close();
				}
				if (stmt != null){
				    stmt.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	    return res;
	}

	/**
	 * Questo metodo viene utilzzato per convertore il RecordSet in lista di tipo MDFilesTmp
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	private List<String[]> convert(ResultSet rs) throws SQLException{
		Vector<String[]> res = null;
		String[] record = null;

		try {
			while ( rs.next() ) {
				if (res == null){
					res = new Vector<String[]>();
				}
				record = new String[3];
				record[0] = rs.getString("CODICE");
				record[1] = rs.getString("COMUNE");
				record[2] = rs.getString("PROVINCIA");
				res.add(record);
			}
		} catch (SQLException e) {
			throw e;
		}
		return res;
	}
}
