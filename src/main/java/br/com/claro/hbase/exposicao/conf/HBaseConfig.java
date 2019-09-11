package br.com.claro.hbase.exposicao.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class HBaseConfig {

	private Configuration config;
	
	@Value("${hbase.zk.host}")
	private String hostZk;
	
	@Value("${hbase.zk.port}")
	private String portaZk;
	
	@Value("${kerberos.security}")
	private boolean isKerberos;
	
	@Value("${kerberos.master.principal}")
	private String kerberosMasterPrincipal;
	
	@Value("${kerberos.regionserver.principal}")
	private String kerberosRegionserverPrincipal;
	
	@Value("${kerberos.rpc.protection}")
	private String kerberosRpcProtection;
	
	@Value("${kerberos.key.tab}")
	private String keyTab;
	
	@Value("${hbase.cluster.distributed}")
	private boolean isHbaseClusterDistributed;
	
	@Value("${kerberos.debug}")
	private boolean isKerberosDebug;
	
	public Configuration ini(){
		
		if(config == null){
			// Zookeeper quorum
			this.config = HBaseConfiguration.create();
			this.config.set("hbase.zookeeper.quorum", hostZk);
			this.config.set("hbase.zookeeper.property.clientPort", portaZk);
			
			if(this.isKerberos) {
				
				this.config.set("hbase.cluster.distributed", String.valueOf(isHbaseClusterDistributed));
				this.config.set("hbase.rpc.protection", kerberosRpcProtection); //verifique esta configuração no lado do HBase
				
				this.config.set("hadoop.security.authentication", "kerberos");
				this.config.set("hbase.security.authentication", "kerberos");
				
				if( kerberosMasterPrincipal != null && !"".equals(kerberosMasterPrincipal.trim()) ) {
					this.config.set("hbase.regionserver.kerberos.principal", kerberosMasterPrincipal); //qual o principal do mestre / região. uso de servidores
				}
				if( kerberosRegionserverPrincipal != null && !"".equals(kerberosRegionserverPrincipal.trim()) ) {
					this.config.set("hbase.master.kerberos.principal", kerberosRegionserverPrincipal );// isso é necessário mesmo se você se conectar através de rpc / zookeeper
				}
				if( keyTab != null && !"".equals(keyTab.trim()) ) {
					this.config.set("hbase.regionserver.keytab.file", keyTab);
					this.config.set("hbase.master.keytab.file", keyTab);
				}

				System.setProperty("sun.security.krb5.debug", String.valueOf(isKerberosDebug));

			}
		}
		
		return config;
	}
}

