package br.com.claro.hbase.exposicao.vo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.claro.hbase.exposicao.enums.MensagemResponseEnum;

public class GetResponseVo extends ResponseVo{

	public GetResponseVo(int codigo, String msg) {
		super(codigo, msg);
	}

	public GetResponseVo(MensagemResponseEnum mensagemResponseEnum) {
		super(mensagemResponseEnum.getCodigo(), mensagemResponseEnum.getMensagem());
	}
	
	@JsonProperty("familias")
	private List<FamiliaVo> familiaVos;

	public List<FamiliaVo> getFamiliaVos() {
		return familiaVos;
	}

	public void setFamiliaVos(List<FamiliaVo> familiaVos) {
		this.familiaVos = familiaVos;
	}
}
