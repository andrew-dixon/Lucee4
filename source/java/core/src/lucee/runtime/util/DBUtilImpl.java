/**
 * Copyright (c) 2015, Lucee Assosication Switzerland. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package lucee.runtime.util;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;

import lucee.commons.sql.SQLUtil;
import lucee.runtime.PageContext;
import lucee.runtime.config.Config;
import lucee.runtime.config.ConfigImpl;
import lucee.runtime.config.ConfigWebImpl;
import lucee.runtime.db.DataSource;
import lucee.runtime.db.DataSourceUtil;
import lucee.runtime.db.DatasourceConnection;
import lucee.runtime.db.SQL;
import lucee.runtime.db.SQLCaster;
import lucee.runtime.db.SQLImpl;
import lucee.runtime.db.SQLItem;
import lucee.runtime.db.SQLItemImpl;
import lucee.runtime.engine.ThreadLocalPageContext;
import lucee.runtime.exp.PageException;
import lucee.runtime.type.Collection.Key;
import lucee.runtime.type.Query;
import lucee.runtime.type.util.QueryUtil;

public class DBUtilImpl implements DBUtil {

	@Override
	public Object toSqlType(SQLItem item) throws PageException {
		return SQLCaster.toSqlType(item);
	}
	
	@Override
	public void setValue(TimeZone tz, PreparedStatement stat,
			int parameterIndex, SQLItem item) throws PageException,SQLException {
		SQLCaster.setValue(ThreadLocalPageContext.get(),tz, stat, parameterIndex, item);
	}

	@Override
	public void setValue(PageContext pc,TimeZone tz, PreparedStatement stat,
			int parameterIndex, SQLItem item) throws PageException,SQLException {
		SQLCaster.setValue(pc,tz, stat, parameterIndex, item);
	}

	@Override
	public String toString(SQLItem item) {
		return SQLCaster.toString(item);
	}

	@Override
	public String toStringType(int type) throws PageException {
		return SQLCaster.toStringType(type);
	}

	@Override
	public int toSQLType(String strType) throws PageException {
		return SQLCaster.toSQLType(strType);
	}

	@Override
	public Blob toBlob(Connection conn, Object value) throws PageException, SQLException {
		return SQLUtil.toBlob(conn, value);
	}

	@Override
	public Clob toClob(Connection conn, Object value) throws PageException, SQLException {
		return SQLUtil.toClob(conn, value);
	}

	@Override
	public boolean isOracle(Connection conn) {
		return lucee.commons.sql.SQLUtil.isOracle(conn);
	}

	@Override
	public void closeSilent(Statement stat) {
		lucee.commons.sql.SQLUtil.closeEL(stat);
	}

	@Override
	public void closeSilent(Connection conn) {
		lucee.commons.sql.SQLUtil.closeEL(conn);
	}

	@Override
	public void closeSilent(ResultSet rs) {
		lucee.commons.sql.SQLUtil.closeEL(rs);
	}

	@Override
	public SQLItem toSQLItem(Object value, int type) {
		return new SQLItemImpl(value,type);
	}

	@Override
	public SQL toSQL(String sql, SQLItem[] items) {
		return new SQLImpl(sql,items);
	}

	@Override
	public void releaseDatasourceConnection(Config config, DatasourceConnection dc, boolean async) {
		((ConfigImpl)config).getDatasourceConnectionPool().releaseDatasourceConnection(config, dc, async);
	}

	@Override
	public DatasourceConnection getDatasourceConnection(PageContext pc,DataSource datasource, String user, String pass) throws PageException {
		return ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().getDatasourceConnection(pc, datasource, user, pass);
	}

	@Override
	public DatasourceConnection getDatasourceConnection(PageContext pc,String datasourceName, String user, String pass) throws PageException {
		return ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().getDatasourceConnection(pc, pc.getDataSource(datasourceName), user, pass);
	}

	@Override
	public String getDatabaseName(DatasourceConnection dc) throws SQLException {
		return DataSourceUtil.getDatabaseName(dc);
	}

	@Override
	public Key[] getColumnNames(Query qry) {
		return QueryUtil.getColumnNames(qry);
	}

	@Override
	public String getColumnName(ResultSetMetaData meta, int column) throws SQLException {
		return QueryUtil.getColumnName(meta, column);
	}

	@Override
	public Object getObject(ResultSet rs, int columnIndex, Class type) throws SQLException {
		return QueryUtil.getObject(rs, columnIndex, type);
	}

	@Override
	public Object getObject(ResultSet rs, String columnLabel, Class type) throws SQLException {
		return QueryUtil.getObject(rs, columnLabel, type);
	}
}