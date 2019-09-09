package br.com.claro.hbase.exposicao.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FamiliaVo{
	
	public FamiliaVo(String familia, Map<String, String> colunas) {
		super();
		this.nomeFamilia = familia;
		this.colunas = colunas;
	}

	@JsonProperty("familia")
	private String nomeFamilia;
	
	@JsonProperty("colunas")
	private Map<String, String> colunas;

	public String getNomeFamilia() {
		return nomeFamilia;
	}

	public void setNomeFamilia(String nomeFamilia) {
		this.nomeFamilia = nomeFamilia;
	}

	public Map<String, String> getColunas() {
		return colunas;
	}

	public void setColunas(Map<String, String> colunas) {
		this.colunas = colunas;
	}

}
