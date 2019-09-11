package br.com.claro.hbase.exposicao.service;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;
import br.com.claro.hbase.exposicao.enums.MensagemResponseEnum;
import br.com.claro.hbase.exposicao.vo.FamiliaVo;
import br.com.claro.hbase.exposicao.vo.GetResponseVo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class HbaseGetService extends HbaseService{

	private static final Logger logger = LogManager.getLogger(HbaseGetService.class);	
	
	private CircuitBreaker circuitBreaker; 
	private Retry retry;
	private RateLimiter rateLimiter;
	
	public HbaseGetService() {
		circuitBreaker = CircuitBreaker.ofDefaults("serviceGetHbase");	
		retry = Retry.ofDefaults("serviceGetHbase");	
		rateLimiter = RateLimiter.of("serviceGetHbase",  RateLimiterConfig.custom()
			    .timeoutDuration(Duration.ofMillis(100))
			    .limitRefreshPeriod(Duration.ofSeconds(1))
			    .limitForPeriod(1)
			    .build());

	}
			
	public GetResponseVo get(String id){
		return this.get(id, null);
	}
	
	public GetResponseVo get(String id, String familiasColunas){
		
		Supplier<GetResponseVo> supplier = ()->this.start(id, familiasColunas);	
		
		Supplier<GetResponseVo> decoratedSupplier = CircuitBreaker
			    .decorateSupplier(circuitBreaker, supplier);
		
		decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
		
		decoratedSupplier =  RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
		
		return Try.ofSupplier(decoratedSupplier)
			    .recover(throwable -> erro()).get();
		
		//return circuitBreaker.executeSupplier( ()->this.start(id, familiasColunas) );	
		
	}
	
	private GetResponseVo erro() {
		return new GetResponseVo(MensagemResponseEnum.FALHA_HBASE_GET);
	}
	
	/**
	 * @param id
	 * @param familiasColunas
	 * @return
	 */
	private GetResponseVo start(String id, String familiasColunas){
		
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
