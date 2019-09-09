package br.com.claro.hbase.exposicao.service;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.claro.hbase.exposicao.conf.HBaseConfig;



@Component
public class HbaseService {

	@Value("${hbase.tabela}")
	private String nomeTabela;
	
	@Value("${kerberos.master.principal}")
	private String kerberosMasterPrincipal;
	
	@Value("${kerberos.user.keytab}")
	private String kerberosUserKeyTab;
	
	@Value("${kerberos.security}")
	private boolean isKerberos;
	
	@Autowired
	private HBaseConfig hBaseConfig;
	
	
	private static Connection connection;
	
	private UserGroupInformation ugi;
	
	private void iniciaConexao() throws IOException{

		if( connection == null || connection.isClosed() ){
			Configuration conf = hBaseConfig.ini();
			UserGroupInformation.setConfiguration(conf);   
			connection = ConnectionFactory.createConnection(conf);
		}
		
		if(this.isKerberos) {
			if( ugi == null || ugi.isInitialized() ) {
				ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(kerberosMasterPrincipal, kerberosUserKeyTab);
			}else {
				ugi.reloginFromKeytab();
			}
		}
	}
	
	protected Table getTabela() throws Exception{
		this.iniciaConexao();
		return connection.getTable(TableName.valueOf(nomeTabela));
	}
	
	protected String getNomeTabela() {
		return this.nomeTabela;
	}
	
	protected void limpaConexao() {
		connection = null;
	}
}
