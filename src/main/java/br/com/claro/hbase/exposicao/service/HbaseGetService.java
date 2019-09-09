package br.com.claro.hbase.exposicao.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.claro.hbase.exposicao.enums.MensagemResponseEnum;
import br.com.claro.hbase.exposicao.vo.FamiliaVo;
import br.com.claro.hbase.exposicao.vo.GetResponseVo;

@Service
public class HbaseGetService extends HbaseService{

	private static final Logger logger = LoggerFactory.getLogger(HbaseGetService.class);	
	
	public GetResponseVo get(String id){
		return this.get(id,null);
	}
	
	/**
	 * @param id
	 * @param familiasColunas
	 * @return
	 */
	public GetResponseVo get(String id, String familiasColunas){
		
		HashMap<String, FamiliaVo> dados = new HashMap<>();
		Table tabela = null;
		MensagemResponseEnum msg;
		
		try{

			Get g = new Get(Bytes.toBytes(id));
			
			if( familiasColunas != null ) {
			
				Arrays.stream( familiasColunas.split(",") ).forEach( fc -> {
					String[] fcVetor = fc.split(":");
					if( fcVetor.length == 1 ){
						g.addFamily(Bytes.toBytes( fcVetor[0] ));
					}else{
						g.addColumn(Bytes.toBytes( fcVetor[0] ), Bytes.toBytes( fcVetor[1] ));
					}
				});
			}
			
			tabela = getTabela(); 
			Result result = tabela.get(g);			
					
			if( !result.isEmpty() ) {
				result.listCells().stream().forEach( cell -> {

					final Map<String, String> colunas = new HashMap<String, String>();
					
					FamiliaVo familiaVo = geraDado( cell , colunas);
					if( dados.containsKey(familiaVo.getNomeFamilia() )  ) {
						FamiliaVo familiaVoAux = dados.get( familiaVo.getNomeFamilia());
						familiaVoAux.getColunas().putAll( familiaVo.getColunas() );
					}else {
						dados.put( familiaVo.getNomeFamilia() , familiaVo);
					}
				});
			
				msg = MensagemResponseEnum.SUCESSO;
			}else {
				msg = MensagemResponseEnum.NENHUM_RESULTADO;
			}
		}catch (NoSuchColumnFamilyException e1) {
			logger.error(String.format("Erro ao recuperar o campo no hbase %s ", e1));
			msg = MensagemResponseEnum.FALHA_HBASE_RECUPERAR_CAMPOS;
		}catch (Exception e2) {
			super.limpaConexao();
			logger.error(String.format("Erro em get no hbase %s ", e2));
			msg = MensagemResponseEnum.FALHA_HBASE_GET;
		}finally {
			if(tabela != null)
				try {
					tabela.close();
				} catch (IOException e) {
					logger.error(String.format("Erro ao fechar tabela. %s ", e));
				}
		}
		return montaResponse(dados,msg);
	}
	
	private FamiliaVo geraDado(Cell cell, Map<String, String> colunas) {
		
		String familia = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
		String coluna = Bytes.toString(cell.getQualifierArray() , cell.getQualifierOffset(), cell.getQualifierLength() );
		String valor = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength() ); 
		
		colunas.put(coluna, valor);
		
		return new FamiliaVo( familia , colunas);
	}
	
	
	private GetResponseVo montaResponse(HashMap<String, FamiliaVo> dados , MensagemResponseEnum msg) {
		GetResponseVo getResponseVo = new GetResponseVo(msg);
		if(dados != null && !dados.isEmpty())
			getResponseVo.setFamiliaVos(dados.values().parallelStream().collect(Collectors.toList()));
		return getResponseVo;
	}
	
}
