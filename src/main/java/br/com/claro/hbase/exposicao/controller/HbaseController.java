package br.com.claro.hbase.exposicao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.claro.hbase.exposicao.enums.MensagemResponseEnum;
import br.com.claro.hbase.exposicao.service.HbaseGetService;
import br.com.claro.hbase.exposicao.vo.GetResponseVo;
import br.com.claro.hbase.exposicao.vo.ResponseVo;

@RestController
public class HbaseController {

	@Autowired
	private HbaseGetService hbaseGetService;
	
	@GetMapping(value="/noAr" , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVo> noAr(){
		return ResponseEntity.ok( new ResponseVo( MensagemResponseEnum.NO_AR ));
	}
	
	@GetMapping(value="/get/{rowkey}" , produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<GetResponseVo> get(@PathVariable("rowkey") String rowkey){
		return ResponseEntity.ok( hbaseGetService.get(rowkey)); 
	}
	
	@GetMapping(value="/get/{rowkey}/{familiaColunas}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetResponseVo> get(@PathVariable("rowkey") String rowkey,
			@PathVariable("familiaColunas") String familiaColunas){
		return ResponseEntity.ok( hbaseGetService.get(rowkey,familiaColunas)); 
	}
}
